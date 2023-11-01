@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `maven-publish`
    `android-library`
    id("com.aliucord.gradle")
    `kotlin-android`
    id("org.jetbrains.dokka")
}

aliucord {
    projectType = com.aliucord.gradle.ProjectType.CORE
}

kotlin {
    jvmToolchain(17)
    explicitApi()
}

android {
    namespace = "com.aliucord"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        buildConfigField("String", "GIT_REVISION", "\"${getGitHash()}\"")
        buildConfigField("int", "DISCORD_VERSION", libs.versions.discord.get())
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    kotlinOptions {
        freeCompilerArgs += "-Xexplicit-api=warning"
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(libs.appcompat)
    api(libs.material)
    api(libs.constraintlayout)
    api(libs.aliuhook)
    api(libs.core.ktx)

    discord(libs.discord)
    // implementation("com.android.databinding:viewbinding:7.4.2")
    // implementation(project(":core"))
}

tasks {
    listOf(dokkaHtml, dokkaJavadoc).forEach {
        it.configure {
            dokkaSourceSets {
                named("main") {
                    noAndroidSdkLink = false
                    includeNonPublic = false
                }
            }
        }
    }
    create("pushDebuggable") {
        group = "aliucord"

        val aliucordPath = "/storage/emulated/0/Zeetcord"

        doLast {
            exec {
                commandLine(android.adbExecutable, "shell", "touch", "$aliucordPath/.debuggable")
            }

            exec {
                commandLine(
                    android.adbExecutable,
                    "push",
                    rootProject.file(".assets/AndroidManifest-debuggable.xml"),
                    "${aliucordPath}/AndroidManifest.xml"
                )
            }
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>(project.name) {
                from(components["debug"])
                artifact(tasks["debugSourcesJar"])
            }
        }

        repositories {
            val username = System.getenv("MAVEN_USERNAME")
            val password = System.getenv("MAVEN_PASSWORD")

            if (username == null || password == null) {
                mavenLocal()
            } else {
                maven {
                    credentials {
                        this.username = username
                        this.password = password
                    }
                    setUrl("https://maven.aliucord.com/snapshots")
                }
            }
        }
    }
}

fun getGitHash(): String {
    return providers.exec {
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
        isIgnoreExitValue = true
    }.standardOutput.asText.get().trim()
}
