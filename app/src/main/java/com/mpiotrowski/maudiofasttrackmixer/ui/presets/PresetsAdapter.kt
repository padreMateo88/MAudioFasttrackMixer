package com.mpiotrowski.maudiofasttrackmixer.ui.presets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.databinding.ItemPresetBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel


class PresetsAdapter(
    private val appCompatActivity: AppCompatActivity,
    private val mainViewModel: MainViewModel
) :
    RecyclerView.Adapter<PresetsAdapter.PresetsViewHolder>(), PresetSwipeCallback.SwipeListener {
    private lateinit var viewGroup : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PresetsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPresetBinding.inflate(inflater,parent,false)
        viewGroup = parent
        binding.lifecycleOwner = appCompatActivity
        return PresetsViewHolder(binding)
    }

    class PresetsViewHolder(var customView : ItemPresetBinding) : RecyclerView.ViewHolder(customView.root)

    override fun onBindViewHolder(holder: PresetsViewHolder, position: Int) {
        mainViewModel.currentPreset.scenesByOrder[position+1]?.let {
            holder.customView.scene = it.scene
        }
    }

    override fun getItemCount(): Int {
        return mainViewModel.currentPreset.scenes.size
    }

    override fun swipeRight(adapterPosition: Int) {
        mainViewModel.removePreset(mainViewModel.currentPreset)
    }

    override fun swipeLeft(adapterPosition: Int) {
        mainViewModel.loadPreset(mainViewModel.currentPreset)
    }
}