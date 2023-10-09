/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import com.aliucord.Utils.appContext
import com.aliucord.entities.Plugin
import com.aliucord.patcher.GlobalPatcher
import com.aliucord.patcher.PreHook
import com.aliucord.utils.GsonUtils.fromJson
import com.aliucord.utils.GsonUtils.gson
import com.aliucord.utils.MapUtils
import dalvik.system.PathClassLoader
import java.io.File
import java.io.InputStreamReader

/** Aliucord's Plugin Manager  */
@Suppress("unused")
public object PluginManager {
    /** Map containing all loaded plugins  */
    @JvmField
    public val plugins: MutableMap<String, Plugin> = linkedMapOf()

    @JvmField
    public val classLoaders: MutableMap<PathClassLoader, Plugin> = hashMapOf()

    @JvmField
    public val logger: Logger = Logger("PluginManager")

    /** Plugins that failed to load for various reasons. Map of file to String or Exception  */
    @JvmField
    public val failedToLoad: MutableMap<File, Any> = linkedMapOf()

    /**
     * Loads a plugin
     *
     * @param context Context
     * @param file    Plugin file
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    public fun loadPlugin(context: Context, file: File) {
        val fileName = file.getName().replace(".zip", "")

        logger.info("Loading plugin: $fileName")

        try {
            val loader = PathClassLoader(file.absolutePath, context.classLoader)
            var manifest: Plugin.Manifest
            loader.getResourceAsStream("manifest.json").use { stream ->
                if (stream == null) {
                    failedToLoad[file] = "No manifest found"
                    logger.error("Failed to load plugin $fileName: No manifest found", null)
                    return
                }

                InputStreamReader(stream).use { reader ->
                    manifest = gson.fromJson(reader, Plugin.Manifest::class.java)
                }
            }
            val name = manifest.name
            val pluginClass = loader.loadClass(manifest.pluginClassName) as Class<out Plugin>
            GlobalPatcher.patch(
                pluginClass.getDeclaredConstructor(),
                PreHook { param ->
                    val plugin = param.thisObject as Plugin

                    try {
                        plugin.manifest = manifest
                    } catch (e: IllegalAccessException) {
                        logger.errorToast("Failed to set manifest for ${manifest.name}")
                        logger.error(e)
                    }
                }
                )
            val pluginInstance = pluginClass.getDeclaredConstructor().newInstance()
            if (name in plugins) {
                logger.error("Plugin with name $name already exists", null)
                return
            }
            pluginInstance.__filename = fileName
            if (pluginInstance.needsResources) {
                // based on https://stackoverflow.com/questions/7483568/dynamic-resource-loading-from-other-apk
                val assets = AssetManager::class.java.getDeclaredConstructor().newInstance()
                val addAssetPath =
                    AssetManager::class.java.getMethod("addAssetPath", String::class.java)
                addAssetPath(assets, file.absolutePath)
                pluginInstance.resources = Resources(
                    assets,
                    context.resources.displayMetrics,
                    context.resources.configuration
                )
            }

            plugins[name] = pluginInstance
            classLoaders[loader] = pluginInstance

            pluginInstance.load(context)
        } catch (e: Throwable) {
            failedToLoad[file] = e
            logger.error("Failed to load plugin $fileName:\n", e)
        }
    }

    /**
     * Unloads a plugin
     *
     * @param name Name of the plugin to unload
     */
    @JvmStatic
    public fun unloadPlugin(name: String) {
        logger.info("Unloading plugin: $name")

        val plugin = plugins[name] ?: return

        try {
            plugin.unload(appContext)
            plugins.remove(name)
        } catch (e: Throwable) {
            logger.error("Exception while unloading plugin: $name", e)
        }
    }

    /**
     * Enables a loaded plugin if it isn't already enabled
     *
     * @param name Name of the plugin to enable
     */
    @JvmStatic
    public fun enablePlugin(name: String) {
        if (isPluginEnabled(name)) return

        Main.settings.setBool(getPluginPrefKey(name), true)

        try {
            startPlugin(name)
        } catch (e: Throwable) {
            logger.error("Exception while starting plugin: $name", e)
        }
    }

    /**
     * Disables a loaded plugin if it isn't already disables
     *
     * @param name Name of the plugin to disable
     */
    @JvmStatic
    public fun disablePlugin(name: String) {
        if (!isPluginEnabled(name)) return

        Main.settings.setBool(getPluginPrefKey(name), false)

        try {
            stopPlugin(name)
        } catch (e: Throwable) {
            logger.error("Exception while stopping plugin: $name", e)
        }
    }

    /**
     * Toggles a plugin. If it is enabled, it will be disabled and vice versa.
     *
     * @param name Name of the plugin to toggle
     */
    @JvmStatic
    public fun togglePlugin(name: String): Unit = if (isPluginEnabled(name)) {
        disablePlugin(name)
    } else {
        enablePlugin(name)
    }

    /**
     * Starts a plugin
     *
     * @param name Name of the plugin to start
     */
    @JvmStatic
    public fun startPlugin(name: String) {
        logger.info("Starting plugin: $name")

        try {
            val startTime = System.currentTimeMillis()
            plugins[name]!!.start(appContext)
            logger.info("Started plugin: $name in ${System.currentTimeMillis() - startTime} milliseconds")
        } catch (e: Throwable) {
            logger.error("Exception while starting plugin: $name", e)
        }
    }

    /**
     * Stops a plugin
     *
     * @param name Name of the plugin to stop
     */
    @JvmStatic
    public fun stopPlugin(name: String) {
        logger.info("Stopping plugin: $name")

        try {
            plugins[name]!!.stop(appContext)
        } catch (e: Throwable) {
            logger.error("Exception while stopping plugin $name", e)
        }
    }

    /**
     * Remounts the plugin (stop -> unload -> load -> start)
     *
     * @param name Name of the plugin to remount
     */
    @JvmStatic
    public fun remountPlugin(name: String) {
        require(name in plugins) { "No such plugin: $name" }
        require(isPluginEnabled(name)) { "Plugin not enabled: $name" }
        stopPlugin(name)
        unloadPlugin(name)
        loadPlugin(appContext, File(Constants.PLUGINS_PATH, "$name.zip"))
        startPlugin(name)
    }

    /**
     * Gets the preferences key for a plugin. This is used as key for plugin settings.
     * Format: AC_PM_{PLUGIN_NAME}
     *
     * @param name Name of the plugin
     */
    @JvmStatic
    public fun getPluginPrefKey(name: String): String = "AC_PM_$name"

    /**
     * Checks whether a plugin is enabled
     *
     * @param name Name of the plugin
     * @return Whether the plugin is enabled
     */
    @JvmStatic
    public fun isPluginEnabled(name: String): Boolean {
        return Main.settings.getBool(getPluginPrefKey(name), true)
    }

    /**
     * Checks whether a plugin is enabled
     *
     * @param plugin Plugin
     * @return Whether the plugin is enabled
     */
    @Suppress("unused")
    public fun isPluginEnabled(plugin: Plugin): Boolean {
        return isPluginEnabled(MapUtils.getMapKey(plugins, plugin)!!)
    }
}
