/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2023 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.api.rn.user

@Suppress("unused")
public class UserProfileData(
    public val accentColor: Int?,
    public val banner: String?,
    public val bio: String?,
    public val guildId: Long?,
    public val pronouns: String,
    public val themeColors: IntArray?
)
