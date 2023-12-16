/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.api

import com.aliucord.SettingsUtilsJSON
import java.lang.reflect.Type

@Suppress("unused", "MemberVisibilityCanBePrivate")
public class SettingsAPI internal constructor(plugin: String) {
    private var settings: SettingsUtilsJSON
    private val pluginName: String

    /**
     * Creates a SettingsAPI for the specified plugin
     */
    init {
        settings = SettingsUtilsJSON(plugin)
        pluginName = plugin
    }

    /**
     * Resets All Settings
     *
     * @return true if successful, else false
     */
    public fun resetSettings(): Boolean {
        val isSuccessful = settings.resetFile()
        settings = SettingsUtilsJSON(pluginName)
        return isSuccessful
    }

    /**
     * Removes Item from settings
     *
     * @param key Key of the value
     * @return True if removed, else false
     */
    public fun remove(key: String): Boolean = settings.remove(key)

    /**
     * All Keys from settings
     * @return List of all keys
     */
    public val allKeys: List<String>
        get() = settings.getAllKeys()

    /**
     * Toggles Boolean and returns it
     *
     * @param key Key of the value
     * @param defValue Default Value if setting doesn't exist
     * @return Toggled boolean
     */
    public fun toggleBool(key: String, defValue: Boolean): Boolean {
        return settings.toggleBool(key, defValue)
    }

    /**
     * Check if Key exists in settings
     *
     * @param key Key of the value
     * @return True if found, else false
     */
    public fun exists(key: String): Boolean = settings.exists(key)

    /**
     * Reads a [Boolean] from the settings.
     * @param key Key of the setting.
     * @param defValue Default value of the setting.
     * @return Stored value, or default value if it doesn't exist.
     */
    public fun getBool(key: String, defValue: Boolean): Boolean = settings.getBool(key, defValue)

    /**
     * Writes a [Boolean] to the settings.
     * @param key Key of the setting.
     * @param val Value of the setting.
     */
    public fun setBool(key: String, `val`: Boolean): Unit = settings.setBool(key, `val`)

    /**
     * Gets an [Int] stored in the settings.
     * @param key Key of the setting.
     * @param defValue Default value of the setting.
     * @return Stored value, or default value if it doesn't exist.
     */
    public fun getInt(key: String, defValue: Int): Int = settings.getInt(key, defValue)

    /**
     * Writes an [Int] to the settings.
     * @param key Key of the setting.
     * @param val Value of the setting.
     */
    public fun setInt(key: String, `val`: Int): Unit = settings.setInt(key, `val`)

    /**
     * Gets a [Float] stored in the settings.
     * @param key Key of the setting.
     * @param defValue Default value of the setting.
     * @return Stored value, or default value if it doesn't exist.
     */
    public  fun getFloat(key: String, defValue: Float): Float = settings.getFloat(key, defValue)

    /**
     * Writes a [Float] to the settings.
     * @param key Key of the setting.
     * @param val Value of the setting.
     */
    public fun setFloat(key: String, `val`: Float): Unit = settings.setFloat(key, `val`)

    /**
     * Gets a [Long] stored in the settings.
     * @param key Key of the setting.
     * @param defValue Default value of the setting.
     * @return Stored value, or default value if it doesn't exist.
     */
    public fun getLong(key: String, defValue: Long): Long = settings.getLong(key, defValue)

    /**
     * Writes a [Long] to the settings.
     * @param key Key of the setting.
     * @param val Value of the setting.
     */
    public fun setLong(key: String, `val`: Long): Unit = settings.setLong(key, `val`)

    /**
     * Gets a [String] stored in the settings.
     * @param key Key of the setting.
     * @param defValue Default value of the setting.
     * @return Stored value, or default value if it doesn't exist.
     */
    public fun getString(key: String, defValue: String?): String? = settings.getString(key, defValue)

    /**
     * Writes a [String] to the settings.
     * @param key Key of the setting.
     * @param val Value of the setting.
     */
    public fun setString(key: String, `val`: String?): Unit = settings.setString(key, `val`)

    /**
     * Gets an [Object] stored in the settings.
     * @param key Key of the setting.
     * @param defValue Default value of the setting.
     * @return Stored value, or default value if it doesn't exist.
     */
    public fun <T> getObject(key: String, defValue: T): T = settings.getObject(key, defValue)

    /**
     * Gets an [Object] stored in the settings.
     * @param key Key of the setting.
     * @param defValue Default value of the setting.
     * @param type [Object] representing the data type.
     * @return Stored value, or default value if it doesn't exist.
     */
    public fun <T> getObject(key: String, defValue: T, type: Type): T = settings.getObject(key, defValue, type)

    /**
     * Writes an [Object] to the settings.
     * @param key Key of the setting.
     * @param val Value of the setting.
     */
    public fun setObject(key: String, `val`: Any?): Unit = settings.setObject(key, `val`)

    /**
     * Get a value of an unknown type
     * @param key Key of the item
     */
    public fun getUnknown(key: String, defValue: Any): Any? = when (defValue) {
        is String -> getString(key, defValue)
        is Boolean -> getBool(key, defValue)
        is Long -> getLong(key, defValue)
        is Float -> getFloat(key, defValue)
        is Int -> getInt(key, defValue)
        else -> getObject(key, defValue)
    }

    /**
     * Set a value of an unknown type
     * @param key Key of the item
     * @param value Value of the item
     */
    public fun setUnknown(key: String, value: Any?): Unit = when (value) {
        is String -> setString(key, value)
        is Boolean -> setBool(key, value)
        is Long -> setLong(key, value)
        is Float -> setFloat(key, value)
        is Int -> setInt(key, value)
        else -> setObject(key, value)
    }
}
