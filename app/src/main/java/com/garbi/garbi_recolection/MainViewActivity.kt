package com.garbi.garbi_recolection

import android.os.Bundle
import com.garbi.garbi_recolection.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity

class MainViewActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}