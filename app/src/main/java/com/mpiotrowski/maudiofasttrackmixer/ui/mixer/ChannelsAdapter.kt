package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.ui.views.audio_channel.AudioChannelView


class ChannelsAdapter() :
    RecyclerView.Adapter<ChannelsAdapter.ChannelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ChannelViewHolder {
        val itemView = AudioChannelView(parent.context)
        itemView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return ChannelViewHolder(itemView)
    }

    class ChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var customView : AudioChannelView = view as AudioChannelView
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {

    }

    override fun getItemCount() = 8
}
