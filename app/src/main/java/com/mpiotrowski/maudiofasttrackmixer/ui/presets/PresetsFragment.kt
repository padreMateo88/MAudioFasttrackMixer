package com.mpiotrowski.maudiofasttrackmixer.ui.presets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentPresetsBinding
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.adapters.PresetsAdapter
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.adapters.ScenesAdapter
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs.AddPresetDialog
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs.OverwritePresetDialog
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs.SavePresetDialog
import kotlinx.android.synthetic.main.fragment_presets.*

class PresetsFragment : Fragment(),
    SavePresetDialog.SavePresetListener,
    OverwritePresetDialog.OverwritePresetListener,
    AddPresetDialog.AddPresetListener {

    lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentPresetsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPresetsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = requireActivity()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        binding.viewmodel = viewModel
        prepareScenesRecyclerView()
        preparePresetsRecyclerView()
        setSavePresetButtonClickListener()
        prepareAddPresetFab()
        prepareCurrentPresetName()
    }

    private fun prepareCurrentPresetName() {
        viewModel.currentState.observe(requireActivity(), Observer {
            val currentState = viewModel.currentState.value?.preset
            val currentPresetName = currentState?.presetName
            textViewPresetName?.text =
                if (currentState?.isDirty == true)
                    "$currentPresetName*"
                else
                    currentPresetName
        })
    }

    private fun prepareAddPresetFab() {
        val presetNames = viewModel.allPresets.value?.map { it.preset.presetName } ?: emptyList()
        fabCreatePreset.setOnClickListener {
            AddPresetDialog(
                requireContext(),
                presetNames,
                this@PresetsFragment
            ).show()
        }
    }

    private fun setSavePresetButtonClickListener() {
//        binding.buttonSavePreset.setOnClickListener {
//            viewModel.currentPreset.value?.preset?.presetName?.let{
//                presetName -> SavePresetDialog(
//                    requireContext(),
//                    presetName,
//                    this@PresetsFragment
//                ).show()
//            }
//        }
    }

    override fun onPresetSaved(presetName: String) {
        if(!viewModel.saveCurrentPresetAs(presetName))
            OverwritePresetDialog(
                requireContext(),
                presetName,
                this@PresetsFragment
            ).show()
    }

    override fun onPresetOverwriteConfirmed(presetName: String) {
        viewModel.saveCurrentPresetAsExistingPreset(presetName)
    }

    private fun prepareScenesRecyclerView() {
        binding.recyclerViewScenes.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        val scenesAdapter = ScenesAdapter(requireActivity() as AppCompatActivity, viewModel)
        binding.recyclerViewScenes.adapter = scenesAdapter

        val callback = object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                scenesAdapter.onItemMoved(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewScenes)
        viewModel.selectedPreset.observe(requireActivity(), Observer {
            scenesAdapter.notifyDataSetChanged()
        })
    }

    private fun preparePresetsRecyclerView() {
        binding.recyclerViewPresets.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val presetsAdapter = PresetsAdapter(
                requireActivity() as AppCompatActivity,
                viewModel
            )
        presetsAdapter.setHasStableIds(true)
        binding.recyclerViewPresets.adapter = presetsAdapter
        val callback = PresetSwipeCallback(0, ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT), binding.recyclerViewPresets.adapter as PresetsAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewPresets)
        viewModel.allPresets.observe(requireActivity(), Observer {
            presetsAdapter.notifyDataSetChanged()
        })

        viewModel.selectedPreset.observe(requireActivity(), Observer {
            presetsAdapter.notifyDataSetChanged()
        })
    }

    companion object {
        fun newInstance() = PresetsFragment()
    }

    override fun onCreatePreset(presetName: String) {
        viewModel.createPreset(presetName)
    }
}