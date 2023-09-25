@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.coreplugins.repo

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.postDelayed
import com.aliucord.Constants.Fonts.WHITNEY_MEDIUM
import com.aliucord.Utils
import com.aliucord.Utils.appActivity
import com.aliucord.Utils.getResId
import com.aliucord.Utils.nestedChildAt
import com.aliucord.Utils.openPage
import com.aliucord.Utils.openPageWithProxy
import com.aliucord.Utils.showToast
import com.aliucord.api.NotificationsAPI.display
import com.aliucord.api.SettingsAPI
import com.aliucord.entities.NotificationData
import com.aliucord.entities.Plugin
import com.aliucord.patcher.Hook
import com.aliucord.settings.UpdaterPage
import com.discord.models.domain.ModelUserSettings
import com.discord.stores.StoreStream
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.settings.WidgetSettings
import com.discord.widgets.settings.WidgetSettingsAppearance
import com.discord.widgets.settings.`WidgetSettingsAppearance$updateTheme$1`
import com.lytefast.flexinput.R
import java.util.Calendar

internal class PluginRepo : Plugin(Manifest("PluginRepo2")) {
    override fun start(context: Context) {
        settingsAPI = settings
        settingsTab = SettingsTab(BottomShit::class.java, SettingsTab.Type.BOTTOM_SHEET)
        if (settings.getBool("checkNewPlugins", true)) {
            Utils.threadPool.execute {
                try {
                    Thread.sleep(10000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                val newPlugins = PluginRepoAPI.checkNewPlugins()
                if (newPlugins) {
                    Utils.mainThread.post {
                        display(
                            NotificationData()
                                .setTitle("PluginRepo")
                                .setBody("New Plugins are available")
                        )
                    }
                }
            }
        }

        /*
        Utils.threadPool.execute(() -> {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Utils.mainThread.post(() -> {
                Utils.openPageWithProxy(Utils.getAppActivity(),new PluginsPage());
            });
        });
         */
        val guh = Calendar.getInstance()[Calendar.MONTH]
        val guh2 = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        if (guh == 3 && guh2 == 1 && settings.getBool("dj391kl", true)) {
            StoreStream.getUserSettingsSystem().setTheme(
                ModelUserSettings.THEME_LIGHT,
                true,
                `WidgetSettingsAppearance$updateTheme$1`(
                    WidgetSettingsAppearance(),
                    ModelUserSettings.THEME_LIGHT
                )
            )
            settings.setBool("dj391kl", false)

            Utils.mainThread.postDelayed(3000) {
                showToast("Have a blind day! **PluginRepo**")
            }
        }
        patcher.patch(
            WidgetSettings::class.java.getDeclaredMethod("onViewBound", View::class.java), Hook { cf ->
                val ctx = (cf.thisObject as WidgetSettings).requireContext()
                val view = cf.args[0] as CoordinatorLayout
                val v = view.nestedChildAt<LinearLayoutCompat>(1, 0)
                val font = ResourcesCompat.getFont(ctx, WHITNEY_MEDIUM)
                // Stole this from Main.java
                val baseIndex = v.indexOfChild(
                    v.findViewById(getResId("developer_options_divider", "id"))
                )
                val openPluginRepo = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                    text = "Open Plugin Repo"
                    typeface = font
                }
                val iconColor = ColorCompat.getThemedColor(ctx, R.b.colorInteractiveNormal)
                val icon = ContextCompat.getDrawable(ctx, R.e.ic_upload_24dp)
                if (icon != null) {
                    val copy = icon.mutate()
                    copy.setTint(iconColor)
                    openPluginRepo.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        copy,
                        null,
                        null,
                        null
                    )
                }
                openPluginRepo.setOnClickListener { e ->
                    openPage(e.context, UpdaterPage::class.java)
                }
                v.addView(openPluginRepo, baseIndex)
                openPluginRepo.setOnClickListener {
                    openPageWithProxy(appActivity, PluginsPage())
                }
            }
        )
    }

    override fun stop(context: Context) {
        patcher.unpatchAll()
        commands.unregisterAll()
    }

    companion object {
        lateinit var settingsAPI: SettingsAPI
    }
}
