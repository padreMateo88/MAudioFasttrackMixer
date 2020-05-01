package com.mpiotrowski.maudiofasttrackmixer.ui.views.audio_channel

import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.MIXER_OUTPUTS_WITH_FX
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.MIXER_STEREO_OUTPUTS_COUNT
import com.mpiotrowski.maudiofasttrackmixer.ui.views.faders.Fader
import kotlinx.android.synthetic.main.view_channel.view.*

object AudioChannelBindingAdapters {

    @BindingAdapter("channelId")
    @JvmStatic fun setChannelId(audioChannelView: AudioChannelView, channelId: Int) {
        audioChannelView.textViewChannelId.text = "$channelId"
    }

//region volume
    @BindingAdapter("volume")
    @JvmStatic fun setVolume(audioChannelView: AudioChannelView, volume: Int) {
        if (audioChannelView.volumeFader.faderValue != volume)
            audioChannelView.volumeFader.faderValue = volume
    }

    @InverseBindingAdapter(attribute = "volume",event = "volumeAttrChanged")
    @JvmStatic fun getVolume(audioChannelView: AudioChannelView): Int {
        return audioChannelView.volumeFader.faderValue
    }

    @BindingAdapter(value = ["onVolumeChanged","volumeAttrChanged"], requireAll = false)
    @JvmStatic fun setVolumeAttrChangedListener(
        audioChannelView: AudioChannelView,
        volumeChangedListener: AudioChannelView.VolumeChangedListener?,
        inverseBindingListener: InverseBindingListener
    ) {
        audioChannelView.volumeChangedListener = volumeChangedListener
        audioChannelView.volumeListener = object : Fader.ValueChangedListener {
            override fun onValueChanged(value: Int) {
                inverseBindingListener.onChange()
                volumeChangedListener?.onVolumeChanged()
            }
        }
        audioChannelView.volumeFader.valueChangedListener = audioChannelView.volumeListener as Fader.ValueChangedListener
    }
//endregion volume

//region panorama
    @BindingAdapter("panorama")
    @JvmStatic fun setPanorama(audioChannelView: AudioChannelView, panorama: Int) {
        if (audioChannelView.panoramaFader.faderValue != panorama) {
            audioChannelView.panoramaFader.faderValue = panorama
        }
    }

    @InverseBindingAdapter(attribute = "panorama")
    @JvmStatic fun getPanorama(audioChannelView: AudioChannelView): Int {
        return audioChannelView.panoramaFader.faderValue
    }

    @BindingAdapter(value = ["panoramaAttrChanged"])
    @JvmStatic fun setPanoramaAttrChangedListener(
        audioChannelView: AudioChannelView,
        inverseBindingListener: InverseBindingListener
    ) {
        audioChannelView.panoramaListener = object : Fader.ValueChangedListener {
            override fun onValueChanged(value: Int) {
                inverseBindingListener.onChange()
                audioChannelView.volumeChangedListener?.onVolumeChanged()
            }
        }
        audioChannelView.panoramaFader.valueChangedListener = audioChannelView.panoramaListener as Fader.ValueChangedListener
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
        if (audioChannelView.fxVolumeFader.faderValue != fxVolume) {
            audioChannelView.fxVolumeFader.faderValue = fxVolume
        }
    }

    @InverseBindingAdapter(attribute = "fxVolume")
    @JvmStatic fun getFxVolume(audioChannelView: AudioChannelView): Int {
        return audioChannelView.fxVolumeFader.faderValue
    }

    @BindingAdapter(value = ["onFxVolumeChanged","fxVolumeAttrChanged"], requireAll = false)
    @JvmStatic fun setFxVolumeAttrChangedListener(
        audioChannelView: AudioChannelView,
        fxVolumeChangedListener: AudioChannelView.FxVolumeChangedListener?,
        inverseBindingListener: InverseBindingListener
    ) {
        audioChannelView.fxVolumeListener = object : Fader.ValueChangedListener {
            override fun onValueChanged(value: Int) {
                inverseBindingListener.onChange()
                fxVolumeChangedListener?.onFxVolumeChanged()
            }
        }
        audioChannelView.fxVolumeFader.valueChangedListener = audioChannelView.fxVolumeListener as Fader.ValueChangedListener
    }
//endregion fxVolume

//region fx volume visibility
    @BindingAdapter("outputIndex")
    @JvmStatic fun setOutputIndex(audioChannelView: AudioChannelView, outputIndex: Int) {
    Log.d("MPdebug", "setOutputIndex $outputIndex")
    val visibility = if(outputIndex in MIXER_OUTPUTS_WITH_FX) View.VISIBLE else View.INVISIBLE
        if (audioChannelView.fxVolumeFader.visibility != visibility) {
            audioChannelView.fxVolumeFader.visibility = visibility
        }
    }
//endregion fx volume visibility
}