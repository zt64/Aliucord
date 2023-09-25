/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers

import com.discord.api.channel.Channel
import com.discord.api.channel.ChannelRecipientNick
import com.discord.api.channel.ForumTag
import com.discord.api.guildhash.GuildHashes
import com.discord.api.permission.PermissionOverwrite
import com.discord.api.thread.ThreadMember
import com.discord.api.thread.ThreadMetadata
import com.discord.api.user.User

/**
 * Wraps the obfuscated [Channel] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class ChannelWrapper(private val channel: Channel) {
    /** Returns the raw (obfuscated) [Channel] Object associated with this wrapper */
    public fun raw(): Channel = channel

    public val applicationId: Long
        get() = channel.applicationId

    public val appliedTags: List<Long>
        get() = channel.appliedTags

    public val availableTags: List<ForumTag>
        get() = channel.availableTags

    public val bitrate: Int
        get() = channel.bitrate

    public val defaultAutoArchiveDuration: Int?
        get() = channel.defaultAutoArchiveDuration

    public val flags: Long?
        get() = channel.flags

    public val guildHashes: GuildHashes?
        get() = channel.guildHashes

    public val guildId: Long
        get() = channel.guildId

    public val icon: String?
        get() = channel.icon

    public val id: Long
        get() = channel.id

    public val lastMessageId: Long
        get() = channel.lastMessageId

    public val member: ThreadMember?
        get() = channel.member

    public val memberListId: String
        get() = channel.memberListId

    public val messageCount: Int?
        get() = channel.messageCount

    public val name: String
        get() = channel.name

    public val nicks: List<ChannelRecipientNick>
        get() = channel.nicks

    @get:JvmName("isNsfw")
    public val nsfw: Boolean
        get() = channel.nsfw

    public val originChannelId: Long
        get() = channel.originChannelId

    public val ownerId: Long
        get() = channel.ownerId

    public val parentId: Long
        get() = channel.parentId

    public val permissionOverwrites: List<PermissionOverwrite>
        get() = channel.permissionOverwrites

    public val position: Int
        get() = channel.position

    public val rateLimitPerUser: Int
        get() = channel.rateLimitPerUser

    public val recipientIds: List<Long>
        get() = channel.recipientIds

    public val recipients: List<User>?
        get() = channel.recipients

    public val rtcRegion: String?
        get() = channel.rtcRegion

    public val threadMetadata: ThreadMetadata?
        get() = channel.threadMetadata

    public val topic: String?
        get() = channel.topic

    public val type: Int
        get() = channel.type

    public val userLimit: Int
        get() = channel.userLimit

    public fun isDM(): Boolean = channel.isDM()

    public fun isGuild(): Boolean = channel.isGuild()

    public companion object {
        @JvmStatic
        public val Channel.applicationId: Long
            get() = b()

        @JvmStatic
        public val Channel.appliedTags: List<Long>
            get() = c()

        @JvmStatic
        public val Channel.availableTags: List<ForumTag>
            get() = d()

        @JvmStatic
        public val Channel.bitrate: Int
            get() = e()

        @JvmStatic
        public val Channel.defaultAutoArchiveDuration: Int?
            get() = f()

        @JvmStatic
        public val Channel.flags: Long?
            get() = g()

        @JvmStatic
        public val Channel.guildHashes: GuildHashes?
            get() = h()

        @JvmStatic
        public val Channel.guildId: Long
            get() = i()

        @JvmStatic
        public val Channel.icon: String?
            get() = j()

        @JvmStatic
        public val Channel.id: Long
            get() = k()

        @JvmStatic
        public val Channel.lastMessageId: Long
            get() = l()

        @JvmStatic
        public val Channel.member: ThreadMember?
            get() = m()

        @JvmStatic
        public val Channel.memberListId: String
            get() = n()

        @JvmStatic
        public val Channel.messageCount: Int?
            get() = o()

        @JvmStatic
        public val Channel.name: String
            get() = p()

        @JvmStatic
        public val Channel.nicks: List<ChannelRecipientNick>
            get() = q()

        @JvmStatic
        @get:JvmName("isNsfw")
        public val Channel.nsfw: Boolean
            get() = r()

        @JvmStatic
        public val Channel.originChannelId: Long
            get() = s()

        @JvmStatic
        public val Channel.ownerId: Long
            get() = t()

        @JvmStatic
        public val Channel.parentId: Long
            get() = u()

        @JvmStatic
        public val Channel.permissionOverwrites: List<PermissionOverwrite>
            get() = v()

        @JvmStatic
        public val Channel.position: Int
            get() = w()

        @JvmStatic
        public val Channel.rateLimitPerUser: Int
            get() = x()

        @JvmStatic
        public val Channel.recipientIds: List<Long>
            get() = y()

        @JvmStatic
        public val Channel.recipients: List<User>?
            get() = z()

        @JvmStatic
        public val Channel.rtcRegion: String?
            get() = A()

        @JvmStatic
        public val Channel.threadMetadata: ThreadMetadata?
            get() = B()

        @JvmStatic
        public val Channel.topic: String?
            get() = C()

        @JvmStatic
        public val Channel.type: Int
            get() = D()

        @JvmStatic
        public val Channel.userLimit: Int
            get() = E()

        @JvmStatic
        public fun Channel.isDM(): Boolean = guildId == 0L

        @JvmStatic
        public fun Channel.isGuild(): Boolean = !isDM()
    }
}
