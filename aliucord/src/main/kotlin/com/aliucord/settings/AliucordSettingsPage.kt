/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.settings

import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.aliucord.*
import com.aliucord.fragments.SettingsPage
import com.discord.stores.StoreStream
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R

// These keys aren't consistent because they were originally part of different modules
internal const val AUTO_DISABLE_ON_CRASH_KEY = "autoDisableCrashingPlugins"
internal const val AUTO_UPDATE_PLUGINS_KEY = "AC_plugins_auto_update_enabled"
internal const val AUTO_UPDATE_ALIUCORD_KEY = "AC_aliucord_auto_update_enabled"
internal const val ALIUCORD_FROM_STORAGE_KEY = "AC_from_storage"

internal class AliucordSettingsPage : SettingsPage() {
    @Suppress("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Zeetcord")
        setActionBarSubtitle("Zeetcord Settings")

        val ctx = view.context

        addHeader("Zeetcord Settings")
        addSwitch(
            AUTO_DISABLE_ON_CRASH_KEY,
            "Automatically disable plugins on crash",
            "When a plugin is found to be causing crashes, it will automatically be disabled",
            true
        )
        addSwitch(AUTO_UPDATE_ALIUCORD_KEY, "Automatically update Zeetcord")
        addSwitch(AUTO_UPDATE_PLUGINS_KEY, "Automatically update plugins")

        if (StoreStream.getUserSettings().isDeveloperMode) {
            addDivider(ctx)
            addHeader("Developer Settings")
            addSwitch(
                ALIUCORD_FROM_STORAGE_KEY,
                "Use Zeetcord from storage",
                "Meant for developers. Do not enable unless you know what you're doing. If someone else is telling you to do this, you are likely being scammed."
            )
        }

        addDivider(ctx)
        addHeader(ctx, "Links")
        addLink("Source Code", R.e.ic_account_github_white_24dp) {
            Utils.launchUrl(Constants.ALIUCORD_GITHUB_REPO)
        }
    }

    private fun addSwitch(
        setting: String,
        title: String,
        subtitle: String? = null,
        default: Boolean = false
    ) {
        Utils.createCheckedSetting(context, CheckedSetting.ViewType.SWITCH, title, subtitle).run {
            isChecked = Main.settings.getBool(setting, default)
            setOnCheckedListener { Main.settings.setBool(setting, it) }
            linearLayout.addView(this)
        }
    }

    private fun addLink(text: String, @DrawableRes drawable: Int, action: View.OnClickListener) {
        val ctx = context

        TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).run {
            this.text = text
            val drawableEnd = ContextCompat.getDrawable(ctx, R.e.ic_open_in_new_white_24dp)?.run {
                mutate()
                Utils.tintToTheme(this)
            }
            val drawableStart = ContextCompat.getDrawable(ctx, drawable)?.run {
                mutate()
                Utils.tintToTheme(this)
            }
            setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, drawableEnd, null)
            setOnClickListener(action)
            linearLayout.addView(this)
        }
    }
}
