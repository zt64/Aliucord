@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.aliucord.core") apply false
    id("com.aliucord.gradle") apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dokka) apply false
}
