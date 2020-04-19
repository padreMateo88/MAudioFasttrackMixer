package com.mpiotrowski.maudiofasttrackmixer.ui.views.faders

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View

abstract class Fader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    private val progressParameter = "PROGRESS_PARAMETER"
    private val superStateParameter = "SUPER_STATE_PARAMETER"
    protected val paint = Paint()

    lateinit var valueChangedListener: ValueChangedListener
    abstract var faderValue: Int
    abstract var speedMultiplier: Int
    protected var scaledBitmap: Bitmap? = null
    protected var initialPosition: Float = 0f
    protected var currentPosition: Float = 0f
    protected var progress: Int = -1
    private var currentWidth = 0
    private var isSeeking: Boolean = false

    interface ValueChangedListener {
        fun onValueChanged(value: Int)
    }

    abstract fun calculateInitialPosition(event: MotionEvent): Float
    abstract fun calculateCurrentPosition(event: MotionEvent): Float
    abstract fun calculateProgress(): Int
    abstract fun initializeBitmap(): Bitmap?
    abstract fun drawBackground(canvas: Canvas?)
    abstract fun drawKnob(canvas: Canvas?)

    init{
        this.setOnTouchListener { _, event ->
            parent.requestDisallowInterceptTouchEvent(true)
            if(event.action == KeyEvent.ACTION_DOWN) {
                isSeeking = true
                initialPosition = calculateInitialPosition(event)
                invalidate()
            }

            if(event.action == MotionEvent.ACTION_MOVE) {
                val initialProgress = progress
                currentPosition = calculateCurrentPosition(event)
                progress = calculateProgress()
                if(progress != initialProgress) {
                    if(::valueChangedListener.isInitialized) {
                        valueChangedListener.onValueChanged(faderValue)
                    }
                    invalidate()
                }
            }

            if(event.action == KeyEvent.ACTION_UP) {
                isSeeking = false
                invalidate()
            }

            true
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(width != currentWidth && width != 0) {
            scaledBitmap = initializeBitmap()
            if(progress < 0)
                faderValue = faderValue
            invalidate()
        }
        currentWidth = width
        drawBackground(canvas)
        drawKnob(canvas)
        drawSeekingState(canvas)
    }

    private fun drawSeekingState(canvas: Canvas?) {
        if (isSeeking) {
            paint.color = Color.GRAY
            paint.strokeWidth = 6f
            paint.isAntiAlias = true;
            paint.style = Paint.Style.STROKE
            canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(superStateParameter, super.onSaveInstanceState())
        bundle.putInt(progressParameter, this.progress)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            this.progress = state.getInt(progressParameter)
            super.onRestoreInstanceState(state.getParcelable(superStateParameter))
        }
    }
}