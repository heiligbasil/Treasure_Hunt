package com.lambdaschool.cs_build_week_2.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.lambdaschool.cs_build_week_2.models.CellDetails
import com.lambdaschool.cs_build_week_2.models.RoomDetails
import com.lambdaschool.cs_build_week_2.utils.UserInteraction
import com.lambdaschool.cs_build_week_2.views.MainActivity.Companion.roomsGraph
import kotlin.math.abs
import kotlin.math.max

class AdventureMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var cellsGrid: Array<Array<Int>> = Array(1) { Array(1) { -1 } }
    private var calculated = false
    private var shiftXGridBy: Int = 40
    private var shiftYGridBy: Int = 20
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

    fun calculateSize() {
        if (roomsGraph.isEmpty())
            return
        convertCoordinatesToGrid()

        cellHeight = height / max((cellsGrid.size - ((shiftXGridBy + shiftYGridBy) / 2)), 1)
        cellWidth = cellHeight
//        cellChecked = Array(numColumns) { BooleanArray(numRows) }
//        cellColors = Array(numColumns) { IntArray(numRows) }
        calculated = true
        invalidate()
    }


    fun convertCoordinatesToGrid() {
        val cellsList = hashMapOf<Int?, CellDetails>()
        val xs = arrayListOf<Int>()
        val ys = arrayListOf<Int>()
        roomsGraph.forEach {
            cellsList[it.key] = it.value[2] as CellDetails
            xs.add((it.value[2] as CellDetails).gridX)
            ys.add((it.value[2] as CellDetails).gridY)
        }
        val largestXcoord = xs.max() ?: 0
        val largestYcoord = ys.max() ?: 0
        val size = max(largestXcoord, largestYcoord) + 1
        cellsGrid = Array(size) { Array(size) { -1 } }
        cellsList.forEach {
            cellsGrid[it.value.gridY][it.value.gridX] = it.key ?: -1
        }
        cellsGrid.reverse()
    }


    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    override fun onDraw(canvas: Canvas) {
        if (!calculated)
            return
//        val width = width
//        val height = height

        for (x in -shiftXGridBy until cellsGrid.size - shiftXGridBy) {
            for (y in shiftYGridBy until cellsGrid.size + shiftYGridBy) {
                if (cellsGrid[y - shiftYGridBy][x + shiftXGridBy] > -1) {
                    val cellNumber = cellsGrid[y - shiftYGridBy][x + shiftXGridBy]
                    val hexColor = (roomsGraph[cellNumber]?.get(2) as CellDetails).color
                    cellPaint.color = Color.parseColor(hexColor)
                    if (cellNumber == MainActivity.currentRoomId) {
                        cellPaint.color = Color.TRANSPARENT
                    }


//        if (roomsGraph.isNotEmpty() && currentRoomId != -1) {
                    /*roomsGraph.forEach {
                        val roomId: Int? = it.key
                        val cellDetails: CellDetails = roomsGraph[roomId]?.get(2) as CellDetails
                        val x: Int = cellDetails.gridY
                        val y: Int = cellDetails.gridX
                        cellPaint.color = Color.parseColor(cellDetails.color)*/
                    // Filled color
                    canvas.drawRect(
                        x * cellWidth.toFloat(), y * cellHeight.toFloat(),
                        (x + 1) * cellWidth.toFloat(), (y + 1) * cellHeight.toFloat(),
                        cellPaint
                    )
                    // Outlined color
                    canvas.drawRect(
                        x * cellWidth.toFloat(), y * cellHeight.toFloat(),
                        (x + 1) * cellWidth.toFloat(), (y + 1) * cellHeight.toFloat(),
                        cellPaintBorder
                    )
                    // Draw "North" door
                    canvas.drawLine(
                        (x * cellWidth.toFloat()) + 15,
                        y * cellHeight.toFloat(),
                        ((x + 1) * cellWidth.toFloat()) - 15,
                        y * cellHeight.toFloat(),
                        cellPaintDoor
                    )
                    // Draw "South" door
                    canvas.drawLine(
                        (x * cellWidth.toFloat()) + 15,
                        (y + 1) * cellHeight.toFloat(),
                        ((x + 1) * cellWidth.toFloat()) - 15,
                        (y + 1) * cellHeight.toFloat(),
                        cellPaintDoor
                    )
                    // Draw "West" door
                    canvas.drawLine(
                        x * cellWidth.toFloat(),
                        (y * cellHeight.toFloat()) + 15,
                        x * cellWidth.toFloat(),
                        ((y + 1) * cellHeight.toFloat()) - 15,
                        cellPaintDoor
                    )
                    // Draw "East" door
                    canvas.drawLine(
                        (x + 1) * cellWidth.toFloat(),
                        (y * cellHeight.toFloat()) + 15,
                        (x + 1) * cellWidth.toFloat(),
                        ((y + 1) * cellHeight.toFloat()) - 15,
                        cellPaintDoor
                    )
                    // Cell number
                    canvas.drawText(
                        cellNumber.toString(),
                        (x + .05F) * cellWidth.toFloat(),
                        (y + .7F) * cellHeight.toFloat(),
                        cellPaintText
                    )
                }
//        }
//        }
//                }
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
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val column = (event.x / cellWidth).toInt()
            val row = (event.y / cellHeight).toInt()
            val selectedCell: Int = cellsGrid[column - 10][row + 30]
            if (selectedCell > -1) {
                val roomDetails: RoomDetails = (roomsGraph[selectedCell]?.get(0) as RoomDetails)
                UserInteraction.askQuestion(context, "Room #$selectedCell", roomDetails.toString(), "Okay", null)
            }
        }
        return true
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