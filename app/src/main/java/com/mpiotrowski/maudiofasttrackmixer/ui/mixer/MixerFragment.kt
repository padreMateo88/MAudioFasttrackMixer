package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.R
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.MIXER_STEREO_OUTPUTS_COUNT
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentMixerBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs.OverwritePresetDialog.*
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.dialogs.ProgressBarDialog
import com.mpiotrowski.maudiofasttrackmixer.util.LogUtil
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_mixer.*
import javax.inject.Inject

class MixerFragment : DaggerFragment() {

    var outputIndex = 1
    private var progressDialog: ProgressBarDialog? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val mixerViewModel by viewModels<MixerViewModel> {
        viewModelFactory
    }

    private val mainViewModel by activityViewModels<MainViewModel> {
        viewModelFactory
    }

    private lateinit var binding: FragmentMixerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View {
       binding = FragmentMixerBinding.inflate(inflater, container, false)
       showProgressbar()
       binding.lifecycleOwner = requireActivity()
       return binding.root
    }

    private fun showProgressbar() {
        progressDialog = ProgressBarDialog(requireContext())
        progressDialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        progressDialog?.show()
        Handler().postDelayed(
            {
                LogUtil.d("progressDialog timeout")
                progressDialog!!.dismiss()
            },
            2000
        )
    }

    private fun dismissProgressbar() {
        progressDialog?.dismiss()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.mixerViewModel = mixerViewModel
        binding.mainViewModel = mainViewModel
        prepareMeasuredViews()

        mainViewModel.deviceOnline.observe(viewLifecycleOwner, Observer {
            if(it) {
                val currentScene = mixerViewModel.currentScene.value
                if(currentScene != null)
                    mixerViewModel.loadMixerState(currentScene)
            }
        })

        mixerViewModel.muteFx.observe(viewLifecycleOwner, Observer {
            mixerViewModel.onFxMuteChanged(it)
        })

        buttonDecreaseOutput.setOnClickListener (View.OnClickListener {
            if(outputIndex == 1)
                return@OnClickListener
            outputIndex --
            textViewOutputIndex.text = "Output:\n${outputIndex*2-1}-${outputIndex*2}"
            mixerViewModel.onOutputSelected(outputIndex)
        })

        buttonIncreaseOutput.setOnClickListener (View.OnClickListener {
            if(outputIndex == MIXER_STEREO_OUTPUTS_COUNT)
                return@OnClickListener
            outputIndex ++
            textViewOutputIndex.text = "Output:\n${outputIndex*2-1}-${outputIndex*2}"
            mixerViewModel.onOutputSelected(outputIndex)
        })
    }

    private fun prepareMeasuredViews() {
        view?.viewTreeObserver?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                prepareChannelMixer()
                prepareSceneSelector()
            }
        })
    }

    private fun prepareChannelMixer() {
        binding.recyclerViewChannels.itemAnimator = null
        binding.recyclerViewChannels.layoutManager =  LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val channelsAdapter =
            ChannelsAdapter(requireActivity() as AppCompatActivity, mixerViewModel)
        channelsAdapter.setHasStableIds(true)
        channelsAdapter.channelsDrawnListener = object: ChannelsAdapter.ChannelsDrawnListener{
            override fun onAdapterDrawn() {
                dismissProgressbar()
            }
        }
        binding.recyclerViewChannels.setHasFixedSize(true)
        binding.recyclerViewChannels.adapter = channelsAdapter
        binding.recyclerViewChannels.requestDisallowInterceptTouchEvent(true)

        mixerViewModel.audioChannels.observe(requireActivity(), Observer {
            (binding.recyclerViewChannels.adapter as ChannelsAdapter).notifyDataSetChanged()
        })
    }

    private fun prepareSceneSelector() {
        val recyclerViewSceneButtons = binding.recyclerViewSceneButtons
        recyclerViewSceneButtons.isNestedScrollingEnabled = false
        recyclerViewSceneButtons.layoutManager = GridLayoutManager(context, 3)
        val sceneButtonsAdapter = SceneButtonsAdapter(mixerViewModel)
        sceneButtonsAdapter.setHasStableIds(true)
        recyclerViewSceneButtons.adapter = sceneButtonsAdapter
        mixerViewModel.currentScene.observe(requireActivity(), Observer {
            (recyclerViewSceneButtons.adapter as SceneButtonsAdapter).notifyDataSetChanged()
        })
    }

    companion object {
        fun newInstance() = MixerFragment()
    }
}