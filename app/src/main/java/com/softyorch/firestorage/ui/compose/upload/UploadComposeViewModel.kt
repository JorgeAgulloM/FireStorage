package com.softyorch.firestorage.ui.compose.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.softyorch.firestorage.data.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UploadComposeViewModel @Inject constructor(
    private val storageService: StorageService
) : ViewModel() {
    fun uploadBasicImage(uri: Uri) {
        storageService.uploadBasicImage(uri)
    }

}