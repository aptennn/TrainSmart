package com.example.trainsmart.ui.workout.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.withClip
import com.example.trainsmart.R
import kotlin.math.min

class WorkoutProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val grayPaint: Paint
    private val accentPaint: Paint

    init {
        val typedAttrs = context.obtainStyledAttributes(attrs, R.styleable.WorkoutProgressBar, defStyleAttr, defStyleRes)

        try {
            val grayColor = (255 shl 24) or typedAttrs.getColor(
                R.styleable.WorkoutProgressBar_color,
                0xC0C0C0
            )
            val accentColor = (255 shl 24) or typedAttrs.getColor(
                R.styleable.WorkoutProgressBar_accentColor,
                context.getColor(R.color.blue_main)
            )
            grayPaint = Paint().apply {
                color = grayColor
                style = Paint.Style.FILL
            }
            accentPaint = Paint().apply {
                color = accentColor
                style = Paint.Style.FILL
            }
        } finally {
            typedAttrs.recycle()
        }
    }

    var setCounts: Array<Int> = arrayOf()
        set(value) {
            for (setCount in value) {
                if (setCount <= 0) throw IllegalArgumentException("non-positive set count")
            }
            field = value
        }
    var currentExercise: Int = 0
    var currentSet: Int = 0
    var partialProgress: Float = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthValue = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightValue = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthValue
            MeasureSpec.AT_MOST -> widthValue
            else -> 250
        }
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightValue
            MeasureSpec.AT_MOST -> min(18 + VERTICAL_PADDING.toInt() * 2, heightValue)
            else -> 18 + VERTICAL_PADDING.toInt() * 2
        }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()

        var spacingsBetweenSets = 0
        var totalSetCount = 0
        for (setCount in setCounts) {
            spacingsBetweenSets += setCount - 1
            totalSetCount += setCount
        }
        val totalNormalSpacing = HORIZONTAL_PADDING * 2f + EXERCISE_SPACING * (setCounts.size - 1f) + SET_SPACING * spacingsBetweenSets
        var totalSpacing = totalNormalSpacing
        var horizontalPadding = HORIZONTAL_PADDING
        var exerciseSpacing = EXERCISE_SPACING
        var setSpacing = SET_SPACING
        if (w - totalNormalSpacing < totalSetCount * MIN_SET_WIDTH) {
            totalSpacing = w - totalSetCount * MIN_SET_WIDTH
            horizontalPadding = totalSpacing * (HORIZONTAL_PADDING * 2f / totalNormalSpacing)
            exerciseSpacing = totalSpacing * (EXERCISE_SPACING * (setCounts.size - 1f) / totalNormalSpacing)
            setSpacing = totalSpacing * (SET_SPACING * spacingsBetweenSets / totalNormalSpacing)
        }
        var setWidth = (w - totalSpacing) / totalSetCount
        var x = horizontalPadding
        val cornerRadius = (h - VERTICAL_PADDING * 2f) * 0.5f
        for (ex in 0..<setCounts.size) {
            val setCount = setCounts[ex]
            for (i in 0..<setCount) {
                val paint = if (ex < currentExercise || (ex == currentExercise && i < currentSet)) accentPaint else grayPaint
                canvas.drawRoundRect(x, VERTICAL_PADDING, x + setWidth, h - VERTICAL_PADDING, cornerRadius, cornerRadius, paint)
                if (ex == currentExercise && i == currentSet && partialProgress != 0f) {
                    canvas.withClip(x, VERTICAL_PADDING, x + (setWidth * partialProgress), h - VERTICAL_PADDING) {
                        this.drawRoundRect(x, VERTICAL_PADDING, x + setWidth, h - VERTICAL_PADDING, cornerRadius, cornerRadius, accentPaint)
                    }
                }
                x += if (i != setCount - 1) {
                    setWidth + setSpacing
                } else {
                    setWidth
                }
            }
            if (ex != setCounts.size - 1) {
                x += exerciseSpacing
            }
        }
    }

    companion object {
        const val SET_SPACING: Float = 10f
        const val EXERCISE_SPACING: Float = 30f
        const val MIN_SET_WIDTH: Float = 20f
        const val HORIZONTAL_PADDING: Float = 15f
        const val VERTICAL_PADDING: Float = 15f
    }
}