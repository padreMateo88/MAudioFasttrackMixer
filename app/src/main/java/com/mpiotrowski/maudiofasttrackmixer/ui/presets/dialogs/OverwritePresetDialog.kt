package com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.mpiotrowski.maudiofasttrackmixer.R
import kotlinx.android.synthetic.main.layout_overwrite_preset.*

class OverwritePresetDialog(
    dialogContext: Context,
    private val presetName: String,
    private val listener: ConfirmOverwritePresetListener
) : Dialog(dialogContext) {

    interface ConfirmOverwritePresetListener {
        fun onPresetOverwriteConfirmed(presetName: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_overwrite_preset)
        textViewOverwriteSceneLabel.text = context.getString(R.string.label_overwrite_preset,presetName)
        buttonOverwrite.setOnClickListener {
            listener.onPresetOverwriteConfirmed(presetName)
            dismiss()
        }

        buttonCancel.setOnClickListener {dismiss()}
    }
}