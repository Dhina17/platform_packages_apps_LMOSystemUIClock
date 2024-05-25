package com.libremobileos.clock

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import com.android.systemui.plugins.annotations.Requires
import com.android.systemui.plugins.clocks.ClockController
import com.android.systemui.plugins.clocks.ClockMetadata
import com.android.systemui.plugins.clocks.ClockProviderPlugin
import com.android.systemui.plugins.clocks.ClockSettings
import com.android.systemui.shared.clocks.ClockId

//private val TAG = LMOClockProvider::class.simpleName
private val TAG = "dhina17test"
const val LMO_CLOCK_ID = "LMOClock"

@Requires(target = ClockProviderPlugin::class, version = ClockProviderPlugin.VERSION)
class LMOClockProvider : ClockProviderPlugin {

    private lateinit var pluginContext: Context

    override fun onCreate(sysuiCtx: Context, pluginCtx: Context) {
        Log.i(TAG, "onCreate")
        pluginContext = pluginCtx
    }

    override fun createClock(settings: ClockSettings): ClockController {
        Log.i(TAG, "createClock")
        if (settings.clockId != LMO_CLOCK_ID) {
            throw IllegalArgumentException("${settings.clockId} is unsupported by $TAG")
        }

        return LMOClockController(
            pluginContext,
            LayoutInflater.from(pluginContext)
        )

    }

    override fun getClockThumbnail(id: ClockId): Drawable? {
        Log.i(TAG, "getThumbnail")
        return null
    }

    override fun getClocks(): List<ClockMetadata> {
        Log.i(TAG, "getClocks")
        return listOf(ClockMetadata(LMO_CLOCK_ID))
    }
}
