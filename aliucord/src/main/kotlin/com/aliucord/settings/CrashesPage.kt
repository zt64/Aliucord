/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.settings

import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils
import com.aliucord.utils.MDUtils
import com.aliucord.views.DangerButton
import com.lytefast.flexinput.R
import java.io.File

private data class CrashLog(val timestamp: String, val stacktrace: String, var times: Int)

internal class CrashesPage : SettingsPage() {
    @Suppress("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Crash Logs")

        val context = view.context
        val p = DimenUtils.defaultPadding / 2

        val dir = File(Constants.CRASHLOGS_PATH)
        val files = dir.listFiles().orEmpty()

        addHeaderButton("Open Crashlog Folder", R.e.ic_open_in_new_white_24dp) {
            if (!dir.exists() && !dir.mkdir()) {
                Utils.showToast("Failed to create crashlogs directory!", true)
            } else {
                Utils.launchFileExplorer(dir)
            }
            true
        }

        headerBar.menu.add("Clear Crashes")
            .setIcon(ContextCompat.getDrawable(context, R.e.ic_delete_24dp))
            .setEnabled(files.isNotEmpty())
            .setOnMenuItemClickListener {
                files.forEach(File::delete)
                reRender()
                true
            }

        val crashes = getCrashes()
        if (crashes.isNullOrEmpty()) {
            TextView(context, null, 0, R.i.UiKit_Settings_Item_Header).run {
                isAllCaps = false
                text = "Woah, no crashes :O"
                typeface = ResourcesCompat.getFont(context, Constants.Fonts.WHITNEY_SEMIBOLD)
                gravity = Gravity.CENTER

                linearLayout.addView(this)
            }
            DangerButton(context).run {
                text = "LET'S CHANGE THAT"
                setPadding(p, p, p, p)
                setOnClickListener { throw RuntimeException("You fool...") }
                linearLayout.addView(this)
            }
        } else {
            TextView(context, null, 0, R.i.UiKit_Settings_Item_SubText).run {
                text = "Hint: Crashlogs are accesible via your file explorer at Aliucord/crashlogs"
                typeface = ResourcesCompat.getFont(context, Constants.Fonts.WHITNEY_MEDIUM)
                gravity = Gravity.CENTER
                linearLayout.addView(this)
            }
            crashes.values.forEach { (timestamp, stacktrace, times) ->
                TextView(context, null, 0, R.i.UiKit_Settings_Item_Header).run {
                    var title = timestamp
                    if (times > 1) title += " ($times)"
                    text = title
                    typeface = ResourcesCompat.getFont(context, Constants.Fonts.WHITNEY_SEMIBOLD)
                    linearLayout.addView(this)
                }
                TextView(context).run {
                    text = MDUtils.renderCodeBlock(
                        context,
                        SpannableStringBuilder(), null, stacktrace
                    )
                    setOnClickListener {
                        Utils.setClipboard("CrashLog-$timestamp", stacktrace)
                        Utils.showToast("Copied to clipboard")
                    }
                    linearLayout.addView(this)
                }
            }
        }
    }

    private fun getCrashes(): Map<Int, CrashLog>? {
        val folder = File(Constants.CRASHLOGS_PATH)
        val files = folder.listFiles { f -> f.isFile }
            ?.sortedByDescending(File::lastModified)
            ?: return null

        val res = linkedMapOf<Int, CrashLog>()

        for (file in files) {
            val content = file.readText()

            res.getOrPut(content.hashCode()) {
                CrashLog(
                    timestamp = file.name
                        .replace(".txt", "")
                        .replace("_".toRegex(), ":"),
                    stacktrace = content,
                    times = 0
                )
            }.times++
        }

        return res
    }
}
