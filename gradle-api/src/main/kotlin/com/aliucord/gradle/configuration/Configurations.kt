package com.aliucord.gradle.configuration

import org.gradle.api.Project

fun Project.registerConfigurations() {
    val providers = listOf(DiscordConfigurationProvider())

    providers.forEach { provider ->
        configurations.register(provider.name) {
            isTransitive = false
        }
    }

    afterEvaluate {
        providers.forEach { provider ->
            val configuration = this@registerConfigurations.configurations.getByName(provider.name)
            val dependencies = configuration.dependencies

            require(dependencies.size <= 1) {
                "Only one '${provider.name}' dependency should be specified, but ${dependencies.size} were!"
            }

            dependencies.forEach { provider.provide(this@registerConfigurations, it) }
        }
    }
}
