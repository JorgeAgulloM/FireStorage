package com.softyorch.firestorage.ui.xml.upload

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.softyorch.firestorage.databinding.ActivityUploadXmlBinding
import com.softyorch.firestorage.databinding.DialogImageSelectorBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@AndroidEntryPoint
class UploadXmlActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent = Intent(context, UploadXmlActivity::class.java)
    }

    private lateinit var binding: ActivityUploadXmlBinding
    private val viewModel: UploadXmlViewModel by viewModels()

    private lateinit var uri: Uri
    private val intentCameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it && uri.path?.isNotEmpty() == true) {
            viewModel.uploadAndGetImage(uri) { downloadUri ->
                showNewImage(downloadUri)
                clearText()
            }
        }
    }
    private val intentGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.uploadAndGetImage(uri) { downloadUri ->
                showNewImage(downloadUri)
            }
        }
    }

    private fun showNewImage(downloadUri: Uri) {
        Glide.with(this).load(downloadUri).into(binding.ivImage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initUiState()
    }

    private fun initUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect {
                    binding.pbImage.isVisible = it
                    if (it) {
                        binding.ivPlaceHolder.isGone = true
                        binding.ivImage.setImageDrawable(null)
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.fabImage.setOnClickListener {
            showImageDialog()
        }
    }

    private fun showImageDialog() {
        val dialogBinding = DialogImageSelectorBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this).apply {
            setView(dialogBinding.root)
        }.create()

        dialogBinding.apply {
            btnTakePhoto.setOnClickListener {
                takePhoto()
                alertDialog.dismiss()
            }
            btnTakeGallery.setOnClickListener {
                getImageFromGallery()
                alertDialog.dismiss()
            }
        }
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private fun getImageFromGallery() {
        intentGalleryLauncher.launch("image/*")
    }

    private fun takePhoto() {
        generateUri()
        intentCameraLauncher.launch(uri)
    }

    private fun generateUri() {
        uri = FileProvider.getUriForFile(
            Objects.requireNonNull(this),
            "com.softyorch.firestorage.provider",
            createFile()
        )
    }

    private fun createFile(): File {
        val userTitle = binding.etTitle.text.toString()
        val name = userTitle.ifEmpty {
            SimpleDateFormat("yyyyMMdd_hhmmss", Locale.ROOT).format(Date()) + "_image_"
        }
        return File.createTempFile(name, ".jpg", externalCacheDir)
    }

    private fun clearText() {
        binding.etTitle.apply {
            setText("")
            clearFocus()
        }
    }

}