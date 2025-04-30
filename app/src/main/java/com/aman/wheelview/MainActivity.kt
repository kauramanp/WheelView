package com.aman.wheelview

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aman.wheelview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sample items for the wheel picker
        val animalItems = listOf(
            NameDataClass(0, "Cat", isSelected = false),
            NameDataClass(1, "Dog", isSelected = false),
            NameDataClass(2, "Elephant", isSelected = false),
            NameDataClass(3, "Tiger", isSelected = false),
            NameDataClass(4, "Lion", isSelected = false),
            NameDataClass(5, "Zebra", isSelected = false),
            NameDataClass(6, "Giraffe", isSelected = false),
            NameDataClass(7, "Monkey", isSelected = false),
            NameDataClass(8, "Panda", isSelected = false),
            NameDataClass(9, "Kangaroo", isSelected = false)
        )

        // Set items to custom wheel picker
        binding.customWheelPicker.setItems(animalItems)

        // Listen to center item changes (snapped)
        binding.customWheelPicker.onValueChanged = { selected ->
            Toast.makeText(this, "Selected: $selected", Toast.LENGTH_SHORT).show()
        }

        // Toggle tick icon when an item is clicked
        binding.customWheelPicker.onValueClicked = {position->
            animalItems[position].isSelected = !(animalItems[position].isSelected)
            binding.customWheelPicker.updateItems(animalItems)

        }
    }

}