package com.mpiotrowski.maudiofasttrackmixer.ui.mixer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mpiotrowski.maudiofasttrackmixer.databinding.FragmentMixerBinding
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import kotlinx.android.synthetic.main.fragment_mixer.*


class MixerFragment : Fragment() {

    lateinit var viewModel: MainViewModel
    private lateinit var viewDataBinding: FragmentMixerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
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
        toggleButtonFine.setOnClickListener {
            viewModel.insertDefaultPreset()
        }

        buttonDecreaseOutput.setOnClickListener {
            viewModel.deleteDefaultPreset()
        }

        buttonIncreaseOutput.setOnClickListener{
            viewModel.updateDefaultPreset()
        }

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
                viewDataBinding.recyclerViewChannels.adapter =
                    ChannelsAdapter(requireActivity() as AppCompatActivity, viewModel)

                viewDataBinding.recyclerViewChannels.requestDisallowInterceptTouchEvent(true)
            }
        })
    }

    private fun prepareSceneSelector() {
        val recyclerViewSceneButtons = viewDataBinding.recyclerViewSceneButtons
        recyclerViewSceneButtons.isNestedScrollingEnabled = false
        recyclerViewSceneButtons.layoutManager = GridLayoutManager(context, 2)
        recyclerViewSceneButtons.adapter = SceneButtonsAdapter(viewModel)
    }

    companion object {
        fun newInstance() = MixerFragment()
    }
}