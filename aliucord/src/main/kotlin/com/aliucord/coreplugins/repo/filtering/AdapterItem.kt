package com.aliucord.coreplugins.repo.filtering

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants
import com.aliucord.Utils.createCheckedSetting
import com.aliucord.utils.DimenUtils.dpToPx
import com.discord.utilities.color.ColorCompat
import com.discord.views.CheckedSetting
import com.lytefast.flexinput.R

internal class AdapterItem(
    context: Context, //-1,0 is spinner,1 is checkbox
    var viewType: Int = 0
) : RelativeLayout(context) {
    var textView: TextView
    var type: FilterAdapter.FilterType? = null
    var setting: View? = null

    init {
        textView = TextView(context).apply {
            val px = dpToPx(15)
            setTextColor(ColorCompat.getColor(context, R.c.primary_dark_200))
            textSize = 16.0f
            setPadding(px, px, px, px)
            setTypeface(ResourcesCompat.getFont(context, Constants.Fonts.WHITNEY_SEMIBOLD))
            movementMethod = LinkMovementMethod.getInstance()
        }
        addView(textView)
        when (viewType) {
            -1, 0 -> setting = Spinner(context)
            1 -> {
                setting = createCheckedSetting(context, CheckedSetting.ViewType.CHECK, "", "")
                (setting as CheckedSetting).isChecked = true
            }
        }
        addView(setting)
        val params = setting!!.layoutParams as LayoutParams
        params.addRule(ALIGN_PARENT_RIGHT, TRUE)
        setting!!.setLayoutParams(params)
    }
}
