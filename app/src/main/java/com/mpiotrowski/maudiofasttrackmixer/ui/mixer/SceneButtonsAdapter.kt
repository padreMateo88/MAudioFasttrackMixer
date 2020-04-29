package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.R
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import kotlinx.android.synthetic.main.scene_buttom_item.view.*

class SceneButtonsAdapter(var viewModel: MainViewModel): RecyclerView.Adapter<SceneButtonsAdapter.SceneButtonViewHolder>() {

    private var lastChecked : Button? = null

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SceneButtonViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.scene_buttom_item, parent, false) as LinearLayout
        return SceneButtonViewHolder(layout)
    }

    class SceneButtonViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onBindViewHolder(holder: SceneButtonViewHolder, position: Int) {
        holder.layout.button.text = (position + 1).toString()
        if(viewModel.currentScene?.scene?.sceneOrder == position + 1) {
            holder.layout.button.isEnabled = false
            lastChecked = holder.layout.button
        }

        holder.layout.button.setOnClickListener {
            val button: Button = it as Button
            if(lastChecked != button) {
                viewModel.onSceneSelected(position + 1)
                lastChecked?.isEnabled = true
                button.isEnabled = false
                lastChecked = button
            }
        }
    }
    override fun getItemCount() = 8
}
