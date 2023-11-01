/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.updater

import android.content.Context
import com.aliucord.*
import com.aliucord.settings.ALIUCORD_FROM_STORAGE_KEY
import com.aliucord.utils.ReflectUtils
import java.io.IOException

public object Updater {
    /**
     * Compares two versions of a plugin to determine whether it is outdated
     *
     * @param plugin     The name of the plugin
     * @param version    The local version of the plugin
     * @param newVersion The latest version of the plugin
     * @return Whether newVersion is newer than version
     */
    @Throws(NumberFormatException::class, NullPointerException::class)
    @JvmStatic
    public fun isOutdated(plugin: String, version: String, newVersion: String): Boolean {
        try {
            val versions = versionRegex.split(version).dropLastWhile(String::isEmpty)
            val newVersions = versionRegex.split(newVersion).dropLastWhile(String::isEmpty)

            if (versions.size > newVersions.size) return false

            versions.indices.toList().forEach { i ->
                val newInt = newVersions[i].toInt()
                val oldInt = versions[i].toInt()
                if (newInt > oldInt) return true
                if (newInt < oldInt) return false
            }
        } catch (th: NullPointerException) {
            PluginUpdater.logger.error(
                "Failed to check updates for plugin $plugin due to an invalid updater/manifest version",
                th
            )
        } catch (th: NumberFormatException) {
            PluginUpdater.logger.error(
                "Failed to check updates for plugin $plugin due to an invalid updater/manifest version",
                th
            )
        }
        return false
    }

    private var isAliucordOutdated: Boolean? = null
    private var isDiscordOutdated: Boolean? = null
    private fun fetchAliucordData(): Boolean = try {
        Http.Request("https://raw.githubusercontent.com/Aliucord/Aliucord/builds/data.json").use { req ->
            val res = req.execute().json(AliucordData::class.java)
            isAliucordOutdated = BuildConfig.GIT_REVISION != res.aliucordHash
            isDiscordOutdated = Constants.DISCORD_VERSION < res.versionCode
            true
        }
    } catch (ex: IOException) {
        PluginUpdater.logger.error("Failed to check updates for Aliucord", ex)
        false
    }

    /**
     * Determines whether Aliucord is outdated
     *
     * @return Whether latest remote Aliucord commit hash is newer than the installed one
     */
    @JvmStatic
    public fun isAliucordOutdated(): Boolean = when {
        usingDexFromStorage() || isUpdaterDisabled -> false
        isAliucordOutdated == null && !fetchAliucordData() -> false
        else -> isAliucordOutdated!!
    }

    /**
     * Determines whether the Base Discord is outdated
     *
     * @return Whether Aliucord's currently supported Discord version is newer than the installed one
     */
    @JvmStatic
    public fun isDiscordOutdated(): Boolean = when {
        isUpdaterDisabled || (isDiscordOutdated == null && !fetchAliucordData()) -> false
        else -> isDiscordOutdated!!
    }

    /**
     * Replaces the local Aliucord version with the latest from Github
     *
     * @param ctx Context
     * @throws Throwable If an error occurred
     */
    @Throws(Throwable::class)
    @JvmStatic
    public fun updateAliucord(ctx: Context) {
        val c = try {
            Class.forName("com.aliucord.injector.InjectorKt")
        } catch (e: ClassNotFoundException) {
            Class.forName("com.aliucord.injector.Injector")
        }
        ReflectUtils.invokeMethod(
            c!!,
            null,
            "downloadLatestAliucordDex",
            ctx.codeCacheDir.resolve("Zeetcord.zip")
        )
    }

    @JvmStatic
    public val isUpdaterDisabled: Boolean
        get() = Main.settings.getBool("disableAliucordUpdater", false)

    @JvmStatic
    public fun usingDexFromStorage(): Boolean {
        return Main.settings.getBool(ALIUCORD_FROM_STORAGE_KEY, false)
    }

    private class AliucordData {
        var aliucordHash: String? = null
        var versionCode = 0
    }

    private val versionRegex = "\\.".toRegex().toPattern()
}
