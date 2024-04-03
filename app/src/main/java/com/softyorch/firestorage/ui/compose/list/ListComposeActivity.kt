package com.softyorch.firestorage.ui.compose.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.softyorch.firestorage.R
import com.softyorch.firestorage.databinding.ActivityListComposeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListComposeActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context) = Intent(context, ListComposeActivity::class.java)
    }

    private lateinit var binding: ActivityListComposeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListComposeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.composeView.setContent {
            ListComposeScreen()
        }
    }

    @Composable
    fun ListComposeScreen() {
        val viewModel: ListComposeViewModel by viewModels()
        val uiState: ListUiStateCompose by viewModel.uiState.collectAsState()

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                items(uiState.images) {
                    Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(24)) {
                        AsyncImage(
                            model = it,
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            if (uiState.isLoading) CircularProgressIndicator(
                modifier = Modifier.size(50.dp), color = colorResource(R.color.green)
            )
        }
    }
}
