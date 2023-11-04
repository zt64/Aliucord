/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import b.a.d.j
import com.aliucord.fragments.*
import com.aliucord.utils.DimenUtils.dp
import com.aliucord.utils.ReflectUtils
import com.aliucord.utils.lazyField
import com.discord.api.commands.ApplicationCommandType
import com.discord.api.commands.CommandChoice
import com.discord.api.message.attachment.MessageAttachment
import com.discord.api.user.User
import com.discord.app.AppActivity
import com.discord.app.AppComponent
import com.discord.models.commands.ApplicationCommandOption
import com.discord.nullserializable.NullSerializable
import com.discord.stores.StoreInviteSettings
import com.discord.stores.StoreStream
import com.discord.utilities.SnowflakeUtils
import com.discord.utilities.fcm.NotificationClient
import com.discord.views.CheckedSetting
import com.discord.widgets.chat.list.WidgetChatList
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemAttachment
import com.discord.widgets.guilds.invite.WidgetGuildInvite
import com.google.android.material.snackbar.Snackbar
import com.lytefast.flexinput.R
import java.io.File
import java.lang.reflect.Field
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.system.exitProcess

/** Utility class that holds miscellaneous Utilities  */
public object Utils {
    /** The main (UI) thread  */
    @JvmField
    public val mainThread: Handler = Handler(Looper.getMainLooper())

    /**
     * ThreadPool. Please use this for asynchronous Tasks instead of creating Threads manually
     * as spinning up new Threads everytime is heavy on the CPU
     */
    @JvmField
    public val threadPool: ExecutorService = Executors.newCachedThreadPool()

    @JvmStatic
    public lateinit var appActivity: AppActivity

    private lateinit var mAppContext: Context

    @JvmStatic
    public val appContext: Context
        get() = if (::mAppContext.isInitialized) {
            mAppContext
        } else {
            NotificationClient.`access$getContext$p`(NotificationClient.INSTANCE)
                .also { mAppContext = it }
        }

    /**
     * Whether Aliucord is debuggable
     */
    @JvmStatic
    public val isDebuggable: Boolean
        get() = appContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

    /**
     * Instance of WidgetChatList. Use this instead of patching it's constructor and storing it.
     */
    @JvmField
    public var widgetChatList: WidgetChatList? = null

    /**
     * Launches an URL in the user's preferred Browser
     * @param url The url to launch
     */
    @JvmStatic
    public fun launchUrl(url: String): Unit = launchUrl(Uri.parse(url))

    /**
     * Launches an URL in the user's preferred Browser
     * @param url The url to launch
     */
    @JvmStatic
    public fun launchUrl(url: Uri) {
        appActivity.startActivity(Intent(Intent.ACTION_VIEW).setData(url))
    }

    /**
     * Prompt to join the Aliucord support server
     *
     * @param ctx Context
     */
    @JvmStatic
    public fun joinSupportServer(ctx: Context) {
        (WidgetGuildInvite.Companion).launch(
            ctx,
            StoreInviteSettings.InviteCode(Constants.ALIUCORD_SUPPORT, "", null)
        )
    }

    /**
     * Get a drawable by attribute
     *
     * @param context Context
     * @param attr The attribute id, e.g. R.b.ic_navigate_next
     * @return Resolved drawable
     */
    @JvmStatic
    @Throws(Resources.NotFoundException::class)
    public fun getDrawableByAttr(context: Context, @AttrRes attr: Int): Drawable {
        val attrs = context.theme.obtainStyledAttributes(intArrayOf(attr))
        val id = attrs.getResourceId(0, 0)
        attrs.recycle()
        return ContextCompat.getDrawable(context, id)
            ?: throw Resources.NotFoundException("Resource ID #0x${attr.toString(16)}")
    }

