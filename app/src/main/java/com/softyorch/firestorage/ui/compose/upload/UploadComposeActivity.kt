package com.softyorch.firestorage.ui.compose.upload

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.runtime.Composable
import com.softyorch.firestorage.R
import com.softyorch.firestorage.databinding.ActivityUploadComposeBinding
import dagger.hilt.android.AndroidEntryPoint

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

    }
}
