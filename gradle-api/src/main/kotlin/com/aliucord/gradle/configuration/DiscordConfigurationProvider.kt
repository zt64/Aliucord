package com.aliucord.gradle.configuration

import com.aliucord.gradle.*
import com.googlecode.d2j.dex.Dex2jar
import com.googlecode.d2j.reader.MultiDexFileReader
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.kotlin.dsl.dependencies
import java.net.URL

class DiscordConfigurationProvider : IConfigurationProvider {
    private companion object {
        private var aliucordSnapshot: Int? = null
    }

    override val name: String = "discord"

    override fun provide(project: Project, dependency: Dependency) {
        val version = when (dependency.version) {
            "aliucord-SNAPSHOT" -> {
                if (aliucordSnapshot == null) {
                    project.logger.lifecycle("Fetching discord version")
                    val data = JsonSlurper()
                        .parse(URL("https://raw.githubusercontent.com/zt64/Zeetcord/builds/data.json")) as Map<*, *>
                    aliucordSnapshot = (data["versionCode"] as String).toInt()
                    project.logger.lifecycle("Fetched discord version: $aliucordSnapshot")
                }

                aliucordSnapshot!!
            }

            else -> dependency.version!!.toInt()
        }

        val extension = project.extensions.getAliucord()
        val discord = DiscordInfo(extension, version).also { extension.discord = it }

        discord.cache.mkdirs()

        if (!discord.apkFile.exists()) {
            project.logger.lifecycle("Downloading discord apk")

            val url = URL("https://aliucord.com/download/discord?v=${discord.version}")

            url.download(discord.apkFile, createProgressLogger(project, "Download discord apk"))
        }

        if (!discord.jarFile.exists()) {
            project.logger.lifecycle("Converting discord apk to jar")

            val reader = MultiDexFileReader.open(discord.apkFile.readBytes())

            with(Dex2jar.from(reader)) {
                topoLogicalSort()
                skipDebug(false)
                noCode(true)
                to(discord.jarFile.toPath())
            }
        }

        project.dependencies {
            "compileOnly"(project.files(discord.jarFile))
        }
    }
}
