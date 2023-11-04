@file:Suppress("UnstableApiUsage")

import com.aliucord.gradle.ProjectType

plugins {
    id("com.aliucord.core")
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

tasks.register("patchApk") {
    dependsOn(tasks.build)
}
