/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.widgets

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.utils.DimenUtils.defaultCardRadius
import com.aliucord.utils.DimenUtils.defaultPadding
import com.aliucord.views.*
import com.discord.utilities.color.ColorCompat
import com.discord.views.CheckedSetting
import com.google.android.material.card.MaterialCardView
import com.lytefast.flexinput.R

@Suppress("SetTextI18n")
internal class PluginCard(ctx: Context) : MaterialCardView(ctx) {
    private val root: LinearLayout
    val switchHeader: CheckedSetting
    val titleView: TextView
    val descriptionView: TextView
    private val buttonLayout: GridLayout
    val settingsButton: Button
    val uninstallButton: DangerButton
    val repoButton: ToolbarButton
    val changeLogButton: ToolbarButton

    init {
        setRadius(defaultCardRadius.toFloat())
        setCardBackgroundColor(ColorCompat.getThemedColor(ctx, R.b.colorBackgroundSecondary))
        setLayoutParams(LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        val p = defaultPadding
        val p2 = p / 2
        root = LinearLayout(ctx)
        switchHeader = CheckedSetting(ctx, null).apply {
            removeAllViews()
            f(CheckedSetting.ViewType.SWITCH)
            l.b().apply {
                setPadding(
                    0,
                    paddingTop,
                    paddingRight,
                    paddingBottom
                )
                setBackgroundColor(
                    ColorCompat.getThemedColor(
                        ctx,
                        R.b.colorBackgroundSecondaryAlt
                    )
                )
            }
            setSubtext(null)
        }

        titleView = switchHeader.l.a().apply {
            textSize = 16.0f
            setTypeface(ResourcesCompat.getFont(ctx, Constants.Fonts.WHITNEY_SEMIBOLD))
            movementMethod = LinkMovementMethod.getInstance()
        }
        root.addView(switchHeader)
        root.addView(Divider(ctx))
        descriptionView = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Addition).apply {
            setPadding(p, p, p, p2)
        }
        root.addView(descriptionView)
        buttonLayout = GridLayout(ctx).apply {
            rowCount = 1
            columnCount = 5
            useDefaultMargins = true
            setPadding(p2, 0, p2, 0)
        }
        settingsButton = Button(ctx).apply {
            text = "Settings"
        }
        uninstallButton = DangerButton(ctx).apply {
            text = "Uninstall"
        }
        repoButton = ToolbarButton(ctx).apply {
            setImageDrawable(ContextCompat.getDrawable(ctx, R.e.ic_account_github_white_24dp))
        }
        changeLogButton = ToolbarButton(ctx).apply {
            setImageDrawable(ContextCompat.getDrawable(ctx, R.e.ic_history_white_24dp))
        }
        buttonLayout.addView(
            settingsButton,
            GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(3))
        )
        buttonLayout.addView(
            uninstallButton,
            GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(4))
        )
        val params = GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(0)).apply {
            setGravity(Gravity.CENTER_VERTICAL)
        }
        buttonLayout.addView(repoButton, params)
        val clparams = GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(1)).apply {
            setGravity(Gravity.CENTER_VERTICAL)
        }
        buttonLayout.addView(changeLogButton, clparams)
        root.addView(buttonLayout)
        addView(root)
    }
}
