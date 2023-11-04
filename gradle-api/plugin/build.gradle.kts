plugins {
    `maven-publish`
}

// gradlePlugin {
//     plugins {
//         create("aliucordInjector") {
//             id = "com.aliucord.injector"
//             implementationClass = "com.aliucord.gradle.AliucordPlugin"
//         }
//     }
// }

publishing {
    repositories {
        val username = System.getenv("GITHUB_ACTOR")
        val password = System.getenv("GITHUB_TOKEN")

        if (username == null || password == null) {
            mavenLocal()
        } else {
            maven {
                name = "GitHubPackages"
                setUrl("https://maven.pkg.github.com/zt64/zeetcord")
                credentials {
                    this.username = username
                    this.password = password
                }
            }
        }
    }
}
