package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.SCENES_IN_PRESET_COUNT
import kotlinx.android.synthetic.main.scene_buttom_item.view.*

class SceneButtonsAdapter(private var viewModel: MixerViewModel): RecyclerView.Adapter<SceneButtonsAdapter.SceneButtonViewHolder>() {

    private var lastChecked : Button? = null
    private lateinit var viewGroup : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SceneButtonViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.scene_buttom_item, parent, false) as LinearLayout
        viewGroup = parent
        return  SceneButtonViewHolder(layout)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class SceneButtonViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onBindViewHolder(holder: SceneButtonViewHolder, position: Int) {
        holder.itemView.rootView.layoutParams.height = if (viewGroup.height > viewGroup.width) viewGroup.width/3 else viewGroup.height/3
        holder.itemView.rootView.layoutParams.width = viewGroup.width/3

        holder.layout.button.text = (position + 1).toString()
        if(viewModel.getCurrentSceneOrder() == position + 1) {
            val button = holder.layout.button
            button.background = ContextCompat.getDrawable(button.context,R.drawable.gray_button_selected)
            button.setTextColor(ContextCompat.getColor(button.context, android.R.color.black))
            lastChecked = button
        }

        holder.layout.button.setOnClickListener {
            val button: Button = it as Button
            if(lastChecked != button) {
                viewModel.onSceneSelected(position + 1)
                lastChecked?.background = ContextCompat.getDrawable(button.context,R.drawable.button_unselected)
                lastChecked?.setTextColor(ContextCompat.getColor(button.context, R.color.lighterGray))
                button.background = ContextCompat.getDrawable(button.context,R.drawable.gray_button_selected)
                button.setTextColor(ContextCompat.getColor(button.context, android.R.color.black))
                lastChecked = button
            }
        }

        holder.layout.button.setOnLongClickListener {
            viewModel.getSceneOfCurrentState(position + 1)?.let { sceneWithComponents ->
                viewModel.getCurrentState()?.let {
                    currentState ->
                    SaveSceneDialog(holder.layout.button.context,
                        sceneWithComponents,currentState,viewModel).show()
                }
            }
            true
        }
    }

    override fun getItemCount() = SCENES_IN_PRESET_COUNT
}
