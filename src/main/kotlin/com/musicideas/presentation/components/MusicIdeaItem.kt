package com.musicideas.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferAction
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.DragAndDropTransferable
import androidx.compose.ui.unit.dp
import com.musicideas.core.model.MusicIdea
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.io.File

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MusicIdeaItem(
    musicIdea: MusicIdea,
    audioFile: File? = null,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {},
    onPlay: () -> Unit = {},
    onStop: () -> Unit = {},
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    onExport: () -> Unit = {},
    isPlaying: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val buttonAlpha by animateFloatAsState(
        targetValue = if (isHovered) 1f else 0f,
        animationSpec = tween(100)
    )

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
            .hoverable(interactionSource)
            .then(
                if (audioFile != null) {
                    val file = audioFile
                    Modifier.dragAndDropSource(drawDragDecoration = {}) {
                        detectDragGestures(
                            onDragStart = {
                                val sanitizedName = musicIdea.metadata.name
                                    .replace(Regex("[\\\\/:*?\"<>|]"), "_")
                                    .trim()
                                    .ifEmpty { "recording" }
                                val tempFile = File(System.getProperty("java.io.tmpdir"), "$sanitizedName.mp3")
                                file.copyTo(tempFile, overwrite = true)
                                startTransfer(
                                    DragAndDropTransferData(
                                        transferable = DragAndDropTransferable(
                                            object : Transferable {
                                                override fun getTransferDataFlavors() = arrayOf(DataFlavor.javaFileListFlavor)
                                                override fun isDataFlavorSupported(flavor: DataFlavor) = flavor == DataFlavor.javaFileListFlavor
                                                override fun getTransferData(flavor: DataFlavor): Any = listOf(tempFile)
                                            }
                                        ),
                                        // Compose 1.7.3 calls exportAsDrag with ACTION_MOVE(2); without Move
                                        // in supportedActions, getSourceActions() returns ACTION_COPY(1) and
                                        // 1 & 2 = 0 = NONE, silently aborting the drag.
                                        supportedActions = listOf(DragAndDropTransferAction.Copy, DragAndDropTransferAction.Move)
                                    )
                                )
                            },
                            onDrag = { _, _ -> }
                        )
                    }
                } else Modifier
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered || isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
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
                    if (musicIdea.metadata.rating > 0) {
                        Row {
                            repeat(musicIdea.metadata.rating) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Box(modifier = Modifier.align(Alignment.CenterEnd).alpha(buttonAlpha)) {
                    Row(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 4.dp),
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