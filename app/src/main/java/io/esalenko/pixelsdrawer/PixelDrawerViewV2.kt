package io.esalenko.pixelsdrawer

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class PixelDrawerViewV2 : View {

    companion object {
        private const val TAG = "PixelDrawerViewV2"
        private const val MIN_GRID_SIZE = 2
        private const val MAX_GRID_SIZE = 64
    }

    var gridSize: Int = MIN_GRID_SIZE

    private var colSize: Float = 0f
    private var rowSize: Float = 0f

    private var cellPaint = setCellColor(Color.BLACK, Paint.Style.FILL)
    private var gridPaint = setCellColor(Color.BLACK)
    private var lastUsedPaint: Paint? = null
    private val defaultColor: Paint = setDefaultWhiteColor()

    private lateinit var cells: Array<Array<Paint?>>

    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attributeSet: AttributeSet?) : super(ctx, attributeSet) {
        val attributes: TypedArray = context.obtainStyledAttributes(
            attributeSet, R.styleable.PixelDrawerViewV2
        )
        gridSize = attributes.getInt(
            R.styleable.PixelDrawerViewV2_grid_size, gridSize
        )
        attributes.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateGridSize()
        invalidate()
    }

    private fun calculateGridSize() {
        if (gridSize <= 0 || gridSize > MAX_GRID_SIZE) throw IllegalStateException("Wrong grid size. It should be > 0 and <= 128")
        colSize = width / gridSize.toFloat()
        rowSize = width / gridSize.toFloat()
        cells = Array(gridSize) { arrayOfNulls<Paint?>(gridSize) }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val width = width.toFloat()

        createTwoDimensArray(gridSize, gridSize) { col, row ->
            canvas?.drawRect(
                col * colSize,
                row * rowSize,
                (col + 1) * colSize,
                (row + 1) * rowSize,
                cells[col][row] ?: defaultColor
            )
        }

        // draw grid columns
        repeatInclusive(gridSize) { i ->
            canvas?.drawLine(
                i * colSize, 0f, i * colSize, width, gridPaint
            )
        }

        // draw grid rows
        repeatInclusive(gridSize) { i ->
            canvas?.drawLine(
                0f, i * rowSize, width, i * rowSize, gridPaint
            )
        }
    }

    private var dx = 0f
    private var dy = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                processTapDrawing(event)
                processDrawingWithInterpolation(event.x, event.y)
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun processTapDrawing(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            dx = event.x
            dy = event.y
            drawCell(dx, dy)
            invalidate()
        }
    }

    private fun processDrawingWithInterpolation(x: Float, y: Float) {
        val midX = lerp(dx, x, .5f)
        val midY = lerp(dy, y, .5f)
        interpolate(dx, dy, midX, midY)
        interpolate(midX, midY, x, y)
        dx = x
        dy = y
    }

    private fun interpolate(x: Float, y: Float, mx: Float, my: Float) {
        val midX = lerp(x, mx, .5f)
        val midY = lerp(y, my, .5f)
        drawCell(midX, midY)
    }

    private fun drawCell(x: Float, y: Float) {
        val column = (x / colSize).toInt()
        val row = (y / rowSize).toInt()
        if (checkBounds(column, row)) return
        cells[column][row] = cellPaint
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = width
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else                -> desiredWidth
        }
        setMeasuredDimension(width, width)
    }

    fun clear() {
        cells.mapTwoDimensArray(::defaultColor)
        dx = 0f
        dy = 0f
        invalidate()
    }

    fun changeGridSize(newSize: Int) {
        if (newSize < 1 || newSize > MAX_GRID_SIZE) return
        gridSize = newSize
        calculateGridSize()
        invalidate()
    }

    fun changeColor(color: Int) {
        cellPaint = setCellColor(newColor = color, newStyle = Paint.Style.FILL)
    }

    fun setMode(mode: MODE) {
        when (mode) {
            MODE.PAINT -> {
                cellPaint = lastUsedPaint ?: defaultColor
            }
            MODE.ERASE -> {
                lastUsedPaint = cellPaint
                cellPaint = defaultColor
            }
        }
    }

    private fun setDefaultWhiteColor(): Paint = setCellColor(Color.WHITE, Paint.Style.FILL)
    private fun setCellColor(newColor: Int, newStyle: Paint.Style = Paint.Style.STROKE) =
        Paint().apply {
            color = newColor
            isAntiAlias = true
            style = newStyle
        }

    private fun checkBounds(column: Int, row: Int): Boolean =
        column < 0 || row < 0 || column >= gridSize || row >= gridSize

    enum class MODE {
        PAINT, ERASE
    }

    private fun lerp(x: Float, y: Float, dist: Float): Float = x + dist * (y - x)
}
