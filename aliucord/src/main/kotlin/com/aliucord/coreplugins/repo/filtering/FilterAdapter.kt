@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.coreplugins.repo.filtering

import android.R
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils
import com.aliucord.coreplugins.repo.PluginRepoAPI
import com.aliucord.coreplugins.repo.PluginsPage
import com.discord.app.AppFragment
import com.discord.utilities.color.ColorCompat
import com.discord.views.CheckedSetting

internal class FilterAdapter(fragment: AppFragment) : RecyclerView.Adapter<FilterAdapter.ViewHolder?>() {
    var fragment: AppFragment
    var ctx: Context
    private var filters = mutableListOf("Author", "Sort By", "Show Installed Plugins")
    var developers = mutableListOf<Developer>()
    var sortOptions = listOf(
        SortOption("None", ""),
        SortOption("Last Updated", "timestamp"),
        SortOption("Last Added", "ID"),
        SortOption("Most Starred", "repo_stars")
    )
    private var viewCount = -2

    init {
        this.fragment = fragment
        ctx = fragment.requireContext()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        viewCount += 1
        return ViewHolder(AdapterItem(ctx, viewCount))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.type = FilterType.values()[position]
        when (holder.item.type) {
            FilterType.AUTHOR -> Utils.threadPool.execute {
                developers = PluginRepoAPI.developers.toMutableList()
                developers.add(0, Developer("None", 0))
                Utils.mainThread.post {
                    (holder.item.setting as Spinner).setAdapter(
                        ArrayAdapter(
                            ctx,
                            R.layout.simple_spinner_dropdown_item,
                            developers
                        )
                    )
                }
            }

            FilterType.SORTED -> (holder.item.setting as Spinner).setAdapter(
                ArrayAdapter(
                    ctx,
                    R.layout.simple_spinner_dropdown_item,
                    sortOptions
                )
            )

            else -> {}
        }
        holder.item.textView.text = filters[position]
    }

    override fun getItemCount() = filters.size

    enum class FilterType {
        AUTHOR,
        SORTED,
        INSTALLED
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item: AdapterItem

        init {
            item = itemView as AdapterItem
            when (item.viewType) {
                -1, 0 -> {
                    val spinner = item.setting as Spinner
                    spinner.setSelection(0, false)
                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long
                        ) {
                            var refresh = true
                            (parent.getChildAt(0) as TextView).setTextColor(
                                ColorCompat.getColor(
                                    ctx,
                                    com.lytefast.flexinput.R.c.primary_dark_200
                                )
                            )
                            when (item.type) {
                                FilterType.SORTED -> when {
                                    sortOptions[position].toString() == "None" -> {
                                        refresh = null != PluginRepoAPI.filters.remove("sort_by")
                                    }
                                    else -> {
                                        PluginRepoAPI.filters["sort_by"] = sortOptions[position].optionValue
                                    }
                                }

                                FilterType.AUTHOR -> if (developers[position].ID != 0) PluginRepoAPI.filters["author"] =
                                    developers[position].ID.toString() else refresh =
                                    null != PluginRepoAPI.filters.remove("author")

                                FilterType.INSTALLED -> {}
                                null -> TODO()
                            }
                            if (refresh) (fragment as PluginsPage).makeSearch()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }

                1 -> (item.setting as CheckedSetting).setOnCheckedListener { aBoolean ->
                    PluginRepoAPI.localFilters["showInstalledPlugins"] = aBoolean
                    (fragment as PluginsPage).makeSearch()
                }
            }
        }
    }
}
