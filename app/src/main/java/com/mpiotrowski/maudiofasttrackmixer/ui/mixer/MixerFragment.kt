package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.MIXER_STEREO_OUTPUTS_COUNT
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentMixerBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import com.mpiotrowski.maudiofasttrackmixer.ui.views.MyFragment
import kotlinx.android.synthetic.main.fragment_mixer.*


class MixerFragment : MyFragment() {
    lateinit var viewModel: MainViewModel

    override fun provideViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    private lateinit var viewDataBinding: FragmentMixerBinding
    var outputIndex = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentMixerBinding.inflate(inflater, container, false)
        viewDataBinding.lifecycleOwner = requireActivity()
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        viewDataBinding.viewmodel = viewModel
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
                viewDataBinding.recyclerViewChannels.layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                val channelsAdapter = ChannelsAdapter(requireActivity() as androidx.appcompat.app.AppCompatActivity, viewModel)
                channelsAdapter.setHasStableIds(true)
                viewDataBinding.recyclerViewChannels.adapter = channelsAdapter
                viewDataBinding.recyclerViewChannels.requestDisallowInterceptTouchEvent(true)

                viewModel.audioChannels.observe(requireActivity(), Observer {
                    //(viewDataBinding.recyclerViewChannels.adapter as ChannelsAdapter).notifyDataSetChanged()
                })
            }
        })
    }

    private fun prepareSceneSelector() {
        val recyclerViewSceneButtons = viewDataBinding.recyclerViewSceneButtons
        recyclerViewSceneButtons.isNestedScrollingEnabled = false
        recyclerViewSceneButtons.layoutManager = GridLayoutManager(context, 3)
        recyclerViewSceneButtons.adapter = SceneButtonsAdapter(viewModel)
    }

    companion object {
        fun newInstance() = MixerFragment()
    }

}