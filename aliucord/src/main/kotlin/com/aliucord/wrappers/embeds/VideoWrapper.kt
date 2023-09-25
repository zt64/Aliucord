/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers.embeds

import com.discord.api.message.embed.EmbedVideo

/**
 * Wraps the obfuscated [EmbedVideo] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class VideoWrapper(private val video: EmbedVideo) {
    /** Returns the raw (obfuscated) [EmbedVideo] Object associated with this wrapper */
    public fun raw(): EmbedVideo = video

    public val url: String
        get() = video.url

    public val proxyUrl: String
        get() = video.proxyUrl

    public val height: Int?
        get() = video.height

    public val width: Int?
        get() = video.width

    public companion object {
        @JvmStatic
        public val EmbedVideo.url: String
            get() = c()

        @JvmStatic
        public val EmbedVideo.proxyUrl: String
            get() = b()

        @JvmStatic
        public val EmbedVideo.height: Int?
            get() = a()

        @JvmStatic
        public val EmbedVideo.width: Int?
            get() = d()
    }
}
