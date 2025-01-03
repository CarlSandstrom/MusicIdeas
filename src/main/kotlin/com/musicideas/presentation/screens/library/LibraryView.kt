package com.musicideas.presentation.screens.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.presentation.components.FilterPanel
import com.musicideas.presentation.components.MusicIdeaItem
import com.musicideas.presentation.components.TagCheckboxList
import com.musicideas.presentation.components.TagFilterInput

@Composable
fun LibraryView(viewModel: LibraryViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Your Music Ideas", style = MaterialTheme.typography.headlineMedium)

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left panel with all filters
            Column(
                modifier = Modifier.width(300.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilterPanel(
                    selectedGenre = viewModel.filterState.selectedGenre,
                    onGenreSelected = { viewModel.updateFilter { copy(selectedGenre = it) } },
                    selectedInstrument = viewModel.filterState.selectedInstrument,
                    onInstrumentSelected = { viewModel.updateFilter { copy(selectedInstrument = it) } },
                    selectedIdeaType = viewModel.filterState.selectedIdeaType,
                    onIdeaTypeSelected = { viewModel.updateFilter { copy(selectedIdeaType = it) } },
                    timeRange = viewModel.filterState.timeRange,
                    onTimeRangeChange = { viewModel.updateFilter { copy(timeRange = it) } }
                )

                TagFilterInput(
                    value = viewModel.filterState.tagSearchQuery,
                    onValueChange = { viewModel.updateFilter { copy(tagSearchQuery = it) } }
                )

                TagCheckboxList(
                    availableTags = viewModel.availableTags,
                    selectedTags = viewModel.filterState.selectedTags,
                    onTagSelected = { viewModel.updateFilter { copy(selectedTags = selectedTags + it) } },
                    onTagDeselected = { viewModel.updateFilter { copy(selectedTags = selectedTags - it) } }
                )
            }

            // Right panel with filtered results
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.filteredMusicIdeas) { musicIdea ->
                    MusicIdeaItem(musicIdea = musicIdea)
                }
            }
        }
    }
}