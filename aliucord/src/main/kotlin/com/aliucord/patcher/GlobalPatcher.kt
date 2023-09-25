/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:JvmName("Patcher")

package com.aliucord.patcher

import com.aliucord.Logger
import com.aliucord.api.PatcherAPI
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.Unhook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Member

internal object GlobalPatcher : PatcherAPI(Logger("Patcher")) {
    private val cl = GlobalPatcher::class.java.getClassLoader()!!

    fun addPatch(member: Member, hook: XC_MethodHook): Unhook {
        return XposedBridge.hookMethod(member, hook)
    }

    fun addPatch(
        clazz: Class<*>,
        methodName: String,
        paramTypes: Array<Class<*>> = emptyArray(),
        hook: XC_MethodHook
    ): Unhook? = try {
        addPatch(clazz.getDeclaredMethod(methodName, *paramTypes), hook)
    } catch (e: Throwable) {
        logger.error(e)
        null
    }

    fun addPatch(
        forClass: String,
        methodName: String,
        paramTypes: Array<Class<*>>,
        hook: XC_MethodHook
    ): Unhook? = try {
        addPatch(cl.loadClass(forClass), methodName, paramTypes, hook)
    } catch (e: Throwable) {
        logger.error(e)
        null
    }
}
