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

fun registerTasks(project: Project) {
    val extension = project.extensions.getAliucord()
    val intermediates = project.buildDir.resolve("intermediates")

    if (project.rootProject.tasks.findByName("generateUpdaterJson") == null) {
        project.rootProject.tasks.register<GenerateUpdaterJsonTask>("generateUpdaterJson") {
            group = TASK_GROUP

            outputs.upToDateWhen { false }

            outputFile.set(project.buildDir.resolve("updater.json"))
        }
    }

    project.tasks.register<GenSourcesTask>("genSources") {
        group = TASK_GROUP
    }

    val pluginClassFile = intermediates.resolve("pluginClass")

    val compileDex by project.tasks.registering(CompileDexTask::class) {
        group = TASK_GROUP

        this.pluginClassFile.set(pluginClassFile)

        for (name in arrayOf("compileDebugJavaWithJavac", "compileDebugKotlin")) {
            val task = project.tasks.named(name).orNull
            if (task != null) {
                dependsOn(task)
                input.from(task.outputs)
            }
        }

        outputFile.set(intermediates.resolve("classes.dex"))
    }

    val compileResources by project.tasks.registering(CompileResourcesTask::class) {
        group = TASK_GROUP

        val processDebugManifest by project.tasks.getting(ProcessLibraryManifest::class)
        dependsOn(processDebugManifest)

        val android = project.extensions.getByName<BaseExtension>("android")
        input.set(android.sourceSets["main"].res.srcDirs.single())
        manifestFile.set(processDebugManifest.manifestOutputFile)

        outputFile.set(intermediates.resolve("res.apk"))

        doLast {
            val resApkFile = outputFile.asFile.get()

            if (resApkFile.exists()) {
                project.tasks.named<AbstractCopyTask>("make") {
                    from(project.zipTree(resApkFile)) {
                        exclude("AndroidManifest.xml")
                    }
                }
            }
        }
    }

    project.afterEvaluate {
        project.tasks.register(
            "make",
            if (extension.projectType.get() == ProjectType.INJECTOR) Copy::class else Zip::class
        ) {
            group = TASK_GROUP

            val compileDexTask = compileDex.get()
            dependsOn(compileDexTask)

            if (extension.projectType.get() == ProjectType.PLUGIN) {
                val manifestFile = intermediates.resolve("manifest.json")

                from(manifestFile)
                doFirst {
                    require(project.version != "unspecified") {
                        "No version is set"
                    }

                    if (extension.pluginClassName == null) {
                        if (pluginClassFile.exists()) {
                            extension.pluginClassName = pluginClassFile.readText()
                        }
                    }

                    require(extension.pluginClassName != null) {
                        "No plugin class found, make sure your plugin class is annotated with @AliucordPlugin"
                    }

                    manifestFile.writeText(
                        PluginManifest(
                            pluginClassName = extension.pluginClassName!!,
                            name = project.name,
                            version = project.version.toString(),
                            description = project.description,
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
                into(project.layout.buildDirectory)
                rename { return@rename "injector.dex" }

                doLast {
                    logger.lifecycle("Copied Injector.dex to ${project.layout.buildDirectory}")
                }
            } else {
                this as Zip

                dependsOn(compileResources.get())
                isPreserveFileTimestamps = false
                archiveBaseName.set(project.name)
                archiveVersion.set("")
                destinationDirectory.set(project.layout.buildDirectory)

                doLast {
                    logger.lifecycle("Made Aliucord package at ${outputs.files.singleFile}")
                }
            }
        }

        project.tasks.register<DeployWithAdbTask>("deployWithAdb") {
            group = TASK_GROUP
            dependsOn("make")
        }
    }
}
