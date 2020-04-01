package com.mpiotrowski.maudiofasttrackmixer.ui.views.vertical_seek_bar

import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener

abstract class SimpleOnSeekBarChangeListener : OnSeekBarChangeListener {
    override fun onProgressChanged(
        seekBar: SeekBar,
        i: Int,
        b: Boolean
    ) {
        onProgressChanged(i)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    abstract fun onProgressChanged(i: Int)
}