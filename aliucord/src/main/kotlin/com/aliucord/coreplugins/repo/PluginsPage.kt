@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS")

package com.aliucord.coreplugins.repo

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.*
import androidx.viewpager.widget.ViewPager
import com.aliucord.Constants
import com.aliucord.Constants.Fonts.WHITNEY_SEMIBOLD
import com.aliucord.Utils
import com.aliucord.coreplugins.repo.filtering.FilterAdapter
import com.aliucord.fragments.SettingsPage
import com.aliucord.utils.DimenUtils
import com.aliucord.views.TextInput
import com.aliucord.views.ToolbarButton
import com.discord.utilities.color.ColorCompat
import com.lytefast.flexinput.R
import java.io.File

internal class PluginsPage : SettingsPage() {
    // com.aliucord.settings.Plugins
    var adapter: PluginsAdapter? = null
    private var loadingIcon: ProgressBar? = null
    lateinit var searchBox: EditText
    var index = 0
    var requestMade = false

    @Suppress("NotifyDataSetChanged")
    fun makeSearch(input: String?, index: Int) {
        if (adapter != null && adapter!!.data.size >= index) {
            Utils.threadPool.execute {
                Utils.mainThread.post {
                    if (index == 0) {
                        loadingIcon!!.visibility = View.VISIBLE
                        adapter!!.data = ArrayList()
                        adapter!!.notifyDataSetChanged()
                    }
                }
                val plugs = PluginRepoAPI.getPlugins(input, index)
                adapter!!.data.addAll(plugs)
                Utils.mainThread.post {
                    loadingIcon!!.visibility = View.GONE
                    if (plugs.isNotEmpty()) adapter!!.notifyDataSetChanged()
                    setActionBarSubtitle("${adapter!!.data.size} Shown")
                }
                requestMade = false
            }
        }
    }

    fun makeSearch() {
        makeSearch(searchBox.getText().toString())
        searchBox.clearFocus()
    }

    fun makeSearch(input: String?) = makeSearch(input, 0)

    @Suppress("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        setActionBarTitle("Plugin Repo")
        val context = view.context
        val padding = DimenUtils.defaultPadding
        val p = padding / 2
        PluginRepoAPI.filters = HashMap()
        setActionBarSubtitle("Loading...")
        if (headerBar.findViewById<View?>(uniqueId) == null) {
            val pluginFolderBtn = ToolbarButton(context)
            pluginFolderBtn.setId(uniqueId)
            val params = Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.END
            params.setMarginEnd(p)
            pluginFolderBtn.setLayoutParams(params)
            pluginFolderBtn.setPadding(p, p, p, p)
            pluginFolderBtn.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.e.ic_open_in_new_white_24dp
                )
            )
            pluginFolderBtn.setOnClickListener {
                val dir = File(Constants.PLUGINS_PATH)
                if (!dir.exists() && !dir.mkdir()) {
                    Utils.showToast("Failed to create plugins directory!", true)
                    return@setOnClickListener
                }
                Utils.launchFileExplorer(dir)
            }
            // addHeaderButton(pluginFolderBtn)
            val input = TextInput(context)
            input.setHint(context.getString(R.h.search))
            val recyclerView = RecyclerView(context)
            recyclerView.setLayoutManager(
                LinearLayoutManager(
                    context,
                    RecyclerView.VERTICAL,
                    false
                )
            )
            adapter = PluginsAdapter(this, ArrayList())
            recyclerView.setAdapter(adapter)
            val shape = ShapeDrawable(RectShape())
            shape.setTint(Color.TRANSPARENT)
            shape.setIntrinsicHeight(padding)
            val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            decoration.setDrawable(shape)
            recyclerView.addItemDecoration(decoration)
            recyclerView.setPadding(0, padding, 0, 0)
            loadingIcon = ProgressBar(context)
            loadingIcon!!.isIndeterminate = true
            loadingIcon!!.visibility = View.GONE
            val filterView = RecyclerView(context)
            filterView.setLayoutManager(LinearLayoutManager(context, RecyclerView.VERTICAL, false))
            val filterAdapter = FilterAdapter(this)
            filterView.setAdapter(filterAdapter)
            filterView.addItemDecoration(decoration)
            filterView.setPadding(0, padding, 0, 0)
            filterView.visibility = View.GONE

            // https://github.com/Vendicated/AliucordPlugins/blob/main/DedicatedPluginSettings/src/main/java/dev/vendicated/aliucordplugs/dps/DedicatedPluginSettings.java#L63-L80
            val openDrawable = ContextCompat.getDrawable(context, R.e.ic_arrow_down_14dp)!!.mutate()
            openDrawable.setTint(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal))
            val closedDrawable = object : LayerDrawable(arrayOf(openDrawable)) {
                override fun draw(canvas: Canvas) {
                    val bounds = openDrawable.getBounds()
                    canvas.save()
                    canvas.rotate(270f, bounds.width() / 2f, bounds.height() / 2f)
                    super.draw(canvas)
                    canvas.restore()
                }
            }
            val header = TextView(context, null, 0, R.i.UiKit_Settings_Item_Header)
            header.text = "Filters"
            header.setTypeface(ResourcesCompat.getFont(context, WHITNEY_SEMIBOLD))
            header.setOnClickListener {
                TransitionManager.beginDelayedTransition(linearLayout)
                if (filterView.visibility == View.VISIBLE) {
                    filterView.visibility = View.GONE
                    header.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        closedDrawable,
                        null,
                        null,
                        null
                    )
                } else {
                    filterView.visibility = View.VISIBLE
                    header.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        openDrawable,
                        null,
                        null,
                        null
                    )
                }
            }
            header.setCompoundDrawablesRelativeWithIntrinsicBounds(closedDrawable, null, null, null)
            val px = DimenUtils.dpToPx(5)
            header.setPadding(px, px * 3, 0, px * 3)
            addView(input)
            addView(header)
            addView(filterView)
            addView(loadingIcon)
            addView(recyclerView)
            makeSearch("")
            searchBox = input.editText
            searchBox.setMaxLines(1)
            searchBox.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (s.toString().isEmpty()) {
                        makeSearch("")
                    }
                }
            })
            searchBox.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    makeSearch(searchBox.getText().toString())
                }
                false
            }
            val shit: NestedScrollView = linearLayout.parent as NestedScrollView
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!requestMade && newState == ViewPager.SCROLL_STATE_IDLE && !shit.canScrollVertically(
                            1
                        )
                    ) {
                        index += 50
                        requestMade = true
                        makeSearch(searchBox.getText().toString(), index)
                    }
                }
            })
        }
    }

    companion object {
        private val uniqueId = View.generateViewId()
    }
}
