package com.mpiotrowski.maudiofasttrackmixer.ui.views.audio_channel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.ui.views.vertical_seek_bar.SimpleOnSeekBarChangeListener
import com.mpiotrowski.maudiofasttrackmixer.ui.views.vertical_seek_bar.VerticalSeekBar


class AudioChannelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    val seekBarVolume: VerticalSeekBar
    var volumeChangedListener: VolumeChangedListener? = null
    var seekBarListener: SimpleOnSeekBarChangeListener? = null

    interface VolumeChangedListener {
        fun onVolumeChanged()
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_channel, this, true)
        seekBarVolume = findViewById(R.id.seekBarVolume)
    }
}