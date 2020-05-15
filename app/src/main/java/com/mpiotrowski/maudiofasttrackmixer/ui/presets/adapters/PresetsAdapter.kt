package com.mpiotrowski.maudiofasttrackmixer.ui.presets.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.databinding.ItemPresetBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.PresetSwipeCallback
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs.LoadDeletePresetDialog


class PresetsAdapter(
    private val appCompatActivity: AppCompatActivity,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<PresetsAdapter.PresetsViewHolder>(),
    PresetSwipeCallback.SwipeListener {

    private val loadListener = object: LoadDeletePresetDialog.DialogListener{
        override fun onActionConfirmed(presetWithScenes: PresetWithScenes) {
            mainViewModel.loadPreset(presetWithScenes)
        }
    }

    private val removeListener = object: LoadDeletePresetDialog.DialogListener{
        override fun onActionConfirmed(presetWithScenes: PresetWithScenes) {
            mainViewModel.removePreset(presetWithScenes)
        }
    }

    private lateinit var viewGroup : ViewGroup

    override fun getItemId(position: Int): Long {
        return  mainViewModel.currentState.value?.scenesByOrder?.get(position)?.scene?.sceneId ?: -1
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
        mainViewModel.getPreset(position)?.let {
            holder.customView.preset = it.preset
            if(it == mainViewModel.selectedPreset.value)
                colorResource = R.color.colorPrimaryDark
        }
        holder.customView.cardViewPresetItemBackground.setCardBackgroundColor(
            ContextCompat.getColor(holder.customView.root.context,colorResource)
        )

        holder.customView.root.setOnClickListener {
            if(holder.adapterPosition in 0 until mainViewModel.getPresetsCount()) {
                    mainViewModel.selectPreset(holder.adapterPosition)
                    this@PresetsAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return mainViewModel.getPresetsCount()
    }

    override fun swipeRight(adapterPosition: Int) {
        val presetToDelete = mainViewModel.getPreset(adapterPosition)
        val presetToDeleteId = presetToDelete?.preset?.presetId

        if(presetToDeleteId == mainViewModel.currentPresetId) {
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
        val currentState = mainViewModel.currentState.value?.preset
        val currentPresetId = mainViewModel.currentPresetId
        val presetToLoadId = mainViewModel.getPreset(adapterPosition)?.preset?.presetId

        if(presetToLoadId == currentPresetId && (currentState?.isDirty == false)) {
            Toast.makeText(
                appCompatActivity,
                appCompatActivity.getString(
                    R.string.message_preset_already_loaded,
                    currentState.presetName
                ),
                Toast.LENGTH_LONG
            ).show()
        } else {
            mainViewModel.allPresets.value?.get(adapterPosition)?.let {
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