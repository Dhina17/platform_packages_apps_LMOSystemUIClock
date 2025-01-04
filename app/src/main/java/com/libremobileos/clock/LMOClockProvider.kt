package com.libremobileos.clock

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.res.ResourcesCompat
import com.android.systemui.plugins.annotations.Requires
import com.android.systemui.plugins.clocks.ClockController
import com.android.systemui.plugins.clocks.ClockId
import com.android.systemui.plugins.clocks.ClockMessageBuffers
import com.android.systemui.plugins.clocks.ClockMetadata
import com.android.systemui.plugins.clocks.ClockProviderPlugin
import com.android.systemui.plugins.clocks.ClockSettings

private val TAG = LMOClockProvider::class.simpleName
const val LMO_CLOCK_ID = "LMOClock"

@Requires(target = ClockProviderPlugin::class, version = ClockProviderPlugin.VERSION)
class LMOClockProvider : ClockProviderPlugin {

    private var messageBuffers: ClockMessageBuffers? = null

    private lateinit var pluginContext: Context
    private lateinit var sysuiContext: Context

    override fun onCreate(sysuiCtx: Context, pluginCtx: Context) {
        Log.i(TAG, "onCreate")
        pluginContext = pluginCtx
        sysuiContext = sysuiCtx
    }

    override fun initialize(buffers: ClockMessageBuffers?) {
        messageBuffers = buffers
    }

    override fun getClocks(): List<ClockMetadata> = listOf(ClockMetadata(LMO_CLOCK_ID))

    override fun createClock(settings: ClockSettings): ClockController {
        if (settings.clockId != LMO_CLOCK_ID) {
            throw IllegalArgumentException("${settings.clockId} is unsupported by $TAG")
        }

        return LMOClockController(
            pluginContext,
            sysuiContext,
            LayoutInflater.from(pluginContext),
            pluginContext.resources,
            sysuiContext.resources,
            settings,
            false,
            false,
            messageBuffers,
        )
    }

    override fun getClockThumbnail(id: ClockId): Drawable? {
        if (id != LMO_CLOCK_ID) {
            throw IllegalArgumentException("$id is unsupported by $TAG")
        }

        // TODO: Update placeholder to actual resource
        return ResourcesCompat.getDrawable(
            pluginContext.resources,
            R.drawable.clock_default_thumbnail,
            null
        )
    }
}