package com.mpiotrowski.maudiofasttrackmixer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentSettingsBinding
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import kotlinx.android.synthetic.main.layout_save_preset.*

class SettingsFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val effectTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, FxSettings.FxType.values())
        effectTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEffectType.adapter = effectTypeAdapter

        val sampleRateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, SampleRate.values())
        effectTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSampleRate.adapter = sampleRateAdapter
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}