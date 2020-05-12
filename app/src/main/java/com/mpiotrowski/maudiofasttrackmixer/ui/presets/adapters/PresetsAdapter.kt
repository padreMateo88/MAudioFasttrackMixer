package com.mpiotrowski.maudiofasttrackmixer.ui.presets.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
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

    private var selectedItemId = -1
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
        mainViewModel.currentState.value?.scenesByOrder?.get(position+1)?.let {
            holder.customView.scene = it.scene
        }

        val colorResource = if(selectedItemId == holder.adapterPosition) R.color.colorPrimaryDark else R.color.darkerGray
        holder.customView.cardViewPresetItemBackground.setCardBackgroundColor(
            ContextCompat.getColor(holder.customView.root.context,colorResource)
        )
        holder.customView.root.setOnClickListener {
            selectedItemId = holder.adapterPosition
            this@PresetsAdapter.notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mainViewModel.currentState.value?.scenes?.size ?: 0
    }

    override fun swipeRight(adapterPosition: Int) {
        mainViewModel.currentState.value?.let {
            LoadDeletePresetDialog(
                appCompatActivity,
                it,
                removeListener,
                R.string.delete_preset
            ).show()
        }
    }

    override fun swipeLeft(adapterPosition: Int) {
        mainViewModel.currentState.value?.let {
            LoadDeletePresetDialog(
                appCompatActivity,
                it,
                loadListener,
                R.string.load_preset
            ).show()
        }
    }
}