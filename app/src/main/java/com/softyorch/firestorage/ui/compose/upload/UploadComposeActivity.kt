package com.softyorch.firestorage.ui.compose.upload

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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

        var showImageDialog: Boolean by remember { mutableStateOf(value = false) }
        val intentGalleryLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                if (it?.path?.isNotEmpty() == true) {
                    viewModel.uploadBasicImage(it)
                }
            }

        if (showImageDialog) Dialog(onDismissRequest = { showImageDialog = false }) {
            Card(shape = RoundedCornerShape(12), elevation = 12.dp) {
                Column(modifier = Modifier.padding(24.dp)) {
                    MyButton(text = "Camera") {
                        uri = generateUri()
                        intentCameraLauncher.launch(uri)
                        showImageDialog = false
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    MyButton(text = "From Gallery") {
                        intentGalleryLauncher.launch("image/*")
                        showImageDialog = false
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            FloatingActionButton(
                onClick = { showImageDialog = true },
                backgroundColor = colorResource(R.color.green)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }

    @Composable
    fun MyButton(text: String, onClick: () -> Unit) {
        val colorGreen = colorResource(R.color.green)
        OutlinedButton(
            onClick = { onClick() },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            border = BorderStroke(2.dp, color = colorGreen),
            shape = RoundedCornerShape(42)
        ) {
            Text(text = text, color = colorGreen)
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
