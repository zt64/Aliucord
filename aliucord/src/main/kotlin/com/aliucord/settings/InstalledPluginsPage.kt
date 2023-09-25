/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.settings

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.text.*
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.*
import com.aliucord.Constants
import com.aliucord.PluginManager
import com.aliucord.PluginManager.isPluginEnabled
import com.aliucord.PluginManager.stopPlugin
import com.aliucord.PluginManager.togglePlugin
import com.aliucord.Utils.launchFileExplorer
import com.aliucord.Utils.launchUrl
import com.aliucord.Utils.openPage
import com.aliucord.Utils.openPageWithProxy
import com.aliucord.Utils.promptRestart
import com.aliucord.Utils.showToast
import com.aliucord.entities.Plugin
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.ChangelogUtils.FooterAction
import com.aliucord.utils.ChangelogUtils.show
import com.aliucord.utils.DimenUtils.defaultPadding
import com.aliucord.utils.ReflectUtils
import com.aliucord.views.*
import com.aliucord.widgets.PluginCard
import com.discord.app.AppFragment
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.lytefast.flexinput.R
import java.io.File

@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
internal class InstalledPluginsPage : SettingsPage() {
    private class Adapter(
        private val fragment: AppFragment,
        plugins: Collection<Plugin>?
    ) : RecyclerView.Adapter<Adapter.ViewHolder>(), Filterable {
        private class ViewHolder(private val adapter: Adapter, val card: PluginCard) :
            RecyclerView.ViewHolder(card) {
            init {
                card.repoButton.setOnClickListener { adapter.onGithubClick(adapterPosition) }
                card.changeLogButton.setOnClickListener { adapter.onChangeLogClick(adapterPosition) }
                card.uninstallButton.setOnClickListener { adapter.onUninstallClick(adapterPosition) }
                card.switchHeader.setOnCheckedListener(::onToggleClick)
                card.settingsButton.setOnClickListener { adapter.onSettingsClick(adapterPosition) }
            }

            fun onToggleClick(checked: Boolean) {
                adapter.onToggleClick(this, checked, getAdapterPosition())
            }
        }

        private val ctx = fragment.requireContext()
        private val originalData = plugins!!.sortedBy { it.name }.toMutableList()
        private var data = originalData

        override fun getItemCount() = data.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(this, PluginCard(ctx))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val p = data[position]
            val manifest = p.manifest
            val enabled = isPluginEnabled(p.name)

            holder.card.apply {
                switchHeader.isChecked = enabled
                descriptionView.text = p.manifest.description
                settingsButton.visibility = if (p.settingsTab != null) View.VISIBLE else View.GONE
                settingsButton.isEnabled = enabled
                changeLogButton.visibility =
                    if (p.manifest.changelog != null) View.VISIBLE else View.GONE

                val title = "${p.name} v${manifest.version} by ${manifest.authors.joinToString()}"
                val spannableTitle = getSpannableString(title, manifest, p)
                titleView.text = spannableTitle
            }
        }

