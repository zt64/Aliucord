/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.fragments

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.aliucord.Utils.appContext
import com.aliucord.Utils.getResId
import com.aliucord.Utils.nestedChildAt
import com.aliucord.Utils.tintToTheme
import com.aliucord.utils.DimenUtils.defaultPadding
import com.aliucord.views.Divider
import com.aliucord.views.ToolbarButton
import com.discord.app.AppFragment
import com.lytefast.flexinput.R

/** Settings Page Fragment  */
@Suppress("MemberVisibilityCanBePrivate")
public open class SettingsPage : AppFragment(resId) {
    private lateinit var view: CoordinatorLayout
    private lateinit var layout: LinearLayout
    private var toolbar: Toolbar? = null

    override fun onViewBound(view: View) {
        super.onViewBound(view)
        this.view = view as CoordinatorLayout

        setActionBarSubtitle("Aliucord")
        setActionBarDisplayHomeAsUpEnabled()

        clear()
        headerBar.menu.clear()
        setPadding(defaultPadding)
    }

    public val linearLayout: LinearLayout
        /** Returns the LinearLayout associated with this Page  */
        get() = if (::layout.isInitialized) layout else view.nestedChildAt<LinearLayout>(1, 0)
            .also { layout = it }

    /**
     * All Pages are wrapped into a Scrollview. This makes it so that if the page extends the screen height, it will automatically be scrollable, however it introduces lag if you add a recycler and may mess up your layout.
     *
     *
     * This method removes the scrollview so you are only working with a linear layout.
     */
    public fun removeScrollView() {
        val layout = linearLayout

        (layout.parent as NestedScrollView).removeView(layout)
        // view.removeView(layout.parent as View)
        view.addView(layout, 1)

        val p = defaultPadding
        layout.setPadding(p, p * 4, p, p)
    }

    protected val headerBar: Toolbar
        /** Returns the Toolbar associated with this Page  */
        get() = toolbar ?: view.nestedChildAt<Toolbar>(0, 0).also { toolbar = it }

    /** Sets the padding of the LinearLayout associated with this Page  */
    protected fun setPadding(p: Int): Unit = linearLayout.setPadding(p, p, p, p)

    /**
     * Add a button to the header [Toolbar] of this page
     *
     * @param id       The id of this button
     * @param order    The order to show this button in. See [MenuItem.getOrder]
     * @param title    The title of this button
     * @param drawable The drawable this button should have
     * @param onClick  The onClick listener of this button
     * @return The id of this header button
     * @see Toolbar.getMenu
     * @see Menu.add
     */
    public fun addHeaderButton(
        id: Int,
        order: Int,
        title: String?,
        drawable: Drawable?,
        onClick: MenuItem.OnMenuItemClickListener?
    ): Int {
        headerBar.menu
            .add(Menu.NONE, id, order, title)
            .setIcon(drawable)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            .setOnMenuItemClickListener(onClick)
        return id
    }

    /**
     * Add a button to the header [Toolbar] of this page
     *
     * @param id       The id of this button
     * @param title    The title of this button
     * @param drawable The drawable this button should have
     * @param onClick  The onClick listener of this button
     * @return The id of this header button
     * @see Toolbar.getMenu
     * @see Menu.add
     */
    public fun addHeaderButton(
        id: Int,
        title: String?,
        drawable: Drawable?,
        onClick: MenuItem.OnMenuItemClickListener?
    ): Int = addHeaderButton(id, Menu.NONE, title, drawable, onClick)

    /**
     * Add a button to the header [Toolbar] of this page
     *
     * @param title    The title of this button
     * @param drawable The drawable this button should have
     * @param onClick  The onClick listener of this button
     * @return The id of this header button
     * @see Toolbar.getMenu
     * @see Menu.add
     */
    public fun addHeaderButton(
        title: String?,
        drawable: Drawable?,
        onClick: MenuItem.OnMenuItemClickListener?
    ): Int = addHeaderButton(View.generateViewId(), title, drawable, onClick)

    /**
     * Add a button to the header [Toolbar] of this page
     *
     * @param title      The title of this button
     * @param drawableId The id of the drawable this button should have. Will be tinted to colorInteractiveNormal
     * @param onClick    The onClick listener of this button
     * @return The id of this header button
     * @see Toolbar.getMenu
     * @see Menu.add
     */
    public fun addHeaderButton(
        title: String?,
        @DrawableRes
        drawableId: Int,
        onClick: MenuItem.OnMenuItemClickListener?
    ): Int {
        var drawable = ContextCompat.getDrawable(appContext, drawableId)
            ?: throw Resources.NotFoundException("Drawable not found: $drawableId")
        drawable = drawable.mutate()
        tintToTheme(drawable)
        return addHeaderButton(View.generateViewId(), title, drawable, onClick)
    }

    /**
     * Adds a button from the Toolbar associated with this Page
     */
    @Deprecated("TODO", ReplaceWith("headerBar.addView(button)"))
    protected fun addHeaderButton(button: ToolbarButton?): Unit = headerBar.addView(button)


    /**
     * Removes a button to the Toolbar associated with this Page
     */
    @Deprecated("TODO", ReplaceWith("getHeaderBar().removeView(button)"))
    protected fun removeHeaderButton(button: ToolbarButton?): Unit = headerBar.removeView(button)

    protected fun removeHeaderButton(id: Int): Unit = headerBar.getMenu().removeItem(id)

    /**
     * Adds a Divider
     *
     * @param context Context
     */
    protected fun addDivider(context: Context): Unit = addView(Divider(requireContext()))

    /**
     * Add a header
     * @param context Context
     * @param text Header text
     */
    protected fun addHeader(context: Context = getContext(), text: String?) {
        val header = TextView(context, null, 0, R.i.UiKit_Settings_Item_Header)
        header.text = text
        addView(header)
    }

    /** Adds a view to the LinearLayout associated with this Page  */
    protected fun addView(view: View?): Unit = linearLayout.addView(view)

    /** Removes a view from the LinearLayout associated with this Page  */
    protected fun removeView(view: View?): Unit = linearLayout.removeView(view)

    /** Removes all views from the LinearLayout associated with this Page  */
    private fun clear() = linearLayout.removeAllViews()

    /** Removes all views from the LinearLayout associated with this Page and calls onViewBound  */
    public fun reRender() {
        clear()
        headerBar.getMenu().clear()
        onViewBound(view)
    }

    /** Closes this SettingsPage by simulating a back press  */
    public fun close(): Unit = requireActivity().onBackPressedDispatcher.onBackPressed()

    override fun getContext(): Context = view.context

    private companion object {
        private val resId = getResId("widget_settings_behavior", "layout")
    }
}
