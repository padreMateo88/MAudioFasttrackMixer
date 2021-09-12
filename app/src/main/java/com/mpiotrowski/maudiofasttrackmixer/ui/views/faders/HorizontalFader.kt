package com.mpiotrowski.maudiofasttrackmixer.ui.views.faders

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import com.mpiotrowski.maudiofasttrackmixer.R

abstract class HorizontalFader(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    Fader(context, attrs, defStyleAttr) {

    override val speedMultiplier: Int
        get() = if(fine) 15 else 3

    override fun calculateInitialPosition(event: MotionEvent): Float {
        return event.x - progress*speedMultiplier
    }

    override fun calculateCurrentPosition(event: MotionEvent): Float {
        return event.x
    }

    override fun calculateProgress(): Int {
        scaledBitmap?.let {
            var progress = ((currentPosition - initialPosition)/speedMultiplier.toFloat()).toInt()
            if(progress < 0)
                progress = 0
            if(progress > width - it.width)
                progress = width - it.width
            return progress
        }
        return 0
    }

    override fun initializeBitmap(): Bitmap? {
        val bitmap = BitmapFactory.decodeResource(resources,
            R.drawable.knob
        )
        if(width <= 0 || height <= 0)
            return null

        val dstWidth = (height*bitmap.width*0.5f/bitmap.height).toInt()
        val dstHeight = (height*0.5f).toInt()

        if(dstWidth <= 0 || dstHeight <= 0)
            return null

        return Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false)
    }

    override fun drawKnob(canvas: Canvas?) {
        scaledBitmap?.let {
            canvas?.drawBitmap(it,progress.toFloat(),height*0.25f,paint)
        }
    }
}