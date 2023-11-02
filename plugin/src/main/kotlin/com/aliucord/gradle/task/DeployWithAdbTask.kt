/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.aliucord.gradle.task

import com.aliucord.gradle.*
import com.android.build.gradle.BaseExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByName
import se.vidstige.jadb.*

abstract class DeployWithAdbTask : DefaultTask() {
    @get:Input
    @set:Option(option = "wait-for-debugger", description = "Enables debugging flag when starting the discord activity")
    var waitForDebugger: Boolean = false

    @TaskAction
    fun deployWithAdb() {
        val android: BaseExtension = project.extensions.getByName<BaseExtension>("android")
        val aliucord: AliucordExtension = project.extensions.getAliucord()
        val makeTask = project.tasks.getByName<AbstractCopyTask>("make")

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

        var file = makeTask.outputs.files.singleFile

        if (aliucord.projectType.get() == ProjectType.INJECTOR) {
            file = file.resolve("injector.dex")
        }

        var path = "/storage/emulated/0/Zeetcord"

        if (aliucord.projectType.get() == ProjectType.PLUGIN) path += "/plugins/"

        val device = devices.first()

        device.push(file, RemoteFile("$path/${file.name}"))

        if (aliucord.projectType.get() != ProjectType.INJECTOR) {
            val args = arrayListOf("start", "-S", "-n", "com.aliucord/com.discord.app.AppActivity\$Main")

            if (waitForDebugger) args += "-D"

            val response = device.executeShell("am", *args.toTypedArray()).readAllBytes().decodeToString()

            if ("Error" in response) logger.error(response)
        }

        logger.lifecycle("Deployed $file to ${device.serial}")
    }
}
