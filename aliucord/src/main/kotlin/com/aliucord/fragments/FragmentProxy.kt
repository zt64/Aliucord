/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.fragments

import android.animation.Animator
import android.app.Activity
import android.content.*
import android.content.IntentSender.SendIntentException
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.animation.Animation
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.loader.app.LoaderManager
import com.aliucord.Main
import com.aliucord.Utils.appActivity
import com.discord.app.AppComponent
import rx.subjects.Subject
import java.io.FileDescriptor
import java.io.PrintWriter
import java.lang.reflect.Field

@Suppress("deprecation")
public open class FragmentProxy : Fragment(), AppComponent {
    private var mFragment: Fragment? = null

    override fun getUnsubscribeSignal(): Subject<Void, Void>? = (getmFragment() as? AppComponent)?.unsubscribeSignal

    override fun getLifecycle(): Lifecycle = getmFragment().lifecycle

    override fun getViewLifecycleOwner(): LifecycleOwner = getmFragment().getViewLifecycleOwner()

    override fun getViewLifecycleOwnerLiveData(): LiveData<LifecycleOwner> = getmFragment().viewLifecycleOwnerLiveData

    override fun setArguments(args: Bundle?): Unit = getmFragment().setArguments(args)

    override fun setInitialSavedState(state: SavedState?): Unit = getmFragment().setInitialSavedState(state)

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("getmFragment().setTargetFragment(fragment, requestCode)")
    )
    override fun setTargetFragment(fragment: Fragment?, requestCode: Int) {
        getmFragment().setTargetFragment(fragment, requestCode)
    }

    override fun getContext(): Context? = getmFragment().context

    override fun onHiddenChanged(hidden: Boolean): Unit = getmFragment().onHiddenChanged(hidden)

    @Deprecated("Deprecated in Java", ReplaceWith("getmFragment().setRetainInstance(retain)"))
    override fun setRetainInstance(retain: Boolean): Unit = getmFragment().setRetainInstance(retain)

    override fun setHasOptionsMenu(hasMenu: Boolean): Unit = getmFragment().setHasOptionsMenu(hasMenu)

    override fun setMenuVisibility(menuVisible: Boolean): Unit = getmFragment().setMenuVisibility(menuVisible)

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("getmFragment().setUserVisibleHint(isVisibleToUser)")
    )
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        getmFragment().setUserVisibleHint(isVisibleToUser)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("getmFragment().userVisibleHint"))
    override fun getUserVisibleHint(): Boolean = getmFragment().userVisibleHint

    @Deprecated("Deprecated in Java", ReplaceWith("getmFragment().loaderManager"))
    override fun getLoaderManager(): LoaderManager = getmFragment().loaderManager

    override fun startActivity(intent: Intent): Unit = getmFragment().startActivity(intent)

    override fun startActivity(intent: Intent, options: Bundle?): Unit = getmFragment().startActivity(intent, options)

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("getmFragment().startActivityForResult(intent, requestCode)")
    )
    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        getmFragment().startActivityForResult(intent, requestCode)
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("getmFragment().startActivityForResult(intent, requestCode, options)")
    )
    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        getmFragment().startActivityForResult(intent, requestCode, options)
    }

    @Deprecated("Deprecated in Java")
    @Throws(SendIntentException::class)
    override fun startIntentSenderForResult(
        intent: IntentSender,
        requestCode: Int,
        fillInIntent: Intent?,
        flagsMask: Int,
        flagsValues: Int,
        extraFlags: Int,
        options: Bundle?
    ) {
        getmFragment().startIntentSenderForResult(
            intent,
            requestCode,
            fillInIntent,
            flagsMask,
            flagsValues,
            extraFlags,
            options
        )
    }

    @Deprecated("Deprecated in Java", ReplaceWith("getmFragment().onActivityResult(requestCode, resultCode, data)"))
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getmFragment().onActivityResult(requestCode, resultCode, data)
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("getmFragment().onRequestPermissionsResult(requestCode, permissions, grantResults)")
    )
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        getmFragment().onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        return getmFragment().shouldShowRequestPermissionRationale(permission)
    }

    //    @NonNull
    //    @Override
    //    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
    ////        return mFragment.onGetLayoutInflater(savedInstanceState);
    //        try {
    //            return super.onGetLayoutInflater(savedInstanceState);
    //        } catch (Exception e) {
    //            return null;
    //        }
    //    }
    //
    //    @NonNull
    //    @Override
    //    @SuppressLint("RestrictedApi")
    //    public LayoutInflater getLayoutInflater(@Nullable Bundle savedFragmentState) {
    //        return mFragment.getLayoutInflater(savedFragmentState);
    //    }
    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        getmFragment().onInflate(context, attrs, savedInstanceState)
        super.onInflate(context, attrs, savedInstanceState)
    }

    @Deprecated("Deprecated in Java")
    override fun onInflate(activity: Activity, attrs: AttributeSet, savedInstanceState: Bundle?) {
        getmFragment().onInflate(activity, attrs, savedInstanceState)
        super.onInflate(activity, attrs, savedInstanceState)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("getmFragment().onAttachFragment(childFragment)"))
    override fun onAttachFragment(childFragment: Fragment) {
        getmFragment().onAttachFragment(childFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getmFragment().onAttach(context)
    }

    @Deprecated("Deprecated in Java")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        getmFragment().onAttach(activity)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return getmFragment().onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        return getmFragment().onCreateAnimator(transit, enter, nextAnim)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mFragment?.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    private var mView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = getmFragment()

        try {
            fragment.mFragmentManager = fragmentManager
            fragment.mHost = mHost
        } catch (e: Exception) {
            Main.logger.error(e)
        }

        mView = fragment.onCreateView(inflater, container, savedInstanceState)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getmFragment().onViewCreated(view, savedInstanceState)
    }

    override fun getView(): View? = getmFragment().view ?: mView

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        getmFragment().onActivityCreated(savedInstanceState)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        getmFragment().onViewStateRestored(savedInstanceState)
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onStart() {
        getmFragment().onStart()
        super.onStart()
    }

    override fun onResume() {
        getmFragment().onResume()
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        getmFragment().onSaveInstanceState(outState)
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        getmFragment().onMultiWindowModeChanged(isInMultiWindowMode)
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        getmFragment().onPictureInPictureModeChanged(isInPictureInPictureMode)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        getmFragment().onConfigurationChanged(newConfig)
        super.onConfigurationChanged(newConfig)
    }

    override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
        getmFragment().onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment)
    }

    override fun onPause() {
        getmFragment().onPause()
        super.onPause()
    }

    override fun onStop() {
        getmFragment().onStop()
        super.onStop()
    }

    override fun onLowMemory() {
        getmFragment().onLowMemory()
        super.onLowMemory()
    }

    override fun onDestroyView() {
        getmFragment().onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        getmFragment().onDestroy()
        super.onDestroy()
    }

    override fun onDetach() {
        getmFragment().onDetach()
        super.onDetach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        getmFragment().onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Unit = getmFragment().onPrepareOptionsMenu(menu)

    override fun onDestroyOptionsMenu(): Unit = getmFragment().onDestroyOptionsMenu()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return getmFragment().onOptionsItemSelected(item)
    }

    override fun onOptionsMenuClosed(menu: Menu) {
        getmFragment().onOptionsMenuClosed(menu)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        getmFragment().onCreateContextMenu(menu, v, menuInfo)
    }

    override fun registerForContextMenu(view: View) {
        getmFragment().registerForContextMenu(view)
    }

    override fun unregisterForContextMenu(view: View) {
        getmFragment().unregisterForContextMenu(view)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return getmFragment().onContextItemSelected(item)
    }

    override fun setEnterSharedElementCallback(callback: SharedElementCallback?) {
        getmFragment().setEnterSharedElementCallback(callback)
    }

    override fun setExitSharedElementCallback(callback: SharedElementCallback?) {
        getmFragment().setExitSharedElementCallback(callback)
    }

    override fun setEnterTransition(transition: Any?) {
        getmFragment().enterTransition = transition
    }

    override fun getEnterTransition(): Any? = getmFragment().getEnterTransition()

    override fun setReturnTransition(transition: Any?) {
        getmFragment().returnTransition = transition
    }

    override fun getReturnTransition(): Any? = getmFragment().getReturnTransition()

    override fun setExitTransition(transition: Any?) {
        getmFragment().exitTransition = transition
    }

    override fun getExitTransition(): Any? = getmFragment().getExitTransition()

    override fun setReenterTransition(transition: Any?) {
        getmFragment().reenterTransition = transition
    }

    override fun getReenterTransition(): Any? {
        return getmFragment().getReenterTransition()
    }

    override fun setSharedElementEnterTransition(transition: Any?) {
        getmFragment().sharedElementEnterTransition = transition
    }

    override fun getSharedElementEnterTransition(): Any? {
        return getmFragment().getSharedElementEnterTransition()
    }

    override fun setSharedElementReturnTransition(transition: Any?) {
        getmFragment().sharedElementReturnTransition = transition
    }

    override fun getSharedElementReturnTransition(): Any? {
        return getmFragment().getSharedElementReturnTransition()
    }

    override fun setAllowEnterTransitionOverlap(allow: Boolean) {
        getmFragment().allowEnterTransitionOverlap = allow
    }

    override fun getAllowEnterTransitionOverlap(): Boolean {
        return getmFragment().allowEnterTransitionOverlap
    }

    override fun setAllowReturnTransitionOverlap(allow: Boolean) {
        getmFragment().allowReturnTransitionOverlap = allow
    }

    override fun getAllowReturnTransitionOverlap(): Boolean {
        return getmFragment().allowReturnTransitionOverlap
    }

    override fun postponeEnterTransition(): Unit = getmFragment().postponeEnterTransition()

    override fun startPostponedEnterTransition() {
        getmFragment().startPostponedEnterTransition()
    }

    override fun dump(
        prefix: String,
        fd: FileDescriptor?,
        writer: PrintWriter,
        args: Array<String>?
    ) {
        getmFragment().dump(prefix, fd, writer, args)
    }

    private var didHack = false
    public open fun getmFragment(): Fragment {
        if (mFragment == null) {
            val bundle = arguments
            if (bundle != null) {
                val id = bundle.getString("AC_FRAGMENT_ID")
                mFragment = fragments[id]
                fragments.remove(id)
            }
        }

        // Horrible hack but hey it is better than crash
        if (mFragment == null) {
            if (!didHack) {
                Main.logger.warn("Proxied fragment is null. Closing...")
                didHack = true
                appActivity.onBackPressed()
            }
            return Fragment()
        }
        return mFragment as Fragment
    }

    public companion object {
        @JvmField
        public val fragments: MutableMap<String?, Fragment> = HashMap()

        private lateinit var _mFragmentManager: Field
        private var Fragment.mFragmentManager: FragmentManager?
            get() = _mFragmentManager[this] as FragmentManager
            set(value) {
                _mFragmentManager[this] = value
            }

        private lateinit var _mHost: Field
        private var Fragment.mHost: Any?
            get() = _mHost[this]
            set(value) {
                _mHost[this] = value
            }

        init {
            val cl = Fragment::class.java

            try {
                _mFragmentManager = cl.getDeclaredField("mFragmentManager")
                _mFragmentManager.isAccessible = true

                _mHost = cl.getDeclaredField("mHost")
                _mHost.isAccessible = true
            } catch (e: Exception) {
                Main.logger.error(e)
            }
        }
    }
}
