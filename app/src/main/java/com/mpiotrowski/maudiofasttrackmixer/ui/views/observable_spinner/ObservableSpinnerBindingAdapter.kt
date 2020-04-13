package com.mpiotrowski.maudiofasttrackmixer.ui.views.observable_spinner

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

object ObservableSpinnerBindingAdapter {
//region fxVolume
    @BindingAdapter("selectedItem")
    @JvmStatic fun setSelectedPosition(spinner: ObservableSpinner, selectedItem: Any) {
        for(i in 0 until spinner.adapter.count) {
            if(spinner.adapter.getItem(i) == selectedItem)
                spinner.setSelection(i)
        }
    }

    @InverseBindingAdapter(attribute = "selectedItem")
    @JvmStatic fun getSelectedPosition(spinner: ObservableSpinner): Any {
        return spinner.adapter.getItem(spinner.selectedItemPosition)
    }

    @BindingAdapter(value = ["onSelectedItemChanged","selectedItemAttrChanged"], requireAll = false)
    @JvmStatic fun setFxVolumeAttrChangedListener(
        spinner: ObservableSpinner,
        selectedPositionListener: ObservableSpinner.OnSelectedPositionListener?,
        inverseBindingListener: InverseBindingListener
    ) {
        val listener = object: SimpleSpinnerListener() {
            override fun onItemSelected(position: Int) {
                inverseBindingListener.onChange()
                selectedPositionListener?.onSelectedPositionChanged()
            }
        }
        spinner.onItemSelectedListener = listener
    }
//endregion fxVolume
}