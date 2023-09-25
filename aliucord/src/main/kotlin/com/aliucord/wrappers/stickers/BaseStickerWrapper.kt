/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers.stickers

import com.discord.api.sticker.BaseSticker
import com.discord.api.sticker.StickerFormatType
import com.discord.api.sticker.StickerPartial

/**
 * Wraps the obfuscated [BaseSticker] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class BaseStickerWrapper(private val sticker: BaseSticker) {
    /** Returns the raw (obfuscated) [BaseSticker] Object associated with this wrapper */
    public fun raw(): BaseSticker = sticker

    public val formatType: StickerFormatType
        get() = sticker.formatType

    public val format: String
        get() = sticker.format

    public val stickerPartial: StickerPartial
        get() = sticker.stickerPartial

    public val id: Long
        get() = sticker.id

    public companion object {
        @JvmStatic
        public val BaseSticker.formatType: StickerFormatType
            get() = a()

        @JvmStatic
        public val BaseSticker.format: String
            get() = b()

        @JvmStatic
        public val BaseSticker.stickerPartial: StickerPartial
            get() = c()

        @JvmStatic
        public val BaseSticker.id: Long
            get() = d()
    }
}
