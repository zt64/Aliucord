/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.fragments

import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.aliucord.Utils.getResId
import com.aliucord.Utils.nestedChildAt
import com.discord.app.AppDialog
import com.discord.databinding.WidgetKickUserBinding
import com.discord.widgets.user.`WidgetKickUser$binding$2`
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.lytefast.flexinput.R

/**
 * Creates a Input Dialog similar to the **Kick User** dialog.
 * This class offers convenient builder methods so you should usually not have to do any layouts manually.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
public open class InputDialog : AppDialog(resId) {
    private var title: CharSequence? = "Input"
    private var description: CharSequence? = "Please enter some text"
    private var placeholder: CharSequence? = null
    private var inputType: Int? = null
    private var onCancelListener: View.OnClickListener? = View.OnClickListener { dismiss() }
    private var onOkListener: View.OnClickListener? = View.OnClickListener { dismiss() }
    private var onDialogShownListener: OnDialogShownListener? = null

    private lateinit var binding: WidgetKickUserBinding

    @Suppress("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)

        binding = `WidgetKickUser$binding$2`.INSTANCE(view)

        // Hide ugly redundant secondary "REASON FOR KICK" header
        (view as ViewGroup).nestedChildAt(2, 0, 1).visibility = View.GONE

        header.text = title

        body.apply {
            text = description
            movementMethod = LinkMovementMethod.getInstance()
        }

        inputLayout.apply {
            hint = placeholder ?: title
            inputType?.let { editText?.inputType = it }
        }

        val buttonLayout = okButton.parent as LinearLayout
        buttonLayout.apply {
            // The button has no room to breathe - Discord moment, it doesn't even line up with the input field ~ Whatever, fix that
            setPadding(
                paddingLeft,
                paddingTop,
                paddingRight * 2,
                paddingBottom
            )
        }

        okButton.apply {
            setBackgroundColor(
                view.getResources()
                    .getColor(R.c.uikit_btn_bg_color_selector_brand, view.getContext().theme)
            )
        }

        onDialogShownListener?.onDialogShown(view)
    }

    /**
     * Returns the root layout of this dialog.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     */
    public val root: LinearLayout
        get() = binding.a

    /**
     * Returns the cancel [MaterialButton] of this dialog.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     * @see [setOnCancelListener]
     */
    public val cancelButton: MaterialButton
        get() = binding.c

    /**
     * Returns the OK [MaterialButton] of this dialog.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     * @see [setOnOkListener]
     */
    public val okButton: MaterialButton
        @JvmName("getOKButton")
        get() = binding.d

    /**
     * Returns the body of this dialog.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     * @see [setDescription]
     */
    public val body: TextView
        get() = binding.b

    /**
     * Returns the header of this dialog.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     * @see [setTitle]
     */
    public val header: TextView
        get() = binding.f

    /**
     * Returns the [TextInputLayout] of this dialog.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     * @see [setInputType]
     * @see [input]
     */
    public val inputLayout: TextInputLayout
        get() = binding.e

    /**
     * Returns the input the user entered.
     * Should only be called from within onClickHandlers or onViewBound as it will likely throw a [NullPointerException] in other cases
     * @see [inputLayout]
     */
    public val input: String
        get() = inputLayout.editText!!.getText().toString()

    /**
     * Sets the title of this dialog
     * @param title The description
     * @return Builder for chaining
     */
    public fun setTitle(title: CharSequence?): InputDialog = apply {
        this.title = title
    }

    /**
     * Sets the description of this dialog
     * @param description The description
     * @return Builder for chaining
     */
    public fun setDescription(description: CharSequence?): InputDialog = apply {
        this.description = description
    }

    /**
     * Sets the placeholder text for the input field (By default the title if set or "Text")
     * @param placeholder The placeholder text
     * @return Builder for chaining
     */
    public fun setPlaceholderText(placeholder: CharSequence?): InputDialog = apply {
        this.placeholder = placeholder
    }

    /**
     * Sets the [View.OnClickListener] that will be called when the OK button is pressed (By default simply closes this dialog)
     * @param listener The listener
     * @return Builder for chaining
     */
    public fun setOnOkListener(listener: View.OnClickListener?): InputDialog = apply {
        onOkListener = listener
    }

    /**
     * Sets the [View.OnClickListener] that will be called when the cancel button is pressed (By default simply closes this dialog)
     * @param listener The listener
     * @return Builder for chaining
     */
    public fun setOnCancelListener(listener: View.OnClickListener?): InputDialog = apply {
        onCancelListener = listener
    }

    /**
     * Sets the [android.text.InputType]
     * @param type The input type
     * @return Builder for chaining
     */
    public fun setInputType(type: Int): InputDialog = apply {
        inputType = type
    }

    /**
     * Sets the [InputDialog.onDialogShownListener] that will be called when the dialog is shown
     * @param listener Listener
     */
    public fun setOnDialogShownListener(listener: OnDialogShownListener?) {
        onDialogShownListener = listener
    }

    public interface OnDialogShownListener {
        public fun onDialogShown(v: View?)
    }

    public companion object {
        private val resId = getResId("widget_kick_user", "layout")
    }
}
