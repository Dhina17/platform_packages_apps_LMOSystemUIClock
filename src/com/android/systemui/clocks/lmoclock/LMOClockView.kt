package com.android.systemui.clocks.lmoclock

import android.annotation.ColorInt
import android.annotation.SuppressLint
import android.widget.TextView
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.android.systemui.log.core.Logger
import com.android.systemui.log.core.MessageBuffer

private val TAG = LMOClockView::class.simpleName!!

@SuppressLint("AppCompatCustomView")
class LMOClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    var messageBuffer: MessageBuffer? = null
        set(value) {
            logger = if (value != null) Logger(value, TAG) else null
        }

    private var logger: Logger? = null

    @ColorInt
    private var dozingColor = 0

    @ColorInt
    private var lockScreenColor = 0

    fun setColors(@ColorInt dozingColor: Int, lockScreenColor: Int) {
        this.dozingColor = dozingColor
        this.lockScreenColor = lockScreenColor
    }
}