package com.android.systemui.clocks

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import com.android.systemui.clocks.R

import android.view.ViewTreeObserver.InternalInsetsInfo
import android.view.ViewTreeObserver.OnComputeInternalInsetsListener
import com.android.systemui.plugins.clocks.ClockController
import com.android.systemui.plugins.clocks.ClockId
import com.android.systemui.plugins.clocks.ClockProviderPlugin
import com.android.systemui.plugins.clocks.ClockMetadata
import com.android.systemui.plugins.clocks.ClockSettings
import com.android.systemui.plugins.annotations.Requires

private val TAG = LMOClockProvider::class.simpleName
const val LMO_CLOCK_ID = "LMOClock"

@Requires(target = ClockProviderPlugin::class, version = ClockProviderPlugin.VERSION)
class LMOClockProvider : ClockProviderPlugin {
    private lateinit var ctx: Context
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var resources: Resources
    private val hasStepClockAnimation: Boolean = false
    private val migratedClocks: Boolean = false

    override fun onCreate(context: Context, plugin: Context) {
        ctx = plugin
        layoutInflater = LayoutInflater.from(ctx)
        resources = ctx.resources
    }

    override fun getClocks(): List<ClockMetadata> = listOf(ClockMetadata(LMO_CLOCK_ID))

    override fun createClock(settings: ClockSettings): ClockController {
        if (settings.clockId != LMO_CLOCK_ID) {
            throw IllegalArgumentException("${settings.clockId} is unsupported by $TAG")
        }
        return LMOClockController(
            ctx,
            layoutInflater,
            resources,
            settings,
            hasStepClockAnimation,
            migratedClocks,
        )
    }

    override fun getClockThumbnail(id: ClockId): Drawable? {
        return resources.getDrawable(R.drawable.clock_default_thumbnail, null)
    }

}
