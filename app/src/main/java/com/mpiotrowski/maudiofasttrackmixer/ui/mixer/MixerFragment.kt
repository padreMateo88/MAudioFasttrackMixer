package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentMixerBinding

class MixerFragment : Fragment(){

    private lateinit var viewDataBinding: FragmentMixerBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
       viewDataBinding = FragmentMixerBinding.inflate(inflater, container, false)
       viewDataBinding.recyclerViewChannels.adapter =
       return viewDataBinding.root
    }

    companion object {
        fun newInstance() =
            MixerFragment()
    }
}