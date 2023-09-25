package com.aliucord.patcher

import com.aliucord.api.PatcherAPI
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam

public typealias HookCallback<T> = T.(MethodHookParam) -> Unit
public typealias InsteadHookCallback<T> = T.(MethodHookParam) -> Any?

/**
 * Replaces a constructor of a class.
 * @param paramTypes parameters of the method. Useful for patching individual overloads
 * @param callback callback for the patch
 * @return The [Runnable] object of the patch
 * @see [XC_MethodHook.beforeHookedMethod]
 */
public inline fun <reified T> PatcherAPI.instead(
    vararg paramTypes: Class<*>,
    crossinline callback: InsteadHookCallback<T>
): Runnable = patch(
    m = T::class.java.getDeclaredConstructor(*paramTypes),
    hook = PreHook { param ->
        try {
            param.result = callback(param.thisObject as T, param)
        } catch (th: Throwable) {
            logger.error(
                "Exception while replacing constructor of ${param.method.declaringClass}",
                th
            )
        }
    }
)

/**
 * Replaces a method of a class.
 * @param methodName name of the method to patch
 * @param paramTypes parameters of the method. Useful for patching individual overloads
 * @param callback callback for the patch
 * @return The [Runnable] object of the patch
 * @see [XC_MethodHook.beforeHookedMethod]
 */
public inline fun <reified T> PatcherAPI.instead(
    methodName: String,
    vararg paramTypes: Class<*>,
    crossinline callback: InsteadHookCallback<T>
): Runnable = patch(
    T::class.java.getDeclaredMethod(methodName, *paramTypes),
    PreHook { param ->
        try {
            param.result = callback(param.thisObject as T, param)
        } catch (th: Throwable) {
            logger.error(
                "Exception while replacing ${param.method.declaringClass.name}.${param.method.name}",
                th
            )
        }
    }
)

/**
 * Adds a [PreHook] to a constructor of a class.
 * @param paramTypes parameters of the constructor. Useful for patching individual overloads
 * @param callback callback for the patch
 * @return The [Runnable] object of the patch
 * @see [XC_MethodHook.beforeHookedMethod]
 */
public inline fun <reified T> PatcherAPI.before(
    vararg paramTypes: Class<*>,
    noinline callback: HookCallback<T>
): Runnable = patch(
    m = T::class.java.getDeclaredConstructor(*paramTypes),
    hook = PreHook { param ->
        try {
            callback(param)
        } catch (th: Throwable) {
            logger.error(
                "Exception while pre-hooking constructor of ${param.method.declaringClass}",
                th
            )
        }
    }
)

/**
 * Adds a [PreHook] to a method of a class.
 * @param methodName name of the method to patch
 * @param paramTypes parameters of the method. Useful for patching individual overloads
 * @param callback callback for the patch
 * @return The [Runnable] object of the patch
 * @see [XC_MethodHook.beforeHookedMethod]
 */
public inline fun <reified T> PatcherAPI.before(
    methodName: String,
    vararg paramTypes: Class<*>,
    noinline callback: HookCallback<T>
): Runnable = patch(
    m = T::class.java.getDeclaredMethod(methodName, *paramTypes),
    hook = PreHook { param ->
        try {
            callback(param)
        } catch (th: Throwable) {
            logger.error(
                "Exception while pre-hooking ${param.method.declaringClass.name}.${param.method.name}",
                th
            )
        }
    }
)

/**
 * Adds a [Hook] to a constructor of a class.
 * @param paramTypes parameters of the constructor. Useful for patching individual overloads
 * @param callback callback for the patch
 * @return the [Runnable] object of the patch
 * @see [XC_MethodHook.afterHookedMethod]
 */
public inline fun <reified T> PatcherAPI.after(
    vararg paramTypes: Class<*>,
    noinline callback: HookCallback<T>
): Runnable = patch(
    m = T::class.java.getDeclaredConstructor(*paramTypes),
    hook = Hook { param ->
        try {
            callback(param)
        } catch (th: Throwable) {
            logger.error(
                "Exception while hooking constructor of ${param.method.declaringClass}",
                th
            )
        }
    }
)

/**
 * Adds a [Hook] to a method of a class.
 * @param methodName name of the method to patch
 * @param paramTypes parameters of the method. Useful for patching individual overloads
 * @param callback callback for the patch
 * @return the [Runnable] object of the patch
 * @see [XC_MethodHook.afterHookedMethod]
 */
public inline fun <reified T> PatcherAPI.after(
    methodName: String,
    vararg paramTypes: Class<*>,
    noinline callback: HookCallback<T>
): Runnable = patch(
    m = T::class.java.getDeclaredMethod(methodName, *paramTypes),
    hook = Hook { param ->
        try {
            callback(param)
        } catch (th: Throwable) {
            logger.error(
                "Exception while hooking ${param.method.declaringClass.name}.${param.method.name}",
                th
            )
        }
    }
)

@Suppress("UNCHECKED_CAST")
public operator fun <T> HookCallback<T>.invoke(param: MethodHookParam) {
    invoke(param.thisObject as T, param)
}
