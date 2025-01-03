package com.musicideas.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.domain.model.Genre
import com.musicideas.domain.model.IdeaType
import com.musicideas.domain.model.Instrument
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
    onTimeRangeChange: (ClosedRange<Long>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Filters", style = MaterialTheme.typography.h6)

        // Genre Filter
        FilterDropdown(
            label = "Genre",
            options = Genre.entries.toList(),
            selectedOption = selectedGenre,
            onOptionSelected = onGenreSelected,
            includeAny = true
        )

        // Instrument Filter
        FilterDropdown(
            label = "Instrument",
            options = Instrument.entries.toList(),
            selectedOption = selectedInstrument,
            onOptionSelected = onInstrumentSelected,
            includeAny = true
        )

        // Idea Type Filter
        FilterDropdown(
            label = "Idea Type",
            options = IdeaType.entries.toList(),
            selectedOption = selectedIdeaType,
            onOptionSelected = onIdeaTypeSelected,
            includeAny = true
        )

        // Time Range Filter
        TimeRangeSlider(
            timeRange = timeRange,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimeRangeSlider(
    timeRange: ClosedRange<Long>,
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
            valueRange = (System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000).toFloat()..System.currentTimeMillis().toFloat()
        )
    }
}