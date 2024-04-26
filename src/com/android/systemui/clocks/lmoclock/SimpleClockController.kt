package com.android.systemui.clocks.lmoclock

import android.content.res.Resources
import com.android.systemui.plugins.clocks.ClockConfig
import com.android.systemui.plugins.clocks.ClockController
import com.android.systemui.plugins.clocks.ClockEvents
import com.android.systemui.plugins.clocks.ClockFaceController
import java.io.PrintWriter

class SimpleClockController : ClockController {
    override val config: ClockConfig
        get() = TODO("Not yet implemented")
    override val events: ClockEvents
        get() = TODO("Not yet implemented")
    override val largeClock: ClockFaceController
        get() = TODO("Not yet implemented")
    override val smallClock: ClockFaceController
        get() = TODO("Not yet implemented")

    override fun dump(pw: PrintWriter) {
        TODO("Not yet implemented")
    }

    override fun initialize(resources: Resources, dozeFraction: Float, foldFraction: Float) {
        TODO("Not yet implemented")
    }
}