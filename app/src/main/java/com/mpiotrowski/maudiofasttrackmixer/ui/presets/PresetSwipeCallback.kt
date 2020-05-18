package com.mpiotrowski.maudiofasttrackmixer.ui.presets

import android.annotation.SuppressLint
import android.graphics.*
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mpiotrowski.maudiofasttrackmixer.R

class PresetSwipeCallback(dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    enum class IconPosition{LEFT, RIGHT}
    enum class ItemState{CENTER, LEFT, RIGHT}

    private var itemState: ItemState = ItemState.CENTER

    private lateinit var swipeListener: SwipeListener

    constructor(dragDirs: Int, swipeDirs: Int,swipeListener: SwipeListener ): this(dragDirs, swipeDirs) {
        this.swipeListener = swipeListener
    }

    interface SwipeListener {
        fun swipeRight(adapterPosition: Int)
        fun swipeLeft(adapterPosition: Int)
    }
    private var swipeBack = false

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onChildDraw(canvas: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float,
                             actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            setTouchListener(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            if(itemState !=ItemState.LEFT && dX < -viewHolder.itemView.width) {
                itemState = ItemState.LEFT
                if(this::swipeListener.isInitialized)
                    swipeListener.swipeLeft(viewHolder.adapterPosition)
            } else if(itemState !=ItemState.RIGHT && dX > viewHolder.itemView.width) {
                itemState = ItemState.RIGHT
                if(this::swipeListener.isInitialized)
                    swipeListener.swipeRight(viewHolder.adapterPosition)

            } else if(dX in -viewHolder.itemView.width.toFloat() .. viewHolder.itemView.width.toFloat()) {
                itemState = ItemState.CENTER
            }

            if(dX > 0) {
                drawLoadBackground(viewHolder, canvas, R.color.darkRed, R.drawable.delete, IconPosition.LEFT)
            } else
                drawLoadBackground(viewHolder, canvas, R.color.darkGreen, R.drawable.load, IconPosition.RIGHT)
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawLoadBackground(
        viewHolder: RecyclerView.ViewHolder,
        canvas: Canvas,
        backgroundColor: Int,
        icon: Int,
        iconPosition: IconPosition
    ) {
        val leftButton = RectF(
            viewHolder.itemView.left.toFloat(),
            viewHolder.itemView.top.toFloat(),
            viewHolder.itemView.width.toFloat(),
            viewHolder.itemView.bottom.toFloat()
        )
        drawBackground(viewHolder, backgroundColor, canvas, leftButton)
        drawBitmap(viewHolder, icon, iconPosition, canvas)
    }

    private fun drawBitmap(
        viewHolder: RecyclerView.ViewHolder,
        icon: Int,
        iconPosition: IconPosition,
        canvas: Canvas
    ) {
        val bitmap = BitmapFactory.decodeResource(
            viewHolder.itemView.context.resources,
            icon,
            null
        )
        val scaledBitmapDelete = Bitmap.createScaledBitmap(
            bitmap,
            (viewHolder.itemView.height * 0.7 * bitmap.width / bitmap.height).toInt(),
            (viewHolder.itemView.height * 0.7).toInt(),
            false
        )
        val leftMargin = viewHolder.itemView.resources.getDimension(R.dimen.dimen_8dp)
        val left = if (iconPosition == IconPosition.LEFT)
            viewHolder.itemView.left + leftMargin
        else
            viewHolder.itemView.right - scaledBitmapDelete.width - leftMargin
        canvas.drawBitmap(
            scaledBitmapDelete,
            left,
            viewHolder.itemView.top + viewHolder.itemView.height * 0.15f,
            Paint()
        )
    }

    private fun drawBackground(
        viewHolder: RecyclerView.ViewHolder,
        backgroundColor: Int,
        canvas: Canvas,
        leftButton: RectF
    ) {
        val cornerRadius = viewHolder.itemView.resources.getDimension(R.dimen.dimen_2dp)
        val backgroundPaint = Paint()
        backgroundPaint.color = ContextCompat.getColor(viewHolder.itemView.context, backgroundColor)
        canvas.drawRoundRect(leftButton, cornerRadius, cornerRadius, backgroundPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            false
        }
    }

    override fun convertToAbsoluteDirection(flags: Int,layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }
}