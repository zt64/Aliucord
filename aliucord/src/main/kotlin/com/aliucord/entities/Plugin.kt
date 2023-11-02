/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.entities

import android.content.Context
import android.content.res.Resources
import android.view.View
import com.aliucord.Logger
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.*
import com.discord.app.AppBottomSheet
import com.discord.app.AppFragment

/** Base Plugin class all plugins must extend  */
@Suppress("unused")
public abstract class Plugin @JvmOverloads constructor(manifest: Manifest? = null) {
    public lateinit var manifest: Manifest
        internal set

    /** The [Logger] of your plugin. Use this to log information  */
    @JvmField
    public val logger: Logger

    public val name: String
        get() = manifest.name

    /** SettingsTab associated with this plugin. Set this to register a settings page  */
    @JvmField
    public var settingsTab: SettingsTab? = null

    /** The resources of your plugin. You need to set [.needsResources] to true to use this  */
    @JvmField
    public var resources: Resources? = null

    /** Whether your plugin has resources that need to be loaded  */
    @JvmField
    public var needsResources: Boolean = false

    /** The filename of your plugin  */
    @Suppress("PropertyName")
    @JvmField
    public var __filename: String? = null

    /** The [CommandsAPI] of your plugin. You can register/unregister commands here  */
    @JvmField
    protected val commands: CommandsAPI

    /** The [PatcherAPI] of your plugin. You can add/remove patches here  */
    @JvmField
    protected val patcher: PatcherAPI

    /** The [SettingsAPI] of your plugin. Use this to store persistent data  */
    @JvmField
    public val settings: SettingsAPI

    protected val annotation: com.aliucord.annotations.AliucordPlugin?
        /**
         * Returns the @AliucordPlugin annotation if exists
         */
        get() = javaClass.getAnnotation(com.aliucord.annotations.AliucordPlugin::class.java)


    init {
        var m = manifest
        if (this::manifest.isInitialized) {
            if (m != null) {
                throw IllegalStateException("You cannot override manifest of a plugin loaded by PluginManager")
            }

            m = this.manifest;
        } else if (m != null) this.manifest = m
        if (m == null) {
            throw IllegalStateException("Manifest was null, this should never happen")
        }
        logger = Logger(m.name)
        commands = CommandsAPI(m.name)
        patcher = PatcherAPI(logger)
        settings = SettingsAPI(m.name)
    }

    /** Plugin Manifest  */
    public data class Manifest @JvmOverloads constructor(
        @JvmField val name: String,
        @JvmField val pluginClassName: String? = null,
        @JvmField var authors: Array<Author> = arrayOf(),
        @JvmField var description: String = "",
        @JvmField var version: String = "1.0.0",
        @JvmField var updateUrl: String? = null,
        @JvmField var changelog: String? = null,
        @JvmField val changelogMedia: String? = null
    ) {
        /**
         * Plugin author
         *
         * @property name The name of the plugin author
         * @property id The id of the plugin author
         */
        public data class Author(
            @JvmField var name: String,
            @JvmField var id: Long = 0
        ) {
            override fun toString(): String = name
        }
    }


    /** Plugin SettingsTab  */
    public class SettingsTab {
        /** The type of this SettingsTab. PAGE is a dedicated page, BOTTOM_SHEET is a popup at the bottom of the screen.  */
        public enum class Type {
            PAGE,
            BOTTOM_SHEET
        }

        public interface SettingsPage {
            public fun onViewBound(view: View?)
        }

        /** The [Type] of this SettingsTab  */
        @JvmField
        public var type: Type

        /** The Page fragment  */
        @JvmField
        public var page: Class<out AppFragment?>? = null

        /** The BottomSheet component  */
        @JvmField
        public var bottomSheet: Class<AppBottomSheet>? = null

        /** The arguments that will be passed to the constructor of the component  */
        @JvmField
        public var args: Array<out Any> = emptyArray()

        /**
         * Creates a SettingsTab with a dedicated page
         * @param settings The settings page fragment
         */
        public constructor(settings: Class<out AppFragment?>?) {
            type = Type.PAGE
            page = settings
        }

        /**
         * Creates a SettingsTab of the specified type
         * @param settings The component to use for this SettingsTab
         * @param type The [Type] of this SettingsTab
         */
        public constructor(settings: Class<*>?, type: Type) {
            this.type = type

            @Suppress("UNCHECKED_CAST")
            when (type) {
                Type.PAGE -> {
                    page = settings as Class<out AppFragment?>?
                }

                Type.BOTTOM_SHEET -> {
                    bottomSheet = settings as Class<AppBottomSheet>?
                }
            }
        }

        /**
         * Sets the constructor args that will be passed to this SettingsTab
         * @param args The arguments that should be passed
         */
        public fun withArgs(vararg args: Any): SettingsTab = apply {
            this.args = args
        }
    }

    /**
     * Returns whether the user will be prompted to restart after enabling/disabling.
     * @return [AliucordPlugin.requiresRestart]
     */
    public open fun requiresRestart(): Boolean = annotation?.requiresRestart ?: false

    /**
     * Called when your Plugin is loaded
     * @param context Context
     */
    @Throws(Throwable::class)
    public open fun load(context: Context) {
    }

    /**
     * Called when your Plugin is unloaded
     * @param context Context
     */
    @Throws(Throwable::class)
    public fun unload(context: Context?) {
    } // not used now

    /**
     * Called when your Plugin is started
     * @param context Context
     */
    @Throws(Throwable::class)
    public open fun start(context: Context) {
    }

    /**
     * Called when your Plugin is stopped
     * @param context Context
     */
    @Throws(Throwable::class)
    public open fun stop(context: Context): Unit = patcher.unpatchAll()
}
