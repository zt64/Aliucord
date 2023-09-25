/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2023 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.coreplugins

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.aliucord.entities.Plugin
import com.aliucord.patcher.GlobalPatcher
import com.aliucord.patcher.after
import com.discord.widgets.channels.list.WidgetChannelListModel
import com.discord.widgets.channels.list.WidgetChannelsList

internal class PrivateChannelsListScroll : Plugin(Manifest("PrivateChannelsListScroll")) {
    private lateinit var unhook: Runnable

    override fun load(context: Context) {
        unhook = GlobalPatcher.after<WidgetChannelsList>("configureUI", WidgetChannelListModel::class.java) {
            val model = it.args[0] as WidgetChannelListModel

            if (!model.isGuildSelected && model.items.size > 1) {
                val manager = WidgetChannelsList.`access$getBinding$p`(it.thisObject as WidgetChannelsList).c.layoutManager!! as LinearLayoutManager
                if (manager.findFirstVisibleItemPosition() != 0) {
                    manager.scrollToPosition(0)
                    unhook.run()
                }
            }
        }
    }
}
