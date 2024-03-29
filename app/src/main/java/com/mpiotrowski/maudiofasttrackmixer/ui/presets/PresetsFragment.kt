package com.mpiotrowski.maudiofasttrackmixer.ui.presets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentPresetsBinding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.adapters.PresetsAdapter
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.adapters.ScenesAdapter
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs.AddPresetDialog
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs.OverwritePresetDialog
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs.RenameOrCreateNewPresetDialog
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs.SavePresetDialog
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_presets.*
import javax.inject.Inject

class PresetsFragment : DaggerFragment(),
    AddPresetDialog.AddPresetListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<PresetsViewModel> {
        viewModelFactory
    }

    private lateinit var binding: FragmentPresetsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPresetsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = requireActivity()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewmodel = viewModel
        prepareScenesRecyclerView()
        preparePresetsRecyclerView()
        setSavePresetAsButtonClickListener()
        prepareAddPresetFab()
        prepareCurrentPresetName()
    }

    private fun prepareCurrentPresetName() {
        viewModel.currentState.observe(requireActivity(), Observer {
            val currentPresetName = viewModel.getCurrentPresetName()
            textViewPresetName?.text =
                if (viewModel.isCurrentStateDirty())
                    "$currentPresetName*"
                else
                    currentPresetName
        })
    }

    private fun prepareAddPresetFab() {
        val presetNames = viewModel.getAllPresets()?.map { it.preset.presetName } ?: emptyList()
        fabCreatePreset.setOnClickListener {
            AddPresetDialog(
                requireContext(),
                presetNames,
                this@PresetsFragment
            ).show()
        }
    }

//region save preset
    private fun setSavePresetAsButtonClickListener() {

        val confirmOverwritePresetListener = object: OverwritePresetDialog.ConfirmOverwritePresetListener {
            override fun onPresetOverwriteConfirmed(presetName: String) {
                viewModel.saveCurrentPresetAsExisting(presetName)
            }
        }

        val renameCreateNewListener = object: RenameOrCreateNewPresetDialog.RenameCreateNewListener{
            override fun onRenamePreset(presetName: String) {
                viewModel.saveAndRenameCurrentPreset(presetName)
            }

            override fun onSaveAsNewPreset(presetName: String) {
                viewModel.saveCurrentPresetAsNewPreset(presetName)
            }

        }

        val confirmSavePresetListener = object: SavePresetDialog.ConfirmSavePresetListener {
            override fun onSavePresetConfirmed(presetName: String) {
                if(!viewModel.saveCurrentPreset(presetName)) {
                    if (viewModel.getAllPresets()?.map { it.preset.presetName }
                            ?.contains(presetName) == true) {
                        OverwritePresetDialog(
                            requireContext(),
                            presetName,
                            confirmOverwritePresetListener
                        ).show()
                    } else {
                        viewModel.getCurrentPresetName()?.let {
                            RenameOrCreateNewPresetDialog(
                                requireContext(),
                                it,
                                presetName,
                                renameCreateNewListener
                            ).show()
                        }
                    }
                }
            }
        }

        binding.buttonSavePresetAs.setOnClickListener {
            viewModel.getCurrentPresetName()?.let{
                presetName -> SavePresetDialog(
                    requireContext(),
                    presetName,
                    confirmSavePresetListener
                ).show()
            }
        }

    }

//endregion save button

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
            scenesAdapter.scenesMap = viewModel.getSelectedPresetScenes()
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
            presetsAdapter.selectedPreset = viewModel.selectedPreset.value
            presetsAdapter.presetsList = viewModel.getAllPresets()
            presetsAdapter.notifyDataSetChanged()
        })

        viewModel.selectedPreset.observe(requireActivity(), Observer {
            presetsAdapter.selectedPreset = viewModel.selectedPreset.value
            presetsAdapter.presetsList = viewModel.getAllPresets()
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