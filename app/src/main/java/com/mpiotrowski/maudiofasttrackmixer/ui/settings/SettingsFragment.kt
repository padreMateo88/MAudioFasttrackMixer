package com.mpiotrowski.maudiofasttrackmixer.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentSettingsBinding
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SettingsFragment: DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<SettingsViewModel> {
        viewModelFactory
    }

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = requireActivity()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = viewModel
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