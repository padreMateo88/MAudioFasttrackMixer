package com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.*
import android.view.Window
import com.mpiotrowski.maudiofasttrackmixer.R
import kotlinx.android.synthetic.main.layout_add_preset.*
import kotlinx.android.synthetic.main.layout_save_preset.editTextPresetName
import kotlinx.android.synthetic.main.layout_save_scene.buttonAddPreset

class AddPresetDialog(private val dialogContext: Context,
                      val presetNames: List<String>,
                      val listener: AddPresetListener
) : Dialog(dialogContext) {

    interface AddPresetListener {
        fun onCreatePreset(presetName: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_add_preset)
        editTextPresetName.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonAddPreset.isEnabled = (!presetNames.contains(s.toString()) || s.toString().isEmpty())
                textViewPresetExists.visibility = if(presetNames.contains(s.toString())) VISIBLE else GONE
            }
        })
        buttonAddPreset.setOnClickListener {
            listener.onCreatePreset(editTextPresetName.text.toString())
            this@AddPresetDialog.dismiss()
        }
    }
}