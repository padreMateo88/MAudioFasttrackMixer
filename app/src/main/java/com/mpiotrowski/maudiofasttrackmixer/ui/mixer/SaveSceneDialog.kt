package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import kotlinx.android.synthetic.main.layout_save_scene.*

class SaveSceneDialog(private var dialogContext: Context,
                      var copyFrom: SceneWithComponents,
                      var presetWIthScenes: PresetWithScenes,
                      var viewModel: MainViewModel
) : Dialog(dialogContext) {

    interface CopySceneListener{
        fun onSceneCopied(copyFrom: SceneWithComponents, copyTo: SceneWithComponents, newName: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_save_scene)

        val scenesByOrder = presetWIthScenes.scenesByOrder
        val scenes = presetWIthScenes.scenes.sortedBy {it.scene.sceneOrder}
        val sceneNames = scenes.map {it.scene.sceneName}

        val scenesAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, sceneNames)
        scenesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerScenes.adapter = scenesAdapter
        spinnerScenes.setSelection(copyFrom.scene.sceneOrder - 1)
        spinnerScenes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                scenesByOrder[position+1]?.let {
                    editTextSceneName.setText(it.scene.sceneName)
                }
            }
        }
        textViewSaveSceneName.text = copyFrom.scene.sceneName
        editTextSceneName.setText(copyFrom.scene.sceneName)

        buttonSaveScene.setOnClickListener {
            scenesByOrder[spinnerScenes.selectedItemPosition + 1]?.let{
                viewModel.saveSceneAs(copyFrom, it, editTextSceneName.text.toString())
            }
            this@SaveSceneDialog.dismiss()
        }
    }
}