package com.mpiotrowski.maudiofasttrackmixer.ui

import android.app.Activity
import androidx.fragment.app.Fragment

open class MainFragment : Fragment() {
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (activity != null) {
            val mainActivity = activity as MainActivity
            mainActivity.setBottomBarVisibility()
        }
    }
}