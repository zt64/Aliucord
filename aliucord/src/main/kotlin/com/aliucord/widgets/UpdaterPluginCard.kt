/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.widgets

import android.content.Context
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.updater.PluginUpdater
import com.aliucord.utils.ChangelogUtils.show
import com.aliucord.utils.DimenUtils.defaultCardRadius
import com.aliucord.utils.DimenUtils.defaultPadding
import com.aliucord.views.ToolbarButton
import com.discord.utilities.color.ColorCompat
import com.google.android.material.card.MaterialCardView
import com.lytefast.flexinput.R

@Suppress("SetTextI18n", "ViewConstructor")
internal class UpdaterPluginCard(context: Context, plugin: String, forceUpdate: Runnable) : MaterialCardView(context) {
    init {
        val paddingHalf = defaultPadding / 2

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, paddingHalf, 0, 0)
        }
        useCompatPadding = true
        setCardBackgroundColor(ColorCompat.getThemedColor(context, R.b.colorBackgroundSecondary))
        radius = defaultCardRadius.toFloat()
        defaultPadding.let { setPadding(it, it, it, it) }

        val p = PluginManager.plugins[plugin]!!
        val layout = ConstraintLayout(context)

        val id = generateViewId()
        var tv = TextView(context, null, 0, R.i.UiKit_TextView_H2).apply {
            this.id = id
            text = plugin
        }
        layout.addView(tv)
        with(ConstraintSet()) {
            clone(layout)
            constrainedHeight(id, true)
            connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
            connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            applyTo(layout)
        }
        val btnLayoutId = generateViewId()
        val buttonLayout = GridLayout(context).apply {
            this.id = btnLayoutId
            rowCount = 1
            columnCount = 2
            useDefaultMargins = true
            setPadding(0, 0, 0, 0)
        }
        val verid = generateViewId()
        tv = TextView(context, null, 0, R.i.UiKit_TextView_Subtext).apply {
            val info = PluginUpdater.getUpdateInfo(p)
            this.id = verid
            text = "v${p.manifest.version} -> v${info?.version ?: "?"}"
            if (info?.changelog != null) {
                val changeLogButton = ToolbarButton(context).apply {
                    setImageDrawable(ContextCompat.getDrawable(context, R.e.ic_history_white_24dp))
                    setPadding(paddingHalf, paddingHalf, paddingHalf, paddingHalf)
                    setOnClickListener {
                        show(
                            context, "${p.name} v${info.version}", info.changelogMedia, info.changelog
                        )
                    }
                    layoutParams = GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(0)).apply {
                        setGravity(Gravity.CENTER_VERTICAL)
                    }
                }

                buttonLayout.addView(changeLogButton)
            }
        }
        layout.addView(tv)
        with(ConstraintSet()) {
            clone(layout)
            constrainedHeight(verid, true)
            connect(verid, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
            connect(verid, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            applyTo(layout)
        }

        val update = ToolbarButton(context).apply {
            setImageDrawable(
                ContextCompat.getDrawable(context, R.e.ic_file_download_white_24dp)
            )
            setPadding(paddingHalf, paddingHalf, 0, paddingHalf)
            setOnClickListener {
                isEnabled = false
                Utils.threadPool.execute {
                    try {
                        PluginUpdater.update(plugin)
                        PluginUpdater.updates.remove(plugin)
                        PluginManager.logger.infoToast("Successfully updated ${p.name}")
                    } catch (t: Throwable) {
                        PluginManager.logger.errorToast(
                            "Sorry, something went wrong while updating ${p.name}",
                            t
                        )
                    } finally {
                        Utils.mainThread.post(forceUpdate)
                    }
                }
            }
            layoutParams = GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(1)).apply {
                setGravity(Gravity.CENTER_VERTICAL)
            }
        }

        buttonLayout.addView(update)
        layout.addView(buttonLayout)

        with(ConstraintSet()) {
            clone(layout)
            connect(
                btnLayoutId,
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT
            )
            connect(btnLayoutId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            applyTo(layout)
        }

        addView(layout)
    }
}
