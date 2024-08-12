package com.example.branchapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.branchapp.databinding.ActivityMemoryGameBinding

class MemoryGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoryGameBinding
    private lateinit var cards: MutableList<Card>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMemoryGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.main_bg)
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, StartActivity2::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun setupCards() {
        val images = listOf(
            R.drawable.bear,
            R.drawable.bee,
            R.drawable.dog,
            R.drawable.cat,
            R.drawable.elephant,
            R.drawable.giraffe,
            R.drawable.lion,
            R.drawable.rabbit,
            R.drawable.penguin,
            R.drawable.zebra
        )

        cards = (images.shuffled().take(10) + images.shuffled().take(10)).shuffled().mapIndexed { index, image ->
            Card(id = index, image = image)
        }.toMutableList()
    }
}