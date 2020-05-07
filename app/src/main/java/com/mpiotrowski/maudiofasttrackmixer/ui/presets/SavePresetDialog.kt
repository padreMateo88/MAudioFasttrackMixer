package com.mpiotrowski.maudiofasttrackmixer.ui.presets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.mpiotrowski.maudiofasttrackmixer.R
import kotlinx.android.synthetic.main.layout_save_preset.*
import kotlinx.android.synthetic.main.layout_save_scene.buttonSaveScene

class SavePresetDialog(private var dialogContext: Context,
                       var presetName: String,
                       var listener: SavePresetListener
) : Dialog(dialogContext) {

    interface SavePresetListener {
        fun onPresetSaved(presetName: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_save_preset)
        editTextPresetName.setText(presetName)
        editTextPresetName.setSelection(presetName.length)

        buttonSaveScene.setOnClickListener {
            listener.onPresetSaved(editTextPresetName.text.toString())
            this@SavePresetDialog.dismiss()
        }
    }
}