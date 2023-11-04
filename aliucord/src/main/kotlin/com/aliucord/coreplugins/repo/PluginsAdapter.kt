@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.coreplugins.repo

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.*
import com.aliucord.PluginManager.logger
import com.aliucord.PluginManager.stopPlugin
import com.aliucord.Utils.launchUrl
import com.aliucord.Utils.promptRestart
import com.aliucord.Utils.showToast
import com.aliucord.entities.Plugin
import com.aliucord.fragments.ConfirmDialog
import com.aliucord.utils.ChangelogUtils.FooterAction
import com.aliucord.utils.ChangelogUtils.show
import com.discord.app.AppFragment
import com.discord.widgets.user.usersheet.WidgetUserSheet
import com.lytefast.flexinput.R
import java.io.File

internal class PluginsAdapter(
    private val fragment: AppFragment,
    plugins: MutableCollection<Plugin>
) : RecyclerView.Adapter<PluginsAdapter.ViewHolder>() {
    private val ctx = fragment.requireContext()
    var data: MutableList<Plugin>

    init {
        data = plugins as MutableList<Plugin>
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(this, PluginCard(ctx))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = data[position]
        val (_, _, authors, _, version) = p.manifest
        if (p.name in PluginManager.plugins) {
            holder.card.installButton.visibility = View.GONE
            holder.card.uninstallButton.visibility = View.VISIBLE
        } else {
            holder.card.installButton.visibility = View.VISIBLE
            holder.card.uninstallButton.visibility = View.GONE
        }
        holder.card.descriptionView.text = p.manifest.description
        holder.card.changeLogButton.visibility = if (p.manifest.changelog != "null") View.VISIBLE else View.GONE
        val title = "${p.name} v${version} by ${authors.joinToString()}"
        val spannableTitle = SpannableString(title)
        authors.forEach { (name, id) ->
            if (id < 1) return@forEach
            val i = title.indexOf(name, p.name.length + 2 + version.length + 3)
            spannableTitle.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    WidgetUserSheet.show(id, fragment.parentFragmentManager)
                }
            }, i, i + name.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        holder.card.titleView.text = spannableTitle
    }

    private fun getGithubUrl(plugin: Plugin): String = plugin
        .manifest.updateUrl!!
        .replace("raw.githubusercontent.com", "github.com")
        .substringBefore("/builds")

    private fun onGithubClick(position: Int) = launchUrl(getGithubUrl(data[position]))

    private fun onChangeLogClick(position: Int) {
        val p = data[position]
        val (_, _, _, _, version, _, changelog, changelogMedia) = p.manifest

        if (changelog == null) return

        show(
            context = ctx,
            version = "${p.name} v$version",
            media = changelogMedia,
            body = changelog,
            FooterAction(R.e.ic_account_github_white_24dp, getGithubUrl(p))
        )
    }

    private fun onInstallClick(position: Int) {
        val p = data[position]

        Utils.threadPool.execute {
            if (PluginRepoAPI.installPlugin(p.name, p.manifest.updateUrl)) {
                Utils.mainThread.post {
                    showToast("Successfully installed ${p.name}")
                    notifyItemChanged(position)
                }
            }
        }
    }

    private fun onUninstallClick(position: Int) {
        val p = data[position]
        val dialog: ConfirmDialog = ConfirmDialog()
            .setIsDangerous(true)
            .setTitle("Delete ${p.name}")
            .setDescription("Are you sure you want to delete this plugin? This action cannot be undone.")
        dialog.setOnOkListener {
            val pluginFile = File("${Constants.BASE_PATH}/plugins/${p.__filename}.zip")
            if (pluginFile.exists() && !pluginFile.delete()) {
                logger.errorToast("Failed to delete plugin ${p.name}")
                return@setOnOkListener
            }
            stopPlugin(p.name)
            PluginManager.plugins.remove(p.name)
            notifyItemChanged(position)
            logger.infoToast("Successfully deleted ${p.name}")
            dialog.dismiss()
            if (p.requiresRestart()) promptRestart()
        }
        dialog.show(fragment.parentFragmentManager, "Confirm Plugin Uninstall")
    }

    internal class ViewHolder(
        private val adapter: PluginsAdapter,
        val card: PluginCard
    ) : RecyclerView.ViewHolder(card) {
        init {
            card.apply {
                repoButton.setOnClickListener { adapter.onGithubClick(adapterPosition) }
                changeLogButton.setOnClickListener { adapter.onChangeLogClick(adapterPosition) }
                installButton.setOnClickListener { adapter.onInstallClick(adapterPosition) }
                uninstallButton.setOnClickListener { adapter.onUninstallClick(adapterPosition) }
            }
        }
    }
}
