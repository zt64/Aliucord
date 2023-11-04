package com.aliucord.gradle.task

import com.android.build.gradle.BaseExtension
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByName
import se.vidstige.jadb.*

abstract class DeployWithAdbTask : DefaultTask() {
    @get:Input
    @set:Option(option = "wait-for-debugger", description = "Enables debugging flag when starting the discord activity")
    var waitForDebugger: Boolean = false

    @get:Input
    @set:Option(option = "launch-activity", description = "Launches the discord activity after deploying")
    var launchActivity: Boolean = true

    @get:Input
    var devicePath: String = "/storage/emulated/0/Zeetcord"

    @get:InputFile
    abstract val file: RegularFileProperty

    @TaskAction
    fun deployWithAdb() {
        val android: BaseExtension = project.extensions.getByName<BaseExtension>("android")

        AdbServerLauncher(Subprocess(), android.adbExecutable.absolutePath).launch()

        val jadbConnection = JadbConnection()
        val devices = jadbConnection.devices.filter {
            try {
                it.state == JadbDevice.State.Device
            } catch (e: JadbException) {
                false
            }
        }

        require(devices.size == 1) {
            "Only one ADB device should be connected, but ${devices.size} were!"
        }

        // if (aliucord.projectType.get() == ProjectType.INJECTOR) {
        //     file = file.resolve("injector.dex")
        // }

        // if (aliucord.projectType.get() == ProjectType.PLUGIN) path += "/plugins/"

        val file = file.get().asFile
        val device = devices.first()

        device.push(file, RemoteFile("$devicePath/${file.name}"))

        if (launchActivity) {
            val args = arrayListOf("start", "-S", "-n", "com.aliucord/com.discord.app.AppActivity\$Main")

            if (waitForDebugger) args += "-D"

            val response = device.executeShell("am", *args.toTypedArray()).readAllBytes().decodeToString()

            if ("Error" in response) logger.error(response)
        }

        logger.lifecycle("Deployed $file to ${device.serial}")
    }
}
