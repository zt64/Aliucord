@file:Suppress("UnstableApiUsage")

plugins {
    com.aliucord.core
}

kotlin {
    jvmToolchain(17)
}

aliucord {
    projectType.set(com.aliucord.gradle.ProjectType.INJECTOR)
}

android {
    buildFeatures {
        buildConfig = false
        androidResources = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.aliuhook)
}

tasks.register("patchApk") {

}
