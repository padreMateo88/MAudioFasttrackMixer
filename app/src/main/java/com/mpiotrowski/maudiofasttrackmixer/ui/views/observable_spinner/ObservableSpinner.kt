package com.mpiotrowski.maudiofasttrackmixer.ui.views.observable_spinner

import android.content.Context
import android.util.AttributeSet

class ObservableSpinner: androidx.appcompat.widget.AppCompatSpinner {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    interface OnSelectedPositionListener {
        fun onSelectedPositionChanged()
    }
}