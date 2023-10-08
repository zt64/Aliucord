/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers

import com.discord.api.emoji.GuildEmoji

/**
 * Wraps the obfuscated [GuildEmoji] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class GuildEmojiWrapper(private val emoji: GuildEmoji) {
    /** Returns the raw (obfuscated) [GuildEmoji] Object associated with this wrapper */
    public fun raw(): GuildEmoji = emoji

    @get:JvmName("isAnimated")
    public val animated: Boolean
        get() = emoji.animated

    @get:JvmName("isAvailable")
    public val available: Boolean?
        get() = emoji.available

    public val id: Long
        get() = emoji.id

    @get:JvmName("isManaged")
    public val managed: Boolean
        get() = emoji.managed

    public val name: String
        get() = emoji.name

    public val roles: List<Long>
        get() = emoji.roles

    public companion object {
        @JvmStatic
        @get:JvmName("isAnimated")
        public val GuildEmoji.animated: Boolean
            get() = a()

        @JvmStatic
        @get:JvmName("isAvailable")
        public val GuildEmoji.available: Boolean?
            get() = b()

        @JvmStatic
        public val GuildEmoji.id: Long
            get() = c()

        @JvmStatic
        @get:JvmName("isManaged")
        public val GuildEmoji.managed: Boolean
            get() = d()

        @JvmStatic
        public val GuildEmoji.name: String
            get() = e()

        @JvmStatic
        public val GuildEmoji.requireColons: Boolean
            get() = f()

        @JvmStatic
        public val GuildEmoji.roles: List<Long>
            get() = g()
    }
}
