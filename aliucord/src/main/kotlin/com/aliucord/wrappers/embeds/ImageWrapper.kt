/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers.embeds

import com.discord.api.message.embed.EmbedImage

/**
 * Wraps the obfuscated [EmbedImage] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class ImageWrapper(private val image: EmbedImage) {
    /** Returns the raw (obfuscated) [EmbedImage] Object associated with this wrapper */
    public fun raw(): EmbedImage = image

    public val url: String
        get() = image.url

    public val proxyUrl: String
        get() = image.proxyUrl

    public val height: Int?
        get() = image.height

    public val width: Int?
        get() = image.width

    public companion object {
        @JvmStatic
        public val EmbedImage.url: String
            get() = c()

        @JvmStatic
        public val EmbedImage.proxyUrl: String
            get() = b()

        @JvmStatic
        public val EmbedImage.height: Int?
            get() = a()

        @JvmStatic
        public val EmbedImage.width: Int?
            get() = d()
    }
}
