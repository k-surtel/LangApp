package com.ks.langapp.ui.stats.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class DailyHeatMap @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val MARGIN = 10
    private val SQUARE_SIDE = 60

    private val solidPaint =
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL
        }

    private val solidPaint2 =
        Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.FILL
            textSize = 40F
            textAlign = Paint.Align.CENTER
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            for (i in 0 .. 21) drawWeekSquares(it, i)
        }
    }

    private fun drawWeekSquares(canvas: Canvas, weekNumber: Int) {
        if (weekNumber == 0) drawWeekNames(canvas)
        else {
            val squareLeft = (weekNumber + 1) * MARGIN + weekNumber * SQUARE_SIDE
            for (i in 0 .. 7) {
                drawDaySquare(canvas, i, squareLeft)
            }
        }
    }

    private fun drawWeekNames(canvas: Canvas) {
        for (i in 1 .. 7) {
            val top = (i + 1) * MARGIN + i * SQUARE_SIDE
            val rect = Rect(MARGIN, top, MARGIN + SQUARE_SIDE, top + SQUARE_SIDE)

            val text = when (i) {
                1 -> "M"
                2 -> "T"
                3 -> "W"
                4 -> "T"
                5 -> "F"
                else -> "S"
            }

            val bounds = Rect()
            solidPaint2.getTextBounds(text, 0, text.length, bounds)

            canvas.drawText(text, rect.exactCenterX(), rect.exactCenterY() + (bounds.height() / 2), solidPaint2)
        }
    }

    private fun drawDaySquare(canvas: Canvas, dayOfWeek: Int, squareLeft: Int) {
        if (dayOfWeek == 0) drawMonthName(canvas)
        else {
            val squareTop = (dayOfWeek + 1) * MARGIN + dayOfWeek * SQUARE_SIDE
            val rect = Rect(squareLeft, squareTop, squareLeft + SQUARE_SIDE, squareTop + SQUARE_SIDE)
            canvas.drawRect(rect, solidPaint)
        }
    }

    private fun drawMonthName(canvas: Canvas) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val desiredWidth = 2000
        val desiredHeight = 100

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int

        //Measure Width
        width = if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            Math.min(desiredWidth, widthSize)
        } else {
            //Be whatever you want
            desiredWidth
        }

        //Measure Height
        height = if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            Math.min(desiredHeight, heightSize)
        } else {
            //Be whatever you want
            desiredHeight
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height)
    }
}