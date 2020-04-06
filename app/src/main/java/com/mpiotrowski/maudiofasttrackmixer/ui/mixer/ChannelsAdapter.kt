package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.databinding.ChannelsAdapterItemBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel

class ChannelsAdapter(
    private val appCompatActivity: AppCompatActivity,
    private val mainViewModel: MainViewModel
) :
    RecyclerView.Adapter<ChannelsAdapter.ChannelViewHolder>() {
    private lateinit var viewGroup : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ChannelViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChannelsAdapterItemBinding.inflate(inflater,parent,false)
        this.viewGroup = parent
        binding.viewmodel = ViewModelProviders.of(appCompatActivity).get(MainViewModel::class.java)
        binding.lifecycleOwner = appCompatActivity
        return ChannelViewHolder(binding)
    }

    class ChannelViewHolder(var customView : ChannelsAdapterItemBinding) : RecyclerView.ViewHolder(customView.root)

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.customView.root.minimumWidth = viewGroup.width/mainViewModel.audioChannels.value!!.size
        holder.customView.audioChannel = mainViewModel.audioChannels.value!![position]
        holder.customView.fxSend = mainViewModel.fxSends.value!![position]
    }

    override fun getItemCount() = mainViewModel.audioChannels.value!!.size
}
