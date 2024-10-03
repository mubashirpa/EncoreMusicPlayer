package com.encore.music.presentation.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

// Custom View with a changeable gradient
class GradientView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : View(context, attrs) {
        private val paint = Paint()
        private var startColor: Int = 0xFFE91E63.toInt() // Default start color (Red)
        private var endColor: Int = 0xFF2196F3.toInt() // Default end color (Blue)

        // Function to change the gradient colors dynamically
        fun setGradientColors(
            startColor: Int,
            endColor: Int,
        ) {
            this.startColor = startColor
            this.endColor = endColor
            invalidate() // Triggers a redraw to update the gradient
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // Create the LinearGradient shader with updated colors
            val linearGradient =
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    height.toFloat(), // Vertical gradient from top to bottom
                    startColor,
                    endColor, // Use dynamic colors
                    Shader.TileMode.CLAMP,
                )

            // Set the shader to the Paint object
            paint.shader = linearGradient

            // Draw a rectangle filling the view
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }
