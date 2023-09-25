/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers

import com.discord.api.channel.Channel
import com.discord.api.emoji.GuildEmoji
import com.discord.api.guild.*
import com.discord.api.guildhash.GuildHashes
import com.discord.api.guildmember.GuildMember
import com.discord.api.guildscheduledevent.GuildScheduledEvent
import com.discord.api.presence.Presence
import com.discord.api.role.GuildRole
import com.discord.api.sticker.Sticker

/**
 * Wraps the obfuscated [Guild] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class GuildWrapper(private val guild: Guild) {
    /** Returns the raw (obfuscated) [Guild] Object associated with this wrapper */
    public fun raw() = guild

    public val afkChannelId
        get() = guild.afkChannelId

    public val afkTimeout
        get() = guild.afkTimeout

    public val approximatePresenceCount
        get() = guild.approximatePresenceCount

    public val banner
        get() = guild.banner

    public val channelUpdates
        get() = guild.channelUpdates

    public val channels
        get() = guild.channels

    public val defaultMessageNotifications
        get() = guild.defaultMessageNotifications

    public val description
        get() = guild.description

    public val emojis
        get() = guild.emojis

    public val explicitContentFilter
        get() = guild.explicitContentFilter

    public val features
        get() = guild.features

    public val guildHashes
        get() = guild.hashes

    public val scheduledEvents
        get() = guild.scheduledEvents

    public val icon
        get() = guild.icon

    public val id
        get() = guild.id

    public val joinedAt
        get() = guild.joinedAt

    public val maxVideoChannelUsers: GuildMaxVideoChannelUsers?
        get() = guild.maxVideoChannelUsers

    public val approxMemberCount
        get() = guild.approxMemberCount

    public val cachedMembers
        get() = guild.cachedMembers

    public val mfaLevel
        get() = guild.mfaLevel

    public val name
        get() = guild.name

    @get:JvmName("isNsfw")
    public val nsfw: Boolean
        get() = guild.nsfw

    public val ownerId: Long
        get() = guild.ownerId

    public val preferredLocale: String?
        get() = guild.preferredLocale

    public val premiumSubscriptionCount: Int
        get() = guild.premiumSubscriptionCount

    public val premiumTier: Int
        get() = guild.premiumTier

    public val presences: List<Presence>
        get() = guild.presences

    public val publicUpdatesChannelId: Long?
        get() = guild.publicUpdatesChannelId

    public val region: String?
        get() = guild.region

    public val roles: List<GuildRole>
        get() = guild.roles

    public val rulesChannelId: Long?
        get() = guild.rulesChannelId

    public val splash: String?
        get() = guild.splash

    public val stickers: List<Sticker>
        get() = guild.stickers

    public val systemChannelFlags: Int
        get() = guild.systemChannelFlags

    public val systemChannelId: Long?
        get() = guild.systemChannelId

    public val threads: List<Channel>
        get() = guild.threads

    @get:JvmName("isUnavailable")
    public val unavailable: Boolean
        get() = guild.unavailable

    public val vanityUrlCode: String?
        get() = guild.vanityUrlCode

    public val verificationLevel: GuildVerificationLevel
        get() = guild.verificationLevel

    public companion object {
        @JvmStatic
        public val Guild.afkChannelId: Long?
            get() = b()

        @JvmStatic
        public val Guild.afkTimeout: Int
            get() = c()

        @JvmStatic
        public val Guild.approximatePresenceCount: Int
            get() = d()

        @JvmStatic
        public val Guild.banner: String?
            get() = e()

        @JvmStatic
        public val Guild.channelUpdates: List<Channel>
            get() = f()

        @JvmStatic
        public val Guild.channels: List<Channel>
            get() = g()

        @JvmStatic
        public val Guild.defaultMessageNotifications: Int?
            get() = h()

        @JvmStatic
        public val Guild.description: String?
            get() = i()

        @JvmStatic
        public val Guild.emojis: List<GuildEmoji>
            get() = k()

        @JvmStatic
        public val Guild.explicitContentFilter: GuildExplicitContentFilter?
            get() = l()

        @JvmStatic
        public val Guild.features: List<GuildFeature>
            get() = m()

        @JvmStatic
        public val Guild.hashes: GuildHashes
            get() = n()

        @JvmStatic
        public val Guild.scheduledEvents: List<GuildScheduledEvent>
            get() = o()

        @JvmStatic
        public val Guild.icon: String?
            get() = q()

        @JvmStatic
        public val Guild.id: Long
            get() = r()

        @JvmStatic
        public val Guild.joinedAt: String?
            get() = s()

        @JvmStatic
        public val Guild.maxVideoChannelUsers: GuildMaxVideoChannelUsers?
            get() = t()

        @JvmStatic
        public val Guild.approxMemberCount: Int
            get() = u()

        @JvmStatic
        public val Guild.cachedMembers: List<GuildMember>
            get() = v()

        @JvmStatic
        public val Guild.mfaLevel: Int
            get() = w()

        @JvmStatic
        public val Guild.name: String
            get() = x()

        @JvmStatic
        @get:JvmName("isNsfw")
        public val Guild.nsfw: Boolean
            get() = y()

        @JvmStatic
        public val Guild.ownerId: Long
            get() = z()

        @JvmStatic
        public val Guild.preferredLocale: String?
            get() = A()

        @JvmStatic
        public val Guild.premiumSubscriptionCount: Int
            get() = B()

        @JvmStatic
        public val Guild.premiumTier: Int
            get() = C()

        @JvmStatic
        public val Guild.presences: List<Presence>
            get() = D()

        @JvmStatic
        public val Guild.publicUpdatesChannelId: Long?
            get() = E()

        @JvmStatic
        public val Guild.region: String?
            get() = F()

        @JvmStatic
        public val Guild.roles: List<GuildRole>
            get() = G()

        @JvmStatic
        public val Guild.rulesChannelId: Long?
            get() = H()

        @JvmStatic
        public val Guild.splash: String?
            get() = I()

        @JvmStatic
        public val Guild.stickers: List<Sticker>
            get() = K()

        @JvmStatic
        public val Guild.systemChannelFlags: Int
            get() = L()

        @JvmStatic
        public val Guild.systemChannelId: Long?
            get() = M()

        @JvmStatic
        public val Guild.threads: List<Channel>
            get() = N()

        @JvmStatic
        @get:JvmName("isUnavailable")
        public val Guild.unavailable: Boolean
            get() = O()

        @JvmStatic
        public val Guild.vanityUrlCode: String?
            get() = P()

        @JvmStatic
        public val Guild.verificationLevel: GuildVerificationLevel
            get() = Q()
    }
}
