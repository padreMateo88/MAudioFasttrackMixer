package com.mpiotrowski.maudiofasttrackmixer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return viewDataBinding.root
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}