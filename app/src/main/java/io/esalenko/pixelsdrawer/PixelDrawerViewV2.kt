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

    // default min gridSize is 2
    var gridSize: Int = 2

    private var colSize: Float = 0f
    private var rowSize: Float = 0f

    private var mRowIndex: Int = 0
    private var mColumnIndex: Int = 0

    private var cellPaint = setCellColor(Color.BLACK, Paint.Style.FILL)
    private var gridPaint = setCellColor(Color.BLACK)
    private var lastUsedPaint: Paint? = null

    private val defaultColor = setDefaultWhiteColor()

    private lateinit var cells: Array<Array<Paint?>>

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attributeSet: AttributeSet?) : super(ctx, attributeSet) {
        val attributes: TypedArray = context.obtainStyledAttributes(
                attributeSet,
                R.styleable.PixelDrawerViewV2
        )
        gridSize = attributes.getInt(
                R.styleable.PixelDrawerViewV2_grid_size,
                gridSize
        )
        attributes.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateGridSize()
        invalidate()
    }

    private fun calculateGridSize() {
        if (gridSize <= 0 || gridSize > 129) throw IllegalStateException("Wrong grid size. It should be > 0 and <= 128")
        colSize = width / gridSize.toFloat()
        rowSize = width / gridSize.toFloat()
        cells = Array(gridSize) { arrayOfNulls<Paint?>(gridSize) }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val width = width.toFloat()
        for (col in 0 until gridSize) {
            for (row in 0 until gridSize) {
                canvas?.drawRect(
                        col * colSize,
                        row * rowSize,
                        (col + 1) * colSize,
                        (row + 1) * rowSize,
                        cells[col][row] ?: defaultColor
                )
            }
        }

        // draw grid columns
        for (i in 0 until gridSize) {
            canvas?.drawLine(
                    i * colSize,
                    0f,
                    i * colSize,
                    width,
                    gridPaint
            )
        }

        // draw grid rows
        for (i in 0..gridSize) {
            canvas?.drawLine(
                    0f,
                    i * rowSize,
                    width,
                    i * rowSize,
                    gridPaint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = width
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }
        setMeasuredDimension(width, width)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {
                val column = (event.x / colSize).toInt()
                val row = (event.y / rowSize).toInt()

                if (inBounds(column, row) || isLastCellCoordinates(column, row)) return true

                mColumnIndex = column
                mRowIndex = row
                cells[column][row] = cellPaint
                invalidate()
            }
        }
        return true
    }

    fun clear() {
        for (col in cells.indices) {
            for (row in cells.indices) {
                cells[col][row] = defaultColor
            }
        }
        invalidate()
    }

    fun changeGridSize(newSize: Int) {
        if (newSize < 1) return
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

    private fun setCellColor(newColor: Int, newStyle: Paint.Style = Paint.Style.STROKE) = Paint().apply {
        color = newColor
        isAntiAlias = true
        style = newStyle
    }

    private fun inBounds(column: Int, row: Int): Boolean = column < 0
            || row < 0
            || column >= gridSize
            || row >= gridSize

    private fun isLastCellCoordinates(column: Int, row: Int): Boolean = mColumnIndex == column
            && mRowIndex == row

    companion object {
        private const val TAG = "PixelDrawerViewV2"
    }

    enum class MODE {
        PAINT,
        ERASE
    }
}
