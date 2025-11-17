package com.example.happykidneys.ui.dashboard

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter // --- ADD THIS IMPORT ---
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.example.happykidneys.R
import kotlin.math.sin
import kotlin.math.tan
import kotlin.random.Random

class FillingGlassView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentFillPercentage = 0f
    private var targetFillPercentage = 0f

    // --- PAINTS ---
    private val waterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val glassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#BDBDBD") // Grey color for outline
        style = Paint.Style.STROKE
        strokeWidth = 12f
    }

    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        alpha = 150 // Slightly transparent
    }

    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 12f
        alpha = 50
    }
    private val highlightStrongPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 6f
        alpha = 90
    }

    // --- MODIFIED: This is now the FADE effect ---
    private val waterFadePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.primary_blue) // Use light blue
        style = Paint.Style.STROKE
        strokeWidth = 20f // Make it thick
        alpha = 90 // Semi-transparent
        // Add a blur to create the "fade"
        maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.NORMAL)
    }

    // --- PATHS ---
    private val waterPath = Path()
    private val glassPath = Path()
    private val waterFadePath = Path() // Renamed from meniscusPath

    // --- GEOMETRY ---
    private val topWidthRatio = 0.8f
    private val bottomWidthRatio = 0.6f
    private val glassHeightRatio = 0.9f

    private var glassSlopeL: Float = 0f
    private var glassInterceptL: Float = 0f
    private var glassSlopeR: Float = 0f
    private var glassInterceptR: Float = 0f

    // --- ANIMATION ---
    private var waveOffset = 0f
    private var waveAmplitude = 10f

    private val waveAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1500
        interpolator = LinearInterpolator()
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            waveOffset = it.animatedValue as Float
            updateBubbles()
            invalidate()
        }
    }

    private val fillAnimator = ValueAnimator().apply {
        duration = 1000
        addUpdateListener {
            currentFillPercentage = it.animatedValue as Float
            invalidate()
        }
    }

    // --- BUBBLES ---
    private data class Bubble(
        var x: Float,
        var y: Float,
        val radius: Float,
        val speed: Float,
        val xDrift: Float
    )
    private val bubbles = mutableListOf<Bubble>()
    private var isInitialized = false

    // --- PHYSICS PROPERTIES ---
    private var phoneRoll: Float = 0f
    private var smoothedRoll: Float = 0f

    init {
        waveAnimator.start()
    }

    fun setPhoneRotation(roll: Float) {
        // We still coerce the *tilt* to a reasonable value
        this.phoneRoll = roll.coerceIn(-0.6f, 0.6f)

        // --- NEW "SPLASH" LOGIC ---
        // Check the *raw* un-coerced roll
        val absoluteRoll = kotlin.math.abs(roll)

        // If the phone is tilted more than ~35 degrees (0.6 radians)
        if (absoluteRoll > 0.6f) {
            // Make the waves "splash" by doubling their height
            waveAmplitude = 20f
        } else {
            // Otherwise, return to normal calm waves
            waveAmplitude = 10f
        }
        // --- END NEW LOGIC ---
    }

    fun setPercentage(percentage: Int) {
        val newPercent = percentage.coerceIn(0, 100) / 100f
        if (newPercent == targetFillPercentage) return

        targetFillPercentage = newPercent

        fillAnimator.cancel()
        fillAnimator.setFloatValues(currentFillPercentage, targetFillPercentage)
        fillAnimator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val viewWidth = w.toFloat()
        val viewHeight = h.toFloat()
        val topWidth = viewWidth * topWidthRatio
        val bottomWidth = viewWidth * bottomWidthRatio

        val topMargin = (viewWidth - topWidth) / 2
        val rightMargin = topMargin + topWidth
        val bottomMargin = (viewWidth - bottomWidth) / 2
        val bottomY = viewHeight

        // Define the glass outline path
        glassPath.reset()
        glassPath.moveTo(topMargin, 0f) // Top-left
        glassPath.lineTo(bottomMargin, bottomY) // Bottom-left
        glassPath.lineTo(viewWidth - bottomMargin, bottomY) // Bottom-right
        glassPath.lineTo(rightMargin, 0f) // Top-right

        // Calculate glass wall equations
        glassSlopeL = (bottomY - 0f) / (bottomMargin - topMargin)
        glassInterceptL = 0f - (glassSlopeL * topMargin) // c = y - mx

        glassSlopeR = (bottomY - 0f) / ((viewWidth - bottomMargin) - rightMargin)
        glassInterceptR = 0f - (glassSlopeR * rightMargin)
    }

    private fun createBubbles() {
        if (width == 0 || isInitialized) return
        val viewWidth = width.toFloat()
        val bottomWidth = viewWidth * bottomWidthRatio
        val bottomMargin = (viewWidth - bottomWidth) / 2
        val bottomY = height.toFloat()

        for (i in 0..15) {
            bubbles.add(Bubble(
                (bottomMargin..width - bottomMargin).random(),
                (bottomY..bottomY + 100).random(),
                (2f..10f).random(),
                (1.5f..4f).random(),
                (-0.5f..0.5f).random()
            ))
        }
        isInitialized = true
    }

    private fun updateBubbles() {
        if (!isInitialized) return

        val viewWidth = width.toFloat()
        val bottomWidth = viewWidth * bottomWidthRatio
        val bottomMargin = (viewWidth - bottomWidth) / 2
        val bottomY = height.toFloat()
        val glassHeight = height.toFloat() * glassHeightRatio
        val fillY = height.toFloat() - (glassHeight * currentFillPercentage)
        val centerX = width / 2f

        bubbles.forEach { bubble ->
            bubble.y -= bubble.speed
            bubble.x += bubble.xDrift

            // --- ROTATION FIX IS HERE ---
            val mWater = tan(smoothedRoll)
            // Use (centerX - bubble.x) to correctly calculate inverted tilt
            val waterSurfaceY = (mWater * (centerX - bubble.x)) + fillY
            // --- END FIX ---

            if (bubble.y < waterSurfaceY - 20 || bubble.x < bottomMargin || bubble.x > width - bottomMargin) {
                bubble.x = (bottomMargin..width - bottomMargin).random()
                bubble.y = bottomY + (1..50).random()
            }
        }
    }

    private fun getWaveY(x: Float): Float {
        val wavePhase = (waveOffset * 2 * Math.PI).toFloat()
        val waveLength = width / 2f
        return sin((x / waveLength) + wavePhase) * waveAmplitude
    }

    // --- HELPER (ROTATION FIX IS HERE) ---
    private fun getTiltedY(x: Float, centerWaterY: Float): Float {
        // --- ROTATION FIX IS HERE ---
        // Use (centerX - x) to correctly calculate inverted tilt
        val y = tan(smoothedRoll) * (width / 2f - x) + centerWaterY
        // --- END FIX ---
        return y + getWaveY(x)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isInitialized) createBubbles()

        smoothedRoll += (phoneRoll - smoothedRoll) * 0.1f // Smoothing

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val topWidth = viewWidth * topWidthRatio
        val bottomWidth = viewWidth * bottomWidthRatio
        val glassHeight = viewHeight * glassHeightRatio

        val topMargin = (viewWidth - topWidth) / 2
        val bottomMargin = (viewWidth - bottomWidth) / 2
        val bottomY = viewHeight
        val centerX = viewWidth / 2f

        val fillY = viewHeight - (glassHeight * currentFillPercentage)

        // --- 1. SET WATER GRADIENT (NO FADE) ---
        val darkBlue = try {
            ContextCompat.getColor(context, R.color.primary_blue_dark)
        } catch (e: Exception) { Color.parseColor("#1A6A9C") }
        val lightBlue = try {
            ContextCompat.getColor(context, R.color.primary_blue)
        } catch (e: Exception) { Color.parseColor("#3EAFDE") }
        waterPaint.shader = LinearGradient(0f, fillY, 0f, bottomY, lightBlue, darkBlue, Shader.TileMode.CLAMP)

        // --- 2. BUILD WATER PATH (ROTATION FIX IS HERE) ---
        waterPath.reset()

        // A. Find water line slope and intercept
        val mWater = tan(smoothedRoll)
        // c = y - m*x. But our slope is inverted (m * (centerX - x)),
        // so y = -m*x + (m*centerX + fillY)
        val mWaterInv = -mWater
        val cWaterInv = (mWater * centerX) + fillY

        // B. Find intersection of water line and glass walls
        // x = (c2 - c1) / (m1 - m2)
        val waterLeftX = (glassInterceptL - cWaterInv) / (mWaterInv - glassSlopeL)
        val waterLeftY = (glassSlopeL * waterLeftX) + glassInterceptL + getWaveY(waterLeftX)

        val waterRightX = (glassInterceptR - cWaterInv) / (mWaterInv - glassSlopeR)
        val waterRightY = (glassSlopeR * waterRightX) + glassInterceptR + getWaveY(waterRightX)

        // C. Build the path
        waterPath.moveTo(waterLeftX, waterLeftY)
        val segments = 20
        for (i in 1..segments) {
            val progress = i / segments.toFloat()
            val x = waterLeftX + (waterRightX - waterLeftX) * progress
            val y = getTiltedY(x, fillY) // Use our helper
            waterPath.lineTo(x, y)
        }
        waterPath.lineTo(viewWidth + 500f, viewHeight + 500f)
        waterPath.lineTo(-500f, viewHeight + 500f)
        waterPath.close()

        // --- 3. BUILD WATER FADE PATH (Replaces Meniscus) ---
        waterFadePath.reset()
        waterFadePath.moveTo(waterLeftX, waterLeftY)
        for (i in 1..segments) {
            val progress = i / segments.toFloat()
            val x = waterLeftX + (waterRightX - waterLeftX) * progress
            val y = getTiltedY(x, fillY)
            waterFadePath.lineTo(x, y)
        }

        // --- 4. DRAW IN ORDER ---
        canvas.save()
        canvas.clipPath(glassPath) // Clip everything *inside* the glass

        // Layer 1: The Water Gradient
        canvas.drawPath(waterPath, waterPaint)

        // Layer 2: The Bubbles
        canvas.save()
        canvas.clipPath(waterPath) // Clip bubbles *inside* the water
        bubbles.forEach { canvas.drawCircle(it.x, it.y, it.radius, bubblePaint) }
        canvas.restore()

        // Layer 3: The Water Fade (on top of water)
        canvas.drawPath(waterFadePath, waterFadePaint)

        // Layer 4: The Highlights
        val highlightProgress = 0.15f
        val highlightTopX = topMargin + (topWidth * highlightProgress)
        val highlightBottomX = bottomMargin + (bottomWidth * highlightProgress)
        canvas.drawLine(highlightTopX, 10f, highlightBottomX, bottomY - 10f, highlightPaint)

        val strongHighlightTopX = topMargin + (topWidth * 0.20f)
        val strongHighlightBottomX = bottomMargin + (bottomWidth * 0.20f)
        canvas.drawLine(strongHighlightTopX, 20f, strongHighlightBottomX, bottomY - 20f, highlightStrongPaint)

        canvas.restore() // Restore from glass clip

        // Layer 5: The Glass Outline (on top of everything)
        canvas.drawPath(glassPath, glassPaint)
    }

    // --- Helper Functions ---
    private fun ClosedFloatingPointRange<Float>.random(): Float {
        return Random.nextFloat() * (endInclusive - start) + start
    }
    private fun ClosedRange<Int>.random(): Float {
        return (Random.nextInt(endInclusive - start) + start).toFloat()
    }
}