package com.ks.langapp.ui.stats.bar_chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import com.ks.langapp.data.database.entities.DeckStats

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var stats: List<DeckStats>? = null


    private val solidPaint =
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL
        }

    private val textPaint =
        Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.FILL
            textSize = 40F
        }

    val rect = Rect(100, 30, 130, 500)
    val rect2 = Rect(200, 30, 230, 500)
    val rect3 = Rect(400, 400, 460, 460)
    val rect4 = Rect(400, 480, 460, 540)
    val rect5 = Rect(480, 400, 540, 460)
    val rect6 = Rect(560, 400, 620, 460)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) Toast.makeText(context, "no canvas...", Toast.LENGTH_SHORT).show()
        else {
            //setTextSizeForWidth(textPaint, 30F, "du")
            canvas.drawRect(rect, solidPaint)
            canvas.drawText("du", 100F, 20F, textPaint)

            //setTextSizeForWidth(textPaint, 30F, "100")
            canvas.drawRect(rect2, solidPaint)
            canvas.drawText("100", 200F, 20F, textPaint)
            drawHorizontallyCenteredText(canvas, textPaint, "200", 510F, 215F)

            canvas.drawRect(rect3, solidPaint)
            drawTextInsideARect(canvas, textPaint, "26", rect3)

            canvas.drawRect(rect4, solidPaint)
            drawTextInsideARect(canvas, textPaint, "7", rect4)

            canvas.drawRect(rect5, solidPaint)
            drawTextInsideARect(canvas, textPaint, "225", rect5)

            canvas.drawRect(rect6, solidPaint)
            drawTextInsideARect(canvas, textPaint, "4625", rect6)

        }
    }

    private fun drawTextInsideARect(canvas: Canvas, paint: Paint, text: String, rect: Rect) {
        // check if will fit
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        if (bounds.width() > rect.width() || bounds.height() > rect.height()) {
            Log.d("LANGUS", "TEXT TOO LARGE")
            setTextSizeForWidth(paint, (rect.width() - 10).toFloat(), text)
            paint.getTextBounds(text, 0, text.length, bounds)
        }


        // CENTER
        val textX = (rect.exactCenterX()) - bounds.exactCenterX()
        val textY = (rect.exactCenterY()) - bounds.exactCenterY()

        canvas.drawText(text, textX, textY, paint)

    }

    private fun drawHorizontallyCenteredText(canvas: Canvas, paint: Paint, text: String, y: Float, centerX: Float) {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val boundsCenterX = bounds.exactCenterX()
        val textX = centerX - boundsCenterX

        canvas.drawText(text, textX, y, paint)
    }

    /**
     * Sets the text size for a Paint object so a given string of text will be a
     * given width.
     *
     * @param paint
     * the Paint to set the text size for
     * @param desiredWidth
     * the desired width
     * @param text
     * the text that should be that width
     */
    private fun setTextSizeForWidth(
        paint: Paint, desiredWidth: Float,
        text: String
    ) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        val testTextSize = 48f

        // Get the bounds of the text, using our testTextSize.
        paint.textSize = testTextSize
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        // Calculate the desired size as a proportion of our testTextSize.
        val desiredTextSize = testTextSize * desiredWidth / bounds.width()

        // Set the paint for that size.
        paint.textSize = desiredTextSize
    }
}