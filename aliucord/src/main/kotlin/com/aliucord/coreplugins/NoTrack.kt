/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
// based on https://gitdab.com/distok/cutthecord/src/branch/master/patches/notrack/1371.patch

package com.aliucord.coreplugins

import android.content.Context
import com.aliucord.entities.Plugin
import com.aliucord.patcher.InsteadHook
import com.google.firebase.crashlytics.FirebaseCrashlytics
import de.robv.android.xposed.XposedBridge

internal class NoTrack : Plugin(Manifest("NoTrack")) {
    @Throws(Throwable::class)
    override fun load(context: Context) {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)

        val map: MutableMap<String, List<String>> = HashMap()

        // com.google.firebase.crashlytics.internal.common.CommonUtils getMappingFileId
        // https://github.com/firebase/firebase-android-sdk/blob/master/firebase-crashlytics/src/main/java/com/google/firebase/crashlytics/internal/common/CommonUtils.java#L582
        map["b.i.c.m.d.k.h"] = listOf("l")
        map["b.i.a.f.i.b.k9"] = listOf("n", "Q")
        map["b.i.a.b.j.t.h.g"] = listOf("run")
        // map.put("c.i.a.f.h.i.r", Collections.singletonList("R"));

        val analyticsPkg = "com.discord.utilities.analytics"

        map["$analyticsPkg.AdjustConfig"] = listOf("init")
        map["$analyticsPkg.AdjustConfig\$AdjustLifecycleListener"] =
            listOf("onActivityPaused", "onActivityResumed")
        map["$analyticsPkg.AnalyticsTracker\$AdjustEventTracker"] =
            listOf("trackLogin", "trackRegister")
        map["$analyticsPkg.AnalyticSuperProperties"] =
            listOf("setCampaignProperties")
        map["$analyticsPkg.AnalyticsUtils\$Tracker"] =
            listOf("drainEventsQueue", "setTrackingData", "track", "trackFireBase")
        map["com.discord.utilities.integrations.SpotifyHelper\$openPlayStoreForSpotify$1"] =
            listOf("run")

        val cl = NoTrack::class.java.getClassLoader()!!

        map.forEach { (className, value) ->
            val clazz = cl.loadClass(className)
            value.forEach { fn ->
                XposedBridge.hookAllMethods(clazz, fn, InsteadHook.DO_NOTHING)
            }
        }
    }
}
