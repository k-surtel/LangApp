package com.ks.simpledatechart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import org.apache.commons.lang3.time.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class BarChart @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : View(context, attrs) {

    private var data = emptyList<DataEntry>()

    private val rect = Rect()
    private val bounds = Rect()
    private val calendar = Calendar.getInstance()
    private val emptyDataEntry = DataEntry(calendar.time, 0.0)
    private val dayDateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val weekDateFormat = SimpleDateFormat("ww/yyyy")
    private val monthDateFormat = SimpleDateFormat("MM/yyyy")

    val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.rgb(155, 155, 155)
        style = Paint.Style.FILL
        textSize = 35F
    }

    val barPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#b87284")
        style = Paint.Style.FILL
    }

    var barMargin = 35
    var barWidth = 50
    private var barBottom = 0
    private val barTop = 50

    private var stackBy = StackBy.DAY
    private var displayFullDate = false // todo
    var showBlankDates = true
    var sortDataChronologically = false

    private var month: Int? = null
    private var year: Int? = null


    fun setData(listOfEntries: List<DataEntry>) {
        data = listOfEntries
        if (sortDataChronologically) sortDataChronologically()
        minimumWidth = 0
    }

    private fun sortDataChronologically() {
        data = data.sortedBy { it.date }
    }

    fun stackBy(value: StackBy) {
        stackBy = value
        minimumWidth = 0
    }

    override fun onDraw(canvas: Canvas) {
        barBottom = if (displayFullDate) height - 190 else height - 120
        if (data.isEmpty()) return

        if (stackBy == StackBy.NONE) {
            val maxValue = data.maxOf { it.value }
            for ((i, dataEntry) in data.withIndex())
                drawBar(canvas, i, maxValue, dataEntry.value, dataEntry.date)
        } else {
            val stacked = when (stackBy) {
                StackBy.DAY -> data.groupBy { dayDateFormat.format(it.date) }
                StackBy.WEEK -> data.groupBy { weekDateFormat.format(it.date) }
                StackBy.MONTH -> data.groupBy { monthDateFormat.format(it.date) }
                else -> null
            }

            stacked?.let {
                val maxStacked = (stacked.maxOf { it.value.size } - 1) * 0.01
                val maxValue = stacked.maxOf { stack -> stack.value.sumOf { it.value } } + maxStacked
                var i = 0
                if (showBlankDates) {
                    calendar.time = data.first().date
                    for (stack in stacked) {
                        while (!isSameDateRange(calendar.time, stack.value.first().date) && calendar.time.before(stack.value.first().date)) {
                            emptyDataEntry.date = calendar.time
                            drawBar(canvas, i, maxValue, listOf(emptyDataEntry))
                            incrementDate(calendar)
                            i++
                        }
                        drawBar(canvas, i, maxValue, stack.value)
                        incrementDate(calendar)
                        i++
                    }
                } else {
                    for (stack in stacked) {
                        drawBar(canvas, i, maxValue, stack.value)
                        i++
                    }
                }
            }
        }

        month = null
        year = null
    }

    private fun isSameDateRange(date1: Date, date2: Date): Boolean {
        return when (stackBy) {
            StackBy.DAY -> DateUtils.isSameDay(date1, date2)
            StackBy.WEEK -> weekDateFormat.format(date1).equals(weekDateFormat.format(date2))
            StackBy.MONTH -> monthDateFormat.format(date1).equals(monthDateFormat.format(date2))
            else -> false
        }
    }

    private fun incrementDate(cal: Calendar) {
        if (stackBy == StackBy.DAY) cal.add(Calendar.DAY_OF_MONTH, 1)
        if (stackBy == StackBy.WEEK) cal.add(Calendar.WEEK_OF_YEAR, 1)
        if (stackBy == StackBy.MONTH) cal.add(Calendar.MONTH, 1)
    }

    private fun drawBar(canvas: Canvas, position: Int, maxValue: Double, value: Double, date: Date) {
        rect.left = ((position + 1) * barMargin) + (position * barWidth)
        rect.right = rect.left + barWidth
        rect.bottom = barBottom
        rect.top = (barBottom - (barBottom - barTop) * value / maxValue).toInt()
        canvas.drawRect(rect, barPaint)
        drawBarText(canvas, value.toString(), date, rect.exactCenterX(), rect.top.toFloat())
    }

    private fun drawBar(canvas: Canvas, position: Int, maxValue: Double, values: List<DataEntry>) {
        var bottom = barBottom
        for (value in values) {
            rect.left = ((position + 1) * barMargin) + (position * barWidth)
            rect.right = rect.left + barWidth
            rect.bottom = bottom
            rect.top = (bottom - (barBottom - barTop) * value.value / maxValue).toInt()
            canvas.drawRect(rect, barPaint)
            bottom = rect.top - 1
        }
        drawBarText(canvas, values.sumOf { it.value }.toString(), values.first().date, rect.exactCenterX(), rect.top.toFloat())
    }

    private fun drawBarText(canvas: Canvas, topText: String, date: Date, topX: Float, topY: Float) {
        drawText(canvas, topText, topX, topY, true)
        calendar.time = date
        drawTimeline(canvas, calendar)
    }

    private fun drawTimeline(canvas: Canvas, calendar: Calendar) {
        if (!displayFullDate) {
            when (stackBy) {
                StackBy.NONE, StackBy.DAY ->
                    drawText(canvas, calendar.get(Calendar.DAY_OF_MONTH).toString(),
                        rect.exactCenterX(), barBottom.toFloat() + 35, false)

                StackBy.WEEK ->
                    drawText(canvas, calendar.get(Calendar.WEEK_OF_YEAR).toString(),
                        rect.exactCenterX(), barBottom.toFloat() + 35, false)

                StackBy.MONTH ->
                    drawText(canvas, resources.getTextArray(R.array.months_shortened)[calendar.get(Calendar.MONTH)].toString(),
                        rect.exactCenterX(), barBottom.toFloat() + 35, false)

            }

            if ((month != calendar.get(Calendar.MONTH) || year != calendar.get(Calendar.YEAR)) && stackBy != StackBy.MONTH) {
                month = calendar.get(Calendar.MONTH)
                val string = resources.getTextArray(R.array.months_shortened)[month!!].toString()
                drawText(canvas, string, rect.exactCenterX(), barBottom.toFloat() + 76, false)
            }

            if (year != calendar.get(Calendar.YEAR) && stackBy == StackBy.MONTH) {
                year = calendar.get(Calendar.YEAR)
                val string = "$year"
                drawText(canvas, string, rect.exactCenterX(), barBottom.toFloat() + 76, false)
            }

            if (year != calendar.get(Calendar.YEAR) && stackBy != StackBy.MONTH) {
                year = calendar.get(Calendar.YEAR)
                val string = "$year"
                drawText(canvas, string, rect.exactCenterX(), barBottom.toFloat() + 109, false)
            }
        } else {
//            when (stackBy) {
//                StackBy.NONE, StackBy.DAY -> {
//                    val text = calendar.get(Calendar.DAY_OF_MONTH).toString()
//                    val text2 = "23.05.2023"
//                    textPaint.getTextBounds(text2, 0, text2.length, bounds)
//                    val boundsCenterX = bounds.exactCenterX()
//                    val textX = rect.exactCenterX() - bounds.exactCenterY()
//
//
//                    val xx = textX
//                    val yy = barBottom.toFloat() + bounds.right
//
//                    canvas.save()
//                    canvas.rotate(-70F, xx, yy)
//                    canvas.drawText(text2, xx, yy, textPaint)
//                    canvas.restore()
//                }
//
//                StackBy.WEEK -> {
//                    drawBottomBarText(
//                        canvas,
//                        textPaint,
//                        calendar.get(Calendar.WEEK_OF_YEAR).toString(),
//                        barBottom.toFloat() + 35,
//                        rect.exactCenterX()
//                    )
//                }
//
//                StackBy.MONTH -> {
//                    drawBottomBarText(
//                        canvas,
//                        textPaint,
//                        months[calendar.get(Calendar.MONTH)],
//                        barBottom.toFloat() + 35,
//                        rect.exactCenterX()
//                    )
//                }
//            }
        }
    }

    private fun drawText(canvas: Canvas, text: String, centerX: Float, y: Float, isTopValue: Boolean) {
        val printText = if (isTopValue && text.endsWith(".0")) text.dropLast(2) else text
        textPaint.getTextBounds(printText, 0, printText.length, bounds)
        val boundsCenterX = bounds.exactCenterX()
        val textX = centerX - boundsCenterX
        val textY = if (isTopValue) y - 10 else y
        canvas.drawText(printText, textX, textY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) =
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))

    private fun measureWidth(measureSpec: Int): Int {
        if (data.isEmpty()) return getMeasurement(measureSpec, 0)

        val barNumber: Int

        if (showBlankDates && stackBy != StackBy.NONE) {
            calendar.time = data.first().date
            var i = 0
            while (!isSameDateRange(calendar.time, data.last().date)) {
                i++
                incrementDate(calendar)
            }
            i++
            barNumber = i
        } else {
            val dateFormat = when (stackBy) {
                StackBy.DAY -> dayDateFormat
                StackBy.WEEK -> weekDateFormat
                StackBy.MONTH -> monthDateFormat
                else -> null
            }

            barNumber = if (stackBy == StackBy.NONE) data.size
            else {
                val stacked = data.groupBy { dateFormat!!.format(it.date) }
                stacked.size
            }
        }

        val width = barNumber * (barMargin + barWidth) + barMargin
        return getMeasurement(measureSpec, width)
    }

    private fun measureHeight(measureSpec: Int): Int =
        getMeasurement(measureSpec, 100)

    private fun getMeasurement(measureSpec: Int, preferred: Int): Int {
        return when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(measureSpec)
            MeasureSpec.AT_MOST -> preferred.coerceAtMost(MeasureSpec.getSize(measureSpec))
            else -> preferred
        }
    }
}