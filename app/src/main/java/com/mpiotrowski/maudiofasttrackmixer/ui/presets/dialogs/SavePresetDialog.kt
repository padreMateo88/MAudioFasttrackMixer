package com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.mpiotrowski.maudiofasttrackmixer.R
import kotlinx.android.synthetic.main.layout_save_preset.*
import kotlinx.android.synthetic.main.layout_save_preset.editTextPresetName

class SavePresetDialog(
    dialogContext: Context,
    private val presetName: String,
    private val listenerConfirm: ConfirmSavePresetListener
) : Dialog(dialogContext) {
    interface ConfirmSavePresetListener {
        fun onSavePresetConfirmed(presetName: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_save_preset)
        editTextPresetName.setText(presetName)
        editTextPresetName.setSelection(presetName.length)

        buttonSave.setOnClickListener {
            listenerConfirm.onSavePresetConfirmed(editTextPresetName.text.toString())
            this@SavePresetDialog.dismiss()
        }
        buttonCancel.setOnClickListener {
            this@SavePresetDialog.dismiss()
        }
    }
}