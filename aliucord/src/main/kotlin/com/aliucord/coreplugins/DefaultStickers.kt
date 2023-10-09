/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2023 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.coreplugins

import android.content.Context
import com.aliucord.Http
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.utils.GsonUtils
import com.discord.api.channel.Channel
import com.discord.api.message.Message
import com.discord.api.sticker.Sticker
import com.discord.api.sticker.StickerType
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.utilities.messagesend.`MessageQueue$doSend$2`
import com.discord.utilities.rest.SendUtils
import com.discord.utilities.stickers.StickerUtils
import com.discord.utilities.stickers.StickerUtils.StickerSendability
import com.discord.utilities.user.UserUtils
import com.discord.widgets.chat.input.sticker.StickerCategoryItem
import com.discord.widgets.chat.input.sticker.StickerPickerViewModel
import de.robv.android.xposed.XC_MethodHook
import rx.subjects.BehaviorSubject
import java.util.Locale

internal class DefaultStickers : Plugin(Manifest("DefaultStickers")) {
    override fun load(context: Context) {
        val stickerPickerViewModel = StickerPickerViewModel::class.java
        val localeField = stickerPickerViewModel.getDeclaredField("locale").apply { isAccessible = true }

        @Suppress("UNCHECKED_CAST")
        GlobalPatcher.patch(
            stickerPickerViewModel.getDeclaredMethod(
                "createCategoryItems",
                StickerPickerViewModel.StoreState.Loaded::class.java,
                List::class.java,
                List::class.java
            ),
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val data = param.args[0] as StickerPickerViewModel.StoreState.Loaded
                    val me = data.meUser

                    if (UserUtils.INSTANCE.getCanUsePremiumStickers(me)) return

                    val items = param.args[1] as MutableList<*>
                    val companion = StickerPickerViewModel.Companion
                    val locale = localeField[param.thisObject] as Locale
                    val query = data.searchInputStringUpper.lowercase(locale)
                    val animationSettings = data.stickerAnimationSettings

                    data.enabledStickerPacks.forEach { pack ->
                        items += StickerPickerViewModel.Companion.`access$buildStickerListItems`(
                            companion,
                            pack,
                            query,
                            animationSettings,
                            locale,
                            me
                        ) as List<Nothing>
                    }
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    val data = param.args[0] as StickerPickerViewModel.StoreState.Loaded
                    if (UserUtils.INSTANCE.getCanUsePremiumStickers(data.meUser)) return
                    val res = param.result as MutableList<StickerCategoryItem>
                    val selected = data.selectedCategoryId
                    var i = res.lastOrNull()?.categoryRange?.second ?: 0
                    data.enabledStickerPacks.forEach { pack ->
                        val j = pack.stickers.size + 1 + i
                        res += StickerCategoryItem.PackItem(pack, i to j, selected == pack.id)
                        i = j
                    }
                }
            })

        GlobalPatcher.before<StickerUtils>(
            "getStickerSendability",
            Sticker::class.java,
            User::class.java,
            Channel::class.java,
            Long::class.javaObjectType
        ) { (it, sticker: Sticker) ->
            if (sticker.k() == StickerType.STANDARD) it.result = StickerSendability.SENDABLE
        }

        patcher.before<`MessageQueue$doSend$2`<*, *>>(
            "call",
            SendUtils.SendPayload.ReadyToSend::class.java
        ) { (it, payload: SendUtils.SendPayload.ReadyToSend) ->
            if (UserUtils.INSTANCE.getCanUsePremiumStickers(StoreStream.getUsers().me)) return@before

            val message = payload.message
            if (message.stickerIds.isEmpty()) return@before
            it.result = BehaviorSubject.l0(
                Http.Request.newDiscordRNRequest("/channels/${`$message`.channelId}/messages", "POST")
                    .executeWithJson(GsonUtils.gsonRestApi, message)
                    .json(GsonUtils.gsonRestApi, Message::class.java)
            )
        }
    }
}