    /**
     * Nested childAt. Used to turn nightmares like
     * ```kt
     * val layout = ((v.getChildAt(1) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(1) as LinearLayout
     * ```
     * into the much nicer
     * ```kt
     * val layout = v.nestedChildAt<LinearLayout>(1, 0, 1)
     * ```
     * @receiver The root that holds the children
     * @param indices Indices of the children. They will be done in order
     * @return Child at the specified nested index
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    public fun <R : View> ViewGroup.nestedChildAt(vararg indices: Int): R {
        return indices.fold(this as View) { last, curr ->
            (last as ViewGroup).getChildAt(curr) as View
        } as R
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmName("nestedViewAt")
    @JvmSynthetic
    @JvmStatic
    public inline fun ViewGroup.nestedChildAt(vararg indices: Int): View {
        return nestedChildAt<View>(*indices)
    }

    /**
     * Launches the file explorer in the specified folder.
     * May not work on all Roms, will show an error with advice in that case.
     *
     * @param folder The folder to launch
     * @throws IllegalArgumentException If [folder] does not exist or is not a directory.
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    public fun launchFileExplorer(folder: File) {
        val path = folder.absolutePath

        require(folder.exists()) { "No such folder: $path" }
        require(folder.isDirectory) { "Not a folder: $path" }

        val uri = Uri.parse(path)
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, "resource/folder")

        // TODO: Do we need to add query permission to AndroidManifest? I tried on Android 11 and it resolved MiXplorer correctly
        @Suppress("QueryPermissionsNeeded")
        if (intent.resolveActivityInfo(appActivity.packageManager, 0) == null) {
            ConfirmDialog()
                .setTitle(":(")
                .setDescription("No file explorer found")
                .show(appActivity.supportFragmentManager, "Open Folder")
        } else {
            appActivity.startActivity(Intent.createChooser(intent, "Open folder"))
        }
    }

    /**
     * Sets the clipboard content
     * @param label User-visible label for the clip data
     * @param text The actual text
     */
    @JvmStatic
    public fun setClipboard(label: CharSequence?, text: CharSequence) {
        val clipboard = appContext.getSystemService<ClipboardManager>()!!
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    /**
     * Converts the singular term of the `noun` into plural.
     * Appends an `s` to the end of the `noun` if `amount` is not 1.
     * @param amount Amount of the noun.
     * @param noun The noun
     * @return Pluralised `noun`
     */
    @JvmStatic
    public fun pluralise(amount: Int, noun: String): String {
        return "$amount $noun${if (amount != 1) "s" else ""}"
    }

    /**
     * Alias for [pluralise]
     */
    @Suppress("NOTHING_TO_INLINE")
    @JvmStatic
    public inline fun pluralize(amount: Int, noun: String): String = pluralise(amount, noun)

    /**
     * Send a toast from any [Thread]
     * @param message Message to show.
     * @param showLonger Whether to show toast for an extended period of time.
     */
    @JvmOverloads
    @JvmStatic
    public fun showToast(message: String, showLonger: Boolean = false) {
        mainThread.post {
            Toast.makeText(
                appContext,
                message,
                if (showLonger) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val resIdCache = hashMapOf<String, Int>()

    /**
     * Get resource id from discord package.
     * @param name Name of the resource.
     * @param type Type of the resource.
     * @return ID of the resource, or 0 if not found.
     */
    @Suppress("DiscouragedApi")
    @JvmStatic
    public fun getResId(name: String, type: String): Int = resIdCache.getOrPut(name) {
        appContext.resources.getIdentifier(
            name,
            type,
            "com.discord"
        )
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmStatic
    @JvmOverloads
    public inline fun openPage(
        context: Context,
        clazz: Class<out AppComponent>,
        intent: Intent? = null
    ): Unit = j.d(context, clazz, intent)

    @JvmStatic
    public fun openPageWithProxy(context: Context, fragment: Fragment) {
        SnowflakeUtils.fromTimestamp(System.currentTimeMillis() * 100).toString().let {
            FragmentProxy.fragments[it] = fragment
            openPage(context, AppFragmentProxy::class.java, Intent().putExtra("AC_FRAGMENT_ID", it))
        }
    }

    /**
     * Creates a CommandChoice that can be used inside Command args
     * @param name The name of the choice
     * @param value The value representing this choice
     * @return CommandChoice
     */
    @JvmStatic
    public fun createCommandChoice(name: String, value: String): CommandChoice = CommandChoice(name, value)

    // kept for compatibility
    /**
     * Creates a CommandOption that can be used for commands
     *
     * @param type The type of this argument
     * @param name The name of this argument
     * @param description The description of this argument
     * @param descriptionRes Optional ID of a string resource that will be used as description
     * @param required Whether this option is required
     * @param default Whether this option is the default selection (I think so at least I'm not 100% sure lol)
     * @param channelTypes Channel types this command is enabled in
     * @param choices List of choices the user may pick from
     * @param subCommandOptions List of command options if this argument is of [type] [ApplicationCommandType.SUBCOMMAND]
     * @param autocomplete Whether autocomplete is enabled
     */
    @JvmStatic
    @JvmOverloads
    public fun createCommandOption(
        type: ApplicationCommandType = ApplicationCommandType.STRING,
        name: String,
        description: String? = null,
        descriptionRes: Int? = null,
        required: Boolean = false,
        default: Boolean = false,
        channelTypes: List<Int?> = emptyList(),
        choices: List<CommandChoice> = emptyList(),
        subCommandOptions: List<ApplicationCommandOption> = emptyList(),
        autocomplete: Boolean = false,
    ): ApplicationCommandOption = createCommandOption(
        type,
        name,
        description,
        descriptionRes,
        required,
        default,
        channelTypes,
        choices,
        subCommandOptions,
        autocomplete,
        null
    )

    /**
     * Creates a CommandOption that can be used for commands
     *
     * @param type The type of this argument
     * @param name The name of this argument
     * @param description The description of this argument
     * @param descriptionRes Optional ID of a string resource that will be used as description
     * @param required Whether this option is required
     * @param default Whether this option is the default selection (I think so at least I'm not 100% sure lol)
     * @param channelTypes Channel types this command is enabled in
     * @param choices List of choices the user may pick from
     * @param subCommandOptions List of command options if this argument is of [type] [ApplicationCommandType.SUBCOMMAND]
     * @param autocomplete Whether autocomplete is enabled
     * @param minValue minValue for number type options
     * @param maxValue maxValue for number type options
     */
    @JvmStatic
    public fun createCommandOption(
        type: ApplicationCommandType = ApplicationCommandType.STRING,
        name: String,
        description: String? = null,
        descriptionRes: Int? = null,
        required: Boolean = false,
        default: Boolean = false,
        channelTypes: List<Int?> = emptyList(),
        choices: List<CommandChoice> = emptyList(),
        subCommandOptions: List<ApplicationCommandOption> = emptyList(),
        autocomplete: Boolean = false,
        minValue: Number? = null,
        maxValue: Number? = null,
    ): ApplicationCommandOption = ApplicationCommandOption(
        type,
        name,
        description,
        descriptionRes,
        required,
        default,
        channelTypes,
        choices,
        subCommandOptions,
        autocomplete,
        minValue,
        maxValue
    )

    /**
     * Builds Clyde User
     * @param name Name of user
     * @param avatarUrl Avatar URL of user
     * @return Built Clyde
     */
    @JvmStatic
    public fun buildClyde(name: String?, avatarUrl: String?): User {
        return User(
            -1,
            name ?: "Clyde",
            NullSerializable.b(avatarUrl ?: Constants.Icons.CLYDE),
            null,
            "0000",
            0,
            null,
            true,
            false,
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0
        )
    }

    /**
     * Creates a checkable [View].
     * @param context [Context]
     * @param type [CheckedSetting.ViewType] of the checkable item.
     * @param text Title of the checkable item.
     * @param subtext Summary of the checkable item.
     * @return Checkable item.
     */
    @JvmStatic
    public fun createCheckedSetting(
        context: Context,
        type: CheckedSetting.ViewType,
        text: CharSequence?,
        subtext: CharSequence?,
    ): CheckedSetting = CheckedSetting(context, null).apply {
        if (type != CheckedSetting.ViewType.CHECK) {
            removeAllViews()
            f(type)
        }

        l.a().run {
            textSize = 16.0f
            typeface = ResourcesCompat.getFont(context, Constants.Fonts.WHITNEY_MEDIUM)
            this.text = text
        }

        setSubtext(subtext)
        l.b().run {
            setPadding(0, paddingTop, paddingRight, paddingBottom)
        }
    }

    /**
     * Tints a [Drawable] to match the user's current theme.
     * More specifically, tints the drawable to [R.c.primary_light_600] if the user is using light theme,
     * [R.c.primary_dark_300] otherwise
     *
     * Make sure you call [Drawable.mutate] first or the drawable will change in the entire app.
     * @param drawable Drawable
     * @return Drawable for chaining
     */
    @JvmStatic
    public fun tintToTheme(drawable: Drawable?): Drawable? = drawable?.apply {
        // This should instead be setTint(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal)) but Themer plugin
        // doesn't support attributes. The below code is the equivalent
        val colorName = if (StoreStream.getUserSettingsSystem().theme == "light") {
            R.c.primary_light_600
        } else {
            R.c.primary_dark_300
        }

        setTint(ContextCompat.getColor(appContext, colorName))
    }

    private val fileNameField: Field by lazyField<MessageAttachment>("filename")
    private val idField: Field by lazyField<MessageAttachment>()
    private val urlField: Field by lazyField<MessageAttachment>()
    private val proxyUrlField: Field by lazyField<MessageAttachment>()

    @JvmStatic
    public fun openMediaViewer(url: String, filename: String) {
        val attachment = ReflectUtils.allocateInstance(MessageAttachment::class.java)

        try {
            fileNameField[attachment] = filename
            idField[attachment] = SnowflakeUtils.fromTimestamp(System.currentTimeMillis())
            urlField[attachment] = url
            proxyUrlField[attachment] = url
        } catch (th: Throwable) {
            error(th)
        }

        WidgetChatListAdapterItemAttachment.Companion.`access$navigateToAttachment`(
            WidgetChatListAdapterItemAttachment.Companion,
            appActivity,
            attachment
        )
    }

    /**
     * Prompts the user to restart Aliucord
     *
     * @param msg Message
     * @param position position, see [Gravity]
     */
    @Suppress("ShowToast", "InternalInsetResource", "DiscouragedApi")
    @JvmStatic
    @JvmOverloads
    public fun promptRestart(
        msg: String = "Restart required. Restart now?",
        position: Int = Gravity.TOP
    ) {
        val resources = appContext.resources
        val id = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = if (id > 0) resources.getDimensionPixelSize(id) else 0

        val view = appActivity.findViewById<View>(android.R.id.content)
        val bar = try {
            Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE)
        } catch (e: Throwable) {
            Main.logger.errorToast("Failed to show SnackBar", e)
            return
        }

        bar.view.layoutParams = (bar.view.layoutParams as FrameLayout.LayoutParams).apply {
            topMargin = statusBarHeight + 4.dp
            gravity = position
        }

        bar.setAction("Restart") {
            val ctx = it.context
            val intent = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)
            appActivity.startActivity(Intent.makeRestartActivityTask(intent!!.component))
            exitProcess(0)
        }
        bar.show()
    }
}
