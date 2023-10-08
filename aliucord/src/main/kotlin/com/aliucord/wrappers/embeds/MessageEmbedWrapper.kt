/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers.embeds

import com.discord.api.message.embed.*
import com.discord.api.utcdatetime.UtcDateTime

/**
 * Wraps the obfuscated [MessageEmbed] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class MessageEmbedWrapper(private val embed: MessageEmbed) {
    /** Returns the raw (obfuscated) [MessageEmbed] Object associated with this wrapper */
    public fun raw(): MessageEmbed = embed

    public val author: AuthorWrapper?
        get() = getAuthor(embed)

    /** Returns the raw (obfuscated) [EmbedAuthor] Object associated with this wrapper */
    public val rawAuthor: EmbedAuthor?
        get() = embed.rawAuthor

    public val color: Int?
        get() = embed.color

    public val description: String?
        get() = embed.description

    public val fields: List<FieldWrapper>?
        get() = getFields(embed)

    /** Returns the raw (obfuscated) [EmbedField]s associated with this wrapper */
    public val rawFields: List<EmbedField>?
        get() = embed.rawFields

    public val footer: FooterWrapper?
        get() = getFooter(embed)

    /** Returns the raw (obfuscated) [EmbedFooter] Object associated with this wrapper */
    public val rawFooter: EmbedFooter?
        get() = embed.rawFooter

    public val thumbnail: ThumbnailWrapper?
        get() = getThumbnail(embed)

    /** Returns the raw (obfuscated) [EmbedThumbnail] Object associated with this wrapper */
    public val rawThumbnail: EmbedThumbnail?
        get() = embed.rawThumbnail

    public val image: ImageWrapper?
        get() = getImage(embed)

    /** Returns the raw (obfuscated) [EmbedImage] Object associated with this wrapper */
    public val rawImage: EmbedImage?
        get() = embed.rawImage

    public val video: VideoWrapper?
        get() = getVideo(embed)

    /** Returns the raw (obfuscated) [EmbedVideo] Object associated with this wrapper */
    public val rawVideo: EmbedVideo?
        get() = embed.rawVideo

    public val provider: ProviderWrapper?
        get() = getProvider(embed)

    /** Returns the raw (obfuscated) [EmbedProvider] Object associated with this wrapper */
    public val rawProvider: EmbedProvider?
        get() = embed.rawProvider

    public val timestamp: UtcDateTime?
        get() = embed.timestamp

    public val title: String?
        get() = embed.title

    public val type: EmbedType
        get() = embed.type

    public val url: String?
        get() = embed.url

    public companion object {
        @JvmStatic
        public fun getAuthor(embed: MessageEmbed): AuthorWrapper? = embed.rawAuthor
            .run { if (this == null) null else AuthorWrapper(this) }

        @JvmStatic
        public fun getFields(embed: MessageEmbed): List<FieldWrapper>? = embed.rawFields?.map { FieldWrapper(it) }

        @JvmStatic
        public fun getFooter(embed: MessageEmbed): FooterWrapper? = embed.rawFooter
            .run { if (this == null) null else FooterWrapper(this) }

        @JvmStatic
        public fun getImage(embed: MessageEmbed): ImageWrapper? = embed.rawImage
            .run { if (this == null) null else ImageWrapper(this) }

        @JvmStatic
        public fun getProvider(embed: MessageEmbed): ProviderWrapper? = embed.rawProvider
            .run { if (this == null) null else ProviderWrapper(this) }

        @JvmStatic
        public fun getThumbnail(embed: MessageEmbed): ThumbnailWrapper? = embed.rawThumbnail
            .run { if (this == null) null else ThumbnailWrapper(this) }

        @JvmStatic
        public fun getVideo(embed: MessageEmbed): VideoWrapper? = embed.rawVideo
            .run { if (this == null) null else VideoWrapper(this) }

        @JvmStatic
        public val MessageEmbed.rawAuthor: EmbedAuthor?
            get() = a()

        @JvmStatic
        public val MessageEmbed.color: Int?
            get() = b()

        @JvmStatic
        public val MessageEmbed.description: String?
            get() = c()

        @JvmStatic
        public val MessageEmbed.rawFields: List<EmbedField>?
            get() = d()

        @JvmStatic
        public val MessageEmbed.rawFooter: EmbedFooter?
            get() = e()

        @JvmStatic
        public val MessageEmbed.rawThumbnail: EmbedThumbnail?
            get() = h()

        @JvmStatic
        public val MessageEmbed.rawImage: EmbedImage?
            get() = f()

        @JvmStatic
        public val MessageEmbed.rawVideo: EmbedVideo?
            get() = m()

        @JvmStatic
        public val MessageEmbed.rawProvider: EmbedProvider?
            get() = g()

        @JvmStatic
        public val MessageEmbed.timestamp: UtcDateTime?
            get() = i()

        @JvmStatic
        public val MessageEmbed.title: String?
            get() = j()

        @JvmStatic
        public val MessageEmbed.type: EmbedType
            get() = k()

        @JvmStatic
        public val MessageEmbed.url: String?
            get() = l()
    }
}
