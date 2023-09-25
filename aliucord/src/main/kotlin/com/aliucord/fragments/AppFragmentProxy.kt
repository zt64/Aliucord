/*
 * Copyright (c) 2021 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */
package com.aliucord.fragments

import androidx.fragment.app.Fragment
import com.discord.app.AppActivity

public class AppFragmentProxy : FragmentProxy() {
    private var mFragment: Fragment? = null

    override fun getmFragment(): Fragment {
        if (mFragment == null) {
            val activity = activity as AppActivity?

            if (activity != null) {
                val id = activity.c().getStringExtra("AC_FRAGMENT_ID")
                mFragment = fragments[id]
                fragments -= id
            }
        }

        return mFragment ?: super.getmFragment()
    }
}