        private fun getSpannableString(
            title: String,
            manifest: Plugin.Manifest,
            p: Plugin
        ): SpannableString {
            val spannableTitle = SpannableString(title)

            manifest.authors.forEach { (name, id) ->
                if (id < 1) return@forEach

                val i = title.indexOf(name, p.name.length + 2 + manifest.version.length + 3)

                spannableTitle.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        WidgetUserSheet.show(id, fragment.parentFragmentManager)
                    }
                }, i, i + name.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            return spannableTitle
        }

        private val filter: Filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence) = FilterResults().apply {
                values = if (constraint.isBlank()) originalData else {
                    val search = constraint.toString().lowercase().trim { it <= ' ' }
                    originalData.filter { p ->
                        if (search in p.name.lowercase()) return@filter true

                        val (_, _, authors, description) = p.manifest

                        if (search in description.lowercase()) return@filter true

                        if (
                            authors.any { (name) -> search in name.lowercase() }
                        ) return@filter true

                        false
                    }
                }
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                val res = results.values as ArrayList<Plugin>
                val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize() = itemCount
                    override fun getNewListSize() = res.size

                    override fun areItemsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean = data[oldItemPosition].name == res[newItemPosition].name

                    override fun areContentsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean = true
                }, false)
                data = res.toMutableList()
                diff.dispatchUpdatesTo(this@Adapter)
            }
        }

        override fun getFilter(): Filter = filter

        private fun getGithubUrl(plugin: Plugin): String = plugin
            .manifest.updateUrl!!
            .replace("raw.githubusercontent.com", "github.com")
            .let { "/builds.*".toRegex().replace(it, "") }

        fun onGithubClick(position: Int) = launchUrl(getGithubUrl(data[position]))

        fun onChangeLogClick(position: Int) {
            val p = data[position]
            val (_, _, _, _, version, _, changelog, changelogMedia) = p.manifest
            if (changelog != null) {
                val url = getGithubUrl(p)
                show(
                    ctx,
                    "${p.name} v$version",
                    changelogMedia,
                    changelog,
                    FooterAction(R.e.ic_account_github_white_24dp, url)
                )
            }
        }

        fun onSettingsClick(position: Int) {
            val p = data[position]
            val tab = p.settingsTab ?: return

            when {
                tab.type == Plugin.SettingsTab.Type.PAGE && tab.page != null -> {
                    val page = ReflectUtils.invokeConstructorWithArgs(tab.page!!, *tab.args)!!
                    openPageWithProxy(ctx, page)
                }

                tab.type == Plugin.SettingsTab.Type.BOTTOM_SHEET && tab.bottomSheet != null -> {
                    val sheet = ReflectUtils.invokeConstructorWithArgs(tab.bottomSheet!!, *tab.args)
                    sheet.show(fragment.parentFragmentManager, "${p.name}Settings")
                }
            }
        }

        fun onToggleClick(holder: ViewHolder, state: Boolean, position: Int) {
            val p = data[position]
            togglePlugin(p.name)
            holder.card.settingsButton.setEnabled(state)
            if (p.requiresRestart()) promptRestart()
        }

        fun onUninstallClick(position: Int) {
            val p = data[position]
            val dialog = ConfirmDialog()
                .setIsDangerous(true)
                .setTitle("Delete ${p.name}")
                .setDescription("Are you sure you want to delete this plugin? This action cannot be undone.")

            dialog.setOnOkListener {
                val pluginFile = File("${Constants.BASE_PATH}/plugins/${p.__filename}.zip")
                if (pluginFile.exists() && !pluginFile.delete()) {
                    PluginManager.logger.errorToast("Failed to delete plugin ${p.name}")
                    return@setOnOkListener
                }
                stopPlugin(p.name)
                PluginManager.plugins.remove(p.name)
                PluginManager.logger.infoToast("Successfully deleted ${p.name}")
                dialog.dismiss()
                data.removeAt(position)
                if (originalData !== data) originalData.remove(p)
                notifyItemRemoved(position)
                if (p.requiresRestart()) promptRestart()
            }

            dialog.show(fragment.getParentFragmentManager(), "Confirm Plugin Uninstall")
        }
    }

    @Suppress("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        setActionBarTitle("Plugins")
        setActionBarSubtitle("${PluginManager.plugins.size} Installed")
        removeScrollView()

        val context = view.context
        val padding = defaultPadding
        addHeaderButton("Open Plugins Folder", R.e.ic_open_in_new_white_24dp) {
            val dir = File(Constants.PLUGINS_PATH)
            if (!dir.exists() && !dir.mkdir()) {
                showToast("Failed to create plugins directory!", true)
                return@addHeaderButton true
            }
            launchFileExplorer(dir)
            true
        }
        if (PluginManager.failedToLoad.isNotEmpty()) {
            val failedPluginsView = Button(context)
            failedPluginsView.text = "Plugin Errors"
            failedPluginsView.setOnClickListener {
                openPage(context, FailedPluginsPage::class.java)
            }
            addView(failedPluginsView)
            addView(Divider(context))
        }
        val input = TextInput(context)
        input.setHint(context.getString(R.h.search))
        val recyclerView = RecyclerView(context)
        recyclerView.setLayoutManager(LinearLayoutManager(context, RecyclerView.VERTICAL, false))
        val adapter = Adapter(this, PluginManager.plugins.values)
        recyclerView.setAdapter(adapter)
        val shape = ShapeDrawable(RectShape()).apply {
            setTint(Color.TRANSPARENT)
            setIntrinsicHeight(padding)
        }

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        decoration.setDrawable(shape)
        recyclerView.addItemDecoration(decoration)
        recyclerView.setPadding(0, padding, 0, 0)
        addView(input)
        addView(recyclerView)

        input.editText.apply {
            maxLines = 1
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) = adapter.filter.filter(s)
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            })
        }
    }
}
