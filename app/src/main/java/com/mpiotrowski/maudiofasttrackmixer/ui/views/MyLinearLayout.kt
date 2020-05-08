package com.mpiotrowski.maudiofasttrackmixer.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mpiotrowski.maudiofasttrackmixer.ui.MainActivity
import com.mpiotrowski.maudiofasttrackmixer.ui.mixer.MixerFragment
import com.mpiotrowski.maudiofasttrackmixer.util.OnSwipeTouchListener
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
private const val TIME_TO_HIDE: Long = 2500

class MyLinearLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private val mTouchSlop: Int = android.view.ViewConfiguration.get(context).scaledTouchSlop
    private var gestureDirection: GestureDirection = GestureDirection.UP
    private var previousGestureDirection: GestureDirection = GestureDirection.UP

    init {
        val bottomNavigationView: BottomNavigationView = getBottomBar()
        val handler: Handler? = bottomNavigationView.handler
        handler?.removeCallbacksAndMessages(null)
        selfHide(bottomNavigationView)
    }

    private fun setOnSwipeTouchLister(bottomNavigationView: BottomNavigationView) {
        setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeBottom() {
                gestureDirection = GestureDirection.DOWN
                setBottomBarAnimation(bottomNavigationView)
            }

            override fun onSwipeTop() {
                gestureDirection = GestureDirection.UP
                setBottomBarAnimation(bottomNavigationView)
            }
        })
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
        val bottomNavigationView: BottomNavigationView = getBottomBar()
        return when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (fragment !is MixerFragment) {
                    setOnSwipeTouchLister(bottomNavigationView)
                }
                false
            }
            MotionEvent.ACTION_MOVE -> {
                if (fragment is MixerFragment) {
                    val xDiff: Int = getDistance(ev)
                    xDiff > mTouchSlop
                } else {
                    false
                }
            } else -> {
                false
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Here we actually handle the touch event (e.g. if the action is ACTION_MOVE, scroll this container).
        // This method will only be called if the touch event was intercepted in onInterceptTouchEvent
        val bottomNavigationView: BottomNavigationView = getBottomBar()
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                setBottomBarAnimation(bottomNavigationView)
                return true
            }
        }
        return false;
    }

    private fun getBottomBar(): BottomNavigationView {
        val activity: MainActivity = context as MainActivity
        return activity.bottom_nav
    }

    private fun setBottomBarAnimation(bottomNavBar: BottomNavigationView) {
        if (previousGestureDirection != gestureDirection) {
            if (gestureDirection == GestureDirection.UP) {
                bottomNavBar.slideAnimation(SlideDirection.UP, SlideType.SHOW)
                selfHide(bottomNavBar)
            } else if (gestureDirection == GestureDirection.DOWN) {
                bottomNavBar.slideAnimation(SlideDirection.DOWN, SlideType.HIDE)
            }
            previousGestureDirection = gestureDirection
        }
    }

    private fun selfHide(bottomNavBar: BottomNavigationView) {
        bottomNavBar.postDelayed({
            gestureDirection = GestureDirection.DOWN
            bottomNavBar.slideAnimation(SlideDirection.DOWN, SlideType.HIDE)
            previousGestureDirection = gestureDirection
        }, TIME_TO_HIDE)
    }
}
