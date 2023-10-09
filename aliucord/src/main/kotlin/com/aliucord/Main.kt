/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.buildSpannedString
import com.aliucord.Constants.Fonts
import com.aliucord.PluginManager.getPluginPrefKey
import com.aliucord.PluginManager.isPluginEnabled
import com.aliucord.PluginManager.loadPlugin
import com.aliucord.PluginManager.startPlugin
import com.aliucord.PluginManager.stopPlugin
import com.aliucord.Utils.appContext
import com.aliucord.Utils.getResId
import com.aliucord.Utils.isDebuggable
import com.aliucord.Utils.joinSupportServer
import com.aliucord.Utils.launchUrl
import com.aliucord.Utils.nestedChildAt
import com.aliucord.Utils.openPage
import com.aliucord.Utils.showToast
import com.aliucord.coreplugins.CorePlugins
import com.aliucord.patcher.*
import com.aliucord.patcher.GlobalPatcher.addPatch
import com.aliucord.patcher.GlobalPatcher.patch
import com.aliucord.patcher.component1
import com.aliucord.patcher.component2
import com.aliucord.settings.*
import com.aliucord.updater.PluginUpdater
import com.aliucord.utils.ChangelogUtils.FooterAction
import com.aliucord.views.Divider
import com.aliucord.views.ToolbarButton
import com.discord.app.AppActivity
import com.discord.app.AppComponent
import com.discord.app.AppLog.LoggedItem
import com.discord.databinding.WidgetDebuggingAdapterItemBinding
import com.discord.models.domain.emoji.ModelEmojiUnicode
import com.discord.utilities.color.ColorCompat
import com.discord.widgets.changelog.WidgetChangeLog
import com.discord.widgets.chat.list.WidgetChatList
import com.discord.widgets.debugging.WidgetDebugging
import com.discord.widgets.settings.WidgetSettings
import com.lytefast.flexinput.R
import java.io.File
import java.sql.Timestamp
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.concurrent.thread
import kotlin.reflect.KClass
import kotlin.system.exitProcess

public object Main {
    /** Whether Aliucord has been preInitialized  */
    private var preInitialized = false

    /** Whether Aliucord has been initialized  */
    private var initialized = false

    private var loadedPlugins = false

    @JvmField
    public val logger: Logger = Logger()
    public lateinit var settings: SettingsUtilsJSON

    private fun preInitWithPermissions(activity: AppCompatActivity) {
        settings = SettingsUtilsJSON("Aliucord")

        CorePlugins.loadAll(activity)
        loadUserPlugins(activity)
    }

    @JvmStatic
    public fun preInit(activity: AppActivity) {
        if (preInitialized) return

        preInitialized = true

        Utils.appActivity = activity

        if (checkPermissions(activity)) preInitWithPermissions(activity)

        GlobalPatcher.after<AppActivity>("onCreate", Bundle::class.java) {
            Utils.appActivity = this
        }
        GlobalPatcher.after<WidgetChatList> {
            Utils.widgetChatList = this
        }
    }

