package com.android.systemui.clocks.lmoclock

//import android.content.res.Resources

import android.content.Context
import android.graphics.drawable.Drawable
import com.android.systemui.plugins.annotations.Requires
import com.android.systemui.plugins.clocks.ClockController
import com.android.systemui.plugins.clocks.ClockMetadata
import com.android.systemui.plugins.clocks.ClockProviderPlugin
import com.android.systemui.plugins.clocks.ClockSettings

private val TAG = LMOClockProvider::class.simpleName

const val LMO_CLOCK_ID = "LMOClock"
typealias ClockId = String

@Requires(target = ClockProviderPlugin::class, version = ClockProviderPlugin.VERSION)
class LMOClockProvider : ClockProviderPlugin {
    private val hasStepClockAnimation: Boolean = false
    private val migratedClocks: Boolean = false

    private lateinit var pluginCtx: Context
    private lateinit var sysuiCtx: Context

    override fun onCreate(sysuiCtx: Context, pluginCtx: Context) {
        this.sysuiCtx = sysuiCtx
        this.pluginCtx = pluginCtx
        android.util.Log.e(TAG, "oncreateclock")

        metadata.add(ClockMetadata(LMO_CLOCK_ID))
    }

//    override fun getClocks(): List<ClockMetadata> = listOf(ClockMetadata(LMO_CLOCK_ID))

    override fun createClock(settings: ClockSettings): ClockController {
        if (settings.clockId == null) {
            throw IllegalArgumentException("No clockId specified")
        }
        android.util.Log.e(TAG, "createClock")
        return LMOClockController(
            pluginCtx,
            settings,
            hasStepClockAnimation,
            migratedClocks,
        )
    }

    //
//    override fun getClockThumbnail(id: ClockId): Drawable? {
//        return ContextCompat.getDrawable(ctx, R.drawable.clock_thumbnail)
//    }
    private val metadata = mutableListOf<ClockMetadata>()
    private val thumbnailCallbacks = mutableMapOf<ClockId, (ClockId) -> Drawable?>()

    override fun getClockThumbnail(id: ClockId): Drawable? = thumbnailCallbacks[id]!!(id)
    override fun getClocks(): List<ClockMetadata> {

        android.util.Log.e(TAG, metadata.toString())

        return metadata
    }
}

