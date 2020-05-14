package com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.mpiotrowski.maudiofasttrackmixer.R
import kotlinx.android.synthetic.main.layout_rename_create_preset.*
import kotlinx.android.synthetic.main.layout_save_preset.editTextPresetName

class RenameOrCreateNewPresetDialog(
    dialogContext: Context,
    private val oldPresetName: String,
    private val newPresetName: String,
    private val renameCreateNewListener: RenameCreateNewListener
) : Dialog(dialogContext) {

    interface RenameCreateNewListener {
        fun onRenamePreset(presetName: String)
        fun onSaveAsNewPreset(presetName: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_rename_create_preset)
        textViewTitle.text = context.getString(R.string.title_rename_or_save_preset, oldPresetName, newPresetName)

        buttonRename.setOnClickListener {
            renameCreateNewListener.onRenamePreset(newPresetName)
            this@RenameOrCreateNewPresetDialog.dismiss()
        }
        buttonSaveAsNew.setOnClickListener {
            renameCreateNewListener.onSaveAsNewPreset(newPresetName)
            this@RenameOrCreateNewPresetDialog.dismiss()
        }
    }
}