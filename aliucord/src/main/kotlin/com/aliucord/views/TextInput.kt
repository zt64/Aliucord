/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.aliucord.Utils.getResId
import com.aliucord.Utils.nestedChildAt
import com.aliucord.Utils.tintToTheme
import com.aliucord.utils.DimenUtils.defaultCardRadius
import com.google.android.material.textfield.TextInputLayout

public class TextInput @JvmOverloads constructor(
    context: Context,
    hint: CharSequence? = null,
    value: String? = null,
    textChangedListener: TextWatcher? = null,
    endIconOnClick: OnClickListener? = null
) : CardView(context) {
    /**
     * Returns the root layout
     * @return TextInputLayout
     */
    public val layout: TextInputLayout

    public fun getRoot(): TextInputLayout = layout

    init {
        val root = LinearLayout(context)
        LayoutInflater.from(context).inflate(getResId("widget_change_guild_identity", "layout"), root)
        layout = root.findViewById(getResId("set_nickname_text", "id"))!!
        (layout.parent as CardView).removeView(layout)
        addView(layout)
        setCardBackgroundColor(Color.TRANSPARENT)

        getRoot().hint = hint ?: "Enter Text"

        if (!value.isNullOrEmpty()) editText.setText(value)

        // if(placeholder != null && !placeholder.isEmpty()) getEditText().setPlaceholder(placeholder);
        radius = defaultCardRadius.toFloat()

        getRoot().isEndIconVisible = false

        editText.addTextChangedListener(/* watcher = */ textChangedListener
            ?: object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    this@TextInput.getRoot().isEndIconVisible = s.isNotEmpty()
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
            })
        getRoot().setEndIconOnClickListener(
            endIconOnClick ?: OnClickListener { editText.setText("") })
    }

    public val editText: EditText
        /**
         * Returns the main edit text
         * @return EditText
         */
        get() = getRoot().nestedChildAt<EditText>(0, 0)

    /**
     * Sets the hint message
     * @param hint The hint
     * @return self
     * @noinspection UnusedReturnValue
     */
    public fun setHint(hint: CharSequence): TextInput = apply {
        getRoot().hint = hint
    }

    /**
     * Sets the hint message
     * @param hint The hint res id
     * @return self
     */
    public fun setHint(@StringRes hint: Int): TextInput = apply {
        getRoot().setHint(hint)
    }

    /**
     * Sets the end icon to the specified drawable and sets it tint to the users's chosen theme
     * @param icon End icon drawable
     * @return self
     */
    public fun setThemedEndIcon(icon: Drawable): TextInput = apply {
        getRoot().endIconDrawable = tintToTheme(icon.mutate())
    }

    /**
     * Sets the end icon to the specified drawable and sets it tint to the users's chosen theme
     * @param icon End icon drawable res id
     * @return self
     */
    public fun setThemedEndIcon(@DrawableRes icon: Int): TextInput = apply {
        getRoot().endIconDrawable = tintToTheme(ContextCompat.getDrawable(getRoot().context, icon))
    }
}
