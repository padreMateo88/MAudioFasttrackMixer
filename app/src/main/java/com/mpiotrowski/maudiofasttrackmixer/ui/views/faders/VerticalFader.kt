package com.mpiotrowski.maudiofasttrackmixer.ui.views.faders

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import com.mpiotrowski.maudiofasttrackmixer.R

class VerticalFader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Fader(context, attrs, defStyleAttr) {

    override var faderValue : Int = 75
        set(value) {
            field = value
            scaledBitmap?.let {
                progress = it.height + ((value/100f)*(height - it.height)).toInt()
                invalidate()
            }
        }
        get() {
            if(progress < 0)
                return field
            scaledBitmap?.let {
                return ((progress - it.height).toFloat()/(height - it.height).toFloat()*100f).toInt()
            }
            return field
        }

    override var speedMultiplier: Int = 1

    override fun calculateInitialPosition(event: MotionEvent): Float {
        return (height - event.y) - progress*speedMultiplier
    }

    override fun calculateCurrentPosition(event: MotionEvent): Float {
        return height - event.y
    }

    override fun calculateProgress(): Int {
        scaledBitmap?.let {
            var progress = ((currentPosition - initialPosition)/speedMultiplier).toInt()
            if(progress < it.height)
                progress = it.height
            if(progress > height)
                progress = height
            return progress
        }
        return 0
    }

    override fun initializeBitmap(): Bitmap? {
        val bitmap = BitmapFactory.decodeResource(resources,
            R.drawable.fader
        )
        if(width <= 0 || height <= 0)
            return null
        return Bitmap.createScaledBitmap(bitmap, (width*0.35f).toInt(), (width*bitmap.height*0.35f/bitmap.width).toInt(), false)
    }

    override fun drawBackground(canvas: Canvas?) {
        paint.color = Color.GRAY
        paint.strokeWidth = 3f
        paint.style = Paint.Style.FILL

        for (i in 1 .. 9 step 2){
            paint.strokeWidth = if(i==3) 5f else 3f
//            val leftMultiplier: Float = if(i == 3) 0f else 0.2f
//            val rightMultiplier: Float = if(i == 3) 1f else 0.8f
//
            val leftMultiplier: Float = if(i == 3) 0.15f else 0.2f
            val rightMultiplier: Float = if(i == 3) 0.85f else 0.8f
//            val leftMultiplier: Float = 0f
//            val rightMultiplier: Float = 1f
            canvas?.drawLine(width*leftMultiplier,height*i/10f,width*rightMultiplier,height*i/10f,paint)
        }

        paint.color =Color.BLACK
        paint.strokeWidth = 3f
        paint.style = Paint.Style.FILL
        canvas?.drawLine(width/2f,0f,width/2f,height.toFloat(),paint)
    }

    override fun drawKnob(canvas: Canvas?) {
        scaledBitmap?.let {
            val faderPosition =
                if(height - it.height > height - progress)
                    (height - progress).toFloat()
                else
                    (height - it.height).toFloat()
            canvas?.drawBitmap(it,width*0.325f,faderPosition,paint)
        }
    }
}