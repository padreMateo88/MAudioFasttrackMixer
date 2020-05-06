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
    RecyclerView.Adapter<PresetsAdapter.PresetsViewHolder>() {
    private lateinit var viewGroup : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PresetsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPresetBinding.inflate(inflater,parent,false)
        viewGroup = parent
        binding.lifecycleOwner = appCompatActivity
        return PresetsViewHolder(binding)
    }

    fun onItemMoved(fromPosition: Int, toPosition: Int) {
        mainViewModel.swapPresetOrder(mainViewModel.currentPreset, fromPosition + 1, toPosition + 1)
        super.notifyItemMoved(fromPosition, toPosition)
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
}