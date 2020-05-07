package com.mpiotrowski.maudiofasttrackmixer.ui.presets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import com.mpiotrowski.maudiofasttrackmixer.R
import kotlinx.android.synthetic.main.layout_overwrite_preset.*
import kotlinx.android.synthetic.main.layout_save_preset.*
import kotlinx.android.synthetic.main.layout_save_scene.buttonSaveScene

class OverwritePresetDialog(private var dialogContext: Context,
                            var presetName: String,
                            var listener: OverwritePresetListener
) : Dialog(dialogContext) {

    interface OverwritePresetListener {
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