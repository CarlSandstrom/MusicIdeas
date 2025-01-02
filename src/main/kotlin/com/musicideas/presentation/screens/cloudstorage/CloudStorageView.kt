package com.musicideas.presentation.screens.cloudstorage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.presentation.components.MusicIdeaItem

@Composable
fun CloudStorageView(viewModel: CloudStorageViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Cloud Storage", style = MaterialTheme.typography.bodyLarge)

        if (viewModel.isUploading) {
            CircularProgressIndicator()
        }

        LazyColumn {
            items(viewModel.musicIdeas) { musicIdea ->
                MusicIdeaItem(
                    musicIdea = musicIdea,
                    onUpload = { viewModel.uploadMusicIdea(it) }
                )
            }
        }
    }
}