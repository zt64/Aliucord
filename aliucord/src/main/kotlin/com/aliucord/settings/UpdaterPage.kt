/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.settings

import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.aliucord.Utils
import com.aliucord.Utils.getDrawableByAttr
import com.aliucord.Utils.pluralise
import com.aliucord.Utils.pluralize
import com.aliucord.Utils.showToast
import com.aliucord.Utils.tintToTheme
import com.aliucord.fragments.SettingsPage
import com.aliucord.updater.PluginUpdater
import com.aliucord.updater.Updater
import com.aliucord.utils.DimenUtils.defaultPadding
import com.aliucord.widgets.UpdaterPluginCard
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lytefast.flexinput.R

internal class UpdaterPage : SettingsPage() {
    private var stateText = "No new updates found"

    @Suppress("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Updater")
        setActionBarSubtitle(stateText)

        val context = requireContext()

        Utils.threadPool.execute {
            val sb = when {
                Updater.usingDexFromStorage() -> {
                    Snackbar.make(
                        linearLayout,
                        "Updater disabled due to using Aliucord from storage.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                }
                Updater.isDiscordOutdated() -> {
                    Snackbar
                        .make(
                            linearLayout,
                            "Your Base Discord is outdated. Please update using the installer.",
                            BaseTransientBottomBar.LENGTH_INDEFINITE
                        )
                        .setAction("Open Installer") { v ->
                            val ctx = v.context
                            val i =
                                ctx.packageManager.getLaunchIntentForPackage("com.aliucord.installer")
                            if (i != null) ctx.startActivity(i) else showToast("Please install the Aliucord installer and try again.")
                        }
                }
                Updater.isAliucordOutdated() -> {
                    Snackbar
                        .make(
                            linearLayout,
                            "Your Aliucord is outdated.",
                            Snackbar.LENGTH_INDEFINITE
                        )
                        .setAction("Update") { v ->
                            Utils.threadPool.execute {
                                val ctx = v.context
                                try {
                                    Updater.updateAliucord(ctx)
                                    showToast("Successfully updated Aliucord.")
                                    Snackbar
                                        .make(
                                            linearLayout,
                                            "Restart to apply the update.",
                                            Snackbar.LENGTH_INDEFINITE
                                        )
                                        .setAction("Restart") {
                                            val intent =
                                                context.packageManager.getLaunchIntentForPackage(context.packageName)
                                            context.startActivity(Intent.makeRestartActivityTask(intent!!.component))
                                            Runtime.getRuntime().exit(0)
                                        }.apply {
                                            setBackgroundTint(-0x44cd)
                                            setTextColor(Color.BLACK)
                                            setActionTextColor(Color.BLACK)
                                            show()
                                        }
                                } catch (th: Throwable) {
                                    PluginUpdater.logger.errorToast(
                                        "Failed to update Aliucord. Check the debug log for more info",
                                        th
                                    )
                                }
                            }
                        }
                }
                else -> return@execute
            }

            sb
                .setBackgroundTint(-0x44cd) // https://developer.android.com/reference/android/R.color#holo_orange_light
                .setTextColor(Color.BLACK)
                .setActionTextColor(Color.BLACK)
                .show()
        }
        addHeaderButton(
            "Refresh",
            tintToTheme(getDrawableByAttr(context, R.b.ic_refresh))
        ) { item ->
            item.isEnabled = false
            setActionBarSubtitle("Checking for updates...")
            Utils.threadPool.execute {
                PluginUpdater.cache.clear()
                PluginUpdater.checkUpdates(false)
                val updateCount = PluginUpdater.updates.size
                stateText = if (updateCount == 0) "No updates found" else "Found ${pluralize(updateCount, "update")}"
                Utils.mainThread.post(::reRender)
            }
            true
        }
        addHeaderButton("Update All", R.e.ic_file_download_white_24dp) { item ->
            item.isEnabled = false
            setActionBarSubtitle("Updating...")
            Utils.threadPool.execute {
                val updateCount = PluginUpdater.updateAll()
                stateText = when (updateCount) {
                    0 -> "No updates found"
                    -1 -> "Something went wrong while updating. Please try again"
                    else -> "Successfully updated ${pluralise(updateCount, "plugin")}!"
                }
                Utils.mainThread.post(::reRender)
            }
            true
        }
        val updateCount = PluginUpdater.updates
        if (updateCount.isEmpty()) {
            val state = TextView(context, null, 0, R.i.UiKit_Settings_Item_SubText).apply {
                text = stateText
                setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding)
                setGravity(Gravity.CENTER)
            }
            return addView(state)
        }
        stateText = "Found ${pluralise(updateCount.size, "update")}"
        setActionBarSubtitle(stateText)
        for (plugin in PluginUpdater.updates) {
            addView(UpdaterPluginCard(context, plugin, ::reRender))
        }
    }
}
