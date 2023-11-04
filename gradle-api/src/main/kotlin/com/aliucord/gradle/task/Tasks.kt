package com.aliucord.gradle.task

import com.aliucord.gradle.ProjectType
import com.aliucord.gradle.entities.PluginManifest
import com.aliucord.gradle.getAliucord
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.tasks.ProcessLibraryManifest
import groovy.json.JsonBuilder
import org.gradle.api.Project
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip
import org.gradle.kotlin.dsl.*

internal const val TASK_GROUP = "aliucord"

fun Project.registerTasks() {
    val extension = extensions.getAliucord()
    val intermediates = layout.buildDirectory.dir("intermediates").get()

    // if (rootProject.tasks.findByName("generateUpdaterJson") == null) {
    //     rootProject.tasks.register<GenerateUpdaterJsonTask>("generateUpdaterJson") {
    //         group = TASK_GROUP
    //
    //         outputs.upToDateWhen { false }
    //
    //         outputFile = layout.buildDirectory.file("updater.json")
    //     }
    // }

    tasks.register<GenSourcesTask>("genSources") {
        group = TASK_GROUP
    }

    val pluginClassFile = intermediates.file("pluginClass").asFile

    val compileDex by tasks.registering(CompileDexTask::class) {
        group = TASK_GROUP

        this.pluginClassFile = pluginClassFile

        for (name in arrayOf("compileDebugJavaWithJavac", "compileDebugKotlin")) {
            val task = tasks.named(name).orNull ?: continue
            dependsOn(task)
            input.from(task.outputs)
        }

        outputFile = intermediates.file("classes.dex")
    }

    val compileResources by tasks.registering(CompileResourcesTask::class) {
        group = TASK_GROUP

        val processDebugManifest by tasks.getting(ProcessLibraryManifest::class)
        dependsOn(processDebugManifest)

        val android = project.extensions.getByName<BaseExtension>("android")
        input = android.sourceSets["main"].res.srcDirs.single()
        manifestFile = processDebugManifest.manifestOutputFile

        outputFile = intermediates.file("res.apk")

        doLast {
            val resApkFile = outputFile.asFile.get()

            if (resApkFile.exists()) {
                tasks.named<AbstractCopyTask>("make") {
                    from(zipTree(resApkFile)) {
                        exclude("AndroidManifest.xml")
                    }
                }
            }
        }
    }

    afterEvaluate {
        val make by tasks.registering(
            type = if (extension.projectType.get() == ProjectType.INJECTOR) {
                Copy::class
            } else {
                Zip::class
            }
        ) {
            group = TASK_GROUP

            val compileDexTask = compileDex.get()
            dependsOn(compileDex)

            if (extension.projectType.get() == ProjectType.PLUGIN) {
                val manifestFile = intermediates.file("manifest.json").asFile

                from(manifestFile)
                doFirst {
                    require(version != "unspecified") {
                        "No version is set"
                    }

                    if (extension.pluginClassName == null && pluginClassFile.exists()) {
                        extension.pluginClassName = pluginClassFile.readText()
                    }

                    require(extension.pluginClassName != null) {
                        "No plugin class found, make sure your plugin class is annotated with @AliucordPlugin"
                    }

                    manifestFile.writeText(
                        PluginManifest(
                            pluginClassName = extension.pluginClassName!!,
                            name = name,
                            version = version.toString(),
                            description = description,
                            authors = extension.authors.get(),
                            links = extension.links,
                            updateUrl = extension.updateUrl.orNull,
                            changelog = extension.changelog.orNull,
                            changelogMedia = extension.changelogMedia.orNull
                        ).let { JsonBuilder(it).toPrettyString() }
                    )
                }
            }

            from(compileDexTask.outputFile)

            if (extension.projectType.get() == ProjectType.INJECTOR) {
                into(layout.buildDirectory)
                rename { return@rename "injector.dex" }
                // set output file as injector.dex


                doLast {
                    logger.lifecycle("Copied injector.dex to ${layout.buildDirectory}")
                }
            } else {
                this as Zip

                dependsOn(compileResources.get())
                isPreserveFileTimestamps = false
                archiveBaseName = "zeetcord"
                archiveVersion = ""
                destinationDirectory = layout.buildDirectory

                doLast {
                    logger.lifecycle("Made Zeetcord package at ${outputs.files.singleFile}")
                }
            }
        }

        tasks.register<DeployWithAdbTask>("deployWithAdb") {
            group = TASK_GROUP
            description = "Deploys to a device using ADB"
            file = make.get().outputs.files.singleFile

            if (extension.projectType.get() == ProjectType.INJECTOR) file = file.get().asFile.resolve("injector.dex")
            if (extension.projectType.get() == ProjectType.PLUGIN) devicePath += "/plugins/"

            dependsOn(make)
        }
    }
}
