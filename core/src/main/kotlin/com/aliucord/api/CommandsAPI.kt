/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.api

import android.os.Build
import com.aliucord.*
import com.aliucord.Utils.buildClyde
import com.aliucord.Utils.createCommandOption
import com.aliucord.api.ButtonsAPI.ButtonData
import com.aliucord.api.ButtonsAPI.addButton
import com.aliucord.entities.CommandContext
import com.aliucord.utils.ReflectUtils.setField
import com.aliucord.wrappers.ChannelWrapper
import com.discord.api.commands.ApplicationCommandData
import com.discord.api.commands.ApplicationCommandType
import com.discord.api.message.MessageFlags
import com.discord.api.message.MessageTypes
import com.discord.api.message.embed.MessageEmbed
import com.discord.models.commands.*
import com.discord.models.domain.NonceGenerator
import com.discord.models.message.Message
import com.discord.models.user.User
import com.discord.stores.StoreApplicationInteractions
import com.discord.stores.StoreMessages
import com.discord.stores.StoreStream
import com.discord.utilities.SnowflakeUtils
import com.discord.utilities.attachments.AttachmentUtilsKt
import com.discord.utilities.message.LocalMessageCreatorsKt
import com.discord.utilities.time.ClockFactory
import com.discord.utilities.user.UserUtils
import com.discord.widgets.chat.MessageContent
import com.discord.widgets.chat.input.ChatInputViewModel
import com.discord.widgets.chat.input.WidgetChatInput
import com.discord.widgets.chat.input.`WidgetChatInput$configureSendListeners$2`
import com.discord.widgets.chat.list.sheet.WidgetApplicationCommandBottomSheetViewModel
import com.lytefast.flexinput.R

