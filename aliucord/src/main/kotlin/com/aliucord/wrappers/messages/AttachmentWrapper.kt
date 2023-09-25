/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers.messages

import com.discord.api.message.attachment.MessageAttachment
import com.discord.api.message.attachment.MessageAttachmentType

/**
 * Wraps the obfuscated [MessageAttachment] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class AttachmentWrapper(private val attachment: MessageAttachment) {
    /** Returns the raw (obfuscated) [MessageAttachment] Object associated with this wrapper */
    public fun raw(): MessageAttachment = attachment

    public val filename: String
        get() = attachment.filename

    public val height: Int?
        get() = attachment.height

    public val proxyUrl: String
        get() = attachment.proxyUrl

    public val size: Long
        get() = attachment.size

    public val type: MessageAttachmentType
        get() = attachment.type

    public val url: String
        get() = attachment.url

    public val width: Int?
        get() = attachment.width

    public companion object {
        @JvmStatic
        public val MessageAttachment.filename: String
            get() = a()

        @JvmStatic
        public val MessageAttachment.height: Int?
            get() = b()

        @JvmStatic
        public val MessageAttachment.proxyUrl: String
            get() = c()

        @JvmStatic
        public val MessageAttachment.size: Long
            get() = d()

        @JvmStatic
        public val MessageAttachment.type: MessageAttachmentType
            get() = e()

        @JvmStatic
        public val MessageAttachment.url: String
            get() = f()

        @JvmStatic
        public val MessageAttachment.width: Int?
            get() = g()
    }
}
