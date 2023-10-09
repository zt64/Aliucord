/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.coreplugins

import android.content.Context
import android.os.Build
import com.aliucord.*
import com.aliucord.api.CommandsAPI.CommandResult
import com.aliucord.entities.Plugin
import com.discord.api.commands.ApplicationCommandType
import java.io.File

internal class CoreCommands : Plugin(Manifest("CoreCommands")) {
    override fun start(context: Context) {
        fun List<Plugin>.formatPlugins(showVersions: Boolean): String {
            return joinToString { p ->
                "${p.name}${if (showVersions) " (${p.manifest.version})" else ""}"
            }
        }

        commands.registerCommand(
            name = "plugins",
            description = "Lists installed plugins",
            options = listOf(
                Utils.createCommandOption(
                    type = ApplicationCommandType.BOOLEAN,
                    name = "send",
                    description = "Whether the result should be visible for everyone",
                ),
                Utils.createCommandOption(
                    type = ApplicationCommandType.BOOLEAN,
                    name = "versions",
                    description = "Whether to show the plugin versions",
                )
            )
        ) {
            val showVersions = it.getBoolOrDefault("versions", false)

            val plugins = PluginManager.plugins
            val (enabled, disabled) = plugins.values.partition(PluginManager::isPluginEnabled)
            val enabledStr = enabled.formatPlugins(showVersions)
            val disabledStr = disabled.formatPlugins(showVersions)

            if (plugins.isEmpty()) {
                CommandResult("No plugins installed", send = false)
            } else {
                CommandResult(
                    """
            **Enabled Plugins (${enabled.size}):**
            ${if (enabled.isEmpty()) "None" else "> $enabledStr"}
            **Disabled Plugins (${disabled.size}):**
            ${if (disabled.isEmpty()) "None" else "> $disabledStr"}
                            """,
                    send = it.getBoolOrDefault("send", false)
                )
            }
        }

        commands.registerCommand("debug", "Posts debug info") {
            // .trimIndent() is broken sadly due to collision with Discord's Kotlin
            val str = """
**Debug Info:**
> Discord: ${Constants.DISCORD_VERSION}
> Zeetcord: ${BuildConfig.GIT_REVISION} (${PluginManager.plugins.size} plugins)
> System: Android ${Build.VERSION.RELEASE} (SDK v${Build.VERSION.SDK_INT}) - ${getArchitecture()}
> Rooted: ${getIsRooted() ?: "Unknown"}
            """

            CommandResult(str)
        }
    }

    private fun getIsRooted(): Boolean? {
        return System.getenv("PATH")?.split(':')?.any {
            File(it, "su").exists()
        }
    }

    private fun getArchitecture(): String {
        Build.SUPPORTED_ABIS.forEach {
            when (it) {
                "arm64-v8a" -> return "aarch64"
                "armeabi-v7a" -> return "arm"
                "x86_64" -> return "x86_64"
                "x86" -> return "i686"
            }
        }

        return System.getProperty("os.arch")
            ?: System.getProperty("ro.product.cpu.abi")
            ?: "Unknown Architecture"
    }
}
