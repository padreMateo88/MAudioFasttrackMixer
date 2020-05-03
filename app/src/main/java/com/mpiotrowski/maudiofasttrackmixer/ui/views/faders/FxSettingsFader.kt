package com.mpiotrowski.maudiofasttrackmixer.ui.views.faders

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet

class FxSettingsFader @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : UnsignedHorizontalFader(context, attrs, defStyleAttr) {


    var faderValueChangedListener: FaderValueChangedListener ?= null

    interface FaderValueChangedListener {
        fun onValueChanged()
    }

    override fun drawBackground(canvas: Canvas?) {
        paint.color = Color.GRAY
        paint.strokeWidth = 3f
        paint.style = Paint.Style.FILL

        val halfKnobWidth = (scaledBitmap?.width ?: 0) / 2f
        for (i in 1..8)
            canvas?.drawLine(
                (width-2*halfKnobWidth) * (9-i)/9 + halfKnobWidth,
                height * ((i)*4 / 4f) / 20f,
                (width-2*halfKnobWidth) * (9-i)/9 + halfKnobWidth,
                height * (20 - (i)*4 / 4f) / 20f,
                paint
            )

        paint.color = Color.BLACK
        canvas?.drawLine(halfKnobWidth, height / 2f, width - halfKnobWidth, height / 2f, paint)

        paint.color = Color.GRAY
        canvas?.drawLine((width-2*halfKnobWidth) + halfKnobWidth, 0f, (width-2*halfKnobWidth) + halfKnobWidth, height.toFloat(), paint)
        canvas?.drawLine(halfKnobWidth-3f, height * 9/20f,halfKnobWidth-3f, height * 11/20f, paint)
    }
}