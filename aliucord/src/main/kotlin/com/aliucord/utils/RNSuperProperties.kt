/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2023 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.utils

import android.os.Build
import android.util.Base64
import com.aliucord.Main
import org.json.JSONObject
import java.util.Locale
import java.util.UUID

@Suppress("MemberVisibilityCanBePrivate")
public object RNSuperProperties {
    // vendorId is a random UUID even in normal Discord RN
    @JvmStatic
    public val vendorId: String = Main.settings.getString("rnVendorId", null) ?: UUID.randomUUID().toString().also {
        Main.settings.setString("rnVendorId", it)
    }

    @JvmStatic
    public val superProperties: JSONObject = JSONObject().apply {
        put("os", "Android")
        put("browser", "Discord Android")
        put("device", Build.DEVICE)
        put("system_locale", Locale.getDefault().toString().replace("_", "-"))
        put("client_version", VERSION_STRING)
        put("release_channel", "betaRelease")
        put("device_vendor_id", vendorId)
        put("browser_user_agent", "")
        put("brows  er_version", "")
        put("os_version", Build.VERSION.SDK_INT.toString())
        put("client_build_number", VERSION_CODE)
        put("client_event_source", JSONObject.NULL)
        put("design_id", 0)
    }

    @JvmStatic
    public val superPropertiesBase64: String = Base64.encodeToString(superProperties.toString().toByteArray(), 2)

    // update to latest Beta branch sometimes
    public const val VERSION_CODE: Int = 183109
    public const val VERSION_STRING: String = "183.9 - rn"
    public const val USER_AGENT: String = "Discord-Android/$VERSION_CODE;RNA"
}
