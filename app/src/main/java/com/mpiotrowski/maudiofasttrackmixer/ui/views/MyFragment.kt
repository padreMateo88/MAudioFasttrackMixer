package com.mpiotrowski.maudiofasttrackmixer.ui.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mpiotrowski.maudiofasttrackmixer.ui.MainViewModel
import kotlinx.android.synthetic.main.fragment_presets.*

abstract class MyFragment : Fragment() {
    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        val myLinearLayout: MyLinearLayout = this.layoutMain as MyLinearLayout
        val lockBottomBar: Boolean = viewModel.lockBottomBar.value!!
        myLinearLayout.lockBottomBar(lockBottomBar)
        if (lockBottomBar) {
            myLinearLayout.stopAnimations()
        }
        provideViewCreated(view, savedInstanceState)
    }

    abstract fun provideViewCreated(view: View, savedInstanceState: Bundle?)
}