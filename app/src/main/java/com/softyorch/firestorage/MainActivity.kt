package com.softyorch.firestorage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.softyorch.firestorage.databinding.ActivityMainBinding
import com.softyorch.firestorage.ui.xml.upload.UploadXmlActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
    }

    private fun initListeners() {
        binding.apply {
            btnNavigateToXml.setOnClickListener {
                startActivity(UploadXmlActivity.create(this@MainActivity))
            }

            btnNavigateToCompose.setOnClickListener {
                
            }
        }
    }
}
