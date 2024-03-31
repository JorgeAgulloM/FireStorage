package com.softyorch.firestorage.ui.xml.upload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.softyorch.firestorage.R
import com.softyorch.firestorage.databinding.ActivityUploadXmlBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadXmlActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadXmlBinding
    private val viewModel: UploadXmlViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
    }

    private fun initListeners() {

    }
}