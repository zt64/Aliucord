@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.aliucord.gradle) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.compatibility)
}

subprojects {
    if (project.name !in arrayOf("Aliucord", "Injector")) return@subprojects

    apply {
    //     plugin("com.android.library")
    //     // plugin("kotlin-android")
    //     plugin("com.aliucord.gradle")
    }
    //
    // android {
    //     namespace = "com.aliucord"
    //
    //     compileSdkVersion(30)
    //
    //     @Suppress("ExpiredTargetSdkVersion")
    //     defaultConfig {
    //         minSdk = 24
    //         targetSdk = 30
    //     }
    //
    //     buildTypes {
    //         get("release").isMinifyEnabled = false
    //     }
    //
    //     compileOptions {
    //         sourceCompatibility = JavaVersion.VERSION_11
    //         targetCompatibility = JavaVersion.VERSION_11
    //     }
    // }
    //
    dependencies {
        // val discord by configurations

        // discord(rootProject.libs.discord)
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            // freeCompilerArgs = freeCompilerArgs +
            //     "-Xno-call-assertions" +
            //     "-Xno-param-assertions" +
            //     "-Xno-receiver-assertions"
        }
    }
}

apiValidation {
    ignoredProjects += listOf("plugin")
}
