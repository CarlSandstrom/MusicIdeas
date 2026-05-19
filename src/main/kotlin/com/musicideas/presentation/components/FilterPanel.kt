package com.musicideas.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.core.model.Genre
import com.musicideas.core.model.IdeaType
import com.musicideas.core.model.Instrument
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FilterPanel(
    selectedGenre: Genre?,
    onGenreSelected: (Genre?) -> Unit,
    selectedInstrument: Instrument?,
    onInstrumentSelected: (Instrument?) -> Unit,
    selectedIdeaType: IdeaType?,
    onIdeaTypeSelected: (IdeaType?) -> Unit,
    timeRange: ClosedRange<Long>,
    fullTimeRange: ClosedRange<Long>,
    onTimeRangeChange: (ClosedRange<Long>) -> Unit,
    minRating: Int = 0,
    onMinRatingChange: (Int) -> Unit = {},
    showGenre: Boolean = true,
    showInstrument: Boolean = true,
    showIdeaType: Boolean = true,
    showRating: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Filters", style = MaterialTheme.typography.h6)

        if (showGenre) {
            FilterDropdown(
                label = "Genre",
                options = Genre.entries.toList(),
                selectedOption = selectedGenre,
                onOptionSelected = onGenreSelected,
                includeAny = true
            )
        }

        if (showInstrument) {
            FilterDropdown(
                label = "Instrument",
                options = Instrument.entries.toList(),
                selectedOption = selectedInstrument,
                onOptionSelected = onInstrumentSelected,
                includeAny = true
            )
        }

        if (showIdeaType) {
            FilterDropdown(
                label = "Idea Type",
                options = IdeaType.entries.toList(),
                selectedOption = selectedIdeaType,
                onOptionSelected = onIdeaTypeSelected,
                includeAny = true
            )
        }

        if (showRating) {
            RatingFilter(minRating = minRating, onMinRatingChange = onMinRatingChange)
        }

        TimeRangeSlider(
            timeRange = timeRange,
            fullRange = fullTimeRange,
            onTimeRangeChange = onTimeRangeChange
        )
    }
}

@Composable
fun <T> FilterDropdown(
    label: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T?) -> Unit,
    includeAny: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, style = MaterialTheme.typography.subtitle1)
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedOption?.toString() ?: "Any")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (includeAny) {
                DropdownMenuItem(onClick = {
                    onOptionSelected(null)
                    expanded = false
                }) {
                    Text("Any")
                }
                Divider()
            }

            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(option.toString())
                }
            }
        }
    }
}

@Composable
fun RatingFilter(
    minRating: Int,
    onMinRatingChange: (Int) -> Unit
) {
    Column {
        Text("Min Rating", style = MaterialTheme.typography.subtitle1)
        Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
            (1..5).forEach { star ->
                IconButton(
                    onClick = { onMinRatingChange(if (star == minRating) 0 else star) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Minimum $star stars",
                        modifier = Modifier.size(20.dp),
                        tint = if (star <= minRating) MaterialTheme.colors.primary
                               else MaterialTheme.colors.onSurface.copy(alpha = 0.25f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimeRangeSlider(
    timeRange: ClosedRange<Long>,
    fullRange: ClosedRange<Long>,
    onTimeRangeChange: (ClosedRange<Long>) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Column {
        Text("Time Range", style = MaterialTheme.typography.subtitle1)
        Text(
            "${dateFormatter.format(Date(timeRange.start))} - ${dateFormatter.format(Date(timeRange.endInclusive))}",
            style = MaterialTheme.typography.caption
        )

        RangeSlider(
            value = timeRange.start.toFloat()..timeRange.endInclusive.toFloat(),
            onValueChange = { range ->
                onTimeRangeChange(range.start.toLong()..range.endInclusive.toLong())
            },
            valueRange = fullRange.start.toFloat()..fullRange.endInclusive.toFloat()
        )
    }
}