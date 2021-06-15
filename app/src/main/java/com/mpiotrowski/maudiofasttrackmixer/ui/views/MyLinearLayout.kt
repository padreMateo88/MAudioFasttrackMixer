package com.mpiotrowski.maudiofasttrackmixer.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mpiotrowski.maudiofasttrackmixer.ui.MainActivity
import com.mpiotrowski.maudiofasttrackmixer.ui.mixer.MixerFragment
import com.mpiotrowski.maudiofasttrackmixer.util.SlideDirection
import com.mpiotrowski.maudiofasttrackmixer.util.SlideType
import com.mpiotrowski.maudiofasttrackmixer.util.slideAnimation
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Handler
import kotlin.math.sqrt

private enum class GestureDirection {
    UP,
    DOWN
}

object MyRunnable : Runnable {
    private var bottomNavBar: BottomNavigationView? = null

    fun setBnb(bnb: BottomNavigationView) {
        bottomNavBar = bnb
    }
    override fun run() {
        bottomNavBar?.slideAnimation(SlideDirection.DOWN, SlideType.HIDE)
    }
}

class MyLinearLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private val mTouchSlop: Int = android.view.ViewConfiguration.get(context).scaledTouchSlop
    private var gestureDirection: GestureDirection = GestureDirection.UP

    init {
        val bottomNavigationView: BottomNavigationView = getBottomBar()
        val handler: Handler? = bottomNavigationView.handler
        handler?.removeCallbacks(MyRunnable)
    }

    private fun getDistance(ev: MotionEvent): Int {
        var x = ev.x
        var y = ev.y
        var distanceSum: Double = 0.0
        val historySize: Int = ev.historySize
        for (h in 0 until historySize step 1) {
            // historical point
            val hx: Float = ev.getHistoricalX(0, h)
            val hy: Float = ev.getHistoricalY(0, h)
            // distance between startX,startY and historical point
            val dx: Float = (hx - x)
            val dy: Float = (hy - y)
            distanceSum += sqrt((dx * dx + dy * dy).toDouble())
            // make historical point the start point for next loop iteration
            x = hx;
            y = hy;
        }
        // add distance from last historical point to event's point
        val dx: Float = (ev.getX(0) - x)
        val dy: Float = (ev.getY(0) - y)
        gestureDirection = if(dy > 0) GestureDirection.DOWN else GestureDirection.UP
        distanceSum += sqrt((dx * dx + dy * dy).toDouble())
        return distanceSum.toInt()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val activity: MainActivity = context as MainActivity
        val navHostFragment: Fragment = activity.nav_host_fragment
        val fragment: Fragment = navHostFragment.childFragmentManager.fragments[0]
        return when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (fragment is MixerFragment) {
                    handleMotionAction(ev)
                }
                false
            }
            MotionEvent.ACTION_MOVE -> {
                if (fragment is MixerFragment) {
                    handleMotionAction(ev)
                } else {
                    false
                }
            } else -> {
                false
            }
        }
    }

    private fun handleMotionAction(ev: MotionEvent): Boolean {
        val xDiff: Int = getDistance(ev)
        return xDiff > mTouchSlop
    }

    private fun getBottomBar(): BottomNavigationView {
        val activity: MainActivity = context as MainActivity
        return activity.bottom_nav
    }
}
