package com.aliucord.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageButton
import com.aliucord.Utils.tintToTheme
import com.lytefast.flexinput.R

/**
 * Settings Header Toolbar Button
 */
public class ToolbarButton(context: Context) : AppCompatImageButton(
    /* context = */ ContextThemeWrapper(context, R.i.UiKit_ImageView_Clickable),
    /* attrs = */ null,
    /* defStyleAttr = */ 0
) {
    override fun setImageDrawable(drawable: Drawable?) {
        setImageDrawable(drawable, true)
    }

    public fun setImageDrawable(drawable: Drawable?, forceTint: Boolean) {
        val mutatedDrawable = if (forceTint && drawable != null) {
            tintToTheme(drawable.mutate())
        } else drawable
        super.setImageDrawable(mutatedDrawable)
    }
}
