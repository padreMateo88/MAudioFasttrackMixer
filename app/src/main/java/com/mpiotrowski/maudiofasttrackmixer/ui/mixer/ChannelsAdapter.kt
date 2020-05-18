package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.databinding.ChannelsAdapterItemBinding

class ChannelsAdapter(
    private val appCompatActivity: AppCompatActivity,
    private val viewModel: MixerViewModel
) :
    RecyclerView.Adapter<ChannelsAdapter.ChannelViewHolder>() {
    private lateinit var viewGroup : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ChannelViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChannelsAdapterItemBinding.inflate(inflater,parent,false)
        viewGroup = parent
        binding.viewmodel = viewModel
        binding.lifecycleOwner = appCompatActivity
        return ChannelViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ChannelViewHolder(var customView : ChannelsAdapterItemBinding) : RecyclerView.ViewHolder(customView.root)

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channelsCount = viewModel.getAudioChannelsNumber()
        holder.customView.root.layoutParams.width = if(channelsCount == 0) 0 else viewGroup.width/channelsCount
        holder.customView.channelIndex = position
    }

    override fun getItemCount(): Int {
        return viewModel.getAudioChannelsNumber()
    }
}
