package com.mpiotrowski.maudiofasttrackmixer.ui.presets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentPresetsBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel

class PresetsFragment : Fragment(), SavePresetDialog.SavePresetListener, OverwritePresetDialog.OverwritePresetListener {

    lateinit var viewModel: MainViewModel
    private lateinit var viewDataBinding: FragmentPresetsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentPresetsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        prepareScenesRecyclerView()
        preparePresetsRecyclerView()
        setSavePresetButtonClickListener()
    }

    private fun setSavePresetButtonClickListener() {
        viewDataBinding.buttonSavePreset.setOnClickListener {
            SavePresetDialog(requireContext(), viewModel.currentPreset.preset.presetName, this@PresetsFragment).show()
        }
    }

    override fun onPresetSaved(presetName: String) {
        if(!viewModel.saveCurrentPresetAs(presetName))
            OverwritePresetDialog(requireContext(), presetName, this@PresetsFragment).show()
    }

    override fun onPresetOverwriteConfirmed(presetName: String) {
        viewModel.saveCurrentPresetAsExistingPreset(presetName)
    }

    private fun prepareScenesRecyclerView() {
        viewDataBinding.recyclerViewScenes.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        viewDataBinding.recyclerViewScenes.adapter = ScenesAdapter(requireActivity() as AppCompatActivity, viewModel)

        val callback = object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                (viewDataBinding.recyclerViewScenes.adapter as ScenesAdapter).onItemMoved(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(viewDataBinding.recyclerViewScenes)
    }

    private fun preparePresetsRecyclerView() {
        viewDataBinding.recyclerViewPresets.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val presetsAdapter = PresetsAdapter(requireActivity() as AppCompatActivity, viewModel)
        presetsAdapter.setHasStableIds(true)
        viewDataBinding.recyclerViewPresets.adapter = presetsAdapter
        val callback = PresetSwipeCallback(0, ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT), viewDataBinding.recyclerViewPresets.adapter as PresetsAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(viewDataBinding.recyclerViewPresets)
    }

    companion object {
        fun newInstance() = PresetsFragment()
    }
}