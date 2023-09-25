/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2022 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.injector

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.*
import android.widget.Toast
import com.discord.BuildConfig
import com.discord.app.AppActivity
import dalvik.system.BaseDexClassLoader
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import org.json.JSONObject
import java.io.File
import java.lang.reflect.Field
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.reflect.KClass

private const val BASE_URL = "https://raw.githubusercontent.com/Aliucord/Aliucord/builds"
private const val DEX_URL = "$BASE_URL/Aliucord.zip"
private const val DISCORD_VERSION = 126021
private const val ALIUCORD_FROM_STORAGE_KEY = "AC_from_storage"
private val BASE_DIRECTORY = Environment.getExternalStorageDirectory().resolve("Aliucord")

fun init() {
    lateinit var unhook: XC_MethodHook.Unhook

    try {
        Logger.d("Hooking AppActivity.onCreate...")
        unhook = XposedBridge.hookMethod(
            AppActivity::class.java.getDeclaredMethod(
                "onCreate",
                Bundle::class.java
            ), object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    init(param.thisObject as AppActivity)
                    unhook.unhook()
                }
            })
    } catch (th: Throwable) {
        Logger.e("Failed to initialize Aliucord", th)
    }
}

private fun error(ctx: Context, msg: String, th: Throwable? = null) {
    Logger.e(msg, th)
    Handler(Looper.getMainLooper()).post { Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show() }
}

private fun init(appActivity: AppActivity) {
    if (!XposedBridge.disableProfileSaver())
        Logger.w("Failed to disable profile saver")

    if (!XposedBridge.disableHiddenApiRestrictions())
        Logger.w("Failed to disable hidden api restrictions")

    if (!pruneArtProfile(appActivity))
        Logger.w("Failed to prune art profile")

    Logger.d("Initializing Aliucord...")

    try {
        val dexFile = File(appActivity.codeCacheDir, "Aliucord.zip")

        if (!useLocalDex(appActivity, dexFile) && !dexFile.exists()) {
            val successRef = AtomicBoolean(true)

            thread {
                try {
                    if (DISCORD_VERSION > BuildConfig.VERSION_CODE) {
                        error(
                            appActivity,
                            "Your base Discord is outdated. Please reinstall using the Installer."
                        )
                        successRef.set(false)
                    } else {
                        downloadLatestAliucordDex(dexFile)
                    }
                } catch (e: Throwable) {
                    error(appActivity, "Failed to install aliucord :(", e)
                    successRef.set(false)
                }
            }.join()

            if (!successRef.get()) return
        }

        Logger.d("Adding Aliucord to the classpath...")
        addDexToClasspath(dexFile, appActivity.classLoader)

        val c = Class.forName("com.aliucord.Main")
        val preInit = c.getDeclaredMethod("preInit", AppActivity::class.java)
        val init = c.getDeclaredMethod("init", AppActivity::class.java)

        Logger.d("Invoking main Aliucord entry point...")
        preInit(null, appActivity)
        init(null, appActivity)
        Logger.d("Finished initializing Aliucord")
    } catch (th: Throwable) {
        error(appActivity, "Failed to initialize Zeetcord", th)
        appActivity.codeCacheDir.resolve("Aliucord.zip").delete()
    }
}

/**
 * Checks if app has permission for storage and if so checks settings and copies local dex to code cache
 */
private fun useLocalDex(appActivity: AppActivity, dexFile: File): Boolean {
    if (appActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) return false

    val settingsFile = BASE_DIRECTORY.resolve("settings/Aliucord.json")
        .takeIf(File::exists) ?: return false

    val useLocalDex = settingsFile.readText().let {
        it.isNotEmpty() && JSONObject(it).let { json ->
            json.has(ALIUCORD_FROM_STORAGE_KEY) && json.getBoolean(ALIUCORD_FROM_STORAGE_KEY)
        }
    }

    if (useLocalDex) {
        BASE_DIRECTORY.resolve("Aliucord.zip").run {
            if (exists()) {
                Logger.d("Loading dex from $absolutePath")
                copyTo(dexFile, true)
                return true
            }
        }
    }

    return false
}

/**
 * Public so it can be manually triggered from Aliucord to update itself
 * outputFile should be new File(context.getCodeCacheDir(), "Aliucord.zip");
 */
private fun downloadLatestAliucordDex(outputFile: File) {
    Logger.d("Downloading Aliucord.zip from $DEX_URL...")
    URL(DEX_URL).openStream().use {
        it.copyTo(outputFile.outputStream())
    }
    Logger.d("Finished downloading Aliucord.zip")
}

@Suppress("DiscouragedPrivateApi") // this private api seems to be stable, thanks to facebook who use it in the facebook app
private fun addDexToClasspath(dex: File, classLoader: ClassLoader) {
    Logger.d("Adding Aliucord to the classpath...")

    // https://android.googlesource.com/platform/libcore/+/58b4e5dbb06579bec9a8fc892012093b6f4fbe20/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java#59
    val pathListField = BaseDexClassLoader::class["pathList"]
    val pathList = pathListField[classLoader]!!
    val addDexPath =
        pathList.javaClass.getDeclaredMethod("addDexPath", String::class.java, File::class.java)
            .apply { isAccessible = true }
    addDexPath(pathList, dex.absolutePath, null)

    Logger.d("Successfully added Aliucord to the classpath")
}

/**
 * Try to prevent method inlining by deleting the usage profile used by AOT compilation
 * https://source.android.com/devices/tech/dalvik/configure#how_art_works
 */
private fun pruneArtProfile(ctx: Context): Boolean {
    Logger.d("Pruning ART usage profile...")

    val profile = File("/data/misc/profiles/cur/0/${ctx.packageName}/primary.prof")

    when {
        !profile.exists() -> return false
        profile.length() > 0 -> {
            try {
                // Delete file contents
                profile.outputStream().close()
            } catch (ignored: Throwable) {
                return false
            }
        }
    }

    return true
}

@Suppress("NOTHING_TO_INLINE")
private inline operator fun KClass<*>.get(name: String): Field {
    return java.getDeclaredField(name).apply { isAccessible = true }
}
