package com.mpiotrowski.maudiofasttrackmixer.ui.presets.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.databinding.ItemSceneBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.PresetsViewModel


class ScenesAdapter(
    private val appCompatActivity: AppCompatActivity,
    private val mainViewModel: PresetsViewModel
) : RecyclerView.Adapter<ScenesAdapter.ScenesViewHolder>() {

    private lateinit var viewGroup : ViewGroup
    var scenesMap: Map<Int, SceneWithComponents>? = emptyMap()

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ScenesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSceneBinding.inflate(inflater,parent,false)
        viewGroup = parent
        binding.lifecycleOwner = appCompatActivity
        return ScenesViewHolder(binding)
    }

    fun onItemMoved(fromPosition: Int, toPosition: Int) {
        mainViewModel.swapScenesInSelectedPresetAndCurrentState(fromPosition + 1, toPosition + 1)
        super.notifyItemMoved(fromPosition, toPosition)
    }

    class ScenesViewHolder(var customView : ItemSceneBinding) : RecyclerView.ViewHolder(customView.root)

    override fun onBindViewHolder(holder: ScenesViewHolder, position: Int) {
        scenesMap?.get(position + 1)?.let {
            holder.customView.scene = it.scene
        }
    }

    override fun getItemCount(): Int {
            return scenesMap?.size ?: 0
    }
}