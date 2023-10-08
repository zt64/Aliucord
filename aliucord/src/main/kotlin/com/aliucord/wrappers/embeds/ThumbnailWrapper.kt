/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers.embeds

import com.discord.api.message.embed.EmbedThumbnail

/**
 * Wraps the obfuscated [EmbedThumbnail] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class ThumbnailWrapper(private val thumbnail: EmbedThumbnail) {
    /** Returns the raw (obfuscated) [EmbedThumbnail] Object associated with this wrapper */
    public fun raw(): EmbedThumbnail = thumbnail

    public val url: String
        get() = thumbnail.url

    public val proxyUrl: String
        get() = thumbnail.proxyUrl

    public val height: Int?
        get() = thumbnail.height

    public val width: Int?
        get() = thumbnail.width

    public companion object {
        @JvmStatic
        public val EmbedThumbnail.url: String
            get() = c()

        @JvmStatic
        public val EmbedThumbnail.proxyUrl: String
            get() = b()

        @JvmStatic
        public val EmbedThumbnail.height: Int?
            get() = a()

        @JvmStatic
        public val EmbedThumbnail.width: Int?
            get() = d()
    }
}
