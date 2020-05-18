package com.mpiotrowski.maudiofasttrackmixer.ui.presets.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.databinding.ItemPresetBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.PresetSwipeCallback
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.PresetsViewModel
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs.LoadDeletePresetDialog


class PresetsAdapter(
    private val appCompatActivity: AppCompatActivity,
    private val viewModel: PresetsViewModel
) : RecyclerView.Adapter<PresetsAdapter.PresetsViewHolder>(),
    PresetSwipeCallback.SwipeListener {

    var presetsList: List<PresetWithScenes>? = emptyList()
    var selectedPreset: PresetWithScenes? = null

    private val loadListener = object: LoadDeletePresetDialog.DialogListener{
        override fun onActionConfirmed(presetWithScenes: PresetWithScenes) {
            viewModel.loadPreset(presetWithScenes)
        }
    }

    private val removeListener = object: LoadDeletePresetDialog.DialogListener{
        override fun onActionConfirmed(presetWithScenes: PresetWithScenes) {
            viewModel.removePreset(presetWithScenes)
        }
    }

    private lateinit var viewGroup : ViewGroup

    override fun getItemId(position: Int): Long {
        return  position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PresetsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPresetBinding.inflate(inflater,parent,false)
        viewGroup = parent
        binding.lifecycleOwner = appCompatActivity
        return PresetsViewHolder(
            binding
        )
    }

    class PresetsViewHolder(var customView : ItemPresetBinding) : RecyclerView.ViewHolder(customView.root)

    override fun onBindViewHolder(holder: PresetsViewHolder, position: Int) {
        var colorResource = R.color.darkerGray
        presetsList?.get(position)?.let {
            holder.customView.preset = it.preset
            if(it == selectedPreset)
                colorResource = R.color.colorPrimaryDark
        }
        holder.customView.cardViewPresetItemBackground.setCardBackgroundColor(
            ContextCompat.getColor(holder.customView.root.context,colorResource)
        )

        holder.customView.root.setOnClickListener {
            if(holder.adapterPosition in 0 until (presetsList?.size ?: 0)) {
                    viewModel.selectPreset(holder.adapterPosition)
                    this@PresetsAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return presetsList?.size ?: 0
    }

    override fun swipeRight(adapterPosition: Int) {
        val presetToDelete = presetsList?.get(adapterPosition)
        val presetToDeleteId = presetToDelete?.preset?.presetId

        if(presetToDeleteId == viewModel.currentPresetId) {
            Toast.makeText(
                appCompatActivity,
                appCompatActivity.getString(
                    R.string.message_cant_delete_preset,
                    presetToDelete.preset.presetName
                ),
                Toast.LENGTH_LONG
            ).show()
        } else {
            presetToDelete?.let {
                LoadDeletePresetDialog(
                    appCompatActivity,
                    it,
                    removeListener,
                    R.string.delete_preset
                ).show()
            }
        }
    }

    override fun swipeLeft(adapterPosition: Int) {

        val currentState = viewModel.getCurrentState()?.preset
        val currentPresetId = viewModel.currentPresetId
        val presetToLoadId = presetsList?.get(adapterPosition)?.preset?.presetId

        if(presetToLoadId == currentPresetId && (!viewModel.isCurrentStateDirty())) {
            if (currentState != null) {
                Toast.makeText(
                    appCompatActivity,
                    appCompatActivity.getString(
                        R.string.message_preset_already_loaded,
                        currentState.presetName
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            presetsList?.get(adapterPosition)?.let {
                presetToLoad -> LoadDeletePresetDialog(
                    appCompatActivity,
                    presetToLoad,
                    loadListener,
                    R.string.load_preset
                ).show()
            }
        }
    }
}