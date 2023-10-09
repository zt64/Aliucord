/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.updater

import com.aliucord.*
import com.aliucord.PluginManager.isPluginEnabled
import com.aliucord.PluginManager.remountPlugin
import com.aliucord.Utils.openPage
import com.aliucord.Utils.pluralize
import com.aliucord.Utils.promptRestart
import com.aliucord.api.NotificationsAPI.display
import com.aliucord.entities.NotificationData
import com.aliucord.entities.Plugin
import com.aliucord.settings.*
import com.aliucord.utils.MDUtils
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

internal object PluginUpdater {
    val logger = Logger("Updater")

    // Synchronized to avoid ConcurrentModificationException
    val cache: MutableMap<String?, CachedData> = Collections.synchronizedMap(hashMapOf<String?, CachedData>())
    private val updated: MutableMap<String, String?> = Collections.synchronizedMap(hashMapOf<String, String?>())
    val updates: MutableList<String> = Collections.synchronizedList(ArrayList())
    private val resType = TypeToken.getParameterized(
        MutableMap::class.java,
        String::class.java,
        UpdateInfo::class.java
    ).getType()

    fun checkUpdates(notify: Boolean) {
        updates.clear()

        PluginManager.plugins.forEach { (k, v) ->
            if (checkPluginUpdate(v)) updates += k
        }

        if (!notify || updates.isEmpty() && !(!Updater.usingDexFromStorage() && Updater.isAliucordOutdated())) return
        val notificationData = NotificationData()
            .setTitle("Updater")
            .setAutoDismissPeriodSecs(10)
            .setOnClick {
                openPage(Utils.appActivity, UpdaterPage::class.java)
            }
        val updatablePlugins = updates.joinToString { "**$it**" }
        var body = if (Main.settings.getBool(AUTO_UPDATE_PLUGINS_KEY, false)) {
            when (val res = updateAll()) {
                0 -> return
                -1 -> {
                    "Something went wrong while auto updating plugins. Check the debug log for more info."
                }

                else -> {
                    "Automatically updated ${pluralize(res, "plugin")}: $updatablePlugins"
                }
            }
        } else if (updates.isNotEmpty()) {
            "Updates for plugins are available: $updatablePlugins"
        } else "All plugins up to date!"

        when {
            !Updater.usingDexFromStorage() -> {
                when {
                    Updater.isDiscordOutdated() -> {
                        body = "Your Base Discord is outdated. Please update using the installer - $body"
                    }

                    Updater.isAliucordOutdated() -> {
                        body = if (Main.settings.getBool(AUTO_UPDATE_ALIUCORD_KEY, false)) {
                            try {
                                Updater.updateAliucord(Utils.appActivity)
                                "Auto updated Aliucord. Please restart Aliucord to load the update - $body"
                            } catch (th: Throwable) {
                                "Failed to auto update Aliucord. Please update it manually - $body"
                            }
                        } else {
                            "Your Aliucord is outdated. Please update it to the latest version - $body"
                        }
                    }
                }
            }
        }

        notificationData.setBody(MDUtils.render(body))
        display(notificationData)
    }

    private fun checkPluginUpdate(plugin: Plugin): Boolean {
        val updateUrl = plugin.manifest.updateUrl

        if (updateUrl == null || updateUrl == "") return false

        try {
            val updateInfo = getUpdateInfo(plugin)

            if (updateInfo == null || updateInfo.minimumDiscordVersion > Constants.DISCORD_VERSION) return false
            val updatedVer = updated[plugin.javaClass.getSimpleName()]

            return if (updatedVer != null && !Updater.isOutdated(
                    plugin.name,
                    updateInfo.version!!,
                    updatedVer
                )
            ) false else {
                Updater.isOutdated(plugin.name, plugin.manifest.version, updateInfo.version!!)
            }
        } catch (e: Throwable) {
            logger.error("Failed to check update for: ${plugin.javaClass.getSimpleName()}", e)
        }

        return false
    }

    fun getUpdateInfo(plugin: Plugin): UpdateInfo? {
        val updateUrl = plugin.manifest.updateUrl

        if (updateUrl == null || updateUrl == "") return null

        val name = plugin.name
        val cached = cache[updateUrl]

        if (
            cached != null && cached.time > System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(
                30
            )
        ) {
            val defaultInfo = cached.data["default"]
            val updateInfo = cached.data[name] ?: return defaultInfo

            addDefaultInfo(updateInfo, defaultInfo)

            return updateInfo
        }

        val res = Http.simpleJsonGet<Map<String, UpdateInfo>>(
            updateUrl,
            resType
        ) ?: return null

        cache[updateUrl] = CachedData(res)

        val defaultInfo = res["default"]
        val updateInfo = res[name] ?: return defaultInfo

        addDefaultInfo(updateInfo, defaultInfo)

        return updateInfo
    }

    /**
     * Checks for updates for all plugins and updates them
     *
     * @return The number of plugins updated, -1 if an error occurred
     */
    fun updateAll(): Int {
        var updateCount = 0

        updates.forEach { plugin ->
            try {
                if (update(plugin) && updateCount != -1) updateCount++
            } catch (t: Throwable) {
                logger.error("Error while updating plugin $plugin", t)
                updateCount = -1
            }
        }
        updates.clear()
        checkUpdates(false)
        return updateCount
    }

    @Throws(Throwable::class)
    fun update(plugin: String): Boolean {
        val p = PluginManager.plugins[plugin]
            ?: throw NoSuchElementException("No such plugin: $plugin")
        val updateInfo = getUpdateInfo(p) ?: return false
        val url = updateInfo.build!!.replace("%s", plugin)

        Http.Request(url).execute().use { res ->
            res.saveToFile(
                File(Constants.PLUGINS_PATH, "${p.__filename}.zip"),
                updateInfo.sha1sum
            )
        }

        if (isPluginEnabled(plugin)) {
            Utils.mainThread.post {
                remountPlugin(plugin)
                if (PluginManager.plugins[plugin]!!.requiresRestart()) promptRestart()
            }
        }

        updated[plugin] = updateInfo.version
        return true
    }

    private fun addDefaultInfo(updateInfo: UpdateInfo, defaultInfo: UpdateInfo?) {
        if (defaultInfo == null) return

        if (updateInfo.minimumDiscordVersion == 0) {
            updateInfo.minimumDiscordVersion = defaultInfo.minimumDiscordVersion
        }
        if (updateInfo.version == null) updateInfo.version = defaultInfo.version
        if (updateInfo.build == null) updateInfo.build = defaultInfo.build
    }

    class UpdateInfo {
        var minimumDiscordVersion = 0
        var version: String? = null
        var build: String? = null
        val changelog: String? = null
        var changelogMedia: String? = null
        var sha1sum: String? = null
    }

    class CachedData(var data: Map<String, UpdateInfo>) {
        var time = System.currentTimeMillis()
    }
}
