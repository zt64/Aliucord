/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.entities

import com.aliucord.Main.logger
import com.aliucord.utils.ReflectUtils.allocateInstance
import com.aliucord.utils.ReflectUtils.setField
import com.aliucord.utils.lazyField
import com.discord.api.message.embed.*
import com.discord.api.utcdatetime.UtcDateTime
import java.lang.reflect.Field

/** [MessageEmbed] builder  */
@Suppress("unused", "MemberVisibilityCanBePrivate")
public class MessageEmbedBuilder @JvmOverloads constructor(type: EmbedType? = EmbedType.RICH) {
    private val embed: MessageEmbed? = allocateInstance<MessageEmbed>()
    /**
     * Creates an embed with a specific type.
     * @param type [EmbedType]
     */
    /**
     * Creates a rich embed
     */
    init {
        setType(type)
    }

    /** Builds the MessageEmbed  */
    public fun build(): MessageEmbed? = embed

    /**
     * @param name Name of the author.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setAuthor(name: String?): MessageEmbedBuilder {
        return setAuthor(name, null, null)
    }

    /**
     * @param name Name of the author.
     * @param iconUrl Icon URL of the author.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setAuthor(name: String?, iconUrl: String?): MessageEmbedBuilder {
        return setAuthor(name, iconUrl, iconUrl)
    }

    /**
     * @param name Name of the author.
     * @param proxyIconUrl Proxy icon URL of the author.
     * @param iconUrl Icon URL of the author.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setAuthor(
        name: String?,
        iconUrl: String?,
        proxyIconUrl: String?
    ): MessageEmbedBuilder = apply {
        val c = EmbedAuthor::class.java
        val author = allocateInstance(c)
        try {
            setField(c, author, "name", name)
            setField(c, author, "iconUrl", iconUrl)
            setField(c, author, "proxyIconUrl", proxyIconUrl)
            setAuthor(author)
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed author.
     * @param author [EmbedAuthor]
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setAuthor(author: EmbedAuthor?): MessageEmbedBuilder = apply {
        try {
            authorField[embed] = author
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets a random embed color.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setRandomColor(): MessageEmbedBuilder {
        return setColor(kotlin.random.Random.nextInt(0xffffff + 1))
    }

    /**
     * Sets the embed color.
     * @param color Embed color.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setColor(color: Int?): MessageEmbedBuilder = apply {
        try {
            colorField[embed] = color
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed description.
     * @param description Embed description.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setDescription(description: String?): MessageEmbedBuilder = apply {
        try {
            descriptionField[embed] = description
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Adds a field to the embed.
     * @param name Name of the field.
     * @param value Content of the field.
     * @param inline Whether to inline the field or not.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun addField(name: String?, value: String?, inline: Boolean): MessageEmbedBuilder {
        return addField(createField(name, value, inline))
    }

    /**
     * Adds a field to the embed.
     * @param field [EmbedField]
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.addField
     * @see MessageEmbedBuilder.createField
     */
    public fun addField(field: EmbedField?): MessageEmbedBuilder = apply {
        if (field == null) return@apply

        try {
            val o = fieldsField[embed] as List<EmbedField>?
            fieldsField[embed] = if (o == null) {
                listOf(field)
            } else {
                val aList = if (o is ArrayList<*>) o as ArrayList<EmbedField> else ArrayList(o)
                aList += field
                aList
            }
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets embed fields.
     * @param fields [List] of [EmbedField]
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.createField
     */
    public fun setFields(fields: List<EmbedField?>?): MessageEmbedBuilder = apply {
        try {
            fieldsField[embed] = fields
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed footer.
     * @param text Footer text.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setFooter(text: String?): MessageEmbedBuilder = setFooter(text, null, null)

    /**
     * Sets the embed footer.
     * @param text Footer text.
     * @param iconUrl Footer icon URL.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setFooter(text: String?, iconUrl: String?): MessageEmbedBuilder {
        return setFooter(text, iconUrl, iconUrl)
    }

    /**
     * Sets the embed footer.
     * @param text Footer text.
     * @param iconUrl Footer icon URL.
     * @param proxyIconUrl Footer Proxy icon URL.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setFooter(
        text: String?,
        iconUrl: String?,
        proxyIconUrl: String?
    ): MessageEmbedBuilder = apply {
        val c = EmbedFooter::class.java
        val footer = allocateInstance(c)
        try {
            setField(c, footer, "text", text)
            setField(c, footer, "iconUrl", iconUrl)
            setField(c, footer, "proxyIconUrl", proxyIconUrl)
            setFooter(footer)
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed footer.
     * @param footer [EmbedFooter]
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.setFooter
     */
    public fun setFooter(footer: EmbedFooter?): MessageEmbedBuilder = apply {
        try {
            footerField[embed] = footer
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed image.
     * @param imageUrl Image URL.
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.setImage
     */
    public fun setImage(imageUrl: String?): MessageEmbedBuilder {
        return setImage(imageUrl, imageUrl, 512, 512)
    }

    /**
     * Sets the embed image.
     * @param imageUrl Image URL.
     * @param proxyImageUrl Proxy image URL.
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.setImage
     */
    public fun setImage(imageUrl: String?, proxyImageUrl: String?): MessageEmbedBuilder {
        return setImage(imageUrl, proxyImageUrl, 512, 512)
    }

    /**
     * Sets the embed image.
     * @param imageUrl Image URL.
     * @param proxyImageUrl Proxy image URL.
     * @param imageHeight Image height.
     * @param imageWidth Image width.
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.setImage
     */
    public fun setImage(
        imageUrl: String?,
        proxyImageUrl: String?,
        imageHeight: Int?,
        imageWidth: Int?
    ): MessageEmbedBuilder = apply {
        val c = EmbedImage::class.java
        val image = allocateInstance(c)
        try {
            setField(c, image, "url", imageUrl)
            setField(c, image, "proxyUrl", proxyImageUrl)
            setField(c, image, "height", imageHeight)
            setField(c, image, "width", imageWidth)
            setImage(image)
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed image.
     * @param image [EmbedImage]
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.setImage
     */
    public fun setImage(image: EmbedImage?): MessageEmbedBuilder = apply {
        try {
            imageField[embed] = image
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed provider.
     * @param provider [EmbedProvider].
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setProvider(provider: EmbedProvider?): MessageEmbedBuilder = apply {
        try {
            providerField[embed] = provider
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed thumbnail.
     * @param imageUrl Image URL.
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.setThumbnail
     */
    public fun setThumbnail(imageUrl: String?): MessageEmbedBuilder {
        return setThumbnail(imageUrl, imageUrl, 512, 512)
    }

    /**
     * Sets the embed thumbnail.
     * @param imageUrl Image URL.
     * @param proxyImageUrl Proxy image URL.
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.setThumbnail
     */
    public fun setThumbnail(imageUrl: String?, proxyImageUrl: String?): MessageEmbedBuilder {
        return setThumbnail(imageUrl, proxyImageUrl, 512, 512)
    }

    /**
     * Sets the embed thumbnail.
     * @param imageUrl Image URL.
     * @param proxyImageUrl Proxy image URL.
     * @param imageHeight Image height.
     * @param imageWidth Image width.
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.setThumbnail
     */
    public fun setThumbnail(
        imageUrl: String?,
        proxyImageUrl: String?,
        imageHeight: Int?,
        imageWidth: Int?
    ): MessageEmbedBuilder = apply {
        val c = EmbedThumbnail::class.java
        val image = allocateInstance(c)
        try {
            setField(c, image, "url", imageUrl)
            setField(c, image, "proxyUrl", proxyImageUrl)
            setField(c, image, "height", imageHeight)
            setField(c, image, "width", imageWidth)
            setThumbnail(image)
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed thumbnail.
     * @param image [EmbedThumbnail]
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.setThumbnail
     */
    public fun setThumbnail(image: EmbedThumbnail?): MessageEmbedBuilder = apply {
        try {
            thumbnailField[embed] = image
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed timestamp.
     * @param timestamp [UtcDateTime] timestamp.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setTimestamp(timestamp: UtcDateTime?): MessageEmbedBuilder = apply {
        try {
            timestampField[embed] = timestamp
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed title.
     * @param title Embed title.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setTitle(title: String?): MessageEmbedBuilder = apply {
        try {
            titleField[embed] = title
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed type.
     * @param type [EmbedType].
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setType(type: EmbedType?): MessageEmbedBuilder = apply {
        try {
            typeField[embed] = type
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed URL.
     * @param url Embed URL.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setUrl(url: String?): MessageEmbedBuilder = apply {
        try {
            urlField[embed] = url
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed video.
     * @param videoUrl Video URL.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setVideo(
        videoUrl: String?,
        proxyVideoUrl: String?,
        height: Int?,
        width: Int?
    ): MessageEmbedBuilder = apply {
        val c = EmbedVideo::class.java
        val video = allocateInstance(c)
        try {
            setField(c, video, "url", videoUrl)
            setField(c, video, "proxyUrl", proxyVideoUrl)
            setField(c, video, "height", height)
            setField(c, video, "width", width)
            setVideo(video)
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    /**
     * Sets the embed video.
     * @param videoUrl Video URL.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setVideo(videoUrl: String?): MessageEmbedBuilder {
        return setVideo(videoUrl, videoUrl, 512, 512)
    }

    /**
     * Sets the embed video.
     * @param videoUrl Video URL.
     * @return [MessageEmbedBuilder] for chaining.
     */
    public fun setVideo(videoUrl: String?, proxyVideoUrl: String?): MessageEmbedBuilder {
        return setVideo(videoUrl, proxyVideoUrl, 512, 512)
    }

    /**
     * Sets the embed video.
     * @param video [EmbedVideo].
     * @return [MessageEmbedBuilder] for chaining.
     * @see MessageEmbedBuilder.setVideo
     */
    public fun setVideo(video: EmbedVideo?): MessageEmbedBuilder = apply {
        try {
            videoField[embed] = video
        } catch (e: Throwable) {
            logger.error(e)
        }
    }

    public companion object {
        // reflect moment
        private val authorField: Field by lazyField<MessageEmbed>()
        private val colorField: Field by lazyField<MessageEmbed>()
        private val descriptionField: Field by lazyField<MessageEmbed>()
        private val fieldsField: Field by lazyField<MessageEmbed>()
        private val footerField: Field by lazyField<MessageEmbed>()
        private val imageField: Field by lazyField<MessageEmbed>()
        private val providerField: Field by lazyField<MessageEmbed>()
        private val thumbnailField: Field by lazyField<MessageEmbed>()
        private val timestampField: Field by lazyField<MessageEmbed>()
        private val titleField: Field by lazyField<MessageEmbed>()
        private val typeField: Field by lazyField<MessageEmbed>()
        private val urlField: Field by lazyField<MessageEmbed>()
        private val videoField: Field by lazyField<MessageEmbed>()

        /**
         * @param name Field name.
         * @param value Field content.
         * @param inline Whether to inline the field or not.
         * @return [MessageEmbedBuilder] for chaining.
         * @see MessageEmbedBuilder.addField
         */
        public fun createField(name: String?, value: String?, inline: Boolean?): EmbedField? {
            val c = EmbedField::class.java
            val field = allocateInstance(c)

            return try {
                setField(c, field, "name", name)
                setField(c, field, "value", value)
                setField(c, field, "inline", inline)
                field
            } catch (e: Throwable) {
                logger.error(e)
                null
            }
        }
    }
}
