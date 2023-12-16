package com.aliucord.gradle.task

import com.android.zipflinger.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import java.util.zip.Deflater.DEFAULT_COMPRESSION

abstract class GenerateApkTask : DefaultTask() {
    @get:InputFile
    abstract val inputApk: RegularFileProperty

    @get:InputFile
    abstract val aliuhook: RegularFileProperty

    @get:InputFile
    abstract val kotlinDex: RegularFileProperty

    @get:InputFile
    abstract val injectorDex: RegularFileProperty

    @get:OutputFile
    abstract val outputApk: RegularFileProperty

    @get:Input
    @set:Option(option = "package-name", description = "The package name of the generated APK")
    var packageName: String = "com.aliucord"

    @get:Input
    @set:Option(option = "app-name", description = "The app name of the generated APK")
    var appName: String = "Minkycord"

    @get:Input
    @set:Option(option = "debuggable", description = "Enables debugging flag when starting the discord activity")
    var debuggable: Boolean = true

    @get:Input
    @set:Option(option = "replace-icons", description = "Replaces the discord icons with the aliucord ones")
    var replaceIcons: Boolean = true

    @get:Input
    @set:Option(option = "arch", description = "The architecture of the generated APK")
    var arch = "arm64-v8a"

    @TaskAction
    fun patchApk() {
        val input = inputApk.get().asFile
        val output = outputApk.get().asFile
        val injectorDex = injectorDex.get().asFile
        val kotlinDex = kotlinDex.get().asFile

        input.copyTo(output.absoluteFile, true)

        val baseApkPath = output.toPath()
        var baseApk = ZipArchive(baseApkPath)

        if (replaceIcons) {
            val foregroundIcon = GenerateApkTask::class.java.classLoader.getResource("icons/ic_logo_foreground.png")!!.readBytes()
            val squareIcon = GenerateApkTask::class.java.classLoader.getResource("icons/ic_logo_square.png")!!.readBytes()

            val replacements = mapOf(
                arrayOf("MbV.png", "kbF.png", "_eu.png", "EtS.png") to foregroundIcon,
                arrayOf("_h_.png", "9MB.png", "Dy7.png", "kC0.png", "oEH.png", "RG0.png", "ud_.png", "W_3.png") to squareIcon
            )

            for ((files, replacement) in replacements) {
                files.forEach { file ->
                    val path = "res/$file"
                    baseApk.delete(path)
                    baseApk.add(BytesSource(replacement, path, DEFAULT_COMPRESSION))
                }
            }
        }

        baseApk.close()
        baseApk = ZipArchive(baseApkPath)

        val patchedBytes = ManifestPatcher.patchManifest(
            manifestBytes = baseApk.getContent("AndroidManifest.xml").array(),
            packageName = packageName,
            appName = appName,
            debuggable = debuggable
        )

        baseApk.delete("AndroidManifest.xml")
        baseApk.add(BytesSource(patchedBytes, "AndroidManifest.xml", DEFAULT_COMPRESSION))

        baseApk.close()
        baseApk = ZipArchive(baseApkPath)

        val dexCount = baseApk.listEntries()
            .filter { it.endsWith(".dex") }
            .size
        val originalClasses = baseApk.getContent("classes.dex")
        baseApk.delete("classes.dex")
        baseApk.close()
        baseApk = ZipArchive(baseApkPath)
        baseApk.add(Sources.from(injectorDex, "classes.dex", DEFAULT_COMPRESSION))
        baseApk.add(BytesSource(originalClasses.array(), "classes${dexCount + 1}.dex", DEFAULT_COMPRESSION))
        baseApk.add(Sources.from(kotlinDex, "classes${dexCount + 2}.dex", DEFAULT_COMPRESSION))

        ZipArchive(aliuhook.get().asFile.toPath()).use {
            for (libFile in arrayOf("aliuhook", "c++_shared", "lsplant")) {
                for (arch in arrayOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")) {
                    val decoratedName = "lib$libFile.so"
                    val stream = it.getInputStream("jni/$arch/$decoratedName")
                    baseApk.add(Sources.from(stream, "lib/$arch/$decoratedName", DEFAULT_COMPRESSION))
                }
            }

            val stream = it.getInputStream("classes.dex")
            baseApk.add(Sources.from(stream, "classes${dexCount + 3}.dex", DEFAULT_COMPRESSION))
        }

        baseApk.close()

        logger.lifecycle("Generated APK at ${output.absolutePath}")
    }
}
