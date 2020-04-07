package com.mpiotrowski.maudiofasttrackmixer.ui.views.audio_channel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.ui.views.vertical_seek_bar.SimpleOnSeekBarChangeListener


class AudioChannelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    //attribute listener
    var volumeChangedListener: VolumeChangedListener? = null
    var soloChangedListener: SoloChangedListener? = null
    var fxSendChangedListener: FxSendChangedListener? = null

    //view listeners
    var volumeSeekBarListener: SimpleOnSeekBarChangeListener? = null
    var fxSendSeekBarListener: SimpleOnSeekBarChangeListener? = null
    var panoramaSeekBarListener: SimpleOnSeekBarChangeListener? = null

    var muteListener: CompoundButton.OnCheckedChangeListener? = null
    var soloListener: CompoundButton.OnCheckedChangeListener? = null

    interface VolumeChangedListener {
        fun onVolumeChanged()
    }

    interface SoloChangedListener {
        fun onSoloChanged()
    }

    interface FxSendChangedListener {
        fun onFxSendChanged()
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_channel, this, true)
    }
}