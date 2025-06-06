/*
 * SPDX-FileCopyrightText: 2022 The Android Open Source Project
 * SPDX-FileCopyrightText: 2024-2025 The LibreMobileOS Foundation
 * SPDX-License-Identifier: Apache-2.0
 */
package com.libremobileos.clock

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.icu.text.NumberFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import com.android.systemui.log.core.MessageBuffer
import com.android.systemui.plugins.clocks.AlarmData
import com.android.systemui.plugins.clocks.ClockAnimations
import com.android.systemui.plugins.clocks.ClockConfig
import com.android.systemui.plugins.clocks.ClockController
import com.android.systemui.plugins.clocks.ClockEvents
import com.android.systemui.plugins.clocks.ClockFaceConfig
import com.android.systemui.plugins.clocks.ClockFaceController
import com.android.systemui.plugins.clocks.ClockFaceEvents
import com.android.systemui.plugins.clocks.ClockMessageBuffers
import com.android.systemui.plugins.clocks.ClockReactiveSetting
import com.android.systemui.plugins.clocks.ClockSettings
import com.android.systemui.plugins.clocks.DefaultClockFaceLayout
import com.android.systemui.plugins.clocks.WeatherData
import com.android.systemui.plugins.clocks.ZenData
import java.io.PrintWriter
import java.util.Locale
import java.util.TimeZone

/**
 * Controls the default clock visuals.
 *
 * This serves as an adapter between the clock interface and the AnimatableClockView used by the
 * existing lockscreen clock.
 */
