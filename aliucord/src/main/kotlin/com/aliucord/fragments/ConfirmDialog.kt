/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

package com.aliucord.fragments

import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aliucord.Utils.getResId
import com.discord.app.AppDialog
import com.discord.databinding.LeaveGuildDialogBinding
import com.discord.views.LoadingButton
import com.discord.widgets.guilds.leave.`WidgetLeaveGuildDialog$binding$2`
import com.google.android.material.button.MaterialButton
import com.lytefast.flexinput.R

/**
 * Creates a Confirmation Dialog similar to the **Leave Guild** dialog.
 * This class offers convenient builder methods so you should usually not have to do any layouts manually.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
public class ConfirmDialog : AppDialog(resId) {
    private var title: CharSequence? = null
    private var description: CharSequence? = null
    private var isDangerous = false
    private var onCancelListener: View.OnClickListener? = null
    private var onOkListener: View.OnClickListener? = null

    private lateinit var binding: LeaveGuildDialogBinding

    override fun onViewBound(view: View) {
        super.onViewBound(view)

        binding = `WidgetLeaveGuildDialog$binding$2`.INSTANCE(view)

        val okButton = oKButton.apply {
            setText("OK")
            setIsLoading(false)
            setOnClickListener(onOkListener ?: View.OnClickListener { dismiss() })
        }
        val btnColor = if (isDangerous) {
            R.c.uikit_btn_bg_color_selector_red
        } else {
            R.c.uikit_btn_bg_color_selector_brand
        }
        okButton.setBackgroundColor(ContextCompat.getColor(view.context, btnColor))
        cancelButton.setOnClickListener(onCancelListener ?: View.OnClickListener { dismiss() })
        header.text = title ?: "Confirm"
        body.apply {
            text = description ?: "Are you sure?"
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    /**
     * Returns the root layout of this dialog.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     */
    public val root: LinearLayout
        get() = binding.a

    /**
     * Returns the cancel button of this dialog.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     * @see [setOnOkListener]
     */
    public val cancelButton: MaterialButton
        get() = binding.b

    /**
     * Returns the OK button of this dialog.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     * @see [setOnOkListener]
     */
    public val oKButton: LoadingButton
        get() = binding.c

    /**
     * Returns the body of this dialog.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     * @see [setDescription]
     */
    public val body: TextView
        get() = binding.d

    /**
     * Returns the header of this dialog.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     * @see [setTitle]
     */
    public val header: TextView
        get() = binding.e

    /**
     * Sets the title of this dialog
     * @param title The description
     * @return Builder for chaining
     */
    public fun setTitle(title: CharSequence?): ConfirmDialog = apply {
        this.title = title
    }

    /**
     * Sets the description of this dialog
     * @param description The description
     * @return Builder for chaining
     */
    public fun setDescription(description: CharSequence?): ConfirmDialog = apply {
        this.description = description
    }

    /**
     * Sets the [View.OnClickListener] that will be called when the OK button is pressed (By default simply closes this dialog)
     * @param listener The listener
     * @return Builder for chaining
     */
    public fun setOnOkListener(listener: View.OnClickListener?): ConfirmDialog = apply {
        onOkListener = listener
    }

    /**
     * Sets the [View.OnClickListener] that will be called when the cancel button is pressed (By default simply closes this dialog)
     * @param listener The listener
     * @return Builder for chaining
     */
    public fun setOnCancelListener(listener: View.OnClickListener?): ConfirmDialog = apply {
        onCancelListener = listener
    }

    /**
     * Indicates that this confirm dialog is for a dangerous action by making the OK button Red
     * @param isDangerous Whether this action is dangerous
     * @return Builder for chaining
     */
    public fun setIsDangerous(isDangerous: Boolean): ConfirmDialog = apply {
        this.isDangerous = isDangerous
    }

    private companion object {
        private val resId = getResId("leave_guild_dialog", "layout")
    }
}
