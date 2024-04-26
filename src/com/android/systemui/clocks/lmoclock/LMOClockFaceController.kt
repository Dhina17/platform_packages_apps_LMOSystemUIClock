package com.android.systemui.clocks.lmoclock

import android.view.View
import com.android.systemui.log.core.MessageBuffer
import com.android.systemui.plugins.clocks.ClockAnimations
import com.android.systemui.plugins.clocks.ClockFaceConfig
import com.android.systemui.plugins.clocks.ClockFaceController
import com.android.systemui.plugins.clocks.ClockFaceEvents
import com.android.systemui.plugins.clocks.ClockFaceLayout

class LMOClockFaceController(view: View) : ClockFaceController {
    override val animations: ClockAnimations
        get() = TODO("Not yet implemented")

    override val config = ClockFaceConfig()
    override val events: ClockFaceEvents
        get() = TODO("Not yet implemented")
    override val layout: ClockFaceLayout
        get() = TODO("Not yet implemented")
    override var messageBuffer: MessageBuffer?
        get() = TODO("Not yet implemented")
        set(value) {}
    override val view: View
        get() = TODO("Not yet implemented")
}