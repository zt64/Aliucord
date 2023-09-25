/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.patcher

import de.robv.android.xposed.XC_MethodHook
import java.lang.reflect.Member

/**
 * Runs the specified [callback] **instead of** the hooked [Member]
 *
 * @property callback The callback to run instead of the method
 */
public class InsteadHook(public val callback: Function1<MethodHookParam, Any?>) : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        try {
            param.result = callback.invoke(param)
        } catch (th: Throwable) {
            GlobalPatcher.logger.error(
                "Exception while replacing ${param.method.declaringClass.name}.${param.method.name}",
                th
            )
        }
    }

    public companion object {
        /**
         * [InsteadHook] that always returns null
         */
        @JvmField
        public val DO_NOTHING: InsteadHook = returnConstant(null)

        /**
         * [InsteadHook] that always returns the specified [constant]
         *
         * @param constant Constant to return
         */
        @JvmStatic
        public fun returnConstant(constant: Any?): InsteadHook = InsteadHook { constant }
    }
}
