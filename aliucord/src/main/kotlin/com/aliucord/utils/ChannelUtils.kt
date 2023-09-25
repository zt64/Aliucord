/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.utils

import android.content.Context
import com.discord.api.channel.Channel
import com.discord.api.channel.ChannelUtils
import com.discord.models.user.User

@Suppress("unused")
public object ChannelUtils {
    @JvmStatic
    public fun getRecipients(channel: Channel?): List<User> = ChannelUtils.g(channel)

    @JvmStatic
    public fun isGuildTextyChannel(channel: Channel?): Boolean = ChannelUtils.v(channel)

    @JvmStatic
    public fun getDMRecipient(channel: Channel?): User = ChannelUtils.a(channel)

    @JvmStatic
    public fun isTextChannel(channel: Channel?): Boolean = ChannelUtils.F(channel)

    @JvmStatic
    public fun getDisplayName(channel: Channel?): String = ChannelUtils.c(channel)

    @JvmStatic
    public fun getDisplayNameOrDefault(
        channel: Channel?,
        context: Context?,
        addPrefix: Boolean
    ): String = ChannelUtils.d(channel, context, addPrefix)
}
