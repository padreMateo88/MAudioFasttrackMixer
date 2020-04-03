package com.mpiotrowski.maudiofasttrackmixer.ui.views.audio_channel

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.mpiotrowski.maudiofasttrackmixer.ui.views.vertical_seek_bar.SimpleOnSeekBarChangeListener
import kotlinx.android.synthetic.main.view_channel.view.*

object AudioChannelBindingAdapters {

    @BindingAdapter("channelId")
    @JvmStatic fun setChannelId(audioChannelView: AudioChannelView, channelId: Int) {
        audioChannelView.textViewChannelId.text = "$channelId"
    }


//region volume
    @BindingAdapter("onVolumeChanged")
    @JvmStatic fun setVolumeAttrChangedListener (
        audioChannelView: AudioChannelView,
        volumeChangedListener: AudioChannelView.VolumeChangedListener?
    ) {
        if (audioChannelView.volumeChangedListener == null) {
            audioChannelView.volumeChangedListener = volumeChangedListener
        }
        audioChannelView.seekBarVolume.setOnSeekBarChangeListener(audioChannelView.volumeSeekBarListener)
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
        audioChannelView.volumeSeekBarListener = object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(i: Int) {
                inverseBindingListener.onChange()
                audioChannelView.volumeChangedListener?.onVolumeChanged()
            }
        }
        audioChannelView.seekBarVolume.setOnSeekBarChangeListener(audioChannelView.volumeSeekBarListener)
    }
//endregion volume


//region fxSend
    @BindingAdapter("onFxSendChanged")
    @JvmStatic fun setFxSendAttrChangedListener (
        audioChannelView: AudioChannelView,
        fxSendChangedListener: AudioChannelView.FxSendChangedListener?
    ) {
        if (audioChannelView.fxSendChangedListener == null) {
            audioChannelView.fxSendChangedListener = fxSendChangedListener
        }
        audioChannelView.seekBarFx.setOnSeekBarChangeListener(audioChannelView.fxSendSeekBarListener)
    }

    @BindingAdapter("fxSend")
    @JvmStatic fun setFxSend(audioChannelView: AudioChannelView, fxSend: Int) {
        if (audioChannelView.seekBarFx.progress != fxSend) {
            audioChannelView.seekBarFx.progress = fxSend
        }
    }

    @InverseBindingAdapter(attribute = "fxSend")
    @JvmStatic fun getFxSend(audioChannelView: AudioChannelView): Int {
        return audioChannelView.seekBarFx.progress
    }

    @BindingAdapter(value = ["fxSendAttrChanged"])
    @JvmStatic fun setFxSendAttrChangedListener(
        audioChannelView: AudioChannelView,
        inverseBindingListener: InverseBindingListener
    ) {
        audioChannelView.fxSendSeekBarListener = object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(i: Int) {
                inverseBindingListener.onChange()
                audioChannelView.fxSendChangedListener?.onFxSendChanged()
            }
        }
        audioChannelView.seekBarVolume.setOnSeekBarChangeListener(audioChannelView.fxSendSeekBarListener)
    }
//endregion fxSend

//region fxSend
    @BindingAdapter("panorama")
    @JvmStatic fun setPanorama(audioChannelView: AudioChannelView, panorama: Int) {
        if (audioChannelView.seekBarPanorama.progress != panorama) {
            audioChannelView.seekBarPanorama.progress = panorama
        }
    }

    @InverseBindingAdapter(attribute = "panorama")
    @JvmStatic fun getPanorama(audioChannelView: AudioChannelView): Int {
        return audioChannelView.seekBarPanorama.progress
    }

    @BindingAdapter(value = ["panoramaAttrChanged"])
    @JvmStatic fun setPanoramaAttrChangedListener(
        audioChannelView: AudioChannelView,
        inverseBindingListener: InverseBindingListener
    ) {
        audioChannelView.panoramaSeekBarListener = object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(i: Int) {
                inverseBindingListener.onChange()
                audioChannelView.volumeChangedListener?.onVolumeChanged()
            }
        }
        audioChannelView.seekBarPanorama.setOnSeekBarChangeListener(audioChannelView.panoramaSeekBarListener)
    }
//endregion fxSend
}