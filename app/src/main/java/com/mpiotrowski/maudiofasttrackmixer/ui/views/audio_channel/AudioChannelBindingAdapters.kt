package com.mpiotrowski.maudiofasttrackmixer.ui.views.audio_channel

import android.widget.CompoundButton
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
    @BindingAdapter("volume")
    @JvmStatic fun setVolume(audioChannelView: AudioChannelView, volume: Int) {
        if (audioChannelView.seekBarVolume.progress != volume)
            audioChannelView.seekBarVolume.progress = volume
    }

    @InverseBindingAdapter(attribute = "volume",event = "volumeAttrChanged")
    @JvmStatic fun getVolume(audioChannelView: AudioChannelView): Int {
        return audioChannelView.seekBarVolume.progress
    }

    @BindingAdapter(value = ["onVolumeChanged","volumeAttrChanged"], requireAll = false)
    @JvmStatic fun setVolumeAttrChangedListener(
        audioChannelView: AudioChannelView,
        volumeChangedListener: AudioChannelView.VolumeChangedListener?,
        inverseBindingListener: InverseBindingListener
    ) {
        audioChannelView.volumeChangedListener = volumeChangedListener
        audioChannelView.volumeSeekBarListener = object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(i: Int) {
                inverseBindingListener.onChange()
                volumeChangedListener?.onVolumeChanged()
            }
        }
        audioChannelView.seekBarVolume.setOnSeekBarChangeListener(audioChannelView.volumeSeekBarListener)
    }
//endregion volume

//region panorama
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
//endregion panorama

//region mute
    @BindingAdapter("mute")
    @JvmStatic fun setMute(audioChannelView: AudioChannelView, mute: Boolean) {
        if (audioChannelView.toggleButtonMute.isChecked != mute) {
            audioChannelView.toggleButtonMute.isChecked = mute
        }
    }

    @InverseBindingAdapter(attribute = "mute")
    @JvmStatic fun getMute(audioChannelView: AudioChannelView): Boolean {
        return audioChannelView.toggleButtonMute.isChecked
    }

    @BindingAdapter(value = ["muteAttrChanged"])
    @JvmStatic fun setMuteAttrChangedListener(
        audioChannelView: AudioChannelView,
        inverseBindingListener: InverseBindingListener
    ) {
        audioChannelView.muteListener =
            CompoundButton.OnCheckedChangeListener { _, _ ->
                inverseBindingListener.onChange()
                audioChannelView.volumeChangedListener?.onVolumeChanged()
            }
        audioChannelView.toggleButtonMute.setOnCheckedChangeListener(audioChannelView.muteListener)
    }
//endregion mute

//region solo
    @BindingAdapter("solo")
    @JvmStatic fun setSolo(audioChannelView: AudioChannelView, solo: Boolean) {
        if (audioChannelView.toggleButtonSolo.isChecked != solo) {
            audioChannelView.toggleButtonSolo.isChecked = solo
        }
    }

    @InverseBindingAdapter(attribute = "solo")
    @JvmStatic fun getSolo(audioChannelView: AudioChannelView): Boolean {
        return audioChannelView.toggleButtonSolo.isChecked
    }

    @BindingAdapter(value = ["onSoloChanged","soloAttrChanged"],requireAll = false)
    @JvmStatic fun setSoloAttrChangedListener(
        audioChannelView: AudioChannelView,
        soloChangedListener: AudioChannelView.SoloChangedListener?,
        inverseBindingListener: InverseBindingListener
    ) {
        audioChannelView.soloListener =
            CompoundButton.OnCheckedChangeListener { _, _ ->
                inverseBindingListener.onChange()
                soloChangedListener?.onSoloChanged()
                audioChannelView.volumeChangedListener?.onVolumeChanged()
            }
        audioChannelView.toggleButtonSolo.setOnCheckedChangeListener(audioChannelView.soloListener)
    }
//endregion mute

//region fxVolume
    @BindingAdapter("fxVolume")
    @JvmStatic fun setFxVolume(audioChannelView: AudioChannelView, fxVolume: Int) {
        if (audioChannelView.seekBarFx.progress != fxVolume) {
            audioChannelView.seekBarFx.progress = fxVolume
        }
    }

    @InverseBindingAdapter(attribute = "fxVolume")
    @JvmStatic fun getFxVolume(audioChannelView: AudioChannelView): Int {
        return audioChannelView.seekBarFx.progress
    }

    @BindingAdapter(value = ["onFxVolumeChanged","fxVolumeAttrChanged"], requireAll = false)
    @JvmStatic fun setFxVolumeAttrChangedListener(
        audioChannelView: AudioChannelView,
        fxVolumeChangedListener: AudioChannelView.FxVolumeChangedListener?,
        inverseBindingListener: InverseBindingListener
    ) {
        audioChannelView.fxVolumeSeekBarListener = object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(i: Int) {
                inverseBindingListener.onChange()
                fxVolumeChangedListener?.onFxVolumeChanged()
            }
        }
        audioChannelView.seekBarFx.setOnSeekBarChangeListener(audioChannelView.fxVolumeSeekBarListener)
    }
//endregion fxVolume
}