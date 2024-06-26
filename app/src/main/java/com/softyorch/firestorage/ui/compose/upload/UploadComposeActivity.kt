package com.softyorch.firestorage.ui.compose.upload

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.softyorch.firestorage.R
import com.softyorch.firestorage.databinding.ActivityUploadComposeBinding
import com.softyorch.firestorage.ui.compose.list.ListComposeActivity
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
        var resultUri: Uri? by remember { mutableStateOf(value = null) }
        val isLoading: Boolean by viewModel.isLoading.collectAsState()
        var userTitle: String by remember { mutableStateOf(value = "") }

        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        val intentCameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                if (it && uri?.path?.isNotEmpty() == true) {
                    viewModel.uploadAndGetImage(uri!!) { newUri ->
                        userTitle = ""
                        focusManager.clearFocus()
                        resultUri = newUri
                    }
                }
            }

        var showImageDialog: Boolean by remember { mutableStateOf(value = false) }
        val intentGalleryLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                if (it?.path?.isNotEmpty() == true) {
                    viewModel.uploadAndGetImage(it) { newUri ->
                        resultUri = newUri
                    }
                }
            }

        if (showImageDialog) Dialog(onDismissRequest = { showImageDialog = false }) {
            Card(shape = RoundedCornerShape(12), elevation = 12.dp) {
                Column(modifier = Modifier.padding(24.dp)) {
                    MyButton(text = "Camera") {
                        uri = generateUri(userTitle)
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

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(36.dp))
            Card(
                elevation = 12.dp,
                shape = RoundedCornerShape(12),
                modifier = Modifier.fillMaxWidth().height(300.dp).padding(horizontal = 36.dp)
            ) {
                if (resultUri != null) AsyncImage(
                    model = resultUri,
                    contentDescription = "Image result ",
                    contentScale = ContentScale.Crop
                )

                if (isLoading) Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = colorResource(R.color.green)
                    )
                }

                if (!isLoading && resultUri == null) Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_place_holder),
                        contentDescription = "Empty image",
                        modifier = Modifier.size(100.dp),
                        tint = colorResource(R.color.green)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            TextField(
                value = userTitle,
                onValueChange = { userTitle = it },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .border(
                        width = 2.dp,
                        color = colorResource(id = R.color.green),
                        shape = RoundedCornerShape(22)
                    )
                    .focusRequester(focusRequester),
                colors = textFieldColors(
                    backgroundColor = White,
                    focusedIndicatorColor = Transparent,
                    unfocusedIndicatorColor = Transparent
                ),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                FloatingActionButton(
                    onClick = { showImageDialog = true },
                    backgroundColor = colorResource(R.color.green)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = null,
                        tint = White
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            val context = LocalContext.current
            OutlinedButton(
                onClick = { startActivity(ListComposeActivity.create(context)) },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .align(CenterHorizontally),
                border = BorderStroke(2.dp, colorResource(R.color.green)),
                shape = RoundedCornerShape(42)
            ) {
                Text(text = "Navigate To List", color = colorResource(R.color.green))
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

    private fun generateUri(userTitle: String): Uri {
        return FileProvider.getUriForFile(
            Objects.requireNonNull(this),
            "com.softyorch.firestorage.provider",
            createFile(userTitle)
        )
    }

    private fun createFile(userTitle: String): File {
        val name = userTitle.ifEmpty {
            SimpleDateFormat(
                "yyyyMMdd_hhmmss",
                Locale.ROOT
            ).format(Date()) + "_image_"
        }
        return File.createTempFile(name, ".jpg", externalCacheDir)
    }
}
