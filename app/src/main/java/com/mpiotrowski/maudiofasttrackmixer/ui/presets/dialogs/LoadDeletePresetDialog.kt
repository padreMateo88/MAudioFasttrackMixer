package com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import kotlinx.android.synthetic.main.layout_overwrite_preset.buttonCancel
import kotlinx.android.synthetic.main.load_delete_preset_dialog.*

class LoadDeletePresetDialog(
    dialogContext: Context,
    private val presetWithScenes: PresetWithScenes,
    private val listener: DialogListener,
    private val labelResourceId: Int
) : Dialog(dialogContext) {

    interface DialogListener {
        fun onActionConfirmed(presetWithScenes: PresetWithScenes)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.load_delete_preset_dialog)
        textViewLabel.text = context.getString(labelResourceId, presetWithScenes.preset.presetName)
        buttonOk.setOnClickListener {
            listener.onActionConfirmed(presetWithScenes)
            dismiss()
        }
        buttonCancel.setOnClickListener {dismiss()}
    }
}