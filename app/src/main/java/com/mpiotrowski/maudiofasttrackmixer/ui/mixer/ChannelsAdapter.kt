package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.databinding.LayoutChannelItemBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import com.mpiotrowski.maudiofasttrackmixer.ui.views.audio_channel.AudioChannelView


class ChannelsAdapter(
    private val appCompatActivity: AppCompatActivity,
    private val mainViewModel: MainViewModel
) :
    RecyclerView.Adapter<ChannelsAdapter.ChannelViewHolder>() {
    lateinit var viewGroup : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ChannelViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutChannelItemBinding.inflate(inflater,parent,false)
        this.viewGroup = parent
        binding.viewmodel = ViewModelProviders.of(appCompatActivity).get(MainViewModel::class.java)
        binding.lifecycleOwner = appCompatActivity
        return ChannelViewHolder(binding)
    }

    class ChannelViewHolder(var customView : LayoutChannelItemBinding) : RecyclerView.ViewHolder(customView.root)

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.customView.root.minimumWidth = viewGroup.width/mainViewModel.audioChannels.value!!.size
        holder.customView.audioChannel = mainViewModel.audioChannels.value!![position]
    }

    override fun getItemCount() = mainViewModel.audioChannels.value!!.size
}
