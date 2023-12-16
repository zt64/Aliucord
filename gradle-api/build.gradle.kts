plugins {
    `kotlin-dsl`
}

subprojects {
    apply(plugin = "org.gradle.kotlin.kotlin-dsl")
}

gradlePlugin {
    plugins {
        create("aliucordInjector") {
            id = "com.aliucord.gradle"
            implementationClass = "com.aliucord.gradle.AliucordPlugin"
        }
    }
}

dependencies {
    compileOnlyApi("com.google.guava:guava:32.1.3-jre")
    compileOnlyApi("com.android.tools:sdk-common:31.2.0")
    compileOnlyApi("com.android.tools.build:gradle:${libs.versions.android.gradle.get()}")
    implementation("com.aliucord:axml:1.0.1")

    implementation(gradleApi())
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")

    api(libs.dex.translator)
    api(libs.jadx.core)
    api(libs.jadx.dex.input)
    api(libs.jadb)
}

subprojects {
    dependencies {
        api(rootProject)
    }
}
