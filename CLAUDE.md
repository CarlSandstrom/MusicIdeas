# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This Is

A Kotlin/Compose Desktop app for musicians to capture and tag audio ideas. Users record raw audio, attach metadata (genre, instrument, type, custom tags), browse/filter their library, and export to MP3.

## Commands

```bash
./gradlew run        # Run the desktop app
./gradlew build      # Build the project
./gradlew package    # Package for distribution (Msi, Dmg, Deb)
```

There are no tests in this project.

## Architecture

Clean Architecture + MVVM with three layers:

**Core (domain)** — `com.musicideas.core`
- `model/MusicIdea.kt` — the central domain entity plus all enums (`Genre`, `Instrument`, `IdeaType`)
- `repository/` — interfaces only; no implementations here

**Data** — `com.musicideas.data`
- `audio/recording/` — `JavaSoundRecorder` records to `ByteArray` using Java Sound API
- `audio/playback/` — `JavaSoundPlayer` plays back via `AudioInputStream`
- `audio/encoding/Mp3Encoder.kt` — FFmpeg/JavaCV-based MP3 export
- `audio/config/AudioFormat.kt` — shared audio format constants (sample rate, bit depth, channels)
- `storage/MusicIdeaStorageFileSystem.kt` — persists to `~/MusicIdeas/audio/` (raw bytes) and `~/MusicIdeas/metadata/` (JSON). Audio is lazy-loaded.
- `repository/` — implementations of the core interfaces

**Presentation** — `com.musicideas.presentation`
- Each screen lives in its own folder under `screens/` (record, library, save, settings, cloudstorage), always as a `View.kt` + `ViewModel.kt` pair
- `common/ViewModel.kt` — base class providing `viewModelScope`
- `navigation/AppNavigation.kt` — tab-based navigation scaffold; `MainViewModel.kt` acts as a factory for screen-level ViewModels
- `components/` — shared reusable Composables (waveform, volume gauge, filter panel, etc.)

**DI** — `com.musicideas.di/AppModule.kt` — all Koin bindings; singletons for storage, recorder, player, and repositories. Add new bindings here.

## Key Flows

- **Recording**: `RecordViewModel` drives `AudioRecorder` → collects `ByteArray` chunks → stores via `AudioRepository`
- **Saving**: `SaveViewModel` writes metadata + raw audio via `MusicIdeaRepository`
- **Library**: `LibraryViewModel` loads ideas lazily and applies in-memory filtering by genre, instrument, type, time range, and tags
- **Export**: `Mp3Encoder` wraps JavaCV/FFmpeg; triggered from the library or save screen

## Conventions

- ViewModels expose state via Compose `mutableStateOf` (not `StateFlow`)
- Coroutines are launched in `viewModelScope`; audio operations run on `Dispatchers.IO`
- `MusicIdea` carries a `suspend () -> ByteArray?` lambda for lazy audio loading — avoid eagerly calling it in list rendering
- Koin is the only DI mechanism; do not use constructor injection outside of what Koin wires
- `cloudstorage` screen is a placeholder — not implemented
