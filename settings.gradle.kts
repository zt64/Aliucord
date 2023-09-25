@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://maven.aliucord.com/snapshots")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://maven.aliucord.com/snapshots")
    }
}

rootProject.name = "Aliucord"

include(":aliucord")
include(":injector")
include(":plugin")
// include(":core")
