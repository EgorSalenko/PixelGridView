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

    private val cellPaint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private lateinit var cells: Array<BooleanArray>

    private val gridPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attributeSet: AttributeSet?) : super(ctx, attributeSet) {
        val attributes: TypedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.PixelDrawerViewV2);
        gridSize =
            attributes.getInt(R.styleable.PixelDrawerViewV2_grid_size, gridSize)
        attributes.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateGridSize()
        invalidate()
    }

    private fun calculateGridSize() {
        colSize = width / gridSize.toFloat()
        rowSize = width / gridSize.toFloat()
        cells = Array(gridSize) { BooleanArray(gridSize) }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val width = width.toFloat()
        for (col in 0 until gridSize) {
            for (row in 0 until gridSize) {
                if (cells[col][row]) {
                    canvas?.drawRect(
                        col * colSize,
                        row * rowSize,
                        (col + 1) * colSize,
                        (row + 1) * rowSize,
                        cellPaint
                    )
                }
            }
        }

        // draw grid columns
        for (i in 0 until gridSize) {
            canvas?.drawLine(
                i * colSize, 0f,
                i * colSize, width,
                gridPaint
            )
        }

        // draw grid rows
        for (i in 0..gridSize) {
            canvas?.drawLine(
                0f, i * rowSize,
                width, i * rowSize,
                gridPaint
            )
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val desiredWidth = width
        val desiredHeight = width

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }
        setMeasuredDimension(width, height)
    }

    fun clear() {
        for (col in 0 until gridSize) {
            for (row in 0 until gridSize) {
                cells[col][row] = false
            }
        }
        invalidate()
    }

    private var mRow: Int = 0
    private var mCol: Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_MOVE -> {
                val column = (event.x / colSize).toInt()
                val row = (event.y / rowSize).toInt()
                if (mCol == column && mRow == row || cells[column][row]) return true
                mCol = column
                mRow = row
                cells[column][row] = !cells[column][row]
                invalidate()
            }
        }
        return true
    }

    fun changeGridSize(newSize : Int) {
        gridSize = newSize
        calculateGridSize()
        invalidate()
    }

    companion object {
        private const val TAG = "PixelDrawerViewV2"
    }
}
