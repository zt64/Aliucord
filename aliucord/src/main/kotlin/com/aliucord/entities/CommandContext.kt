/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.entities

import android.content.Context
import com.aliucord.wrappers.ChannelWrapper
import com.aliucord.wrappers.GuildRoleWrapper
import com.discord.api.message.LocalAttachment
import com.discord.api.message.MessageReference
import com.discord.api.role.GuildRole
import com.discord.models.member.GuildMember
import com.discord.models.message.Message
import com.discord.models.user.MeUser
import com.discord.models.user.User
import com.discord.stores.StoreStream
import com.discord.utilities.SnowflakeUtils
import com.discord.utilities.attachments.AttachmentUtilsKt
import com.discord.widgets.chat.MessageContent
import com.discord.widgets.chat.input.ChatInputViewModel
import com.discord.widgets.chat.input.ChatInputViewModel.ViewState.Loaded.PendingReplyState
import com.discord.widgets.chat.input.`WidgetChatInput$configureSendListeners$2`
import com.discord.widgets.chat.input.`WidgetChatInput$configureSendListeners$7$1`
import com.lytefast.flexinput.model.Attachment

/** Context passed to command executors  */
@Suppress("unused")
public class CommandContext(
    /** Returns the raw args  */
    private val rawArgs: Map<String, *>,
    _this: `WidgetChatInput$configureSendListeners$2`,
    _args: Array<*>,
    messageContent: MessageContent
) {
    /**
     * Looks like Discord's required args are unreliable and are sometimes accepted even if empty.
     * Or a plugin dev may just forget to mark an argument as required which leads to ugly NPEs.
     * Thus, throw a custom exception and handle it in the command handler to present the user a simple message, not a scary
     * stacktrace.
     */
    public class RequiredArgumentWasNullException(name: String?) : RuntimeException(
        "Required argument $name was null. Please specify a value for it and try again."
    )

    private val _this: `WidgetChatInput$configureSendListeners$2`
    private val messageContent: MessageContent

    /** Returns the ViewState associated with this Context  */
    @JvmField
    public val viewState: ChatInputViewModel.ViewState.Loaded

    @JvmField
    public var attachments: MutableList<Attachment<*>>

    init {
        this._this = _this
        this.messageContent = messageContent
        attachments = _args[0] as MutableList<Attachment<*>>
        viewState = (_args[2] as `WidgetChatInput$configureSendListeners$7$1`).`this$0`.`$viewState`
    }

    public val context: Context
        /** Returns the AppContext  */
        get() = _this.`$context`
    public val maxFileSizeMB: Int
        /** Returns the maximum size attachments may be  */
        get() = viewState.maxFileSizeMB
    public val replyingState: PendingReplyState.Replying?
        get() {
            val state: PendingReplyState = viewState.pendingReplyState
            return if (state is PendingReplyState.Replying) state else null
        }
    public val messageReference: MessageReference?
        /** Returns the MessageReference  */
        get() = replyingState?.messageReference
    public val referencedMessageAuthor: User?
        /** Returns the Author of the referenced message  */
        get() = replyingState?.repliedAuthor
    public val referencedMessageAuthorGuildMember: GuildMember?
        /** Returns the Author of the referenced message as member of the current guild  */
        get() = replyingState?.repliedAuthorGuildMember
    public val referencedMessage: Message?
        /** Returns the referenced message  */
        get() {
            val ref = messageReference ?: return null
            return StoreStream.getMessages().getMessage(ref.a(), ref.c())
        }
    public val referencedMessageLink: String?
        /** Returns the link of the referenced message  */
        get() {
            val ref = messageReference ?: return null
            val guildId = if (ref.b() != null) ref.b().toString() else "@me"
            return "https://discord.com/channels/$guildId/${ref.a()}/${ref.c()}"
        }
    public var channelId: Long
        /** Returns the current channel id  */
        get() = _this.`$chatInput`.channelId
        /** Sets the current channel id  */
        set(id) {
            _this.`$chatInput`.channelId = id
        }
    public val currentChannel: ChannelWrapper
        /** Returns the current channel  */
        get() = ChannelWrapper(viewState.channel)
    public val rawContent: String
        /** Returns the raw content of the message that invoked this command  */
        get() = messageContent.textContent

    /**
     * Adds an attachment
     * @param uri Uri of the attachment
     * @param displayName file name
     */
    public fun addAttachment(uri: String?, displayName: String?): Unit = addAttachment(
        LocalAttachment(
            SnowflakeUtils.fromTimestamp(System.currentTimeMillis()),
            uri,
            displayName
        )
    )

    /**
     * Adds an attachment
     * @param attachment Attachment
     */
    public fun addAttachment(attachment: LocalAttachment?) {
        addAttachment(AttachmentUtilsKt.toAttachment(attachment))
    }

    /**
     * Adds an attachment
     * @param attachment Attachment
     */
    public fun addAttachment(attachment: Attachment<*>) {
        attachments += attachment
    }

    public val mentionedUsers: List<User>
        /** Returns the mentioned users  */
        get() = messageContent.mentionedUsers
    public val me: MeUser
        /** Returns the current user  */
        get() = StoreStream.getUsers().me

    /**
     * Check if the arguments contain the specified key
     * @param key Key to check
     */
    public fun containsArg(key: String): Boolean = rawArgs.containsKey(key)

    /**
     * Gets the arguments object for the specified subcommand
     * @param key Key of the subcommand
     */
    public fun getRequiredSubCommandArgs(key: String): Map<String, *> {
        return requireNonNull(key, rawArgs[key]) as Map<String, *>
    }

    /**
     * Gets the arguments object for the specified subcommand
     * @param key Key of the subcommand
     */
    public fun getSubCommandArgs(key: String): Map<String, *>? {
        return rawArgs[key] as Map<String, *>?
    }

    /**
     * Gets the raw argument with the specified key
     * @param key The key of the argument
     */
    public operator fun get(key: String): Any? = rawArgs[key]

    /**
     * Gets the **required** raw argument with the specified key
     * @param key The key of the argument
     */
    public fun getRequired(key: String): Any = requireNonNull(key, get(key))

    /**
     * Gets the raw argument with the specified key or the defaultValue if no such argument is present
     * @param key The key of the argument
     * @param defaultValue The default value
     */
    public fun getOrDefault(key: String, defaultValue: Any?): Any = (get(key) ?: defaultValue)!!

    /**
     * Gets the String argument with the specified key
     * @param key The key of the argument
     */
    public fun getString(key: String): String? = rawArgs[key] as String?

    /**
     * Gets the **required** String argument with the specified key
     * @param key The key of the argument
     */
    public fun getRequiredString(key: String): String = requireNonNull(key, getString(key))

    /**
     * Gets the String argument with the specified key or the defaultValue if no such argument is present
     * @param key The key of the argument
     */
    public fun getStringOrDefault(key: String, defaultValue: String): String =
        getString(key) ?: defaultValue

    /**
     * Gets the Integer argument with the specified key
     * @param key The key of the argument
     */
    public fun getInt(key: String): Int? = get(key)?.let {
        when (it) {
            is Int -> it
            is Long -> it.toInt()
            is String -> it.toInt()
            else -> throw ClassCastException(
                "Argument $key is of type ${it.javaClass.getSimpleName()} which cannot be cast to Integer."
            )
        }
    }

    /**
     * Gets the **required** Integer argument with the specified key
     * @param key The key of the argument
     */
    public fun getRequiredInt(key: String): Int = requireNonNull(key, getInt(key))

    /**
     * Gets the Integer argument with the specified key or the defaultValue if no such argument is present
     * @param key The key of the argument
     */
    public fun getIntOrDefault(key: String, defaultValue: Int): Int = getInt(key) ?: defaultValue

    /**
     * Gets the Long argument with the specified key
     * @param key The key of the argument
     */
    public fun getLong(key: String): Long? = get(key)?.let {
        when (it) {
            is Long -> it
            is Int -> it.toLong()
            is String -> it.toLong()
            else -> throw ClassCastException(
                "Argument $key is of type ${it.javaClass.getSimpleName()} which cannot be cast to Long."
            )
        }
    }

    /**
     * Gets the **required** Long argument with the specified key
     * @param key The key of the argument
     */
    public fun getRequiredLong(key: String): Long = requireNonNull(key, getLong(key))

    /**
     * Gets the Long argument with the specified key or the defaultValue if no such argument is present
     * @param key The key of the argument
     */
    public fun getLongOrDefault(key: String, defaultValue: Long): Long =
        getLong(key) ?: defaultValue

    /**
     * Gets the Boolean argument with the specified key
     * @param key The key of the argument
     */
    public fun getBool(key: String): Boolean? = get(key)?.let {
        when (it) {
            is Boolean -> it
            is String -> it.toBoolean()
            else -> throw ClassCastException(
                "Argument $key is of type ${it.javaClass.getSimpleName()} which cannot be cast to Long."
            )
        }
    }

    /**
     * Gets the **required** Boolean argument with the specified key
     * @param key The key of the argument
     */
    public fun getRequiredBool(key: String): Boolean = requireNonNull(key, getBool(key))

    /**
     * Gets the Boolean argument with the specified key or the defaultValue if no such argument is present
     * @param key The key of the argument
     */
    public fun getBoolOrDefault(key: String, defaultValue: Boolean): Boolean =
        getBool(key) ?: defaultValue

    /**
     * Gets the User argument with the specified key
     * @param key The key of the argument
     */
    public fun getUser(key: String): User? = getLong(key)?.let { id ->
        StoreStream.getUsers().users[id]
    }

    /**
     * Gets the **required** User argument with the specified key
     * @param key The key of the argument
     */
    public fun getRequiredUser(key: String): User = requireNonNull(key, getUser(key))

    /**
     * Gets the User argument with the specified key or the defaultValue if no such argument is present
     * @param key The key of the argument
     */
    public fun getUserOrDefault(key: String, defaultValue: User?): User =
        getUser(key) ?: defaultValue!!

    /**
     * Gets the Channel argument with the specified key
     * @param key Key of the argument
     */
    public fun getChannel(key: String): ChannelWrapper? = getLong(key)?.let { id ->
        ChannelWrapper(StoreStream.getChannels().getChannel(id))
    }

    /**
     * Gets the **required** channel argument with the specified key
     * @param key The key of the argument
     */
    public fun getRequiredChannel(key: String): ChannelWrapper = requireNonNull(key, getChannel(key))

    /**
     * Gets the channel argument with the specified key or the defaultValue if no such argument is present
     * @param key The key of the argument
     */
    public fun getChannelOrDefault(key: String, defaultValue: ChannelWrapper?): ChannelWrapper? {
        return getChannel(key) ?: defaultValue
    }

    /**
     * Gets the Role argument with the specified key
     * @param key Key of the argument
     */
    public fun getRole(key: String): GuildRoleWrapper? {
        val id = getLong(key)
        val roles: MutableMap<Long, GuildRole>? =
            StoreStream.getGuilds().roles[currentChannel.guildId]
        if (id == null || roles == null) return null
        val role: GuildRole? = roles[id]
        return if (role != null) GuildRoleWrapper(role) else null
    }

    /**
     * Gets the **required** Role argument with the specified key
     * @param key Key of the argument
     */
    public fun getRequiredRole(key: String): GuildRoleWrapper = requireNonNull(key, getRole(key))

    /**
     * Gets the Role argument with the specified key or the defaultValue if no such argument is present
     * @param key The key of the argument
     */
    public fun getRoleOrDefault(key: String, defaultValue: GuildRoleWrapper?): GuildRoleWrapper? {
        return getRole(key) ?: defaultValue
    }

    private companion object {
        private fun <T> requireNonNull(key: String, `val`: T?): T {
            return `val` ?: throw RequiredArgumentWasNullException(key)
        }
    }
}
