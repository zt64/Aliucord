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
    compileOnlyApi("com.android.tools:sdk-common:31.1.2")
    compileOnlyApi("com.android.tools.build:gradle:${libs.versions.android.gradle.get()}")

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
