package com.mpiotrowski.maudiofasttrackmixer.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mpiotrowski.maudiofasttrackmixer.ui.mixer.MixerFragment
import com.mpiotrowski.maudiofasttrackmixer.ui.presets.PresetsFragment
import com.mpiotrowski.maudiofasttrackmixer.ui.settings.SettingsFragment

class MainViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        //TODO find cleaner solution for default value
        return when (position) {
            0 -> SettingsFragment.newInstance()
            1 -> MixerFragment.newInstance()
            2 -> PresetsFragment.newInstance()
            else -> MixerFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}