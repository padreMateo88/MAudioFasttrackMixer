package com.mpiotrowski.maudiofasttrackmixer.ui.views.faders

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet

class UnsignedHorizontalFader @JvmOverloads constructor(
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

        for (i in 4 .. 26 step 4)
            canvas?.drawLine(width*(32-i)/32f-4,height*(i/4f)/20f,width*(32-i)/32f-4,height*(20-i/4f)/20f,paint)

        paint.strokeWidth = 3f
        paint.color = Color.BLACK
        canvas?.drawLine(width*0.1f,height/2f,width*0.9f,height/2f,paint)
    }
}