class LMOClockController(
    private val clockId: String,
    private val ctx: Context,
    private val sysuiCtx: Context,
    layoutInflater: LayoutInflater,
    resources: Resources,
    private val sysuiResources: Resources,
    settings: ClockSettings?,
    private val hasStepClockAnimation: Boolean = false,
    private val migratedClocks: Boolean = false,
    messageBuffers: ClockMessageBuffers? = null,
) : ClockController {
    override val smallClock: DefaultClockFaceController
    override val largeClock: LargeClockFaceController
    private val clocks: List<AnimatableClockView>

    private val burmeseNf = NumberFormat.getInstance(Locale.forLanguageTag("my"))
    private val burmeseNumerals = burmeseNf.format(FORMAT_NUMBER.toLong())
    private val burmeseLineSpacing =
        resources.getFloat(R.dimen.keyguard_clock_line_spacing_scale_burmese)
    private val defaultLineSpacing by lazy {
        resources.getFloat(AnimatableClockView.getLineSpaceByClockId(clockId))
    }
    protected var onSecondaryDisplay: Boolean = false

    override val events: DefaultClockEvents
    override val config: ClockConfig by lazy {
        ClockConfig(
            clockId,
            getClockName(),
            getClockDescription(),
        )
    }

    init {
        // val parent = FrameLayout(ctx)
        smallClock =
            DefaultClockFaceController(
                // layoutInflater.inflate(R.layout.lmo_clock_small, parent, false)
                //        as AnimatableClockView,
                AnimatableClockView.getSmallClockView(ctx, clockId),
                settings?.seedColor,
                messageBuffers?.smallClockMessageBuffer,
            )
        largeClock =
            LargeClockFaceController(
                // layoutInflater.inflate(R.layout.lmo_clock_large, parent, false)
                //        as AnimatableClockView,
                AnimatableClockView.getLargeClockView(ctx, clockId),
                settings?.seedColor,
                messageBuffers?.largeClockMessageBuffer,
            )
        clocks = listOf(smallClock.view, largeClock.view)

        events = DefaultClockEvents()
        events.onLocaleChanged(Locale.getDefault())
    }

    override fun initialize(resources: Resources, dozeFraction: Float, foldFraction: Float) {
        largeClock.recomputePadding(null)
        largeClock.animations = LargeClockAnimations(largeClock.view, dozeFraction, foldFraction)
        smallClock.animations = DefaultClockAnimations(smallClock.view, dozeFraction, foldFraction)
        events.onColorPaletteChanged(resources)
        events.onTimeZoneChanged(TimeZone.getDefault())
        smallClock.events.onTimeTick()
        largeClock.events.onTimeTick()
    }

    open inner class DefaultClockFaceController(
        override val view: AnimatableClockView,
        var seedColor: Int?,
        messageBuffer: MessageBuffer?,
    ) : ClockFaceController {

        // MAGENTA is a placeholder, and will be assigned correctly in initialize
        private var currentColor = Color.MAGENTA
        private var isRegionDark = false
        protected var targetRegion: Rect? = null

        override val config = ClockFaceConfig()
        override val layout =
            DefaultClockFaceLayout(view).apply {
                views[0].id =
                    sysuiResources.getIdentifier("lockscreen_clock_view", "id", sysuiCtx.packageName)
            }

        override var animations: DefaultClockAnimations = DefaultClockAnimations(view, 0f, 0f)
            internal set

        init {
            if (seedColor != null) {
                currentColor = seedColor!!
            }
            view.setColors(DOZE_COLOR, currentColor)
            messageBuffer?.let { view.messageBuffer = it }
        }

        override val events =
            object : ClockFaceEvents {
                override fun onTimeTick() = view.refreshTime()

                override fun onRegionDarknessChanged(isRegionDark: Boolean) {
                    this@DefaultClockFaceController.isRegionDark = isRegionDark
                    updateColor()
                }

                override fun onTargetRegionChanged(targetRegion: Rect?) {
                    this@DefaultClockFaceController.targetRegion = targetRegion
                    recomputePadding(targetRegion)
                }

                override fun onFontSettingChanged(fontSizePx: Float) {
                    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizePx)
                    recomputePadding(targetRegion)
                }

                override fun onSecondaryDisplayChanged(onSecondaryDisplay: Boolean) {
                    this@LMOClockController.onSecondaryDisplay = onSecondaryDisplay
                    recomputePadding(null)
                }
            }

        open fun recomputePadding(targetRegion: Rect?) {}

        fun updateColor() {
            val color =
                if (seedColor != null) {
                    seedColor!!
                } else if (isRegionDark) {
                    sysuiResources.getColor(android.R.color.system_accent1_100)
                } else {
                    sysuiResources.getColor(android.R.color.system_accent2_600)
                }

            if (currentColor == color) {
                return
            }

            currentColor = color
            view.setColors(DOZE_COLOR, color)
            if (!animations.dozeState.isActive) {
                view.animateColorChange()
            }
        }
    }

    inner class LargeClockFaceController(
        view: AnimatableClockView,
        seedColor: Int?,
        messageBuffer: MessageBuffer?,
    ) : DefaultClockFaceController(view, seedColor, messageBuffer) {
        override val layout =
            DefaultClockFaceLayout(view).apply {
                views[0].id =
                    sysuiResources.getIdentifier("lockscreen_clock_view_large", "id", sysuiCtx.packageName)
            }
        override val config =
            ClockFaceConfig(hasCustomPositionUpdatedAnimation = hasStepClockAnimation)

        init {
            view.migratedClocks = migratedClocks
            view.hasCustomPositionUpdatedAnimation = hasStepClockAnimation
            animations = LargeClockAnimations(view, 0f, 0f)
        }

        override fun recomputePadding(targetRegion: Rect?) {
            if (migratedClocks) {
                return
            }
            if (view.layoutParams !is FrameLayout.LayoutParams) {
                return
            }
            // We center the view within the targetRegion instead of within the parent
            // view by computing the difference and adding that to the padding.
            val lp = view.getLayoutParams() as FrameLayout.LayoutParams
            lp.topMargin =
                if (onSecondaryDisplay) {
                    // On the secondary display we don't want any additional top/bottom margin.
                    0
                } else {
                    val parent = view.parent
                    val yDiff = 0f // for now
//                        if (targetRegion != null && parent is View && parent.isLaidOut())
//                            targetRegion.centerY() - parent.height / 2f
//                        else 0f
                    (-0.5f * view.bottom + yDiff).toInt()
                }
            view.setLayoutParams(lp)
        }

        /** See documentation at [AnimatableClockView.offsetGlyphsForStepClockAnimation]. */
        fun offsetGlyphsForStepClockAnimation(fromLeft: Int, direction: Int, fraction: Float) {
            view.offsetGlyphsForStepClockAnimation(fromLeft, direction, fraction)
        }
    }

    inner class DefaultClockEvents() : ClockEvents {
        override fun onTimeFormatChanged(is24Hr: Boolean) =
            clocks.forEach { it.refreshFormat(is24Hr) }

        override fun onTimeZoneChanged(timeZone: TimeZone) =
            clocks.forEach { it.onTimeZoneChanged(timeZone) }

        override fun onColorPaletteChanged(resources: Resources) {
            largeClock.updateColor()
            smallClock.updateColor()
        }

        override fun onSeedColorChanged(seedColor: Int?) {
            largeClock.seedColor = seedColor
            smallClock.seedColor = seedColor

            largeClock.updateColor()
            smallClock.updateColor()
        }

        override fun onLocaleChanged(locale: Locale) {
            val nf = NumberFormat.getInstance(locale)
            if (nf.format(FORMAT_NUMBER.toLong()) == burmeseNumerals) {
                clocks.forEach { it.setLineSpacingScale(burmeseLineSpacing) }
            } else {
                clocks.forEach { it.setLineSpacingScale(defaultLineSpacing) }
            }

            clocks.forEach { it.refreshFormat() }
        }

        override fun onWeatherDataChanged(data: WeatherData) {}

        override var isReactiveTouchInteractionEnabled: Boolean = false

        override fun onAlarmDataChanged(data: AlarmData) {}

        override fun onZenDataChanged(data: ZenData) {}

        override fun onReactiveAxesChanged(axes: List<ClockReactiveSetting>) {}
    }

    open inner class DefaultClockAnimations(
        val view: AnimatableClockView,
        dozeFraction: Float,
        foldFraction: Float,
    ) : ClockAnimations {
        internal val dozeState = AnimationState(dozeFraction)
        private val foldState = AnimationState(foldFraction)

        init {
            if (foldState.isActive) {
                view.animateFoldAppear(false)
            } else {
                view.animateDoze(dozeState.isActive, false)
            }
        }

        override fun enter() {
            if (!dozeState.isActive) {
                view.animateAppearOnLockscreen()
            }
        }

        override fun charge() = view.animateCharge { dozeState.isActive }

        override fun fold(fraction: Float) {
            val (hasChanged, hasJumped) = foldState.update(fraction)
            if (hasChanged) {
                view.animateFoldAppear(!hasJumped)
            }
        }

        override fun doze(fraction: Float) {
            val (hasChanged, hasJumped) = dozeState.update(fraction)
            if (hasChanged) {
                view.animateDoze(dozeState.isActive, !hasJumped)
            }
        }

        override fun onPickerCarouselSwiping(swipingFraction: Float) {
            // TODO(b/278936436): refactor this part when we change recomputePadding
            // when on the side, swipingFraction = 0, translationY should offset
            // the top margin change in recomputePadding to make clock be centered
            view.translationY = 0.5f * view.bottom * (1 - swipingFraction)
        }

        override fun onPositionUpdated(distance: Float, fraction: Float) {}

        override fun onPositionUpdated(fromLeft: Int, direction: Int, fraction: Float) {}
    }

    inner class LargeClockAnimations(
        view: AnimatableClockView,
        dozeFraction: Float,
        foldFraction: Float,
    ) : DefaultClockAnimations(view, dozeFraction, foldFraction) {
        override fun onPositionUpdated(fromLeft: Int, direction: Int, fraction: Float) {
            largeClock.offsetGlyphsForStepClockAnimation(fromLeft, direction, fraction)
        }
    }

    class AnimationState(
        var fraction: Float,
    ) {
        var isActive: Boolean = fraction > 0.5f
        fun update(newFraction: Float): Pair<Boolean, Boolean> {
            if (newFraction == fraction) {
                return Pair(isActive, false)
            }
            val wasActive = isActive
            val hasJumped =
                (fraction == 0f && newFraction == 1f) || (fraction == 1f && newFraction == 0f)
            isActive = newFraction > fraction
            fraction = newFraction
            return Pair(wasActive != isActive, hasJumped)
        }
    }

    override fun dump(pw: PrintWriter) {
        pw.print("smallClock=")
        smallClock.view.dump(pw)

        pw.print("largeClock=")
        largeClock.view.dump(pw)
    }

    private fun getClockName(): String {
        return when(clockId) {
            ALBERT_SANS_CLOCK_ID -> ctx.getString(R.string.clock_albert_sans_name)
            BLAKA_CLOCK_ID -> ctx.getString(R.string.clock_blaka_name)
            CREEPSTER_CLOCK_ID -> ctx.getString(R.string.clock_creepster_name)
            KABLAMMO_CLOCK_ID -> ctx.getString(R.string.clock_kablammo_name)
            MODAK_CLOCK_ID -> ctx.getString(R.string.clock_modak_name)
            MYSTERY_QUEST_CLOCK_ID -> ctx.getString(R.string.clock_mystery_quest_name)
            RUBIK_DIRT_CLOCK_ID -> ctx.getString(R.string.clock_rubik_dirt_name)
            RUBIK_DISTRESSED_CLOCK_ID -> ctx.getString(R.string.clock_rubik_distressed_name)
            RUBIK_GEMSTONES_CLOCK_ID -> ctx.getString(R.string.clock_rubik_gemstones_name)
            RUBIK_MARKER_HATCH_CLOCK_ID -> ctx.getString(R.string.clock_rubik_market_hatch_name)
            else -> "" // Won't happen
        }
    }

    private fun getClockDescription(): String {
        return when(clockId) {
            ALBERT_SANS_CLOCK_ID -> ctx.getString(R.string.clock_albert_sans_description)
            BLAKA_CLOCK_ID -> ctx.getString(R.string.clock_blaka_description)
            CREEPSTER_CLOCK_ID -> ctx.getString(R.string.clock_creepster_description)
            KABLAMMO_CLOCK_ID -> ctx.getString(R.string.clock_kablammo_description)
            MODAK_CLOCK_ID -> ctx.getString(R.string.clock_modak_description)
            MYSTERY_QUEST_CLOCK_ID -> ctx.getString(R.string.clock_mystery_quest_description)
            RUBIK_DIRT_CLOCK_ID -> ctx.getString(R.string.clock_rubik_dirt_description)
            RUBIK_DISTRESSED_CLOCK_ID -> ctx.getString(R.string.clock_rubik_distressed_description)
            RUBIK_GEMSTONES_CLOCK_ID -> ctx.getString(R.string.clock_rubik_gemstones_description)
            RUBIK_MARKER_HATCH_CLOCK_ID -> ctx.getString(R.string.clock_rubik_market_hatch_description)
            else -> "" // Won't happen
        }
    }

    companion object {
        @VisibleForTesting const val DOZE_COLOR = Color.WHITE
        private const val FORMAT_NUMBER = 1234567890
    }
}
