/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers.embeds

import com.discord.api.message.embed.EmbedField

/**
 * Wraps the obfuscated [EmbedField] class to provide nice method names and require only one central
 * update if method names change after an update
 */
public class FieldWrapper(private val field: EmbedField) {
    /** Returns the raw (obfuscated) [EmbedField] Object associated with this wrapper  */
    public fun raw(): EmbedField = field

    public val name: String
        get() = this.field.value

    public val value: String
        get() = this.field.value

    @get:JvmName("isInline")
    public val inline: Boolean
        get() = this.field.inline

    public companion object {
        @JvmStatic
        public val EmbedField.name: String
            get() = a()

        @JvmStatic
        public val EmbedField.value: String
            get() = b()

        private val inlineField = EmbedField::class.java.getDeclaredField("inline")
            .apply { isAccessible = true }

        @JvmStatic
        @get:JvmName("isInline")
        public val EmbedField.inline: Boolean
            get() = inlineField[this] as Boolean? == java.lang.Boolean.TRUE
    }
}
