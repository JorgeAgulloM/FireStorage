package com.softyorch.firestorage.ui.compose.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softyorch.firestorage.data.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListComposeViewModel @Inject constructor(private val storageService: StorageService): ViewModel() {

    private val _uiState = MutableStateFlow(ListUiStateCompose(false, emptyList()))
    val uiState: StateFlow<ListUiStateCompose> = _uiState

    init {
        getAllImages()
    }

    private fun getAllImages() {
        viewModelScope.launch {
            _uiState.update { _uiState.value.copy(isLoading = true) }

            val result = withContext(Dispatchers.IO) {
                storageService.getAllImages().map { uri -> uri.toString() }
            }

            _uiState.update { _uiState.value.copy(isLoading = false, images = result) }
        }
    }
}
