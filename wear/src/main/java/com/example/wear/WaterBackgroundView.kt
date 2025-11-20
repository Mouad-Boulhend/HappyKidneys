package com.example.wear

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.random.Random

class WaterBackgroundView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // --- PAINTS ---
    private val waterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        alpha = 100 // Slightly transparent bubbles
    }

    // --- ANIMATION ---
    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1000
        interpolator = LinearInterpolator()
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            updateBubbles()
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

    init {
        animator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Hardcoded colors to ensure it works without resource dependency issues
        val darkBlue = Color.parseColor("#1A6A9C")
        val lightBlue = Color.parseColor("#3EAFDE")

        // Create a full-screen vertical gradient
        waterPaint.shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            lightBlue, darkBlue,
            Shader.TileMode.CLAMP
        )
    }

    private fun createBubbles() {
        if (width == 0 || isInitialized) return

        // Create 20 random bubbles
        for (i in 0..20) {
            bubbles.add(Bubble(
                x = (0..width).random().toFloat(),
                y = (0..height).random().toFloat(),
                radius = (4f..12f).random(),
                speed = (0.5f..2.0f).random(),
                xDrift = (-0.2f..0.2f).random()
            ))
        }
        isInitialized = true
    }

    private fun updateBubbles() {
        if (!isInitialized) return

        bubbles.forEach { bubble ->
            bubble.y -= bubble.speed // Move up
            bubble.x += bubble.xDrift // Drift sideways

            // Reset if it goes off the top
            if (bubble.y < -50f) {
                bubble.y = height.toFloat() + 50f
                bubble.x = (0..width).random().toFloat()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isInitialized) createBubbles()

        // 1. Draw the Water Background (Full Screen)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), waterPaint)

        // 2. Draw the Bubbles
        bubbles.forEach {
            canvas.drawCircle(it.x, it.y, it.radius, bubblePaint)
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