    /**
     * Aliucord's init hook. Plugins are started here
     */
    @Suppress("SetTextI18n")
    @JvmStatic
    public fun init(activity: AppActivity) {
        preInit(activity)

        if (initialized) return

        initialized = true

        Thread.setDefaultUncaughtExceptionHandler(::crashHandler)

        // add stacktraces in debug logs page
        try {
            val c = WidgetDebugging.Adapter.Item::class.java
            val debugItemBinding = c.getDeclaredField("binding").apply {
                isAccessible = true
            }

            addPatch(
                c,
                "onConfigure",
                arrayOf(Int::class.javaPrimitiveType!!, LoggedItem::class.java),
                Hook { (param, _: Any, loggedItem: LoggedItem) ->
                    val th = loggedItem.m ?: return@Hook

                    try {
                        val logMessage = (debugItemBinding[param.thisObject] as WidgetDebuggingAdapterItemBinding).b
                        val spannedString = buildSpannedString {
                            append("\n  at ")
                            append(th.stackTrace.take(12).joinToString("\n  at "))
                            setSpan(
                                AbsoluteSizeSpan(12, true),
                                0,
                                length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                        logMessage.append(spannedString)
                    } catch (e: Throwable) {
                        logger.error(e)
                    }
                }
            )
        } catch (e: Throwable) {
            logger.error(e)
        }

        if (loadedPlugins) {
            CorePlugins.startAll(activity)
            startAllPlugins()
        }

        GlobalPatcher.after<WidgetSettings>("onViewBound", View::class.java) { (_, view: ViewGroup) ->
            val layout = view.nestedChildAt<ViewGroup>(1, 0)
            val context = layout.context
            var baseIndex = layout.indexOfChild(
                layout.findViewById(getResId("developer_options_divider", "id"))
            )

            fun addView(view: View) = layout.addView(view, baseIndex++)

            addView(Divider(context))

            val header = TextView(context, null, 0, R.i.UiKit_Settings_Item_Header).apply {
                text = "Aliucord"
                typeface = ResourcesCompat.getFont(context, Fonts.WHITNEY_SEMIBOLD)
            }

            addView(header)

            val font = ResourcesCompat.getFont(context, Fonts.WHITNEY_MEDIUM)
            fun makeSettingsEntry(
                text: String,
                @DrawableRes resId: Int,
                component: KClass<out AppComponent>
            ) {
                val label = TextView(context, null, 0, R.i.UiKit_Settings_Item_Icon).apply {
                    this.text = text
                    typeface = font
                }

                ContextCompat.getDrawable(context, resId)?.let { icon ->
                    icon.mutate().setTint(
                        ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal)
                    )
                    label.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
                }

                label.setOnClickListener { e -> openPage(e.context, component.java) }
                addView(label)
            }

            makeSettingsEntry("Settings", R.e.ic_behavior_24dp, AliucordSettingsPage::class)
            makeSettingsEntry("Plugins", R.e.ic_clear_all_white_24dp, InstalledPluginsPage::class)
            makeSettingsEntry("Updater", R.e.ic_file_download_white_24dp, UpdaterPage::class)
            makeSettingsEntry("Crashes", R.e.ic_history_white_24dp, CrashesPage::class)
            makeSettingsEntry("Open Debug Log", R.e.ic_audit_logs_24dp, WidgetDebugging::class)

            val versionView = layout.findViewById<TextView>(getResId("app_info_header", "id"))
            var text = "${versionView.text} | Aliucord ${BuildConfig.GIT_REVISION}"

            if (isDebuggable) text += " [DEBUGGABLE]"

            versionView.text = text

            layout.findViewById<TextView>(getResId("upload_debug_logs", "id")).apply {
                text = "Aliucord Support Server"
                setOnClickListener { e -> joinSupportServer(e.context) }
            }
        }

        // Patch to repair built-in emotes is needed because installer doesn't recompile resources,
        // so they stay in package com.discord instead of apk package name
        patch(
            ModelEmojiUnicode::class.java,
            "getImageUri",
            arrayOf(String::class.java, Context::class.java),
            InsteadHook { (_, id: String) ->
                "res:///${getResId("emoji_${id}", "raw")}"
            }
        )

        // Patch to allow changelogs without media
        GlobalPatcher.before<WidgetChangeLog>("configureMedia", String::class.java) { param ->
            mostRecentIntent.getStringExtra("INTENT_EXTRA_VIDEO")
                ?: return@before

            WidgetChangeLog.`access$getBinding$p`(this).apply {
                i.visibility = View.GONE // changeLogVideoOverlay
                h.visibility = View.GONE // changeLogVideo
            }

            param.result = null
        }

        // Patch for custom footer actions
        GlobalPatcher.before<WidgetChangeLog>("configureFooter") { param ->
            val binding = WidgetChangeLog.`access$getBinding$p`(this)
            val actions = mostRecentIntent.getParcelableArrayExtra("INTENT_EXTRA_FOOTER_ACTIONS")
                ?: return@before
            val twitterButton = binding.g
            val parent = twitterButton.parent as LinearLayout
            parent.removeAllViewsInLayout()

            actions.forEach { parcelable ->
                val action = parcelable as FooterAction
                val button = ToolbarButton(parent.context).apply {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            parent.context,
                            action.drawableResourceId
                        ), false
                    )
                    setPadding(
                        twitterButton.paddingLeft,
                        twitterButton.paddingTop,
                        twitterButton.paddingRight,
                        twitterButton.paddingBottom
                    )
                    layoutParams = twitterButton.layoutParams
                    setOnClickListener { launchUrl(action.url) }
                }

                parent.addView(button)
            }

            param.result = null
        }
    }

    private fun crashHandler(thread: Thread, throwable: Throwable) {
        if (Looper.getMainLooper().thread !== thread) {
            return logger.error("Uncaught exception on thread ${thread.name}", throwable)
        }

        thread {
            Looper.prepare()

            var badPlugin: String? = null
            var disabledPlugin = false

            for (ele in throwable.stackTrace) {
                val className = ele.className

                for ((key, plugin) in PluginManager.classLoaders) {
                    try {
                        val loadedClass = key.loadClass(className)

                        // class was loaded from the parent classloader, ignore
                        if (loadedClass.classLoader != key) continue

                        badPlugin = plugin.name

                        if (settings.getBool(AUTO_DISABLE_ON_CRASH_KEY, true)) {
                            disabledPlugin = true
                            settings.setBool(getPluginPrefKey(badPlugin), false)
                        }

                        break
                    } catch (ignored: ClassNotFoundException) {
                    }
                }
                if (badPlugin != null) break
            }
            val folder = File(Constants.CRASHLOGS_PATH)

            if (folder.exists() || folder.mkdir()) {
                val file = folder.resolve(
                    Timestamp(System.currentTimeMillis()).toString()
                        .replace(":".toRegex(), "_") + ".txt"
                )

                runCatching {
                    file.printWriter().use(throwable::printStackTrace)
                }
            }

            val msg = buildString {
                append("An unrecoverable crash occurred. ")
                if (badPlugin != null) {
                    append("This crash was caused by $badPlugin")
                    if (disabledPlugin) append(", so I automatically disabled it for you")
                    append(". ")
                }
                append("Check the crashes section in the settings for more info.")
            }

            Toast.makeText(appContext, msg, Toast.LENGTH_LONG).show()
            Looper.loop()
        }.start()

        runCatching {
            Thread.sleep(4200)
        }

        exitProcess(2)
    }

    private fun loadUserPlugins(context: Context) {
        val dir = File(Constants.PLUGINS_PATH)

        if (!dir.exists()) {
            val res = dir.mkdirs()

            if (!res) {
                return logger.error("Failed to create directories!", null)
            }
        }

        dir
            .listFiles { f -> f.extension == "zip" }
            ?.sorted()
            ?.forEach {
                try {
                    loadPlugin(context, it)
                } catch (e: Throwable) {
                    logger.error("Failed to load plugin: ${it.name}", e)
                }
            }

        if (PluginManager.failedToLoad.isNotEmpty()) {
            showToast("Some plugins failed to load. Check the plugins page for more info.")
        }

        loadedPlugins = true
    }

    private fun startAllPlugins() {
        PluginManager.plugins.keys.forEach { name ->
            try {
                if (isPluginEnabled(name)) startPlugin(name)
            } catch (e: Throwable) {
                PluginManager.logger.error("Exception while starting plugin: $name", e)
                stopPlugin(name)
            }
        }

        Utils.threadPool.execute { PluginUpdater.checkUpdates(true) }
    }

    private fun checkPermissions(activity: AppCompatActivity): Boolean {
        if (activity.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) return true

        activity.registerForActivityResult(RequestPermission()) { granted ->
            if (granted) {
                preInitWithPermissions(activity)
                CorePlugins.startAll(activity)
                startAllPlugins()
            } else {
                Toast.makeText(
                    activity,
                    "You have to grant storage permission to use Aliucord",
                    Toast.LENGTH_LONG
                ).show()
            }
        }.launch(WRITE_EXTERNAL_STORAGE)

        return false
    }
}
