/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.wrappers

import com.discord.api.permission.PermissionOverwrite

/**
 * Wraps the obfuscated [PermissionOverwrite] class to provide nice method names and require only one central
 * update if method names change after an update
 */
@Suppress("unused")
public class PermissionOverwriteWrapper(private val overwrite: PermissionOverwrite) {
    /** Returns the raw (obfuscated) [PermissionOverwrite] Object associated with this wrapper */
    public fun raw(): PermissionOverwrite = overwrite

    public val id: Long
        get() = overwrite.id

    public val allowed: Long
        get() = overwrite.allowed

    public val denied: Long
        get() = overwrite.denied

    public val type: PermissionOverwrite.Type
        get() = overwrite.type

    public companion object {
        @JvmStatic
        public val PermissionOverwrite.id: Long
            get() = a()

        @JvmStatic
        public val PermissionOverwrite.allowed: Long
            get() = c()

        @JvmStatic
        public val PermissionOverwrite.denied: Long
            get() = d()

        @JvmStatic
        public val PermissionOverwrite.type: PermissionOverwrite.Type
            get() = f()
    }
}
