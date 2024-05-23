/*
 * SPDX-FileCopyrightText: 2024 The LibreMobileOS Foundation
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.systemui.plugin.libremobileos.clock

import android.graphics.drawable.Drawable
import com.android.systemui.plugins.annotations.ProvidesInterface
import com.android.systemui.plugins.clocks.ClockController
import com.android.systemui.plugins.clocks.ClockMetadata
import com.android.systemui.plugins.clocks.ClockProviderPlugin
import com.android.systemui.plugins.clocks.ClockSettings
import com.android.systemui.shared.clocks.ClockId

@ProvidesInterface(action = ClockProviderPlugin.ACTION, version = ClockProviderPlugin.VERSION)
class LMOClockProvider : ClockProviderPlugin {
    override fun createClock(settings: ClockSettings): ClockController {
        TODO("Not yet implemented")
    }

    override fun getClockThumbnail(id: ClockId): Drawable? {
        TODO("Not yet implemented")
    }

    override fun getClocks(): List<ClockMetadata> {
        TODO("Not yet implemented")
    }
}
