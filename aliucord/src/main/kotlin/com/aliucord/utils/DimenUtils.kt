/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.utils

import com.aliucord.Utils.appContext
import com.discord.utilities.dimen.DimenUtils

public object DimenUtils {
    private val density = appContext.resources.displayMetrics.density

    /**
     * The default padding for items (16 DP)
     */
    @JvmStatic
    public val defaultPadding: Int = dpToPx(16)

    /**
     * The default padding for cards (8 DP)
     */
    @JvmStatic
    public val defaultCardRadius: Int = dpToPx(8)

    /**
     * Converts DP to PX
     * @return `DP` converted to PX
     */
    public val Int.dp: Int
        inline get() = dpToPx(this)

    /**
     * Converts DP to PX.
     * @param dp DP value
     * @return `DP` converted to PX
     */
    @JvmStatic
    public fun dpToPx(dp: Int): Int = dpToPx(dp.toFloat())

    /**
     * Converts DP to PX.
     * @param dp DP value
     * @return `DP` converted to PX
     */
    @JvmStatic
    public fun dpToPx(dp: Float): Int = (dp * density + 0.5f).toInt()

    /**
     * Converts PX to DP.
     * @param px PX value
     * @return `PX` converted to DP
     * @see com.discord.utilities.dimen.DimenUtils.pixelsToDp
     */
    @JvmStatic
    public fun pxToDp(px: Int): Int = DimenUtils.pixelsToDp(px)
}
