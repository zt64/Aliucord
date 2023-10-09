package com.aliucord

import com.aliucord.Constants.SETTINGS_PATH
import com.aliucord.PluginManager.logger
import com.aliucord.utils.GsonUtils.fromJson
import com.aliucord.utils.GsonUtils.gson
import com.aliucord.utils.GsonUtils.toJson
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.lang.reflect.Type

/** Utility class to store and retrieve preferences  */
@Suppress("unused")
public class SettingsUtilsJSON(plugin: String) {
    private val settingsFile = File("$SETTINGS_PATH/$plugin.json")
    private val cache: MutableMap<String, Any?> = hashMapOf()
    private val settings: JSONObject by lazy {
        if (settingsFile.exists()) {
            val read = settingsFile.readText()
            if (read.isNotEmpty()) return@lazy JSONObject(read)
        }
        JSONObject()
    }

    init {
        val dir = File(SETTINGS_PATH)
        if (!dir.exists() && !dir.mkdir()) throw RuntimeException("Failed to create settings dir")
    }

    private fun writeData() {
        if (settings.length() == 0) return

        try {
            settingsFile.writeText(settings.toString(4))
        } catch (e: Throwable) {
            logger.error("Failed to save settings", e)
        }
    }

    /**
     * Resets All Settings
     * @return true if successful, else false
     */
    public fun resetFile(): Boolean = settingsFile.delete()

    /**
     * Toggles Boolean and returns it
     * @param key Key of the value
     * @param defVal Default Value if setting doesn't exist
     * @return Toggled boolean
     */
    public fun toggleBool(key: String, defVal: Boolean): Boolean {
        return (!getBool(key, !defVal)).also {
            setBool(key, it)
        }
    }

    /**
     * Removes Item from settings
     * @param key Key of the value
     * @return True if removed, else false
     */
    @Synchronized
    public fun remove(key: String): Boolean {
        val bool = settings.remove(key) != null
        writeData()
        return bool
    }

    /**
     * Gets All Keys from settings
     * @return List of all keys
     */
    public fun getAllKeys(): List<String> {
        val iterator = settings.keys()
        val copy = arrayListOf<String>()
        while (iterator.hasNext()) copy += iterator.next()
        return copy
    }

    /**
     * Check if Key exists in settings
     * @param key Key of the value
     * @return True if found, else false
     */
    public fun exists(key: String): Boolean = settings.has(key)

    /**
     * Get a boolean from the preferences
     * @param key Key of the value
     * @param defValue Default value
     * @return Value if found, else the defValue
     */
    public fun getBool(key: String, defValue: Boolean): Boolean {
        return if (settings.has(key)) settings.getBoolean(key) else defValue
    }

    /**
     * Set a boolean item
     * @param key Key of the item
     * @param value Value
     */
    public fun setBool(key: String, value: Boolean): Unit = putObject(key, value)

    /**
     * Get an int from the preferences
     * @param key Key of the value
     * @param defValue Default value
     * @return Value if found, else the defValue
     */
    public fun getInt(key: String, defValue: Int): Int {
        return if (settings.has(key)) settings.getInt(key) else defValue
    }

    @Synchronized
    private fun putObject(key: String, value: Any?) {
        settings.put(key, value)
        writeData()
    }

    /**
     * Set an int item
     * @param key Key of the item
     * @param value Value
     */
    public fun setInt(key: String, value: Int): Unit = putObject(key, value)

    /**
     * Get a float from the preferences
     * @param key Key of the value
     * @param defValue Default value
     * @return Value if found, else the defValue
     */
    public fun getFloat(key: String, defValue: Float): Float = if (settings.has(key)) {
        settings.getDouble(key).toFloat()
    } else {
        defValue
    }

    /**
     * Set a float item
     * @param key Key of the item
     * @param value Value
     */
    public fun setFloat(key: String, value: Float): Unit = putObject(key, value)

    /**
     * Get a long from the preferences
     * @param key Key of the value
     * @param defValue Default value
     * @return Value if found, else the defValue
     */
    public fun getLong(key: String, defValue: Long): Long {
        return if (settings.has(key)) settings.getLong(key) else defValue
    }

    /**
     * Set a long item
     * @param key Key of the item
     * @param value Value
     */
    public fun setLong(key: String, value: Long): Unit = putObject(key, value)

    /**
     * Get a [String] from the preferences
     * @param key Key of the value
     * @param defValue Default value
     * @return Value if found, else the defValue
     */
    public fun getString(key: String, defValue: String?): String? {
        return if (settings.has(key)) settings.getString(key) else defValue
    }

    /**
     * Set a [String] item
     * @param key Key of the item
     * @param value Value
     */
    public fun setString(key: String, value: String?): Unit = putObject(key, value)

    /**
     * Get a [JSONObject] item
     * @param key Key of the item
     * @param defValue Default value
     * @return Value if found, else the defValue
     */
    public fun getJSONObject(key: String, defValue: JSONObject?): JSONObject? {
        return if (settings.has(key)) settings.getJSONObject(key) else defValue
    }

    /**
     * Set a [JSONObject] item
     * @param key Key of the item
     * @param value Value
     */
    public fun setJSONObject(key: String, value: JSONObject): Unit = putObject(key, value)

    /**
     * Get an [Object] from the preferences
     * @param key Key of the value
     * @param defValue Default value
     * @return Value if found, else the defValue
     */
    public fun <T> getObject(key: String, defValue: T): T {
        return getObject(key, defValue, defValue!!::class.java)
    }

    /**
     * Get an [Object] from the preferences
     * @param key Key of the value
     * @param defValue Default value
     * @param type Type of the object
     * @return Value if found, else the defValue
     */
    @Suppress("UNCHECKED_CAST")
    public fun <T> getObject(key: String, defValue: T, type: Type?): T {
        val cached = cache[key]
        if (cached != null) {
            runCatching { return cached as T }
        }
        val t: T? = if (settings.has(key)) gson.fromJson(settings.getString(key), type) else null
        return t ?: defValue
    }

    /**
     * Set an [Object] item
     * @param key Key of the item
     * @param value Value
     */
    public fun setObject(key: String, value: Any?) {
        cache[key] = value

        val stringJson = gson.toJson(value)

        putObject(
            key,
            if (stringJson.startsWith("{")) JSONObject(stringJson) else JSONArray(stringJson)
        )
    }
}
