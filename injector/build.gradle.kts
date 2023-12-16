@file:Suppress("UnstableApiUsage")

import com.aliucord.gradle.ProjectType
import com.aliucord.gradle.task.CompileDexTask
import com.aliucord.gradle.task.GenerateApkTask
import com.aliucord.gradle.task.SignApkTask
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.errors.MessageReceiverImpl
import com.android.build.gradle.options.SyncOptions
import com.android.builder.dexing.ClassFileInputs
import com.android.builder.dexing.DexArchiveBuilder
import com.android.builder.dexing.DexParameters
import com.android.builder.dexing.r8.ClassFileProviderFactory
import de.undercouch.gradle.tasks.download.Download

plugins {
    id("com.aliucord.core")
    id("de.undercouch.download") version "5.5.0"
}

aliucord {
    projectType = ProjectType.INJECTOR
}

android {
    buildFeatures {
        buildConfig = false
        androidResources = false
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.aliuhook)
}

val downloadApk by tasks.registering(Download::class) {
    src("https://aliucord.com/download/discord?v=${libs.versions.discord.get()}")
    dest(layout.buildDirectory.file("discord.apk"))
    onlyIfNewer(true)
}

val generateKotlinDex by tasks.registering {
    group = "aliucord"
    outputs.file(layout.buildDirectory.file("classes.dex"))

    val implementation = configurations.implementationDependenciesMetadata

    dependsOn(implementation)
    val outputDir = layout.buildDirectory
    val android = project.extensions.getByName<BaseExtension>("android")

    doLast {
        val kotlinStdlibJar = implementation.get().resolvedConfiguration.getFiles {
            it.group == "org.jetbrains.kotlin" && it.name == "kotlin-stdlib-jdk8"
        }.firstOrNull {
            it.name.startsWith("kotlin-stdlib-1")
        } ?: error("Kotlin stdlib jar not found")

        val dexBuilder = DexArchiveBuilder.createD8DexBuilder(
            DexParameters(
                minSdkVersion = 24,
                debuggable = true,
                dexPerClass = false,
                withDesugaring = true,
                desugarBootclasspath = ClassFileProviderFactory(android.bootClasspath.map(File::toPath)),
                desugarClasspath = ClassFileProviderFactory(emptyList()),
                coreLibDesugarConfig = null,
                enableApiModeling = false,
                messageReceiver = MessageReceiverImpl(
                    errorFormatMode = SyncOptions.ErrorFormatMode.HUMAN_READABLE,
                    logger = logger
                )
            )
        )

        ClassFileInputs.fromPath(kotlinStdlibJar.toPath())
            .entries { _, _ -> true }
            .use { kotlinStdlib ->
                dexBuilder.convert(
                    input = kotlinStdlib,
                    dexOutput = outputDir.get().asFile.toPath(),
                    globalSyntheticsOutput = null,
                )
            }
    }
}

afterEvaluate {
    val compileDex by tasks.getting(CompileDexTask::class)
    val generateApk by tasks.registering(GenerateApkTask::class) {
        group = "aliucord"

        val aliuhookAar = configurations.implementationDependenciesMetadata.get().resolvedConfiguration.getFiles {
            it.group == "com.aliucord" && it.name == "Aliuhook"
        }.single()

        dependsOn(configurations.implementationDependenciesMetadata)

        dependsOn(compileDex, downloadApk, generateKotlinDex.get())

        inputApk = downloadApk.get().outputs.files.singleFile
        aliuhook = aliuhookAar
        kotlinDex = generateKotlinDex.get().outputs.files.singleFile
        injectorDex = compileDex.outputFile
        outputApk = layout.buildDirectory.file("zeetcord.apk")
    }

    tasks.register<SignApkTask>("signApk") {
        group = "aliucord"

        dependsOn(generateApk)

        inputApk = generateApk.flatMap { it.outputApk }.map { it.asFile }
        outputApk = layout.buildDirectory.file("zeetcord-signed.apk")
    }
}
