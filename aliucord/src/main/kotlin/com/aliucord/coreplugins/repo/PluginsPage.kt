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
import android.widget.Toolbar.LayoutParams.WRAP_CONTENT
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
import com.aliucord.utils.DimenUtils.defaultPadding
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

    private val pluginsDir = File(Constants.PLUGINS_PATH)

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
        makeSearch(searchBox.text.toString())
        searchBox.clearFocus()
    }

    fun makeSearch(input: String?) = makeSearch(input, 0)

    @Suppress("SetTextI18n")
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        setActionBarTitle("Plugin Repo")
        val context = view.context
        val p = defaultPadding / 2
        PluginRepoAPI.filters = hashMapOf()
        setActionBarSubtitle("Loading...")

        if (headerBar.findViewById<View?>(uniqueId) != null) return

        val pluginFolderBtn = ToolbarButton(context).apply {
            id = uniqueId
            layoutParams = Toolbar.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                gravity = Gravity.END
                marginEnd = p
            }
            setPadding(p, p, p, p)
            setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.e.ic_open_in_new_white_24dp
                )
            )
            setOnClickListener {
                if (!pluginsDir.exists() && !pluginsDir.mkdir()) {
                    Utils.showToast("Failed to create plugins directory!", true)
                    return@setOnClickListener
                }
                Utils.launchFileExplorer(pluginsDir)
            }
        }
        // addHeaderButton(pluginFolderBtn)
        val input = TextInput(context).apply {
            setHint(context.getString(R.h.search))
        }
        adapter = PluginsAdapter(this, arrayListOf())

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
            val shape = ShapeDrawable(RectShape()).apply {
                setTint(Color.TRANSPARENT)
                intrinsicHeight = defaultPadding
            }

            setDrawable(shape)
        }
        val recyclerView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            this.adapter = adapter

            addItemDecoration(decoration)
            setPadding(0, defaultPadding, 0, 0)
        }

        loadingIcon = ProgressBar(context).apply {
            isIndeterminate = true
            visibility = View.GONE
        }
        val filterAdapter = FilterAdapter(this)

        // https://github.com/Vendicated/AliucordPlugins/blob/main/DedicatedPluginSettings/src/main/java/dev/vendicated/aliucordplugs/dps/DedicatedPluginSettings.java#L63-L80
        val openDrawable = ContextCompat.getDrawable(context, R.e.ic_arrow_down_14dp)!!.mutate().apply {
            setTint(ColorCompat.getThemedColor(context, R.b.colorInteractiveNormal))
        }
        val closedDrawable = object : LayerDrawable(arrayOf(openDrawable)) {
            override fun draw(canvas: Canvas) {
                val bounds = openDrawable.getBounds()
                canvas.save()
                canvas.rotate(270f, bounds.width() / 2f, bounds.height() / 2f)
                super.draw(canvas)
                canvas.restore()
            }
        }
        val filterView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = filterAdapter
            addItemDecoration(decoration)
            setPadding(0, defaultPadding, 0, 0)
            visibility = View.GONE
        }
        val header = TextView(context, null, 0, R.i.UiKit_Settings_Item_Header).apply {
            text = "Filters"
            typeface = ResourcesCompat.getFont(context, WHITNEY_SEMIBOLD)
            setOnClickListener {
                TransitionManager.beginDelayedTransition(linearLayout)
                if (filterView.visibility == View.VISIBLE) {
                    filterView.visibility = View.GONE
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                        closedDrawable,
                        null,
                        null,
                        null
                    )
                } else {
                    filterView.visibility = View.VISIBLE
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                        openDrawable,
                        null,
                        null,
                        null
                    )
                }
            }
            setCompoundDrawablesRelativeWithIntrinsicBounds(closedDrawable, null, null, null)
            val px = DimenUtils.dpToPx(5)
            setPadding(px, px * 3, 0, px * 3)
        }

        addView(input)
        addView(header)
        addView(filterView)
        addView(loadingIcon)
        addView(recyclerView)
        makeSearch("")
        searchBox = input.editText.apply {
            maxLines = 1
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (s.isEmpty()) makeSearch("")
                }
            })
            searchBox.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    makeSearch(searchBox.text.toString())
                }
                false
            }
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
                    makeSearch(searchBox.text.toString(), index)
                }
            }
        })
    }

    companion object {
        private val uniqueId = View.generateViewId()
    }
}
