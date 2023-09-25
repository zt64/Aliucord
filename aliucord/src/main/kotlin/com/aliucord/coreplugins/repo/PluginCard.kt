package com.aliucord.coreplugins.repo

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.widget.GridLayout
import android.widget.GridLayout.LayoutParams
import android.widget.GridLayout.spec
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aliucord.Constants.Fonts.WHITNEY_SEMIBOLD
import com.aliucord.utils.DimenUtils.defaultCardRadius
import com.aliucord.utils.DimenUtils.defaultPadding
import com.aliucord.utils.DimenUtils.dpToPx
import com.aliucord.views.*
import com.aliucord.widgets.LinearLayout
import com.discord.utilities.color.ColorCompat
import com.google.android.material.card.MaterialCardView
import com.lytefast.flexinput.R

@Suppress("SetTextI18n")
internal class PluginCard(ctx: Context) : MaterialCardView(ctx) {
    private val root: LinearLayout
    val titleView: TextView
    val descriptionView: TextView
    private val buttonLayout: GridLayout
    val installButton: Button
    val uninstallButton: DangerButton
    val repoButton: ToolbarButton
    val changeLogButton: ToolbarButton

    // com.aliucord.widgets.PluginCard
    init {
        setRadius(defaultCardRadius.toFloat())
        setCardBackgroundColor(ColorCompat.getThemedColor(ctx, R.b.colorBackgroundSecondary))
        setLayoutParams(LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        val p = defaultPadding
        val p2 = p / 2

        root = LinearLayout(ctx)
        titleView = TextView(ctx).apply {
            textSize = 16.0f
            setTypeface(ResourcesCompat.getFont(ctx, WHITNEY_SEMIBOLD))
            movementMethod = LinkMovementMethod.getInstance()
            setTextColor(ColorCompat.getColor(ctx, R.c.primary_dark_200))
            val px = dpToPx(15)
            setPadding(px, px, px, px)
        }

        root.addView(titleView)
        root.addView(Divider(ctx))
        descriptionView = TextView(ctx, null, 0, R.i.UiKit_Settings_Item_Addition).apply {
            setPadding(p, p, p, p2)
        }
        root.addView(descriptionView)
        buttonLayout = GridLayout(ctx).apply {
            rowCount = 1
            columnCount = 5
            useDefaultMargins = true
            setPadding(p2, 0, p2, 0)
        }
        installButton = Button(ctx).apply {
            text = "Install"
        }
        uninstallButton = DangerButton(ctx).apply {
            text = "Uninstall"
            visibility = GONE
        }

        repoButton = ToolbarButton(ctx).apply {
            setImageDrawable(
                ContextCompat.getDrawable(ctx, R.e.ic_account_github_white_24dp)
            )
        }
        changeLogButton = ToolbarButton(ctx).apply {
            setImageDrawable(ContextCompat.getDrawable(ctx, R.e.ic_history_white_24dp))
        }
        buttonLayout.addView(installButton, LayoutParams(spec(0), spec(4)))
        buttonLayout.addView(uninstallButton, LayoutParams(spec(0), spec(4)))
        val params = LayoutParams(spec(0), spec(0)).apply {
            setGravity(Gravity.CENTER_VERTICAL)
        }
        buttonLayout.addView(repoButton, params)
        val clparams = LayoutParams(spec(0), spec(1)).apply {
            setGravity(Gravity.CENTER_VERTICAL)
        }
        buttonLayout.addView(changeLogButton, clparams)
        root.addView(buttonLayout)
        addView(root)
    }
}
