package com.mpiotrowski.maudiofasttrackmixer.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mpiotrowski.maudiofasttrackmixer.ui.MainActivity
import com.mpiotrowski.maudiofasttrackmixer.util.SlideDirection
import com.mpiotrowski.maudiofasttrackmixer.util.SlideType
import com.mpiotrowski.maudiofasttrackmixer.util.slideAnimation
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.sqrt

private enum class GestureDirection {
    UP,
    DOWN
}

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
    private var previousActionDown: Boolean = false
    private var gestureDirection: GestureDirection = GestureDirection.UP
    private var previousGestureDirection: GestureDirection = GestureDirection.UP

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
    return when (ev.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
            if (!previousActionDown) {
                previousActionDown = true
            }
            false
        }
        MotionEvent.ACTION_MOVE -> {
            previousActionDown = false
            val xDiff: Int = getDistance(ev)
            xDiff > mTouchSlop
        } else -> {
            false
        }
    }
}
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Here we actually handle the touch event (e.g. if the action is ACTION_MOVE, scroll this container).
        // This method will only be called if the touch event was intercepted in onInterceptTouchEvent
        super.onTouchEvent(event);
        when (event.action) {
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
        if (previousGestureDirection != gestureDirection) {
            if (gestureDirection == GestureDirection.UP) {
                bottomNavBar.slideAnimation(SlideDirection.UP, SlideType.SHOW)
            } else if (gestureDirection == GestureDirection.DOWN) {
                bottomNavBar.slideAnimation(SlideDirection.DOWN, SlideType.HIDE)
            }
            previousGestureDirection = gestureDirection
        }
    }
}