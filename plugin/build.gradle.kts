plugins {
    `maven-publish`
    `kotlin-dsl`
}

group = "com.aliucord"

dependencies {
    compileOnly("com.google.guava:guava:30.1.1-jre")
    compileOnly("com.android.tools:sdk-common:31.1.2")
    compileOnly("com.android.tools.build:gradle:8.1.2")

    implementation(libs.dex.translator)
    implementation(libs.jadx.core)
    implementation(libs.jadx.dex.input)
    implementation(libs.jadb)
}

gradlePlugin {
    plugins {
        create("com.aliucord.gradle") {
            id = "com.aliucord.gradle"
            implementationClass = "com.aliucord.gradle.AliucordPlugin"
        }
    }
}

publishing {
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
