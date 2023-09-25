/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.coreplugins

import android.content.Context
import android.widget.TextView
import com.aliucord.api.CommandsAPI
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.discord.api.message.MessageTypes
import com.discord.databinding.WidgetChatInputAutocompleteItemBinding
import com.discord.models.commands.*
import com.discord.models.message.Message
import com.discord.models.user.CoreUser
import com.discord.stores.*
import com.discord.utilities.view.text.SimpleDraweeSpanTextView
import com.discord.widgets.chat.input.`WidgetChatInput$configureSendListeners$2`
import com.discord.widgets.chat.input.autocomplete.ApplicationCommandAutocompletable
import com.discord.widgets.chat.input.autocomplete.adapter.AutocompleteItemViewHolder
import com.discord.widgets.chat.input.models.ApplicationCommandData
import com.discord.widgets.chat.input.models.ApplicationCommandValue
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.entries.MessageEntry
import com.discord.widgets.chat.list.sheet.WidgetApplicationCommandBottomSheetViewModel

@Suppress("UNCHECKED_CAST")
internal class CommandHandler : Plugin(Manifest("CommandHandler")) {
    override fun load(context: Context) {
        GlobalPatcher.after<BuiltInCommands>("getBuiltInCommands") {
            if (it.result == null) return@after

            val list = it.result as MutableList<ApplicationCommand?>
            val addList = CommandsAPI.commands.values

            if (!list.containsAll(addList)) {
                with(if (list is ArrayList<ApplicationCommand?>) list else ArrayList(list).apply { it.result = this }) {
                    removeAll(addList.toSet())
                    addAll(addList)
                }
            }
        }

        val storeApplicationCommands = StoreApplicationCommands::class.java
        GlobalPatcher.patch(storeApplicationCommands, "getApplications", emptyArray(), Hook {
            val list = it.result.run { if (this == null) return@Hook else this as MutableList<Application?> }
            val acApp = CommandsAPI.getAliucordApplication()
            if (!list.contains(acApp)) {
                with(if (list is ArrayList<Application?>) list else ArrayList(list).apply { it.result = this }) {
                    if (size == 0) add(acApp) else add(size - 1, acApp)
                }
            }
        })

        GlobalPatcher.patch(storeApplicationCommands, "getApplicationMap", emptyArray(), Hook {
            val map = it.result.run { if (this == null) return@Hook else this as MutableMap<Long?, Application?> }
            if (!map.containsKey(CommandsAPI.ALIUCORD_APP_ID)) {
                with(if (map is LinkedHashMap<Long?, Application?>) map else LinkedHashMap(map).apply { it.result = this }) {
                    this[CommandsAPI.ALIUCORD_APP_ID] = CommandsAPI.getAliucordApplication()
                }
            }
        })

        GlobalPatcher.patch(storeApplicationCommands, "handleGuildApplicationsUpdate", arrayOf(List::class.java), PreHook {
            val list = it.result.run { if (this == null) return@PreHook else this as MutableList<Application?> }
            if (!list.contains(CommandsAPI.getAliucordApplication())) {
                with(if (list is ArrayList<Application?>) list else ArrayList(list).apply { it.args[0] = this }) {
                    add(CommandsAPI.getAliucordApplication())
                }
            }
        })

        GlobalPatcher.patch(StoreLocalMessagesHolder::class.java, "messageCacheTryPersist", emptyArray(), InsteadHook.DO_NOTHING)

        // needed to reimplement this to:
        // 1. don't send command result if not needed
        // 2. fully support arguments in built-in subcommands
        // 3. clear input after executing command
        GlobalPatcher.before<`WidgetChatInput$configureSendListeners$2`>(
            "invoke",
            List::class.java,
            ApplicationCommandData::class.java,
            Function1::class.java
        ) { (it, _: Any, data: ApplicationCommandData?) ->
            if (data == null) return@before

            val command = data.applicationCommand.takeUnless { c ->
                c == null || c !is RemoteApplicationCommand || !c.builtIn
            } ?: return@before
            val values = data.values ?: return@before
            val commandArgs = LinkedHashMap<String, Any?>(values.size).apply {
                addValues(values)
                this["__this"] = this@before
                this["__args"] = it.args
            }
            val execute = command.execute ?: return@before

            execute(commandArgs)
            it.result = true
        }

        // Show Plugin name instead of 'Aliucord' in the command list
        val autocompleteItemViewHolder = AutocompleteItemViewHolder::class.java
        val bindingField = autocompleteItemViewHolder.getDeclaredField("binding").apply { isAccessible = true }
        GlobalPatcher.after<AutocompleteItemViewHolder>(
            "bindCommand",
            ApplicationCommandAutocompletable::class.java
        ) { (_, autocompletable: ApplicationCommandAutocompletable) ->
            val cmd = autocompletable.command
                .run { if (this is ApplicationSubCommand) rootCommand else this }
                .apply { if (!builtIn) return@after }

            val plugin = CommandsAPI.commandsAndPlugins[cmd.name] ?: return@after
            val binding = bindingField[this] as WidgetChatInputAutocompleteItemBinding
            binding.f.text = plugin.uppercase()
        }

        GlobalPatcher.before<Message>("isLocalApplicationCommand") {
            val type = type ?: return@before
            if (isLoading && type != MessageTypes.LOCAL_APPLICATION_COMMAND && type != MessageTypes.LOCAL_APPLICATION_COMMAND_SEND_FAILED)
                it.result = true
        }

        // don't mark Aliucord command messages as
        GlobalPatcher.after<WidgetChatListAdapterItemMessage>(
            "processMessageText",
            SimpleDraweeSpanTextView::class.java,
            MessageEntry::class.java
        ) { (_, textView: TextView, messageEntry: MessageEntry) ->
            val message = messageEntry.message ?: return@after
            if (message.isLocal && CoreUser(message.author).id == -1L) {
                if (textView.alpha != 1.0f) textView.alpha = 1.0f
            }
        }

        GlobalPatcher.before<WidgetApplicationCommandBottomSheetViewModel>("requestInteractionData") {
            if (applicationId != -1L) return@before
            val state = CommandsAPI.interactionsStore[interactionId]
            if (state != null) WidgetApplicationCommandBottomSheetViewModel.`access$handleStoreState`(this, state)
            it.result = null
        }
    }

    private fun LinkedHashMap<String, Any?>.addValues(values: List<ApplicationCommandValue>) {
        values.forEach { v ->
            val name = v.name
            val value = v.value
            val options = v.options

            if (value == null && options != null) {
                val optionsMap = linkedMapOf<String, Any?>()
                addValues(options)
                put(name, optionsMap)
            } else put(name, value)
        }
    }
}
