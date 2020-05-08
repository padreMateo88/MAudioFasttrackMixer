package com.mpiotrowski.maudiofasttrackmixer.ui.views.faders

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet


class SignedHorizontalFader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HorizontalFader(context, attrs, defStyleAttr) {

    override var faderValue : Int = 0
        set(value) {
            field = value
            scaledBitmap?.let {
                progress = (((value + 100) / 200f) * (width - it.width)).toInt()
            }
            invalidate()
        }
        get() {
            if(progress < 0)
                return field
            scaledBitmap?.let{
                return (progress.toFloat()/(width - it.width)*200f).toInt() - 100
            }
            return field
        }

    override fun drawBackground(canvas: Canvas?) {
        paint.color = Color.GRAY
        paint.strokeWidth = 3f
        paint.style = Paint.Style.FILL

        val halfKnobWidth = (scaledBitmap?.width ?: 0)/2f
        for (i in 1 .. 13 step 4) {
            canvas?.drawLine(halfKnobWidth + width*(i-1)/36f,height*(i/2)/20f,halfKnobWidth + width*(i-1)/36f,height*(20-i/2)/20f,paint)
            canvas?.drawLine(width*(37 - i)/36f-halfKnobWidth,height*(i/2)/20f,width*(37 - i)/36f-halfKnobWidth,height*(20-i/2)/20f,paint)
        }
        canvas?.drawLine(width/2f,height*2/5f,width/2f,height*3/5f,paint)

        paint.strokeWidth = 3f
        paint.color = Color.BLACK
        canvas?.drawLine(halfKnobWidth,height/2f,width - halfKnobWidth,height/2f,paint)
    }
}