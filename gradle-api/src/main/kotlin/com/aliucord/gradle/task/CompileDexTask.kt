package com.aliucord.gradle.task

import com.aliucord.gradle.AliucordExtension
import com.aliucord.gradle.getAliucord
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.errors.MessageReceiverImpl
import com.android.build.gradle.options.SyncOptions.ErrorFormatMode
import com.android.builder.dexing.*
import com.android.builder.dexing.r8.ClassFileProviderFactory
import com.google.common.io.Closer
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.getByName
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.slf4j.LoggerFactory
import java.io.File
import java.util.Arrays
import java.util.stream.Collectors

abstract class CompileDexTask : DefaultTask() {
    @InputFiles
    @SkipWhenEmpty
    @IgnoreEmptyDirectories
    val input: ConfigurableFileCollection = project.objects.fileCollection()

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:OutputFile
    abstract val pluginClassFile: RegularFileProperty

    @Suppress("UnstableApiUsage")
    @TaskAction
    fun compileDex() {
        val android: BaseExtension = project.extensions.getByName<BaseExtension>("android")

        val aliucord: AliucordExtension = project.extensions.getAliucord()

        Closer.create().use { closer ->
            val dexBuilder = DexArchiveBuilder.createD8DexBuilder(
                DexParameters(
                    minSdkVersion = android.defaultConfig.minSdkVersion?.apiLevel ?: 24,
                    debuggable = true,
                    dexPerClass = false,
                    withDesugaring = true,
                    desugarBootclasspath = ClassFileProviderFactory(android.bootClasspath.map(File::toPath))
                        .also { closer.register(it) },
                    desugarClasspath = ClassFileProviderFactory(emptyList()).also { closer.register(it) },
                    coreLibDesugarConfig = null,
                    enableApiModeling = false,
                    messageReceiver = MessageReceiverImpl(
                        ErrorFormatMode.HUMAN_READABLE,
                        LoggerFactory.getLogger(CompileDexTask::class.java)
                    )
                )
            )

            val fileStreams =
                input.map { input -> ClassFileInputs.fromPath(input.toPath()).use { it.entries { _, _ -> true } } }
                    .toTypedArray()

            Arrays.stream(fileStreams).flatMap { it }
                .use { classesInput ->
                    val files = classesInput.collect(Collectors.toList())
                    val dexOutputDir = outputFile.get().asFile.parentFile!!

                    dexBuilder.convert(
                        input = files.stream(),
                        dexOutput = dexOutputDir.toPath(),
                        globalSyntheticsOutput = null,
                    )

                    files.forEach { file ->
                        val reader = ClassReader(file.readAllBytes())
                        val classNode = ClassNode()
                        reader.accept(classNode, 0)

                        for (annotation in classNode.visibleAnnotations.orEmpty() + classNode.invisibleAnnotations.orEmpty()) {
                            if (annotation.desc == "Lcom/aliucord/annotations/AliucordPlugin;") {
                                requireNotNull(aliucord.pluginClassName) {
                                    "Only 1 active plugin class per project is supported"
                                }

                                for (method in classNode.methods) {
                                    if (method.name == "getManifest" && method.desc == "()Lcom/aliucord/entities/Plugin\$Manifest;") {
                                        throw IllegalArgumentException("Plugin class cannot override getManifest, use manifest.json system!")
                                    }
                                }

                                aliucord.pluginClassName = classNode.name.replace('/', '.')
                                    .also { pluginClassFile.asFile.orNull?.writeText(it) }
                            }
                        }
                    }
                }
        }

        logger.lifecycle("Compiled dex to ${outputFile.get()}")
    }
}
