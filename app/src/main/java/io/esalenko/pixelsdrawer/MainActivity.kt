package io.esalenko.pixelsdrawer

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        increaseGridSize()
        decreaseGridSize()
        clearClick()
        color()
        mode()
    }

    private fun mode() {
        switch_mode.setOnClickListener {
            val mode = if (switch_mode.isChecked) {
                PixelDrawerViewV2.MODE.PAINT
            } else {
                PixelDrawerViewV2.MODE.ERASE
            }
            pixel_drawer.setMode(mode)
        }
    }

    private fun clearClick() {
        clear_btn.setOnClickListener {
            pixel_drawer.clear()
        }
    }

    private fun decreaseGridSize() {
        decrease_grid_size_btn.setOnClickListener {
            pixel_drawer.changeGridSize(pixel_drawer.gridSize - 1)
        }
    }

    private fun increaseGridSize() {
        increase_grid_size_btn.setOnClickListener {
            pixel_drawer.changeGridSize(pixel_drawer.gridSize + 1)
        }
    }

    private fun color() {
        switch_color.setOnClickListener {
            val color = if (switch_color.isChecked) {
                Color.BLACK
            } else {
                Color.BLUE
            }
            pixel_drawer.changeColor(color)
        }
    }
}
