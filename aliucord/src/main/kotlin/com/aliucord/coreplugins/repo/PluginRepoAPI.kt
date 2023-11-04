package com.aliucord.coreplugins.repo

import com.aliucord.*
import com.aliucord.coreplugins.repo.filtering.Developer
import com.aliucord.entities.Plugin
import com.aliucord.utils.GsonUtils
import com.aliucord.utils.GsonUtils.fromJson
import com.google.gson.reflect.TypeToken
import org.json.*
import java.io.File
import java.io.IOException

internal object PluginRepoAPI {
    private const val API_URL = "https://mantikralligi1.pythonanywhere.com"
    var localFilters = hashMapOf<String, Any>()
    var filters: MutableMap<String?, String?> = mutableMapOf()
    var logger = Logger("PluginRepoAPI")
    val plugins: List<Plugin>
        get() = getPlugins("")

    private fun getPlugins(query: String?): List<Plugin> = getPlugins(query, 0)

    fun getPlugins(query: String?, index: Int): List<Plugin> {
        val plugins = arrayListOf<Plugin>()
        try {
            val filter = JSONObject(filters.toMap()).apply {
                put("index", index)
                put("query", query)
            }
            val pluginarray = JSONArray(Http.simplePost("$API_URL/getPlugins", filter.toString()))
            logger.info(pluginarray.toString())
            repeat(pluginarray.length()) { i ->
                val plugin = pluginarray[i] as JSONObject
                val pluginobj = getPluginFromJson(plugin)

                if (
                    localFilters.getOrDefault(
                        "showInstalledPlugins",
                        true
                    ) as Boolean || pluginobj.name !in PluginManager.plugins
                ) {
                    plugins += getPluginFromJson(plugin)
                }
            }
        } catch (e: Exception) {
            Logger("PluginRepo").error(e)
        }
        return plugins
    }

    fun checkNewPlugins(): Boolean = try {
        val pluginID = Http.simpleGet("$API_URL/getLastPlugin").toInt()
        val lastID = PluginRepo.settingsAPI.getInt("lastPluginID", 0)
        when {
            lastID == 0 -> {
                PluginRepo.settingsAPI.setInt("lastPluginID", pluginID)
                false
            }

            lastID < pluginID -> {
                PluginRepo.settingsAPI.setInt("lastPluginID", pluginID)
                true
            }

            else -> false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    private fun getPluginFromJson(json: JSONObject): Plugin {
        val manifest = Plugin.Manifest(json.getString("plugin_name")).copy(
            version = json.getString("version"),
            description = json.getString("description"),
            authors = GsonUtils.gson.fromJson(
                json.getString("author"),
                Array<Plugin.Manifest.Author>::class.java
            ),
            changelog = json.getString("changelog"),
            updateUrl = json.getString("download_link") // I know this is not updateurl
        )

        return object : Plugin(manifest) {}
    }

    fun installPlugin(pluginName: String, url: String?): Boolean {
        return try {
            // copied from PluginFile.kt
            val response = Http.Request(url).execute()
            val pluginFile = File(Constants.PLUGINS_PATH, "$pluginName.zip")
            response.saveToFile(pluginFile)
            PluginManager.loadPlugin(Utils.appContext, pluginFile)
            PluginManager.startPlugin(pluginName)
            true
        } catch (e: IOException) {
            logger.error(e)
            false
        }
    }

    fun deletePlugin(plugin: String): Boolean {
        val success = File(Constants.PLUGINS_PATH, "$plugin.zip").delete()
        PluginManager.stopPlugin(plugin)
        PluginManager.unloadPlugin(plugin)
        return success
    }

    private val developersType = TypeToken.getParameterized(
        ArrayList::class.java, Developer::class.java
    ).type

    val developers: List<Developer>
        get() = try {
            GsonUtils.gson.fromJson(Http.simpleGet("$API_URL/getDevelopers"), developersType)
        } catch (e: Exception) {
            emptyList()
        }
}
