/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.entities

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.IdRes
import com.discord.api.sticker.Sticker
import com.discord.app.AppComponent
import d0.e0.c

/**
 * Notification builder.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
public class NotificationData {
    /**
     * @return Title of the notification.
     * @see NotificationData.setTitle
     */

    public var title: CharSequence? = null
        private set

    /**
     * @return Subtitle of the notification.
     * @see NotificationData.setSubtitle
     */
    public var subtitle: CharSequence? = null
        private set

    /**
     * @return Body of the notification.
     * @see NotificationData.setBody
     */
    public var body: CharSequence? = null
        private set

    /**
     * @return Attachment background [Drawable] of the notification.
     * @see NotificationData.setAttachmentBackground
     */
    public var attachmentBackground: Drawable? = null
        private set

    /**
     * @return Attachment URL of the notification.
     * @see NotificationData.setAttachmentUrl
     */
    public var attachmentUrl: String? = null
        private set

    /**
     * @return Attachment [Drawable] of the notification.
     * @see NotificationData.setAttachment
     */
    public var attachment: Drawable? = null
        private set

    /**
     * @return Stickers of the notification.
     * @see NotificationData.setStickers
     */
    public var stickers: List<Sticker>? = null
        private set

    /**
     * @return Icon URL of the notification.
     * @see NotificationData.setIconUrl
     */
    public var iconUrl: String = ""
        private set

    /**
     * @return Title of the notification
     * @see NotificationData.setTitle
     */
    public var iconResId: Int? = null
        private set

    /**
     * @return [Drawable] of the icon in the top right corner of the notification.
     * @see NotificationData.setIconTopRight
     */
    public var iconTopRight: Drawable? = null
        private set

    /**
     * @return Auto dismiss period time in seconds
     * @see NotificationData.setAutoDismissPeriodSecs
     */
    public var autoDismissPeriodSecs: Int? = 5
        private set

    /**
     * @return Valid screens
     * @see NotificationData.setValidScreens
     */
    public var validScreens: List<c<out AppComponent?>?>? = null
        private set

    /**
     * @return Block corresponding the onClick action of the icon in the top right corner of the notification.
     * @see NotificationData.setOnClickTopRightIcon
     */
    public var onClickTopRightIcon: ((View) -> Unit)? = null
        private set

    /**
     * @return Block corresponding the onClick action of the notification.
     * @see NotificationData.setTitle
     */
    public var onClick: ((View) -> Unit)? = null
        private set

    /**
     * Sets the title.
     * @param title Title.
     * @return [NotificationData] for chaining.
     */
    public fun setTitle(title: CharSequence?): NotificationData = apply {
        this.title = title
    }

    /**
     * Sets the subtitle.
     * @param subtitle Subtitle.
     * @return [NotificationData] for chaining.
     */
    public fun setSubtitle(subtitle: CharSequence?): NotificationData = apply {
        this.subtitle = subtitle
    }

    /**
     * Sets the body.
     * @param body Body.
     * @return [NotificationData] for chaining.
     */
    public fun setBody(body: CharSequence?): NotificationData = apply {
        this.body = body
    }

    /**
     * Sets the background for the attachment.
     * @param attachmentBackground Background [Drawable] of the attachment.
     * @return [NotificationData] for chaining.
     * @see NotificationData.setAttachment
     * @see NotificationData.setAttachmentUrl
     */
    public fun setAttachmentBackground(attachmentBackground: Drawable?): NotificationData = apply {
        this.attachmentBackground = attachmentBackground
    }

    /**
     * Sets the attachment URL.
     * @param attachmentUrl URL of the attachment.
     * @return [NotificationData] for chaining.
     */
    public fun setAttachmentUrl(attachmentUrl: String?): NotificationData = apply {
        this.attachmentUrl = attachmentUrl
    }

    /**
     * Sets the attachment.
     * @param attachment Attachment [Drawable].
     * @return [NotificationData] for chaining.
     */
    public fun setAttachment(attachment: Drawable?): NotificationData = apply {
        this.attachment = attachment
    }

    /**
     * Sets the stickers.
     * @param stickers [List] of stickers.
     * @return [NotificationData] for chaining.
     */
    public fun setStickers(stickers: List<Sticker>?): NotificationData = apply {
        this.stickers = stickers
    }

    /**
     * Sets the icon URL.
     * @param iconUrl URL of the icon.
     * @return [NotificationData] for chaining.
     */
    public fun setIconUrl(iconUrl: String): NotificationData = apply {
        this.iconUrl = iconUrl
    }

    /**
     * Sets the icon.
     * @param iconResId [IdRes] of the icon [Drawable].
     * @return [NotificationData] for chaining.
     */
    public fun setIconResId(@IdRes iconResId: Int?): NotificationData = apply {
        this.iconResId = iconResId
    }

    /**
     * Sets the icon on the top right corner of the notification.
     * @param iconTopRight [Drawable] of the icon.
     * @return [NotificationData] for chaining.
     */
    public fun setIconTopRight(iconTopRight: Drawable?): NotificationData = apply {
        this.iconTopRight = iconTopRight
    }

    /**
     * Sets the auto dismiss period.
     * @param autoDismissPeriodSecs Dismiss period in seconds.
     * @return [NotificationData] for chaining.
     */
    public fun setAutoDismissPeriodSecs(autoDismissPeriodSecs: Int?): NotificationData = apply {
        this.autoDismissPeriodSecs = autoDismissPeriodSecs
    }

    /**
     * Sets valid screens for the notification.
     * @param validScreens Valid screens.
     * @return [NotificationData] for chaining.
     */
    public fun setValidScreens(validScreens: List<c<out AppComponent?>?>?): NotificationData {
        return apply {
            this.validScreens = validScreens
        }
    }

    /**
     * Sets the callback for the notification top right corner icon onClick action.
     * @param onClickTopRightIcon Block to execute after clicking the icon in the top right corner of the notification.
     * @return [NotificationData] for chaining.
     */
    public fun setOnClickTopRightIcon(onClickTopRightIcon: ((View) -> Unit)?): NotificationData {
        return apply {
            this.onClickTopRightIcon = onClickTopRightIcon
        }
    }

    /**
     * Sets the callback for the notification onClick action.
     * @param onClick Block to execute after clicking the notification.
     * @return [NotificationData] for chaining.
     */
    public fun setOnClick(onClick: ((View) -> Unit)?): NotificationData = apply {
        this.onClick = onClick
    }
}
