package com.mpiotrowski.maudiofasttrackmixer.ui.views.audio_channel

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.mpiotrowski.maudiofasttrackmixer.ui.views.vertical_seek_bar.SimpleOnSeekBarChangeListener

object AudioChannelBindingAdapters {

    @BindingAdapter("onVolumeChanged")
    @JvmStatic fun setVolumeAttrChangedListener (
        audioChannelView: AudioChannelView,
        volumeChangedListener: AudioChannelView.VolumeChangedListener?
    ) {
        if (audioChannelView.volumeChangedListener == null) {
            audioChannelView.volumeChangedListener = volumeChangedListener
        }
        audioChannelView.seekBarVolume.setOnSeekBarChangeListener(audioChannelView.seekBarListener)
    }

    @BindingAdapter("volume")
    @JvmStatic fun setVolume(audioChannelView: AudioChannelView, volume: Int) {
        if (audioChannelView.seekBarVolume.progress != volume)
            audioChannelView.seekBarVolume.progress = volume
    }

    @InverseBindingAdapter(attribute = "volume")
    @JvmStatic fun getVolume(audioChannelView: AudioChannelView): Int {
        return audioChannelView.seekBarVolume.progress
    }

    @BindingAdapter(value = ["volumeAttrChanged"])
    @JvmStatic fun setVolumeAttrChangedListener(
        audioChannelView: AudioChannelView,
        inverseBindingListener: InverseBindingListener
    ) {
        audioChannelView.seekBarListener = object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(i: Int) {
                inverseBindingListener.onChange()
                if (audioChannelView.volumeChangedListener != null)
                    audioChannelView.volumeChangedListener!!.onVolumeChanged()
            }
        }
        audioChannelView.seekBarVolume.setOnSeekBarChangeListener(audioChannelView.seekBarListener)
    }
}