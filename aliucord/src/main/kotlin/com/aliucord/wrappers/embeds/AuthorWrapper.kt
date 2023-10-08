/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers.embeds

import com.discord.api.message.embed.EmbedAuthor

/**
 * Wraps the obfuscated [EmbedAuthor] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class AuthorWrapper(private val author: EmbedAuthor) {
    /** Returns the raw (obfuscated) [EmbedAuthor] Object associated with this wrapper */
    public fun raw(): EmbedAuthor = author

    public val name: String
        get() = author.name

    public val proxyIconUrl: String?
        get() = author.proxyIconUrl

    public val url: String?
        get() = author.url

    public companion object {
        @JvmStatic
        public val EmbedAuthor.name: String
            get() = a()

        @JvmStatic
        public val EmbedAuthor.proxyIconUrl: String?
            get() = b()

        @JvmStatic
        public val EmbedAuthor.url: String?
            get() = c()
    }
}
