package com.mpiotrowski.maudiofasttrackmixer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mpiotrowski.maudiofasttrackmixer.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MainViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.currentItem = 1
    }
}
