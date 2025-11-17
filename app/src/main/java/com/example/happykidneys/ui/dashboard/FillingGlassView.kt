package com.example.happykidneys.ui.dashboard

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.example.happykidneys.R
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs
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
        color = Color.parseColor("#BDBDBD")
        style = Paint.Style.STROKE
        strokeWidth = 12f
    }

    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        alpha = 150
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

    private val waterFadePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.primary_blue)
        style = Paint.Style.STROKE
        strokeWidth = 20f
        alpha = 90
        maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.NORMAL)
    }

    private val dropletPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = try {
            ContextCompat.getColor(context, R.color.primary_blue)
        } catch (e: Exception) {
            Color.parseColor("#3EAFDE")
        }
        style = Paint.Style.FILL
    }

    // --- PATHS ---
    private val waterPath = Path()
    private val glassPath = Path()
    private val waterFadePath = Path()

    // --- GEOMETRY ---
    private val topWidthRatio = 0.55f
    private val bottomWidthRatio = 0.35f
    private val glassHeightRatio = 0.89f

    private var glassSlopeL: Float = 0f
    private var glassInterceptL: Float = 0f
    private var glassSlopeR: Float = 0f
    private var glassInterceptR: Float = 0f
    private var glassTopMargin: Float = 0f
    private var glassTopRightX: Float = 0f

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
            updateDroplets()
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

    // --- PARTICLES ---
    private data class Droplet(
        var x: Float,
        var y: Float,
        var speedY: Float,
        var speedX: Float,
        val radius: Float
    )
    private val droplets = CopyOnWriteArrayList<Droplet>()

    // --- PHYSICS PROPERTIES ---
    private var phoneRoll: Float = 0f
    private var smoothedRoll: Float = 0f
    private val gravity = 0.2f
    private var lastSpillTime: Long = 0

    init {
        waveAnimator.start()

        // Allow drawing outside bounds for particles
        clipToOutline = false
        setLayerType(LAYER_TYPE_SOFTWARE, null) // Disable hardware acceleration clipping

        // Request parent to not clip this view
        post {
            (parent as? ViewGroup)?.clipChildren = false
            (parent as? ViewGroup)?.clipToPadding = false
        }
    }

    fun setPhoneRotation(roll: Float) {
        this.phoneRoll = roll.coerceIn(-0.6f, 0.6f)
        val absoluteRoll = abs(roll)
        waveAmplitude = if (absoluteRoll > 0.6f) 20f else 10f
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

        glassTopMargin = (viewWidth - topWidth) / 2
        val rightMargin = glassTopMargin + topWidth
        glassTopRightX = rightMargin
        val bottomMargin = (viewWidth - bottomWidth) / 2
        val bottomY = viewHeight

        // Define the glass outline path
        glassPath.reset()
        glassPath.moveTo(glassTopMargin, 0f)
        glassPath.lineTo(bottomMargin, bottomY)
        glassPath.lineTo(viewWidth - bottomMargin, bottomY)
        glassPath.lineTo(rightMargin, 0f)

        // Calculate glass wall equations
        glassSlopeL = (bottomY - 0f) / (bottomMargin - glassTopMargin)
        glassInterceptL = 0f - (glassSlopeL * glassTopMargin)

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

            val mWater = tan(smoothedRoll)
            val waterSurfaceY = (mWater * (centerX - bubble.x)) + fillY

            if (bubble.y < waterSurfaceY - 20 || bubble.x < bottomMargin || bubble.x > width - bottomMargin) {
                bubble.x = (bottomMargin..width - bottomMargin).random()
                bubble.y = bottomY + (1..50).random()
            }
        }
    }

    private fun updateDroplets() {
        droplets.forEach {
            it.y += it.speedY
            it.x += it.speedX
            it.speedY += gravity

            if (it.y > height + 20) {
                droplets.remove(it)
            }
        }
    }

    private fun spawnDroplet(x: Float, y: Float) {
        if (droplets.size > 50) return

        val now = System.currentTimeMillis()
        if (now - lastSpillTime < 30) return
        lastSpillTime = now

        val centerX = width / 2f
        val isLeftSpill = x < centerX

        val spillDirection = if (isLeftSpill) -1f else 1f

        // Create multiple particles for spray effect
        for (i in 0..2) {
            droplets.add(Droplet(
                x = x + (-5f..5f).random(),
                y = y,
                speedY = (0.5f..2.0f).random(),
                speedX = spillDirection * abs(smoothedRoll) * (3f..5f).random(),
                radius = (3f..7f).random()
            ))
        }
    }

    private fun getWaveY(x: Float): Float {
        val wavePhase = (waveOffset * 2 * Math.PI).toFloat()
        val waveLength = width / 8f
        return sin((x / waveLength) + wavePhase) * waveAmplitude
    }

    private fun getTiltedY(x: Float, centerWaterY: Float): Float {
        val y = tan(smoothedRoll) * (width / 2f - x) + centerWaterY
        return y + getWaveY(x)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isInitialized) createBubbles()
        smoothedRoll += (phoneRoll - smoothedRoll) * 0.1f

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val topWidth = viewWidth * topWidthRatio
        val bottomWidth = viewWidth * bottomWidthRatio
        val glassHeight = viewHeight * glassHeightRatio

        val topMargin = glassTopMargin
        val bottomMargin = (viewWidth - bottomWidth) / 2
        val bottomY = viewHeight
        val centerX = viewWidth / 2f

        val fillY = viewHeight - (glassHeight * currentFillPercentage)

        // --- 1. SET WATER GRADIENT ---
        val darkBlue = try {
            ContextCompat.getColor(context, R.color.palette_process_cyan)
        } catch (e: Exception) {
            Color.parseColor("#1A6A9C")
        }
        val lightBlue = try {
            ContextCompat.getColor(context, R.color.primary_blue)
        } catch (e: Exception) {
            Color.parseColor("#3EAFDE")
        }
        waterPaint.shader = LinearGradient(
            0f, fillY, 0f, bottomY,
            lightBlue, darkBlue,
            Shader.TileMode.CLAMP
        )

        // --- 2. BUILD WATER PATH ---
        waterPath.reset()
        val mWater = tan(smoothedRoll)
        val mWaterInv = -mWater
        val cWaterInv = (mWater * centerX) + fillY

        val waterLeftX = (glassInterceptL - cWaterInv) / (mWaterInv - glassSlopeL)
        val waterLeftY = (glassSlopeL * waterLeftX) + glassInterceptL + getWaveY(waterLeftX)

        val waterRightX = (glassInterceptR - cWaterInv) / (mWaterInv - glassSlopeR)
        val waterRightY = (glassSlopeR * waterRightX) + glassInterceptR + getWaveY(waterRightX)

        waterPath.moveTo(waterLeftX, waterLeftY)
        val segments = 20
        for (i in 1..segments) {
            val progress = i / segments.toFloat()
            val x = waterLeftX + (waterRightX - waterLeftX) * progress
            val y = getTiltedY(x, fillY)
            waterPath.lineTo(x, y)
        }
        waterPath.lineTo(viewWidth + 500f, viewHeight + 500f)
        waterPath.lineTo(-500f, viewHeight + 500f)
        waterPath.close()

        // --- 3. BUILD WATER FADE PATH ---
        waterFadePath.reset()
        waterFadePath.moveTo(waterLeftX, waterLeftY)
        for (i in 1..segments) {
            val progress = i / segments.toFloat()
            val x = waterLeftX + (waterRightX - waterLeftX) * progress
            val y = getTiltedY(x, fillY)
            waterFadePath.lineTo(x, y)
        }

        // --- 4. DRAW WATER & BUBBLES (CLIPPED TO GLASS) ---
        canvas.save()
        canvas.clipPath(glassPath)

        // Layer 1: Water
        canvas.drawPath(waterPath, waterPaint)

        // Layer 2: Bubbles (clipped to water)
        canvas.save()
        canvas.clipPath(waterPath)
        bubbles.forEach {
            canvas.drawCircle(it.x, it.y, it.radius, bubblePaint)
        }
        canvas.restore()

        // Layer 3: Water fade
        canvas.drawPath(waterFadePath, waterFadePaint)

        // Layer 4: Highlights
        val highlightProgress = 0.15f
        val highlightTopX = topMargin + (topWidth * highlightProgress)
        val highlightBottomX = bottomMargin + (bottomWidth * highlightProgress)
        canvas.drawLine(highlightTopX, 10f, highlightBottomX, bottomY - 10f, highlightPaint)

        val strongHighlightTopX = topMargin + (topWidth * 0.20f)
        val strongHighlightBottomX = bottomMargin + (bottomWidth * 0.20f)
        canvas.drawLine(strongHighlightTopX, 20f, strongHighlightBottomX, bottomY - 20f, highlightStrongPaint)

        canvas.restore() // RESTORE BEFORE DRAWING PARTICLES

        // --- 5. DRAW GLASS OUTLINE (NO CLIPPING) ---
        canvas.drawPath(glassPath, glassPaint)

        // --- 6. SPAWN & DRAW PARTICLES (NO CLIPPING) ---
        val glassTopLeftX = topMargin
        val glassTopRightX = topMargin + topWidth
        val glassTopY = 0f

        // Check left spill
        if (waterLeftY < glassTopY) {
            if (waterLeftX >= glassTopLeftX - 10 && waterLeftX <= glassTopLeftX + 10) {
                spawnDroplet(glassTopLeftX, glassTopY)
            }
        }

        // Check right spill
        if (waterRightY < glassTopY) {
            if (waterRightX >= glassTopRightX - 10 && waterRightX <= glassTopRightX + 10) {
                spawnDroplet(glassTopRightX, glassTopY)
            }
        }

        // Draw all droplets (completely unclipped)
        droplets.forEach {
            canvas.drawCircle(it.x, it.y, it.radius, dropletPaint)
        }
    }

    // --- Helper Functions ---
    private fun ClosedFloatingPointRange<Float>.random(): Float {
        return Random.nextFloat() * (endInclusive - start) + start
    }

    private fun ClosedRange<Int>.random(): Float {
        return (Random.nextInt(endInclusive - start) + start).toFloat()
    }
}