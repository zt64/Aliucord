plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("com.android.library:com.android.library.gradle.plugin:${libs.versions.android.gradle.get()}")
    implementation("org.jetbrains.kotlin.android:org.jetbrains.kotlin.android.gradle.plugin:${libs.versions.kotlin.get()}")
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        create("androidLibrary") {
            id = "com.aliucord.core"
            implementationClass = "com.aliucord.AndroidLibraryPlugin"
        }
    }
}
