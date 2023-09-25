/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.aliucord.gradle

import com.aliucord.gradle.entities.Author
import com.aliucord.gradle.entities.Links
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.kotlin.dsl.*
import javax.inject.Inject

abstract class AliucordExtension @Inject constructor(val project: Project) {
    val projectType = project.objects.property<ProjectType>().convention(ProjectType.PLUGIN)

    val authors = project.objects.listProperty<Author>()

    fun author(name: String, id: Long) = authors.add(Author(name, id))

    val links = Links()

    fun github(url: String) {
        links.github = url

        if (!updateUrl.isPresent && !buildUrl.isPresent) {
            updateUrl.set("$url/releases/latest/download/updater.json")
            buildUrl.set("$url/releases/download/${project.version}/${project.name}.zip")
        }
    }

    val updateUrl = project.objects.property<String>()
    val changelog = project.objects.property<String>()
    val changelogMedia = project.objects.property<String>()

    val minimumDiscordVersion = project.objects.property<Int>()
    val buildUrl = project.objects.property<String>()

    val excludeFromUpdaterJson = project.objects.property<Boolean>().convention(false)

    val userCache = project.gradle.gradleUserHomeDir.resolve("caches").resolve("aliucord")

    var discord: DiscordInfo? = null
        internal set

    internal var pluginClassName: String? = null
}

class DiscordInfo(extension: AliucordExtension, val version: Int) {
    val cache = extension.userCache.resolve("discord")

    val apkFile = cache.resolve("discord-$version.apk")
    val jarFile = cache.resolve("discord-$version.jar")
}

fun ExtensionContainer.getAliucord() = getByName<AliucordExtension>("aliucord")
fun ExtensionContainer.findAliucord() = findByType<AliucordExtension>()
