package com.example.branchapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.branchapp.databinding.ActivityStart2Binding

class StartActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityStart2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityStart2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.main_bg)
        }

        binding.startButton.setOnClickListener {
            val intent = Intent(this, MemoryGameActivity::class.java)
            startActivity(intent)
        }
    }
}