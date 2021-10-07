package com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.mpiotrowski.maudiofasttrackmixer.R

class ProgressBarDialog(
    dialogContext: Context
) : Dialog(dialogContext) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_progress_bar)
    }
}