/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers

import com.discord.api.role.GuildRole
import com.discord.api.role.GuildRoleTags

/**
 * Wraps the obfuscated [GuildRole] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class GuildRoleWrapper(private val role: GuildRole) {
    /** Returns the raw (obfuscated) [GuildRole] Object associated with this wrapper */
    public fun raw(): GuildRole = role

    public val color: Int
        get() = role.color

    public val hoist: Boolean
        get() = role.hoist

    public val icon: String?
        get() = role.icon

    public val id: Long
        get() = role.id

    public val managed: Boolean
        get() = role.managed

    public val mentionable: Boolean
        get() = role.mentionable

    public val name: String
        get() = role.name

    public val permissions: Long
        get() = role.permissions

    public val position: Int
        get() = role.position

    public val tags: GuildRoleTags?
        get() = role.tags

    public val unicodeEmoji: String?
        get() = role.unicodeEmoji

    public companion object {
        @JvmStatic
        public val GuildRole.color: Int
            get() = b()

        @JvmStatic
        public val GuildRole.hoist: Boolean
            get() = c()

        @JvmStatic
        public val GuildRole.icon: String?
            get() = d()

        @JvmStatic
        public val GuildRole.managed: Boolean
            get() = e()

        @JvmStatic
        public val GuildRole.mentionable: Boolean
            get() = f()

        @JvmStatic
        public val GuildRole.name: String
            get() = g()

        @JvmStatic
        public val GuildRole.permissions: Long
            get() = h()

        @JvmStatic
        public val GuildRole.position: Int
            get() = i()

        @JvmStatic
        public val GuildRole.tags: GuildRoleTags?
            get() = j()

        @JvmStatic
        public val GuildRole.unicodeEmoji: String?
            get() = k()
    }
}
