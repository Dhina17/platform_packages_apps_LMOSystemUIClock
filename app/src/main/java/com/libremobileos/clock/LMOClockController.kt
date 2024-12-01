package com.libremobileos.clock

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.android.systemui.plugins.clocks.AlarmData
import com.android.systemui.plugins.clocks.ClockAnimations
import com.android.systemui.plugins.clocks.ClockConfig
import com.android.systemui.plugins.clocks.ClockController
import com.android.systemui.plugins.clocks.ClockEvents
import com.android.systemui.plugins.clocks.ClockFaceConfig
import com.android.systemui.plugins.clocks.ClockFaceController
import com.android.systemui.plugins.clocks.ClockFaceEvents
import com.android.systemui.plugins.clocks.ClockFaceLayout
import com.android.systemui.plugins.clocks.DefaultClockFaceLayout
import com.android.systemui.plugins.clocks.WeatherData
import com.android.systemui.plugins.clocks.ZenData
import java.io.PrintWriter
import java.util.Locale
import java.util.TimeZone

private const val TAG = "dhina17test"

class LMOClockController(
    context: Context,
    layoutInflater: LayoutInflater
) : ClockController {

    private val clocks: List<TextView>

    override val config: ClockConfig by lazy {
        ClockConfig(
            LMO_CLOCK_ID,
            "LMO Clock",
            "LMO cool clock"
        )
    }

    override val events: DefaultClockEvents
    override val largeClock: ClockFaceController
    override val smallClock: ClockFaceController

    init {
        val parent = FrameLayout(context)
        smallClock =
            LMOClockFaceController(
                layoutInflater.inflate(R.layout.lmo_clock_small, parent, false)
                        as TextView,
            )
        largeClock =
            LMOClockFaceController(
                layoutInflater.inflate(R.layout.lmo_clock_large, parent, false)
                        as TextView
            )
        clocks = listOf(smallClock.view, largeClock.view)
        events = DefaultClockEvents()
        events.onLocaleChanged(Locale.getDefault())
    }

    override fun dump(pw: PrintWriter) {
        Log.i(TAG, "Dump")
    }

    override fun initialize(resources: Resources, dozeFraction: Float, foldFraction: Float) {
        // Later
    }

    private inner class LMOClockFaceController(
        override val view: TextView
    ) : ClockFaceController {

        override val animations: ClockAnimations = DefaultClockAnimations(view)
        override val config: ClockFaceConfig = ClockFaceConfig()

        override val events =
            object : ClockFaceEvents {
                override fun onTimeTick() {
                    Log.i(TAG, "Time changed")
                }

                override fun onRegionDarknessChanged(isRegionDark: Boolean) {
                }

                override fun onTargetRegionChanged(targetRegion: Rect?) {
                }

                override fun onFontSettingChanged(fontSizePx: Float) {
                }

                override fun onSecondaryDisplayChanged(onSecondaryDisplay: Boolean) {
                }
            }

        override val layout: ClockFaceLayout = DefaultClockFaceLayout(view)

        init {
            view.setTextColor(Color.MAGENTA)
        }

    }

    inner class DefaultClockEvents : ClockEvents {
        override fun onTimeFormatChanged(is24Hr: Boolean) {
            Log.i(TAG, "time format changed")
        }


        override fun onTimeZoneChanged(timeZone: TimeZone) {
            Log.i(TAG, "time zone changed")
        }

        override fun onColorPaletteChanged(resources: Resources) {
            Log.i(TAG, "color pallatte changed")
        }

        override fun onSeedColorChanged(seedColor: Int?) {
            Log.i(TAG, "onSeedColorChanged")
        }

        override fun onLocaleChanged(locale: Locale) {
            Log.i(TAG, "onLocaleChanged")
        }

        override fun onWeatherDataChanged(data: WeatherData) {}

        override fun onAlarmDataChanged(data: AlarmData) {}
        override fun onZenDataChanged(data: ZenData) {}

        override var isReactiveTouchInteractionEnabled: Boolean = false
    }

    private inner class DefaultClockAnimations(
        val view: TextView
    ) : ClockAnimations {

        override fun enter() {
        }

        override fun charge() {}

        override fun fold(fraction: Float) {
        }

        override fun doze(fraction: Float) {
        }

        override fun onPickerCarouselSwiping(swipingFraction: Float) {
        }

        override fun onPositionUpdated(distance: Float, fraction: Float) {

        }

        override fun onPositionUpdated(fromLeft: Int, direction: Int, fraction: Float) {}
    }


}