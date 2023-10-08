/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers.embeds

import com.discord.api.message.embed.EmbedFooter

/**
 * Wraps the obfuscated [EmbedFooter] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class FooterWrapper(private val footer: EmbedFooter) {
    /** Returns the raw (obfuscated) [EmbedFooter] Object associated with this wrapper  */
    public fun raw(): EmbedFooter = footer

    public val text: String
        get() = footer.text

    public val iconUrl: String?
        get() = footer.iconUrl

    public val proxyIconUrl: String?
        get() = footer.proxyIconUrl

    public companion object {
        @JvmStatic
        public val EmbedFooter.text: String
            get() = b()

        @JvmStatic
        public val EmbedFooter.proxyIconUrl: String?
            get() = a()

        // why is there no getter for this lol
        // FIXME: Do this without reflection once Discord adds getter
        private val iconUrlField = EmbedFooter::class.java.getDeclaredField("iconUrl")
            .apply { isAccessible = true }

        @JvmStatic
        public val EmbedFooter.iconUrl: String?
            get() = iconUrlField[this] as String?
    }
}
