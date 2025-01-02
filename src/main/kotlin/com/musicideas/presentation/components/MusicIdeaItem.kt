package com.musicideas.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.domain.model.MusicIdea

// presentation/components/MusicIdeaItem.kt
@Composable
fun MusicIdeaItem(
    musicIdea: MusicIdea,
    onUpload: ((MusicIdea) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${musicIdea.metadata.ideaType} - ${musicIdea.metadata.instrument}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text("${musicIdea.metadata.tempo} BPM")
            }

            Text(
                musicIdea.metadata.genre.toString(),
                style = MaterialTheme.typography.headlineSmall
            )

            if (!musicIdea.metadata.tags.isEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    musicIdea.metadata.tags.forEach { tag ->
                        Chip(tag)
                    }
                }
            }

            onUpload?.let { upload ->
                Button(
                    onClick = { upload(musicIdea) },
                    // modifier = Modifier.align(LineHeightStyle.Alignment.Top)
                ) {
                    Text("Upload")
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        // color = MaterialTheme.colorScheme. colors.primary.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}