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
    fun raw() = emoji

    @get:JvmName("isAnimated")
    public val animated
        get() = emoji.animated

    @get:JvmName("isAvailable")
    public val available
        get() = emoji.available

    public val id
        get() = emoji.id

    @get:JvmName("isManaged")
    public val managed
        get() = emoji.managed

    public val name
        get() = emoji.name

    public val roles
        get() = emoji.roles

    companion object {
        @JvmStatic
        @get:JvmName("isAnimated")
        public val GuildEmoji.animated
            get() = a()

        @JvmStatic
        @get:JvmName("isAvailable")
        public val GuildEmoji.available: Boolean?
            get() = b()

        @JvmStatic
        public val GuildEmoji.id
            get() = c()

        @JvmStatic
        @get:JvmName("isManaged")
        public val GuildEmoji.managed
            get() = d()

        @JvmStatic
        public val GuildEmoji.name: String
            get() = e()

        @JvmStatic
        public val GuildEmoji.requireColons
            get() = f()

        @JvmStatic
        public val GuildEmoji.roles: List<Long>
            get() = g()
    }
}
