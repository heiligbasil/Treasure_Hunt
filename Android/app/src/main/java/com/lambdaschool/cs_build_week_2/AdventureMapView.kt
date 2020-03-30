package com.lambdaschool.cs_build_week_2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class AdventureMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var numColumns = 25
    private var numRows = 30
    private var cellWidth = 0
    private var cellHeight = 0
    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        color = Color.GREEN
    }
    private val cellPaintBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5F
        strokeJoin = Paint.Join.ROUND
        color = Color.BLACK
    }
    private val cellPaintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 1.5F
        textSize = 21F
    }
    private val cellPaintDoor = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 5F
    }
    public lateinit var cellChecked: Array<BooleanArray>
    private lateinit var cellColors: Array<IntArray>


    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateSize()
    }

    private fun calculateSize() {
        cellHeight = height / numRows
        cellWidth = cellHeight
        cellChecked = Array(numColumns) { BooleanArray(numRows) }
        cellColors = Array(numColumns) { IntArray(numRows) }
        invalidate()
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    override fun onDraw(canvas: Canvas) {
        val width = width
        val height = height
        for (i in 0 until numColumns) {
            for (j in 0 until numRows) {
                if (cellChecked[i][j]) {
                    if (cellColors[i][j] == 0)
                        cellPaint.color = Color.MAGENTA
                    else
                        cellPaint.color = cellColors[i][j]
                    canvas.drawRect(
                        i * cellWidth.toFloat(), j * cellHeight.toFloat(),
                        (i + 1) * cellWidth.toFloat(), (j + 1) * cellHeight.toFloat(),
                        cellPaint
                    )
                    canvas.drawRect(
                        i * cellWidth.toFloat(), j * cellHeight.toFloat(),
                        (i + 1) * cellWidth.toFloat(), (j + 1) * cellHeight.toFloat(),
                        cellPaintBorder
                    )
                    // Draw "North" door
                    canvas.drawLine(
                        (i * cellWidth.toFloat()) + 15,
                        j * cellHeight.toFloat(),
                        ((i + 1) * cellWidth.toFloat()) - 15,
                        j * cellHeight.toFloat(),
                        cellPaintDoor
                    )
                    // Draw "South" door
                    canvas.drawLine(
                        (i * cellWidth.toFloat()) + 15,
                        (j + 1) * cellHeight.toFloat(),
                        ((i + 1) * cellWidth.toFloat()) - 15,
                        (j + 1) * cellHeight.toFloat(),
                        cellPaintDoor
                    )
                    // Draw "West" door
                    canvas.drawLine(
                        i * cellWidth.toFloat(),
                        (j * cellHeight.toFloat()) + 15,
                        i * cellWidth.toFloat(),
                        ((j + 1) * cellHeight.toFloat()) - 15,
                        cellPaintDoor
                    )
                    // Draw "East" door
                    canvas.drawLine(
                        (i + 1) * cellWidth.toFloat(),
                        (j * cellHeight.toFloat()) + 15,
                        (i + 1) * cellWidth.toFloat(),
                        ((j + 1) * cellHeight.toFloat()) - 15,
                        cellPaintDoor
                    )

                    canvas.drawText("500", (i + .05F) * cellWidth.toFloat(), (j + .7F) * cellHeight.toFloat(), cellPaintText)
                }
            }
        }
    }

    /**
     * Implement this method to handle touch screen motion events.
     *
     *
     * If this method is used to detect click actions, it is recommended that
     * the actions be performed by implementing and calling
     * [.performClick]. This will ensure consistent system behavior,
     * including:
     *
     *  * obeying click sound preferences
     *  * dispatching OnClickListener calls
     *  * handling [ACTION_CLICK][AccessibilityNodeInfo.ACTION_CLICK] when
     * accessibility features are enabled
     *
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    /**
     * Call this view's OnClickListener, if it is defined.  Performs all normal
     * actions associated with clicking: reporting accessibility event, playing
     * a sound, etc.
     *
     * @return True there was an assigned OnClickListener that was called, false
     * otherwise is returned.
     */
    override fun performClick(): Boolean {
        return super.performClick()
    }
}