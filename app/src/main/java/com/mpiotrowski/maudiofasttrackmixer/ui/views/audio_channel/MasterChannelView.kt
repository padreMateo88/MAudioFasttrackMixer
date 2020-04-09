package com.mpiotrowski.maudiofasttrackmixer.ui.views.audio_channel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.ui.views.vertical_seek_bar.SimpleOnSeekBarChangeListener
import kotlinx.android.synthetic.main.view_channel.view.*


class MasterChannelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AudioChannelView(context, attrs) {

    init {
        toggleButtonSolo.visibility = View.INVISIBLE
        seekBarPanorama.visibility = View.INVISIBLE
        textViewChannelId.text = "M"
    }
}