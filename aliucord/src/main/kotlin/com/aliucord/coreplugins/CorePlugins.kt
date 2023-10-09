package com.aliucord.coreplugins

import android.content.Context
import com.aliucord.PluginManager
import com.aliucord.coreplugins.repo.PluginRepo
import com.aliucord.coreplugins.rn.RNAPI

/** CorePlugins Manager */
internal object CorePlugins {
    private var loaded = false
    private var started = false
    private val corePlugins = arrayOf(
        RNAPI(),
        Badges(),
        CommandHandler(),
        CoreCommands(),
        NoTrack(),
        ButtonsAPI(),
        UploadSize(),
        DefaultStickers(),
        PrivateThreads(),
        PrivateChannelsListScroll(),
        MembersListFix(),
        PluginRepo(),
    )

    /** Loads all core plugins */
    fun loadAll(context: Context) {
        check(!loaded) { "CorePlugins already loaded" }

        loaded = true

        corePlugins.forEach { p ->
            PluginManager.logger.info("Loading core plugin: ${p.name}")

            try {
                p.load(context)
            } catch (e: Throwable) {
                PluginManager.logger.errorToast("Failed to load core plugin ${p.name}", e)
            }
        }
    }

    /** Starts all core plugins */
    fun startAll(context: Context) {
        check(!started) { "CorePlugins already started" }

        started = true

        corePlugins.forEach { p ->
            PluginManager.logger.info("Starting core plugin: ${p.name}")

            try {
                p.start(context)
            } catch (e: Throwable) {
                PluginManager.logger.errorToast("Failed to start core plugin ${p.name}", e)
            }
        }
    }
}
