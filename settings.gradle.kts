@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://maven.aliucord.com/snapshots")
    }
    includeBuild("gradle-api")
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://maven.aliucord.com/snapshots")
    }
}

rootProject.name = "Zeetcord"

include(":aliucord")
include(":injector")
