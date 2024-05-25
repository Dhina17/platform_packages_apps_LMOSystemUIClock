/*
 * SPDX-FileCopyrightText: 2024 The LibreMobileOS Foundation
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.systemui.plugin.libremobileos.clock

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.android.systemui.plugins.annotations.ProvidesInterface
import com.android.systemui.plugins.annotations.Requires
import com.android.systemui.plugins.clocks.ClockController
import com.android.systemui.plugins.clocks.ClockMetadata
import com.android.systemui.plugins.clocks.ClockProviderPlugin
import com.android.systemui.plugins.clocks.ClockSettings

/** Identifies a clock design */
typealias ClockId = String

private val TAG = "dhina17test"
const val LMO_CLOCK_ID = "LMOClock"

@Requires(target = ClockProviderPlugin::class, version = ClockProviderPlugin.VERSION)
@ProvidesInterface(action = ClockProviderPlugin.ACTION, version = ClockProviderPlugin.VERSION)
class LMOClockProvider : ClockProviderPlugin {
    private val hasStepClockAnimation: Boolean = false
    private val migratedClocks: Boolean = false

    private lateinit var pluginCtx: Context
    private lateinit var sysuiCtx: Context

    private val metadata = mutableListOf<ClockMetadata>()

    override fun onCreate(sysuiCtx: Context, pluginCtx: Context) {
        this.sysuiCtx = sysuiCtx
        this.pluginCtx = pluginCtx
        Log.e(TAG, "onCreate")
        metadata.add(ClockMetadata(LMO_CLOCK_ID))
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy()")
    }

    override fun createClock(settings: ClockSettings): ClockController {
        if (settings.clockId == null) {
            throw IllegalArgumentException("No clockId specified")
        }
        Log.e(TAG, "createClock")
        return LMOClockController(
                pluginCtx,
                settings,
                hasStepClockAnimation,
                migratedClocks,
        )
    }

    override fun getClockThumbnail(id: ClockId): Drawable? {
        Log.e(TAG, "getClockThumbnail")
        return null
    }

    override fun getClocks(): List<ClockMetadata> {
        Log.e(TAG, "getClocks()")
        return metadata
    }
}
