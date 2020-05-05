package com.mpiotrowski.maudiofasttrackmixer.ui

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.mpiotrowski.maudiofasttrackmixer.R
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mpiotrowski.maudiofasttrackmixer.util.SlideDirection
import com.mpiotrowski.maudiofasttrackmixer.util.SlideType
import com.mpiotrowski.maudiofasttrackmixer.util.slideAnimation

class MainActivity : AppCompatActivity() {

    private var bottomBarVisibility = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setupBottomNavMenu(navController)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        bottom_nav.setupWithNavController(navController)
    }



    fun setBottomBarVisibility() {
        val navHostFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val containerView: View? = navHostFragment?.view
        containerView?.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    bottomBarVisibility = !bottomBarVisibility
                    setBottomBarAnimation()
                }
            }
            v?.onTouchEvent(event) ?: true
        }
    }

    private fun setBottomBarAnimation() {
        if (bottomBarVisibility) {
            bottom_nav.slideAnimation(SlideDirection.UP, SlideType.SHOW)
        } else {
            bottom_nav.slideAnimation(SlideDirection.DOWN, SlideType.HIDE)
        }
    }
}