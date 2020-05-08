package com.mpiotrowski.maudiofasttrackmixer.ui.views.audio_channel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.ui.views.faders.Fader

open class AudioChannelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    var fine = false

    //attribute listener
    open var volumeChangedListener: VolumeChangedListener? = null

    //view listeners
    var volumeListener: Fader.ValueChangedListener? = null
    var fxVolumeListener: Fader.ValueChangedListener? = null
    var panoramaListener: Fader.ValueChangedListener? = null

    var muteListener: CompoundButton.OnCheckedChangeListener? = null
    var soloListener: CompoundButton.OnCheckedChangeListener? = null

    interface VolumeChangedListener {
        fun onVolumeChanged()
    }

    interface SoloChangedListener {
        fun onSoloChanged()
    }

    interface FxVolumeChangedListener {
        fun onFxVolumeChanged()
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_channel, this, true)
    }
}