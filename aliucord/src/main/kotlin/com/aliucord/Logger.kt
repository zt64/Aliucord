/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord

import android.util.Log
import com.aliucord.Utils.showToast
import com.discord.app.AppLog

/**
 * Logger that will log to both logcat and Discord's debug log
 * @param module Name of the module
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
public class Logger(public var module: String = "Zeetcord") {
    private fun format(msg: String) = "[$module] $msg"

    /**
     * Logs a [Log.VERBOSE] message
     * @param msg Message to log
     */
    public fun verbose(msg: String): Unit = AppLog.g.v(format(msg), null)

    /**
     * Logs a [Log.DEBUG] message
     * @param msg Message to log
     */
    public fun debug(msg: String): Unit = AppLog.g.d(format(msg), null)

    /**
     * Logs a [Log.INFO] message and prints the stacktrace of the exception
     * @param msg Message to log
     * @param throwable Exception to log
     */
    @JvmOverloads
    public fun info(msg: String, throwable: Throwable? = null): Unit = AppLog.g.i(format(msg), throwable)

    /**
     * Logs a [Log.INFO] message, and shows it to the user as a toast
     * @param msg Message to log
     */
    public fun infoToast(msg: String) {
        showToast(msg)
        info(msg)
    }

    /**
     * Logs a [Log.WARN] message and prints the stacktrace of the exception
     * @param msg Message to log
     * @param throwable Exception to log
     */
    @JvmOverloads
    public fun warn(msg: String, throwable: Throwable? = null): Unit = AppLog.g.w(format(msg), throwable)

    /**
     * Logs an exception
     * @param throwable Exception to log
     */
    public fun error(throwable: Throwable?): Unit = error("Error:", throwable)

    /**
     * Logs a [Log.ERROR] message and prints the stacktrace of the exception
     * @param msg Message to log
     * @param throwable Exception to log
     */
    public fun error(msg: String, throwable: Throwable? = null): Unit = AppLog.g.e(format(msg), throwable, null)

    /**
     * Logs an exception and shows the user a toast saying "Sorry, something went wrong. Please try again."
     * @param throwable Exception to log
     */
    public fun errorToast(throwable: Throwable?) {
        errorToast("Sorry, something went wrong. Please try again.", throwable)
    }

    /**
     * Logs a [Log.ERROR] message, shows it to the user as a toast and prints the stacktrace of the exception
     * @param msg Message to log
     * @param throwable Exception to log
     */
    @JvmOverloads
    public fun errorToast(msg: String, throwable: Throwable? = null) {
        showToast(msg, true)
        error(msg, throwable)
    }
}
