package io.esalenko.pixelsdrawer

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
}
