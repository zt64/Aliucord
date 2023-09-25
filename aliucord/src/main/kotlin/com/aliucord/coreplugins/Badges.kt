/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.coreplugins

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.aliucord.Http
import com.aliucord.Main
import com.aliucord.Utils
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.utils.DimenUtils.dp
import com.discord.databinding.UserProfileHeaderBadgeBinding
import com.discord.models.guild.Guild
import com.discord.models.user.User
import com.discord.utilities.views.SimpleRecyclerAdapter
import com.discord.widgets.channels.list.WidgetChannelsList
import com.discord.widgets.user.Badge
import com.discord.widgets.user.profile.UserProfileHeaderView
import com.discord.widgets.user.profile.UserProfileHeaderViewModel.ViewState
import com.lytefast.flexinput.R
import java.util.concurrent.atomic.AtomicBoolean

private const val URL = "https://raw.githubusercontent.com/Aliucord/badges/main"

internal class Badges : Plugin(Manifest("Badges")) {
    private class CustomBadge(val id: String?, val url: String?, val text: String) {
        fun getDrawableId() = if (id == null) 0 else try {
            R.e::class.java.getDeclaredField(id).getInt(null)
        } catch (e: Throwable) {
            Main.logger.error("Failed to get drawable for $id", e)
            0
        }

        fun toDiscordBadge() = Badge(getDrawableId(), null, text, false, url)
    }

    private class UserBadges(val roles: Array<String>?, val custom: Array<CustomBadge>?)

    private val userBadges = hashMapOf<Long, List<Badge>?>()
    private val guildBadges = hashMapOf<Long, CustomBadge?>()
    private val cache = hashMapOf<String, Bitmap>()

    private val badgesAdapter = UserProfileHeaderView::class.java.getDeclaredField("badgesAdapter")
        .apply { isAccessible = true }
    private val dataField =
        SimpleRecyclerAdapter::class.java.getDeclaredField("data").apply { isAccessible = true }

    private val guildBadgeViewId = View.generateViewId()

    override fun load(context: Context) {
        val fetchingBadges = AtomicBoolean(false)

        GlobalPatcher.patch(
            UserProfileHeaderView::class.java.getDeclaredMethod(
                "updateViewState",
                ViewState.Loaded::class.java
            ),
            Hook { (it, state: ViewState.Loaded) ->
                val id = state.user.id
                if (userBadges.containsKey(id)) {
                    state.user.addUserBadges(it.thisObject)
                } else if (!fetchingBadges.getAndSet(true)) {
                    Utils.threadPool.execute {
                        try {
                            userBadges[id] = getUserBadges(
                                Http.simpleJsonGet("$URL/users/$id.json", UserBadges::class.java)!!
                            )
                            state.user.addUserBadges(it.thisObject)
                        } catch (e: Throwable) {
                            if (e is Http.HttpException && e.statusCode == 404) {
                                userBadges[id] = null
                            } else {
                                logger.error("Failed to get badges for user $id", e)
                            }
                        } finally {
                            fetchingBadges.set(false)
                        }
                    }
                }
            }
        )

        val bindingField = UserProfileHeaderView.BadgeViewHolder::class.java.getDeclaredField("binding").apply { isAccessible = true }
        GlobalPatcher.after<UserProfileHeaderView.BadgeViewHolder>("bind", Badge::class.java) { (_, badge: Badge) ->
            val url = badge.objectType
            if (badge.icon == 0 && url != null) {
                (bindingField[this] as UserProfileHeaderBadgeBinding).b.setImageUrl(url)
            }
        }

        patcher.after<WidgetChannelsList>("onViewBound", View::class.java) {
            val binding = WidgetChannelsList.`access$getBinding$p`(this)
            val toolbar = binding.g.parent as ViewGroup
            if (toolbar.getChildAt(0).id != guildBadgeViewId) toolbar.addView(ImageView(toolbar.context).apply {
                id = guildBadgeViewId
                setPadding(0, 0, 4.dp, 0)
            }, 0)
        }

        patcher.after<WidgetChannelsList>(
            "configureHeaderIcons",
            Guild::class.java,
            Boolean::class.javaPrimitiveType!!
        ) { (_, guild: Guild?) ->
            val id = guild?.id ?: return@after
            if (guildBadges.containsKey(id)) {
                addGuildBadge(id, this)
            } else {
                Utils.threadPool.execute {
                    try {
                        guildBadges[id] =
                            Http.simpleJsonGet("$URL/guilds/$id.json", CustomBadge::class.java)
                    } catch (e: Throwable) {
                        if (e is Http.HttpException && e.statusCode == 404) {
                            guildBadges[id] = null
                        } else {
                            logger.error("Failed to get badges for guild $id", e)
                        }
                    }
                    Utils.mainThread.post { addGuildBadge(id, this) }
                }
            }
        }
    }

    private fun getUserBadges(badges: UserBadges): List<Badge> {
        val list = ArrayList<Badge>(1)
        badges.roles?.forEach {
            when (it) {
                "dev" -> list.add(
                    Badge(
                        R.e.ic_staff_badge_blurple_24dp,
                        null,
                        "Aliucord Developer",
                        false,
                        null
                    )
                )

                "donor" -> list.add(
                    Badge(
                        0,
                        null,
                        "Aliucord Donor",
                        false,
                        "https://cdn.discordapp.com/emojis/859801776232202280.webp"
                    )
                )

                "contributor" -> list.add(
                    Badge(
                        0,
                        null,
                        "Aliucord Contributor",
                        false,
                        "https://cdn.discordapp.com/emojis/886587553187246120.webp"
                    )
                )
            }
        }
        if (badges.custom?.isNotEmpty() == true) list.addAll(badges.custom.map { it.toDiscordBadge() })
        return list
    }

    @Suppress("UNCHECKED_CAST")
    private fun User.addUserBadges(userProfileHeaderView: Any) {
        val badges = userBadges[id] ?: return
        val adapter = badgesAdapter[userProfileHeaderView] as SimpleRecyclerAdapter<*, *>
        val data = dataField[adapter] as MutableList<Badge>
        data += badges
    }

    private var lastSetGuild: Long = 0
    private fun addGuildBadge(id: Long, widgetChannelsList: WidgetChannelsList) {
        if (widgetChannelsList.view == null || lastSetGuild == id) return
        lastSetGuild = id
        val badge = guildBadges[id]
        val binding = WidgetChannelsList.`access$getBinding$p`(widgetChannelsList)
        val toolbar = binding.g.parent as ViewGroup
        toolbar.findViewById<ImageView>(guildBadgeViewId)?.apply {
            if (badge == null) visibility = View.GONE
            else {
                visibility = View.VISIBLE
                with(badge.getDrawableId()) {
                    if (this != 0) setImageResource(this) else setImageUrl(
                        badge.url ?: ""
                    )
                }
                setOnClickListener { Utils.showToast(badge.text) }
            }
        }
    }

    private fun ImageView.setImageUrl(url: String) = if (cache.containsKey(url)) {
        setImageBitmap(cache[url])
    } else {
        Utils.threadPool.execute {
            Http.Request(url).execute().stream().use { BitmapFactory.decodeStream(it) }.let {
                cache[url] = it
                Utils.mainThread.post { setImageBitmap(it) }
            }
        }
    }
}
