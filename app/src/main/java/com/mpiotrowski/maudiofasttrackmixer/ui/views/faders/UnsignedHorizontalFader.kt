package com.mpiotrowski.maudiofasttrackmixer.ui.views.faders

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet

open class UnsignedHorizontalFader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HorizontalFader(context, attrs, defStyleAttr) {

    override var faderValue : Int = 0
        set(value) {
            field = value
            scaledBitmap?.let {
                progress = ((value/100f)*(width - it.width)).toInt()
            }
            invalidate()
        }
        get() {
            if(progress < 0)
                return field
            scaledBitmap?.let {return (progress.toFloat()/(width - it.width)*100f).toInt()}
            return field
        }

    override fun drawBackground(canvas: Canvas?) {
        paint.color = Color.GRAY
        paint.strokeWidth = 3f
        paint.style = Paint.Style.FILL

        val halfKnobWidth = (scaledBitmap?.width ?: 0)/2f
        for (i in 4 .. 28 step 4)
            canvas?.drawLine(width*(36-i)/32f - halfKnobWidth,height*(i/4f)/20f,width*(36-i)/32f - halfKnobWidth,height*(20-i/4f)/20f,paint)


        paint.strokeWidth = 3f
        paint.color = Color.BLACK
        canvas?.drawLine(halfKnobWidth,height/2f,width - halfKnobWidth,height/2f,paint)
    }
}