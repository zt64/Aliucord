package com.aliucord.fragments

import android.view.*
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aliucord.Utils.getResId
import com.aliucord.utils.DimenUtils.dp
import com.discord.app.AppDialog
import com.lytefast.flexinput.R

/**
 * Creates a dialog similar to the language picker, allows you to supply a list of options for a user to select from.
 */
public class SelectDialog : AppDialog(getResId("widget_settings_language_select", "layout")) {
    private inner class Adapter(private val items: List<String>) : RecyclerView.Adapter<Adapter.ViewHolder?>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            TextView(
                /* context = */ parent.context,
                /* attrs = */ null,
                /* defStyleAttr = */ 0,
                /* defStyleRes = */ R.i.UiKit_Settings_Item_Icon
            ).apply {
                layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            }
        )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
            fun bind(position: Int) {
                textView.run {
                    text = items[position]
                    setOnClickListener { onItemPicked(position) }
                }
            }
        }
    }

    /***
     * Called when an item is selected
     */
    @JvmField
    public var onResultListener: ((Int) -> Unit)? = null

    /***
     * Items for the user to pick from
     */
    @JvmField
    public var items: List<String> = listOf()

    /***
     * Title displayed above the item list
     */
    @JvmField
    public var title: String = "Select an item"

    override fun onViewBound(view: View) {
        super.onViewBound(view)

        val rv = view.findViewById<RecyclerView>(getResId("settings_language_select_list", "id"))

        rv.adapter = Adapter(items)
        (rv.parent as ViewGroup).removeViewAt(0)

        val titleTv = TextView(view.context, null, 0, R.i.UiKit_Sheet_Header_Title).apply {
            text = title
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            val p = 16.dp

            setPadding(p, p, p, p)
        }
        (rv.parent as ViewGroup).addView(titleTv, 0)
    }

    private fun onItemPicked(position: Int) {
        onResultListener?.invoke(position)
        dismiss()
    }
}
