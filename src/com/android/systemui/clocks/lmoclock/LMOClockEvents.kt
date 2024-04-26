package com.android.systemui.clocks.lmoclock

import android.content.res.Resources
import com.android.systemui.plugins.clocks.AlarmData
import com.android.systemui.plugins.clocks.ClockEvents
import com.android.systemui.plugins.clocks.WeatherData
import com.android.systemui.plugins.clocks.ZenData
import java.util.Locale
import java.util.TimeZone

class LMOClockEvents : ClockEvents {
    override fun onAlarmDataChanged(data: AlarmData) {}

    override fun onColorPaletteChanged(resources: Resources) {
        TODO("Not yet implemented")
    }

    override fun onLocaleChanged(locale: Locale) {
        TODO("Not yet implemented")
    }

    override fun onSeedColorChanged(seedColor: Int?) {
        TODO("Not yet implemented")
    }

    override fun onTimeFormatChanged(is24Hr: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onTimeZoneChanged(timeZone: TimeZone) {
        TODO("Not yet implemented")
    }

    override fun onWeatherDataChanged(data: WeatherData) {}

    override fun onZenDataChanged(data: ZenData) {}
}