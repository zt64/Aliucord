/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.widgets

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import com.aliucord.Utils.nestedChildAt
import com.discord.app.AppBottomSheet
import com.discord.widgets.channels.WidgetChannelSelector

/** AppBottomSheet with helper methods  */
@Suppress("unused")
public open class BottomSheet : AppBottomSheet() {
    private var view: NestedScrollView? = null
    private var layout: LinearLayout? = null

    public val linearLayout: LinearLayout?
        /** Returns the LinearLayout associated with this BottomSheet  */
        get() {
            if (layout == null) {
                checkNotNull(view) { "This BottomSheet has not been initialised yet. Did you forget to call super.onViewCreated?" }
                layout = (requireView() as NestedScrollView).nestedChildAt<LinearLayout>(0)
            }
            return layout
        }

    override fun getContentViewResId(): Int {
        if (Companion.id == 0) Companion.id = WidgetChannelSelector().contentViewResId
        return Companion.id
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        this.view = view as NestedScrollView
        clear()
    }

    /** Sets the padding of the LinearLayout associated with this BottomSheet  */
    public fun setPadding(p: Int): Unit = linearLayout!!.setPadding(p, p, p, p)

    /** Removes all views of the LinearLayout associated with this BottomSheet  */
    public fun clear(): Unit = linearLayout!!.removeAllViews()

    /** Adds a view to the LinearLayout associated with this BottomSheet  */
    public fun addView(view: View?): Unit = linearLayout!!.addView(view)

    /** Removes a view from the LinearLayout associated with this BottomSheet  */
    public fun removeView(view: View?): Unit = linearLayout!!.removeView(view)

    private companion object {
        private var id = 0
    }
}
