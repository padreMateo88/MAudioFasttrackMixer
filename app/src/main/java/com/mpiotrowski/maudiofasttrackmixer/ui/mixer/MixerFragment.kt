package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.MIXER_STEREO_OUTPUTS_COUNT
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentMixerBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import kotlinx.android.synthetic.main.fragment_mixer.*


class MixerFragment : Fragment() {
    lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentMixerBinding
    var outputIndex = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
       binding = FragmentMixerBinding.inflate(inflater, container, false)
       binding.lifecycleOwner = requireActivity()
       return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        binding.viewmodel = viewModel
        prepareChannelMixer()
        prepareSceneSelector()

        buttonDecreaseOutput.setOnClickListener (View.OnClickListener {
            if(outputIndex == 1)
                return@OnClickListener
            outputIndex --
            textViewOutputIndex.text = "Output:\n${outputIndex*2-1}-${outputIndex*2}"
            viewModel.onOutputSelected(outputIndex)
        })

        buttonIncreaseOutput.setOnClickListener (View.OnClickListener {
            if(outputIndex == MIXER_STEREO_OUTPUTS_COUNT)
                return@OnClickListener
            outputIndex ++
            textViewOutputIndex.text = "Output:\n${outputIndex*2-1}-${outputIndex*2}"
            viewModel.onOutputSelected(outputIndex)
        })
    }

    private fun prepareChannelMixer() {
        view?.viewTreeObserver?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                binding.recyclerViewChannels.itemAnimator = null
                binding.recyclerViewChannels.layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                val channelsAdapter = ChannelsAdapter(requireActivity() as androidx.appcompat.app.AppCompatActivity, viewModel)
                channelsAdapter.setHasStableIds(true)
                binding.recyclerViewChannels.adapter = channelsAdapter
                binding.recyclerViewChannels.requestDisallowInterceptTouchEvent(true)

                viewModel.audioChannels.observe(requireActivity(), Observer {
                        (binding.recyclerViewChannels.adapter as ChannelsAdapter).notifyDataSetChanged()
                })
            }
        })
    }

    private fun prepareSceneSelector() {
        val recyclerViewSceneButtons = binding.recyclerViewSceneButtons
        recyclerViewSceneButtons.isNestedScrollingEnabled = false
        recyclerViewSceneButtons.layoutManager = GridLayoutManager(context, 3)
        val sceneButtonsAdapter = SceneButtonsAdapter(viewModel)
        sceneButtonsAdapter.setHasStableIds(true)
        recyclerViewSceneButtons.adapter = sceneButtonsAdapter
        viewModel.currentScene.observe(requireActivity(), Observer {
            (recyclerViewSceneButtons.adapter as SceneButtonsAdapter).notifyDataSetChanged()
        })
    }

    companion object {
        fun newInstance() = MixerFragment()
    }

}