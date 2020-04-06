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
import com.lambdaschool.cs_build_week_2.views.MainActivity.Companion.darkGraph
import com.lambdaschool.cs_build_week_2.views.MainActivity.Companion.inDarkWorld
import com.lambdaschool.cs_build_week_2.views.MainActivity.Companion.roomsGraph
import java.util.ArrayList
import kotlin.math.max
import kotlin.math.min

class AdventureMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var cellsGrid: Array<Array<Int>> = Array(1) { Array(1) { -1 } }
    private var calculated = false
    private var shiftXGridBy: Int = 47
    private var shiftYGridBy: Int = 3
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
        color = Color.parseColor("#CC0B215A")
        strokeWidth = 1.2F
        textSize = 16F
    }
    private val cellPaintDoor = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.YELLOW
        strokeWidth = 5F
    }


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
        if ((inDarkWorld && darkGraph.isEmpty()) || (!inDarkWorld && roomsGraph.isEmpty())) {
            return
        }
        convertCoordinatesToGrid()
        cellHeight = height / max((cellsGrid.size - ((shiftXGridBy + shiftYGridBy) / 2)), 1) + 12
        cellWidth = cellHeight
        calculated = true
        invalidate()
    }

    private fun convertCoordinatesToGrid() {
        val cellsList: HashMap<Int?, CellDetails> = hashMapOf<Int?, CellDetails>()
        val xs: ArrayList<Int> = arrayListOf<Int>()
        val ys: ArrayList<Int> = arrayListOf<Int>()
        if (inDarkWorld) {
            darkGraph.forEach {
                cellsList[it.key] = it.value[2] as CellDetails
                xs.add((it.value[2] as CellDetails).gridX)
                ys.add((it.value[2] as CellDetails).gridY)
            }
        } else {
            roomsGraph.forEach {
                cellsList[it.key] = it.value[2] as CellDetails
                xs.add((it.value[2] as CellDetails).gridX)
                ys.add((it.value[2] as CellDetails).gridY)
            }
        }
        val largestXcoord: Int = xs.max() ?: 0
        val largestYcoord :Int= ys.max() ?: 0
        val size: Int = max(largestXcoord, largestYcoord) + 1
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
        for (x in -shiftXGridBy until cellsGrid.size - shiftXGridBy) {
            for (y in shiftYGridBy until cellsGrid.size + shiftYGridBy) {
                if (cellsGrid[y - shiftYGridBy][x + shiftXGridBy] > -1) {
                    val cellNumber: Int = cellsGrid[y - shiftYGridBy][x + shiftXGridBy]
                    val exitDirections: HashMap<*, *> = if (inDarkWorld) {
                        darkGraph[cellNumber]?.get(1) as HashMap<*, *>
                    } else {
                        roomsGraph[cellNumber]?.get(1) as HashMap<*, *>
                    }
                    val cellTitle: String? = if (inDarkWorld) {
                        (darkGraph[cellNumber]?.get(0) as RoomDetails).title
                    } else {
                        (roomsGraph[cellNumber]?.get(0) as RoomDetails).title
                    }
                    cellPaint.color = Color.parseColor(
                        when (cellTitle) {
                            "A brightly lit room" -> "#9AFFD600"
                            "A misty room" -> "#65B8D5B6"
                            "A Dark Cave" -> "#7FA10A0A"
                            "Mt. Holloway" -> "#B2276BCF"
                            "Darkness" -> "#B2333B47"
                            "Shop" -> "#CC42A304"
                            "Wishing Well" -> "#CC42A304"
                            "JKMT Donuts" -> "#CC42A304"
                            "Red Egg Pizza Parlor" -> "#CC42A304"
                            "The Transmogriphier" -> "#CC42A304"
                            "The Peak of Mt. Holloway" -> "#4D6200EA"
                            "Pirate Ry's" -> "#4D6200EA"
                            "Arron's Athenaeum" -> "#4D6200EA"
                            "Sandofsky's Sanctum" -> "#4D6200EA"
                            "Glasowyn's Grave" -> "#4D6200EA"
                            "Linh's Shrine" -> "#4D6200EA"
                            "Fully Shrine" -> "#4D6200EA"
                            else -> "#CDFF6D00"
                        }
                    )
                    if (cellNumber == MainActivity.currentRoomId) {
                        cellPaint.color = Color.parseColor("#CCD50000")
                    }
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
                    if (exitDirections.containsKey("n")) {
                        // Draw "North" door
                        canvas.drawLine(
                            (x * cellWidth.toFloat()) + 5,
                            y * cellHeight.toFloat(),
                            ((x + 1) * cellWidth.toFloat()) - 5,
                            y * cellHeight.toFloat(),
                            cellPaintDoor
                        )
                    }
                    if (exitDirections.containsKey("s")) {
                        // Draw "South" door
                        canvas.drawLine(
                            (x * cellWidth.toFloat()) + 5,
                            (y + 1) * cellHeight.toFloat(),
                            ((x + 1) * cellWidth.toFloat()) - 5,
                            (y + 1) * cellHeight.toFloat(),
                            cellPaintDoor
                        )
                    }
                    if (exitDirections.containsKey("w")) {
                        // Draw "West" door
                        canvas.drawLine(
                            x * cellWidth.toFloat(),
                            (y * cellHeight.toFloat()) + 5,
                            x * cellWidth.toFloat(),
                            ((y + 1) * cellHeight.toFloat()) - 5,
                            cellPaintDoor
                        )
                    }
                    if (exitDirections.containsKey("e")) {
                        // Draw "East" door
                        canvas.drawLine(
                            (x + 1) * cellWidth.toFloat(),
                            (y * cellHeight.toFloat()) + 5,
                            (x + 1) * cellWidth.toFloat(),
                            ((y + 1) * cellHeight.toFloat()) - 5,
                            cellPaintDoor
                        )
                    }
                    // Cell number
                    canvas.drawText(
                        cellNumber.toString().padStart(3, ' '),
                        (x + .07F) * cellWidth.toFloat(),
                        (y + .7F) * cellHeight.toFloat(),
                        cellPaintText
                    )
                    // Coordinates for debugging
                    /*canvas.drawText(coords,height/2f-200,20f+15,cellPaintDebug)*/
                }
            }
        }
    }

    /*private var coords=""
    private val cellPaintDebug = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeWidth=2f;textSize=40f }*/

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
            val column = (event.y / cellWidth).toInt() - 3
            val row = (event.x / cellHeight).toInt() + 47
            val selectedCell: Int = cellsGrid[column][row]
            /*coords="($row,$column) $selectedCell"*/
            if (selectedCell > -1) {
                val roomDetails: RoomDetails = if (inDarkWorld) {
                    (darkGraph[selectedCell]?.get(0) as RoomDetails)
                } else {
                    (roomsGraph[selectedCell]?.get(0) as RoomDetails)
                }
                UserInteraction.askQuestion(context, "Room #$selectedCell", roomDetails.toString(), "Confirm", null)
                MainActivity.traverseToRoom = selectedCell
            }
            performClick()
            invalidate()
            return true
        }
        return false
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