package com.aliucord

import com.android.build.gradle.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidLibraryPlugin : Plugin<Project> {
    @Suppress("UnstableApiUsage")
    override fun apply(target: Project) {
        with(target) {
            apply {
                plugin("com.aliucord.gradle")
                plugin("com.android.library")
                plugin("kotlin-android")
            }

            group = "com.aliucord"

            extensions.getByName<LibraryExtension>("android").apply {
                namespace = "com.aliucord"
                compileSdk = 33

                defaultConfig {
                    minSdk = 24
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                    }
                }

                buildFeatures {
                    androidResources = false
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
            }

            val libs = the<LibrariesForLibs>()

            dependencies {
                val discord by configurations
                val implementation by configurations
                discord(libs.discord)
                // implementation("com.android.databinding:viewbinding:7.1.2")
            }

            tasks.withType<KotlinCompile> {
                kotlinOptions {
                    jvmTarget = "17"
                    // freeCompilerArgs = freeCompilerArgs +
                    //         "-Xno-call-assertions" +
                    //         "-Xno-param-assertions" +
                    //         "-Xno-receiver-assertions"
                }
            }
        }
    }
}
