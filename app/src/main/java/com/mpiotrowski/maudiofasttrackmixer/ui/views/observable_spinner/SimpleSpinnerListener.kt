package com.mpiotrowski.maudiofasttrackmixer.ui.views.observable_spinner

import android.view.View
import android.widget.AdapterView

abstract class SimpleSpinnerListener : AdapterView.OnItemSelectedListener {

    abstract fun onItemSelected(position: Int)

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onItemSelected(position)
    }
}