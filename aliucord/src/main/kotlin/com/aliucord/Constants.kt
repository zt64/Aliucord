/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord

import android.os.Environment
import com.aliucord.utils.ReflectUtils
import com.discord.stores.StoreStream

@Suppress("unused", "ConstPropertyName")
public object Constants {
    /** Link to the Aliucord github repo  */
    public const val ALIUCORD_GITHUB_REPO: String = "https://github.com/Aliucord/Aliucord"

    /** Link to Aliucord's Patreon  */
    public const val ALIUCORD_PATREON: String = "https://patreon.com/Aliucord"

    /** Code of the Aliucord discord server  */
    public const val ALIUCORD_SUPPORT: String = "EsNDvBaHVU"
    public const val ALIUCORD_GUILD_ID: Long = 811255666990907402L
    public const val SUPPORT_CHANNEL_ID: Long = 811261298997460992L
    public const val PLUGIN_SUPPORT_CHANNEL_ID: Long = 847566769258233926L
    public const val PLUGIN_LINKS_CHANNEL_ID: Long = 811275162715553823L
    public const val PLUGIN_LINKS_UPDATES_CHANNEL_ID: Long = 845784407846813696L
    public const val PLUGIN_REQUESTS_CHANNEL_ID: Long = 811275334342541353L
    public const val THEMES_CHANNEL_ID: Long = 824357609778708580L
    public const val PLUGIN_DEVELOPMENT_CHANNEL_ID: Long = 811261478875299840L

    /** Path of Aliucord folder  */
    @JvmField
    public val BASE_PATH: String = Environment.getExternalStorageDirectory().resolve("Aliucord").absolutePath

    /** Path of Plugin folder  */
    @JvmField
    public val PLUGINS_PATH: String = "$BASE_PATH/plugins"

    /** Path of Crashlog folder  */
    @JvmField
    public val CRASHLOGS_PATH: String = "$BASE_PATH/crashlogs"

    /** Path of Settings folder  */
    @JvmField
    public val SETTINGS_PATH: String = "$BASE_PATH/settings"

    public const val NAMESPACE_ANDROID: String = "http://schemas.android.com/apk/res/android"
    public const val NAMESPACE_APP: String = "http://schemas.android.com/apk/res-auto"

    /** The version int of the currently running Discord, currently {@value BuildConfig#DISCORD_VERSION}  */
    @JvmField
    public var DISCORD_VERSION: Int = try {
        ReflectUtils.getField(
            ReflectUtils.getField(
                StoreStream.Companion.`access$getCollector$p`(StoreStream.Companion),
                "clientVersion"
            )!!,
            "clientVersion"
        ) as Int
    } catch (e: Throwable) {
        Main.logger.error("Failed to retrieve client version", e)
        BuildConfig.DISCORD_VERSION
    }

    /**
     * The release suffix of the currently running Discord. Some methods have this as their suffix and it changes with release, so in those
     * cases use `"someMethod$" + Constants.RELEASE_SUFFIX` with reflection so that it works on all releases.
     * <hr></hr><br></br>
     * <h3>One of</h3>
     *
     *  * app_productionGoogleRelease
     *  * app_productionBetaRelease
     *  * app_productionCanaryRelease
     *
     */
    @JvmField
    public var RELEASE_SUFFIX: String? = try {
        // Calculate the third digit of the number:
        //      101207 -> 2
        //      101107 -> 1
        //      101007 -> 0
        val release = DISCORD_VERSION / 100 % 10
        arrayOf(
            "app_productionGoogleRelease",
            "app_productionBetaRelease",
            "app_productionCanaryRelease"
        )[release]
    } catch (e: Throwable) {
        Main.logger.error("Failed to determine discord release. Defaulting to beta", e)
        "app_productionBetaRelease"
    }

    public object Icons {
        /** Clyde avatar  */
        public const val CLYDE: String = "https://canary.discord.com/assets/f78426a064bc9dd24847519259bc42af.png"
    }

    // Font resource ids, they're not defined by any generated package but they seem to be constant so i made this class.
    @Suppress("DEPRECATION") // workaround Java not being able to use delegated properties
    public object Fonts {
        private const val BASE = 0x7f090000

        @Deprecated("Name changed to GINTO_BOLD", ReplaceWith("GINTO_BOLD"))
        public const val ginto_bold: Int = BASE

        @Deprecated("Name changed to GINTO_MEDIUM", ReplaceWith("GINTO_MEDIUM"))
        public const val ginto_medium: Int = BASE + 1

        @Deprecated("Name changed to GINTO_REGULAR", ReplaceWith("GINTO_REGULAR"))
        public const val ginto_regular: Int = BASE + 2

        @Deprecated("Name changed to ROBOTO_MEDIUM_NUMBERS", ReplaceWith("ROBOTO_MEDIUM_NUMBERS"))
        public const val roboto_medium_numbers: Int = BASE + 3

        @Deprecated("Name changed to SOURCECODEPRO_SEMIBOLD", ReplaceWith("SOURCECODEPRO_SEMIBOLD"))
        public const val sourcecodepro_semibold: Int = BASE + 4

        @Deprecated("Name changed to WHITNEY_BOLD", ReplaceWith("WHITNEY_BOLD"))
        public const val whitney_bold: Int = BASE + 5

        @Deprecated("Name changed to WHITNEY_MEDIUM", ReplaceWith("WHITNEY_MEDIUM"))
        public const val whitney_medium: Int = BASE + 6

        @Deprecated("Name changed to WHITNEY_SEMIBOLD", ReplaceWith("WHITNEY_SEMIBOLD"))
        public const val whitney_semibold: Int = BASE + 7

        public val GINTO_BOLD: Int by ::ginto_bold
        public val GINTO_MEDIUM: Int by ::ginto_medium
        public val GINTO_REGULAR: Int by ::ginto_regular
        public val ROBOTO_MEDIUM_NUMBERS: Int by ::roboto_medium_numbers
        public val SOURCECODEPRO_SEMIBOLD: Int by ::sourcecodepro_semibold
        public val WHITNEY_BOLD: Int by ::whitney_bold
        public val WHITNEY_MEDIUM: Int by ::whitney_medium
        public val WHITNEY_SEMIBOLD: Int by ::whitney_semibold
    }
}
