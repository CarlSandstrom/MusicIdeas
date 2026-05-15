package com.musicideas.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.core.model.MusicIdea

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MusicIdeaItem(
    musicIdea: MusicIdea,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {},
    onPlay: () -> Unit = {},
    onStop: () -> Unit = {},
    onShare: () -> Unit = {},
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    onExport: () -> Unit = {},
    isPlaying: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isHovered -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onSelect() }
            .hoverable(interactionSource),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered || isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        musicIdea.metadata.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "${musicIdea.metadata.genre}, ${musicIdea.metadata.ideaType}, ${musicIdea.metadata.instrument}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Tempo: ${if (musicIdea.metadata.tempo == 0) "None specified" else "${musicIdea.metadata.tempo} BPM"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                AnimatedVisibility(
                    visible = isHovered,
                    enter = fadeIn(animationSpec = tween(100)),
                    exit = fadeOut(animationSpec = tween(100))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (isPlaying) onStop() else onPlay()
                            }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Stop" else "Play",
                                tint = if (isPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = onShare,
                            enabled = !isPlaying
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }

                        IconButton(
                            onClick = onExport,
                            enabled = !isPlaying
                        ) {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = "Export"
                            )
                        }

                        IconButton(
                            onClick = onDelete,
                            enabled = !isPlaying
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }

                        IconButton(
                            onClick = onEdit,
                            enabled = !isPlaying
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }
                    }
                }
            }

            if (musicIdea.metadata.tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    musicIdea.metadata.tags.forEach { tag ->
                        Chip(tag)
                    }
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}