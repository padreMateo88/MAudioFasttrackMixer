package com.mpiotrowski.maudiofasttrackmixer.ui.settings

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentSettingsBinding.inflate(inflater, container, false)

        //TODO replace with actual ViewModel
        var effectTypeValues = arrayOf("Room1","Room2","Room3","Hall1","Hall2","Plate","Delay","Echo")
        val effectTypeAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, effectTypeValues)
        effectTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spinnerEffectType.adapter = effectTypeAdapter

        var sampleRateeValues = arrayOf("44.1kHz","48kHz","96kHz")
        val sampleRateAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, sampleRateeValues)
        sampleRateAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        viewDataBinding.spinnerSampleRate.adapter = sampleRateAdapter


        return viewDataBinding.root
    }

    companion object {
        fun newInstance() =
            SettingsFragment()
    }
}