package com.souvik.dictoinary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.souvik.dictoinary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val word = binding.searchET.text;

        binding.findBTN.setOnClickListener {
            Intent(this, DefinitionActivity::class.java).also { i ->
                i.putExtra("word", word.toString())

                if (word.isNotEmpty())
                {
                    startActivity(i)
                }
                else
                {
                    Toast.makeText(this, "Please, Enter a word!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}