@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.coreplugins.repo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants.Fonts.WHITNEY_SEMIBOLD
import com.aliucord.Utils
import com.aliucord.coreplugins.repo.PluginRepo.Companion.settingsAPI
import com.aliucord.widgets.BottomSheet
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R

internal class BottomShit : BottomSheet() {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        val ctx: Context = requireContext()
        setPadding(20)
        val title = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "PluginRepo"
            typeface = ResourcesCompat.getFont(ctx, WHITNEY_SEMIBOLD)
            setGravity(Gravity.START)
        }

        val notifyNewPlugins: CheckedSetting = Utils.createCheckedSetting(
            ctx,
            CheckedSetting.ViewType.CHECK,
            "Notify new plugins",
            ""
        )
        notifyNewPlugins.isChecked = settingsAPI.getBool("checkNewPlugins", true)
        notifyNewPlugins.setOnCheckedListener { aBoolean ->
            settingsAPI.setBool("checkNewPlugins", aBoolean)
        }
        addView(title)
        addView(notifyNewPlugins)
    }
}
