/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers

import com.discord.api.guildmember.GuildMember
import com.discord.api.presence.Presence
import com.discord.api.user.User
import com.discord.api.utcdatetime.UtcDateTime

/**
 * Wraps the obfuscated [GuildMember] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class GuildMemberWrapper(private val guildMember: GuildMember) {
    /** Returns the raw (obfuscated) [GuildMember] Object associated with this wrapper */
    public fun raw(): GuildMember = guildMember

    public val avatar: String?
        get() = guildMember.avatar

    public val banner: String?
        get() = guildMember.banner

    public val bio: String?
        get() = guildMember.bio

    public val guildId: UtcDateTime
        get() = guildMember.guildId

    public val joinedAt: UtcDateTime?
        get() = guildMember.joinedAt

    public val nick: String?
        get() = guildMember.nick

    public val isPending: Boolean
        get() = guildMember.isPending

    public val premiumSince: String?
        get() = guildMember.premiumSince

    public val presence: Presence?
        get() = guildMember.presence

    public val roles: List<Long>
        get() = guildMember.roles

    public val user: User?
        get() = guildMember.user

    public val userId: Long?
        get() = guildMember.userId

    public companion object {
        @JvmStatic
        public val GuildMember.avatar: String?
            get() = b()

        @JvmStatic
        public val GuildMember.banner: String?
            get() = c()

        @JvmStatic
        public val GuildMember.bio: String?
            get() = d()

        @JvmStatic
        public val GuildMember.guildId: UtcDateTime
            get() = e()

        @JvmStatic
        public val GuildMember.joinedAt: UtcDateTime?
            get() = g()

        @JvmStatic
        public val GuildMember.nick: String?
            get() = h()

        @JvmStatic
        public val GuildMember.isPending: Boolean
            get() = i()

        @JvmStatic
        public val GuildMember.premiumSince: String?
            get() = j()

        @JvmStatic
        public val GuildMember.presence: Presence?
            get() = k()

        @JvmStatic
        public val GuildMember.roles: List<Long>
            get() = l()

        @JvmStatic
        public val GuildMember.user: User?
            get() = m()

        @JvmStatic
        public val GuildMember.userId: Long?
            get() = n()
    }
}
