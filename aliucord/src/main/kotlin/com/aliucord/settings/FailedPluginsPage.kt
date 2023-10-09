/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.settings

import android.view.View
import android.widget.LinearLayout.LayoutParams
import com.aliucord.PluginManager
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.widgets.FailedPluginWidget
import com.discord.utilities.color.ColorCompat
import com.google.android.material.card.MaterialCardView
import com.lytefast.flexinput.R

internal class FailedPluginsPage : SettingsPage() {
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Plugin Errors")

        PluginManager.failedToLoad.forEach { (file, reason) ->
            linearLayout.addView(MaterialCardView(view.context).apply {
                setCardBackgroundColor(ColorCompat.getThemedColor(view.context, R.b.colorBackgroundSecondary))
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 8.dp, 0, 8.dp)
                }
                addView(FailedPluginWidget(view.context, file, reason) {
                    linearLayout.removeView(this)
                })
            })
        }
    }
}
