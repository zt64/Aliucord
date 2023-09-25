/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.api

import com.aliucord.entities.NotificationData
import com.discord.utilities.channel.ChannelSelector
import com.discord.utilities.time.ClockFactory
import com.discord.widgets.notice.NoticePopup

object NotificationsAPI {
    /**
     * Displays a notification to the user.
     * @param data [NotificationData]
     * @see NotificationsAPI.display
     */
    @JvmOverloads
    fun display(data: NotificationData, channelId: Long? = null) {
        NoticePopup.`enqueue$default`(
            NoticePopup.INSTANCE,
            "InAppNotif#${ClockFactory.get().currentTimeMillis()}",
            data.title,
            data.subtitle,
            data.body,
            data.attachmentBackground,
            data.attachmentUrl,
            data.attachment,
            data.stickers,
            data.iconUrl,
            data.iconResId,
            data.iconTopRight,
            data.autoDismissPeriodSecs,
            data.validScreens,
            data.onClickTopRightIcon,
            if (data.onClick == null && channelId != null) {
                { v ->
                    ChannelSelector.getInstance().findAndSet(v.context, channelId)
                }
            } else {
                data.onClick
            },
            (if (data.validScreens == null) 4096 else 0) or if (data.onClickTopRightIcon == null) 8192 else 0,
            null
        )
    }
}
