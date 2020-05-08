package com.mpiotrowski.maudiofasttrackmixer.ui.views.faders

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import kotlinx.android.synthetic.main.view_channel.view.*

object FxSettingsFaderBindingAdapters {
    @BindingAdapter("faderValue")
    @JvmStatic fun setFaderValue(fxSettingsFader: FxSettingsFader, faderValue: Int) {
        if (fxSettingsFader.faderValue != faderValue)
            fxSettingsFader.faderValue = faderValue
    }

    @InverseBindingAdapter(attribute = "faderValue",event = "faderValueAttrChanged")
    @JvmStatic fun getFaderValue(fxSettingsFader: FxSettingsFader): Int {
        return fxSettingsFader.faderValue
    }

    @BindingAdapter(value = ["onFaderValueChanged","faderValueAttrChanged"], requireAll = false)
    @JvmStatic fun setFaderValueAttrChangedListener(
        fxSettingsFader: FxSettingsFader,
        volumeChangedListener: FxSettingsFader.FaderValueChangedListener?,
        inverseBindingListener: InverseBindingListener
    ) {
        fxSettingsFader.faderValueChangedListener = volumeChangedListener
        fxSettingsFader.valueChangedListener = object : Fader.ValueChangedListener {
            override fun onValueChanged(value: Int) {
                inverseBindingListener.onChange()
                volumeChangedListener?.onValueChanged()
            }
        }
    }
}