package com.musicideas.ui.screens.cloudstorage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CloudStorageView(viewModel: CloudStorageViewModel = remember { CloudStorageViewModel() }) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "CoudStorageView",
            style = MaterialTheme.typography.headlineMedium
        )
        // Library content
    }
}