package com.mpiotrowski.maudiofasttrackmixer.ui.presets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentPresetsBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.MainFragment
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel

class PresetsFragment : MainFragment() {

    private lateinit var viewDataBinding: FragmentPresetsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentPresetsBinding.inflate(inflater, container, false)
        return viewDataBinding.root
    }

    companion object {
        fun newInstance() = PresetsFragment()
    }
}