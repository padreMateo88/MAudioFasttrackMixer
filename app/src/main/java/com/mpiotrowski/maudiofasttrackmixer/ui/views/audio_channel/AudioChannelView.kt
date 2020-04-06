package com.mpiotrowski.maudiofasttrackmixer.ui.views.audio_channel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.ui.views.vertical_seek_bar.SimpleOnSeekBarChangeListener
import com.mpiotrowski.maudiofasttrackmixer.ui.views.vertical_seek_bar.VerticalSeekBar


class AudioChannelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    val seekBarVolume: VerticalSeekBar
    val seekBarPanorama: SeekBar
    val toggleButtonMute: ToggleButton
    val toggleButtonSolo: ToggleButton

    //attribute listener
    var volumeChangedListener: VolumeChangedListener? = null
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

    interface FxSendChangedListener {
        fun onFxSendChanged()
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_channel, this, true)
        seekBarVolume = findViewById(R.id.seekBarVolume)
        seekBarPanorama = findViewById(R.id.seekBarPanorama)
        toggleButtonMute = findViewById(R.id.buttonMute)
        toggleButtonSolo = findViewById(R.id.buttonSolo)
    }
}