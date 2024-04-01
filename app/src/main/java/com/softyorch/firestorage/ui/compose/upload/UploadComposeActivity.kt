package com.softyorch.firestorage.ui.compose.upload

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.core.content.FileProvider
import com.softyorch.firestorage.R
import com.softyorch.firestorage.databinding.ActivityUploadComposeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@AndroidEntryPoint
class UploadComposeActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context) = Intent(context, UploadComposeActivity::class.java)
    }

    private lateinit var binding: ActivityUploadComposeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadComposeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.composeView.setContent {
            UploadScreen()
        }
    }

    @Composable
    fun UploadScreen() {
        val viewModel: UploadComposeViewModel by viewModels()
        var uri: Uri? by remember { mutableStateOf(null) }
        val intentCameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                if (it && uri?.path?.isNotEmpty() == true) {
                    viewModel.uploadBasicImage(uri!!)
                }
            }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            FloatingActionButton(onClick = {
                uri = generateUri()
                intentCameraLauncher.launch(uri)
            }, backgroundColor = colorResource(R.color.green)) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }

    private fun generateUri(): Uri {
        return FileProvider.getUriForFile(
            Objects.requireNonNull(this),
            "com.softyorch.firestorage.provider",
            createFile()
        )
    }

    private fun createFile(): File {
        val name = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.ROOT).format(Date()) + "_image_"
        return File.createTempFile(name, ".jpg", externalCacheDir)
    }
}