@Suppress("unused")
class CommandsAPI internal constructor(
    /** Name of the plugin associated with this CommandsAPI  */
    val pluginName: String
) {
    /** Command List of the plugin associated with this CommandsAPI  */
    val pluginCommands: MutableList<String> = ArrayList()

    /**
     * Registers a slash command.
     *
     * @param name        Name of the command.
     * @param description Description of the command.
     * @param options     Arguments for the command. see [ApplicationCommandOption]
     * @param execute     Callback for the command.
     */
    fun registerCommand(
        name: String,
        description: String,
        options: List<ApplicationCommandOption>,
        execute: (CommandContext) -> CommandResult
    ) {
        registerCommand(pluginName, name, description, options, execute)
        commandsAndPlugins[name] = pluginName
        pluginCommands.add(name)
    }

    /**
     * Registers a slash command.
     *
     * @param name        Name of the command.
     * @param description Description of the command.
     * @param option      Argument for the command. see [ApplicationCommandOption]
     * @param execute     Callback for the command.
     */
    fun registerCommand(
        name: String,
        description: String,
        option: ApplicationCommandOption,
        execute: (CommandContext) -> CommandResult
    ) {
        registerCommand(name, description, listOf(option), execute)
    }

    /**
     * Registers a slash command.
     *
     * @param name        Name of the command.
     * @param description Description of the command.
     * @param execute     Callback for the command.
     */
    fun registerCommand(
        name: String,
        description: String,
        execute: (CommandContext) -> CommandResult
    ) {
        registerCommand(name, description, emptyList<ApplicationCommandOption>(), execute)
    }

    /**
     * Unregisters a command.
     *
     * @param name Command to unregister.
     */
    fun unregisterCommand(name: String) {
        CommandsAPI.unregisterCommand(name)
        commandsAndPlugins.remove(name)
        pluginCommands.remove(name)
    }

    /**
     * Unregisters all commands
     */
    fun unregisterAll() {
        pluginCommands.forEach { name ->
            unregisterCommand(name)
            commandsAndPlugins.remove(name)
        }
        pluginCommands.clear()
    }

    /** Command result  */
    class CommandResult {
        /** The message content  */
        var content: String?

        /** The embeds  */
        var embeds: List<MessageEmbed>?

        /** Whether the result should be sent visible for everyone  */
        var send: Boolean

        /** The username of the pseudo clyde associated with this CommandResult  */
        var username: String? = null

        /** The avatar url of the pseudo clyde associated with this CommandResult  */
        var avatarUrl: String? = null

        /** Button components that will appear on the response message  */
        var buttons: List<ButtonData>? = null

        @JvmOverloads
        constructor(
            content: String? = null,
            embeds: List<MessageEmbed>? = null,
            send: Boolean = true
        ) {
            this.content = content
            this.embeds = embeds
            this.send = send
        }

        /**
         * @param content  Output message content
         * @param embeds   Embeds to include in the command output. Requires `send` to be false.
         * @param send     Whether to send the message or not. If false, messages will appear locally, otherwise they'll be sent to the current channel.
         * @param username Username for Clyde's customization. Requires `send` to be false.
         */
        constructor(
            content: String?,
            embeds: List<MessageEmbed>?,
            send: Boolean,
            username: String?
        ) {
            this.content = content
            this.embeds = embeds
            this.username = username
            this.send = send
        }

        /**
         * @param content   Output message content
         * @param embeds    Embeds to include in the command output. Requires `send` to be false.
         * @param send      Whether to send the message or not. If false, messages will appear locally, otherwise they'll be sent to the current channel.
         * @param username  Username for Clyde. Requires `send` to be false.
         * @param avatarUrl Avatar URL for Clyde, must be a direct link, not a redirect. Requires `send` to be false.
         */
        constructor(
            content: String?,
            embeds: List<MessageEmbed>?,
            send: Boolean,
            username: String?,
            avatarUrl: String?
        ) {
            this.content = content
            this.embeds = embeds
            this.username = username
            this.avatarUrl = avatarUrl
            this.send = send
        }

        /**
         * @param content   Output message content
         * @param embeds    Embeds to include in the command output. Requires `send` to be false.
         * @param send      Whether to send the message or not. If false, messages will appear locally, otherwise they'll be sent to the current channel.
         * @param username  Username for Clyde. Requires `send` to be false.
         * @param avatarUrl Avatar URL for Clyde, must be a direct link, not a redirect. Requires `send` to be false.
         * @param buttons   Button components that will appear on the response message
         */
        constructor(
            content: String?,
            embeds: List<MessageEmbed>?,
            send: Boolean,
            username: String?,
            avatarUrl: String?,
            buttons: List<ButtonData>?
        ) {
            this.content = content
            this.embeds = embeds
            this.username = username
            this.avatarUrl = avatarUrl
            this.send = send
            this.buttons = buttons
        }
    }

    companion object {
        /** ID of the Aliucord Application  */
        val ALIUCORD_APP_ID = generateId()
        const val DONT_SEND_RESULT = "{ALIUCORD_COMMAND}"
        private val logger = Logger("CommandsAPI")
        private val aliucordApplication =
            Application(ALIUCORD_APP_ID, "Aliucord", null, R.e.ic_slash_command_24dp, 0, null, true)

        /** List of all registered commands  */
        var commands: MutableMap<String, RemoteApplicationCommand> = HashMap()

        /** Mapping of all registered commands to the plugin that registered them  */
        var commandsAndPlugins: MutableMap<String, String> = HashMap()

        /** InteractionsStore  */
        var interactionsStore: MutableMap<Long, WidgetApplicationCommandBottomSheetViewModel.StoreState> =
            HashMap()

        /** Optional CommandOption of type String  */
        var messageOption = createCommandOption(
            ApplicationCommandType.STRING,
            "message",
            null,
            R.h.command_shrug_message_description
        )

        /** Required CommandOption of type String  */
        var requiredMessageOption = createCommandOption(
            ApplicationCommandType.STRING,
            "message",
            null,
            R.h.command_shrug_message_description,
            true
        )

        private fun registerCommand(
            pluginName: String,
            name: String,
            description: String,
            options: List<ApplicationCommandOption>,
            execute: (CommandContext) -> CommandResult?
        ) {
            val command = RemoteApplicationCommand(
                generateIdString(),
                ALIUCORD_APP_ID,
                name,
                description,
                options,
                null,
                null,
                null,
                null
            ) label@{ args ->
                val clock = ClockFactory.get()
                val id = NonceGenerator.computeNonce(clock)
                val channelId = StoreStream.getChannelsSelected().id
                val me: User = StoreStream.getUsers().me
                val thinkingMsg = LocalMessageCreatorsKt.createLocalApplicationCommandMessage(
                    id,
                    name,
                    channelId,
                    UserUtils.INSTANCE.synthesizeApiUser(me),
                    buildClyde(null, null),
                    false,
                    true,
                    id,
                    clock
                )
                val c = Message::class.java

                try {
                    setField(
                        c,
                        thinkingMsg,
                        "flags",
                        MessageFlags.EPHEMERAL or MessageFlags.LOADING
                    )
                    setField(c, thinkingMsg, "type", MessageTypes.LOCAL)
                } catch (ignored: Throwable) {
                }
                val storeMessages = StoreStream.getMessages()
                StoreMessages.`access$handleLocalMessageCreate`(storeMessages, thinkingMsg)
                val _this =
                    args["__this"] as `WidgetChatInput$configureSendListeners$2`?
                val _args = args["__args"] as Array<Any>?
                args -= "__this"
                args -= "__args"
                if (_this == null || _args == null) return@label null
                val inputAutocomplete = WidgetChatInput.`access$getAutocomplete$p`(_this.`this$0`)
                val content: MessageContent = inputAutocomplete.inputContent
                    ?: MessageContent(_this.`$chatInput`.text, emptyList<User>())

                WidgetChatInput.`clearInput$default`(_this.`this$0`, false, true, 0, null)
                val ctx = CommandContext(args, _this, _args, content)
                Utils.threadPool.execute {
                    try {
                        val res = execute.invoke(ctx)
                        if (res == null) {
                            storeMessages.deleteMessage(thinkingMsg)
                            return@execute
                        }
                        val hasContent = res.content != null && res.content!!.isNotEmpty()
                        val hasEmbeds = res.embeds != null && res.embeds!!.isNotEmpty()
                        if (!res.send) {
                            if (!hasContent && !hasEmbeds && ctx.attachments.isEmpty()) {
                                storeMessages.deleteMessage(thinkingMsg)
                                return@execute
                            }
                            try {
                                val commandMessage = LocalMessageCreatorsKt.createLocalMessage(
                                    if (res.content == null) "" else res.content,
                                    channelId,
                                    buildClyde(res.username, res.avatarUrl),
                                    null,
                                    false,
                                    false,  // TODO: Make local uploads work and set this to true
                                    null,
                                    null,
                                    clock,
                                    ctx.attachments.map(AttachmentUtilsKt::toLocalAttachment),
                                    null,
                                    null,
                                    null,
                                    null,
                                    ctx.messageReference,
                                    null,
                                    null
                                )
                                setField(c, commandMessage, "embeds", res.embeds)
                                setField(c, commandMessage, "flags", MessageFlags.EPHEMERAL)
                                setField(c, commandMessage, "interaction", thinkingMsg.interaction)
                                if (res.buttons != null) for (button in res.buttons!!) {
                                    commandMessage.addButton(button)
                                }

                                // TODO: add arguments

                                val guildId: Long = ChannelWrapper(
                                    StoreStream.getChannels().getChannel(channelId)
                                ).guildId
                                interactionsStore[id] =
                                    WidgetApplicationCommandBottomSheetViewModel.StoreState(
                                        me,
                                        if (guildId == 0L) null else StoreStream.getGuilds().members[guildId]!![me.id],
                                        StoreApplicationInteractions.State.Loaded(
                                            ApplicationCommandData(
                                                "",
                                                "",
                                                "",
                                                name,
                                                emptyList(),
                                                emptyList()
                                            )
                                        ),
                                        getAliucordApplication(),
                                        emptySet(),
                                        emptyMap(),
                                        emptyMap(),
                                        emptyMap(),
                                        emptyMap(),
                                        emptyMap()
                                    )
                                StoreMessages.`access$handleLocalMessageCreate`(
                                    storeMessages,
                                    commandMessage
                                )
                            } catch (e: Throwable) {
                                logger.error(e)
                            }
                        } else {
                            if (hasEmbeds) {
                                // imagine selfbot embeds in 2022 (impossible)
                                logger.error(
                                    "[${pluginName}]",
                                    IllegalArgumentException("Embeds may not be specified when send is set to true")
                                )
                            }
                            val attachments = ctx.attachments
                            if (!hasContent && attachments.isEmpty()) {
                                storeMessages.deleteMessage(thinkingMsg)
                                return@execute
                            }
                            Utils.mainThread.post {
                                ChatInputViewModel.`sendMessage$default`(
                                    WidgetChatInput.`access$getViewModel$p`(_this.`this$0`),
                                    _this.`$context`,
                                    _this.`$messageManager`,
                                    MessageContent(res.content, content.mentionedUsers),
                                    attachments,
                                    false,
                                    _args[2] as (Boolean) -> Unit,
                                    16,
                                    null
                                )
                            }
                        }
                        storeMessages.deleteMessage(thinkingMsg)
                    } catch (t: Throwable) {
                        storeMessages.deleteMessage(thinkingMsg)
                        val detailedError: String? =
                            if (t is CommandContext.RequiredArgumentWasNullException) {
                                t.message
                            } else {
                                logger.error("[$name]", t)
                                val argStringB = StringBuilder()
                                for (entry in args.entries) {
                                    argStringB.append(entry).append('\n')
                                }
                                val argString = argStringB.toString()
                                val (_, _, authors, _, version) = PluginManager.plugins[pluginName]!!.manifest
                                """
                                Oops! Something went wrong while running this command:
                                ```java
                                $t```
                                Please search for this error on the Aliucord server to see if it's a known issue. If it isn't, report it to the plugin ${if (authors.size == 1) "author" else "authors"}.

                                Debug:```
                                Command: $name
                                Plugin: $pluginName v$version
                                Discord v${Constants.DISCORD_VERSION}
                                Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
                                Aliucord ${BuildConfig.GIT_REVISION}```
                                Arguments:```
                                ${argString.ifEmpty { "-" }}```
                                """.trimIndent()
                            }
                        val commandMessage = LocalMessageCreatorsKt.createLocalMessage(
                            detailedError,
                            channelId,
                            buildClyde(null, null),
                            null,
                            false,
                            false,
                            null,
                            null,
                            clock,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        )
                        try {
                            setField(c, commandMessage, "flags", MessageFlags.EPHEMERAL)
                        } catch (ignored: Throwable) {
                        }
                        StoreMessages.`access$handleLocalMessageCreate`(
                            storeMessages,
                            commandMessage
                        )
                    }
                }
                null
            }
            try {
                setField(ApplicationCommand::class.java, command, "builtIn", true)
            } catch (e: Throwable) {
                logger.error(e)
            }
            commands[name] = command
            updateCommandCount()
        }

        private fun unregisterCommand(name: String) {
            commands.remove(name)
            updateCommandCount()
        }

        /** Returns the Aliucord Application  */
        fun getAliucordApplication(): Application {
            updateCommandCount()
            return aliucordApplication
        }

        private fun updateCommandCount() {
            if (aliucordApplication.commandCount != commands.size) {
                try {
                    setField(aliucordApplication, "commandCount", commands.size)
                } catch (ignored: Throwable) {
                }
            }
        }

        /** Generate a fake Snowflake  */
        fun generateId(): Long {
            return -SnowflakeUtils.fromTimestamp(System.currentTimeMillis() * 100)
        }

        /** Generate a fake Snowflake String  */
        fun generateIdString(): String = generateId().toString()
    }
}
