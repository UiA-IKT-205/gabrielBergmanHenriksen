package com.example.knotsandcrosses

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.knotsandcrosses.databinding.ActivityGameEndBinding

class GameEndActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameEndBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameEndBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val winningPlayer = intent.getStringExtra("winningPlayer").toString()

        findViewById<TextView>(R.id.winningPlayerText).apply {
            text = winningPlayer
        }
    }
}