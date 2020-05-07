package com.mpiotrowski.maudiofasttrackmixer.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mpiotrowski.maudiofasttrackmixer.ui.MainActivity
import com.mpiotrowski.maudiofasttrackmixer.util.SlideDirection
import com.mpiotrowski.maudiofasttrackmixer.util.SlideType
import com.mpiotrowski.maudiofasttrackmixer.util.slideAnimation
import kotlinx.android.synthetic.main.activity_main.*

class MyLinearLayout: LinearLayout {
    private var ctx: Context? = null
    constructor(context: Context) : super(context) {
        ctx = context
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        ctx = context
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        ctx = context
    }

    private val mTouchSlop: Int = android.view.ViewConfiguration.get(context).scaledTouchSlop
    private var mIsScrolling: Boolean = false
    private var bottomBarVisibility: Boolean = true

    private fun getDistance(ev: MotionEvent): Int {
        var x = ev.getX()
        var y = ev.getY()
        var distanceSum: Double = 0.0
        val historySize: Int = ev.getHistorySize()
        for (h in 0 until historySize step 1) {
            // historical point
            var hx: Float = ev.getHistoricalX(0, h)
            var hy: Float = ev.getHistoricalY(0, h)
            // distance between startX,startY and historical point
            var dx: Float = (hx - x)
            var dy: Float = (hy - y)
            distanceSum += Math.sqrt((dx * dx + dy * dy).toDouble())
            // make historical point the start point for next loop iteration
            x = hx;
            y = hy;
        }
        // add distance from last historical point to event's point
        var dx: Float = (ev.getX(0) - x)
        var dy: Float = (ev.getY(0) - y)
        distanceSum += Math.sqrt((dx * dx + dy * dy).toDouble())
        return distanceSum.toInt()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
    return when (ev.actionMasked) {
        // Always handle the case of the touch gesture being complete.
        MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
            // Release the scroll.
            mIsScrolling = false
            false // Do not intercept touch event, let the child handle it
        }
        MotionEvent.ACTION_DOWN -> {
            bottomBarVisibility = !bottomBarVisibility && mIsScrolling
            false
        }
        MotionEvent.ACTION_MOVE -> {
            if (mIsScrolling) {
                Log.d("----------------->", "SCROLL")
                // We're currently scrolling, so yes, intercept the touch event!
                true
            } else {
                Log.d("----------------->", "NOT_SCROLL")
                // If the user has dragged her finger horizontally more than the touch slop, start the scroll left as an exercise for the reader
                val xDiff: Int = getDistance(ev)
                // Touch slop should be calculated using ViewConfiguration constants.
                if (xDiff > mTouchSlop) {
                    // Start scrolling!
                    mIsScrolling = true
                    true
                } else {
                    false
                }
            }
        }
        else -> {
            false
        }
    }
}
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Here we actually handle the touch event (e.g. if the action is ACTION_MOVE, scroll this container).
        // This method will only be called if the touch event was intercepted in onInterceptTouchEvent
        super.onTouchEvent(event);
        when (event.getAction()) {
            MotionEvent.ACTION_MOVE -> {
                val activity: MainActivity = ctx as MainActivity
                val bottomNavBar: BottomNavigationView = activity.bottom_nav
                setBottomBarAnimation(bottomNavBar)
                return true
            }
        }
        return false;
    }

    private fun setBottomBarAnimation(bottomNavBar: BottomNavigationView) {
        if (bottomBarVisibility) {
            bottomNavBar.slideAnimation(SlideDirection.UP, SlideType.SHOW)
        } else {
            bottomNavBar.slideAnimation(SlideDirection.DOWN, SlideType.HIDE)
        }
    }
}