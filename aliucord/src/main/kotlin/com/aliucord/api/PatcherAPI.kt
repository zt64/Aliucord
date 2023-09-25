/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.api

import com.aliucord.Logger
import com.aliucord.patcher.GlobalPatcher.addPatch
import com.aliucord.patcher.Hook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XC_MethodHook.Unhook
import rx.functions.Action1
import java.lang.reflect.Member

@Suppress("unused")
public open class PatcherAPI internal constructor(
    @JvmField
    public val logger: Logger
) {
    public var unpatches: MutableList<Runnable> = mutableListOf()

    private fun createUnpatch(unhook: Unhook?): Runnable {
        val unpatch = object : Runnable {
            override fun run() {
                unhook!!.unhook()
                unpatches -= this
            }
        }
        unpatches += unpatch
        return unpatch
    }

    /**
     * Patches a method.
     *
     * @param forClass   Class to patch.
     * @param fn         Method to patch.
     * @param paramTypes Parameters of the `fn`. Useful for patching individual overloads.
     * @param hook       Callback for the patch.
     * @return A [Runnable] object.
     */
    public fun patch(
        forClass: String,
        fn: String,
        paramTypes: Array<Class<*>>,
        hook: XC_MethodHook
    ): Runnable = createUnpatch(addPatch(forClass, fn, paramTypes, hook))

    /**
     * Patches a method.
     *
     * @param clazz      Class to patch.
     * @param fn         Method to patch.
     * @param paramTypes Parameters of the `fn`. Useful for patching individual overloads.
     * @param hook       Callback for the patch.
     * @return Method that will remove the patch when invoked
     */
    public fun patch(
        clazz: Class<*>,
        fn: String,
        paramTypes: Array<Class<*>>,
        hook: XC_MethodHook
    ): Runnable = createUnpatch(addPatch(clazz, fn, paramTypes, hook))

    /**
     * Patches a method or constructor.
     *
     * @param m    Method or constructor to patch. see [Member].
     * @param hook Callback for the patch.
     * @return Method that will remove the patch when invoked
     */
    public fun patch(m: Member, hook: XC_MethodHook): Runnable = createUnpatch(addPatch(m, hook))

    /**
     * Patches a method or constructor.
     *
     * @param m    Method or constructor to patch. see [Member].
     * @param callback Callback for the patch.
     * @return Method that will remove the patch when invoked
     */
    public fun patch(m: Member, callback: Action1<MethodHookParam>): Runnable {
        return createUnpatch(addPatch(m, Hook(callback)))
    }

    /**
     * Removes all patches.
     */
    public fun unpatchAll(): Unit = unpatches.forEach(Runnable::run)
}
