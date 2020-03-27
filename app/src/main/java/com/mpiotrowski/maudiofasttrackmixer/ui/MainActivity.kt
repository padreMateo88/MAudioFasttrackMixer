package com.mpiotrowski.maudiofasttrackmixer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.mpiotrowski.maudiofasttrackmixer.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MainViewPagerAdapter(supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.viewPager)
        viewPager.adapter = adapter
        viewPager.currentItem = 1
    }
}
