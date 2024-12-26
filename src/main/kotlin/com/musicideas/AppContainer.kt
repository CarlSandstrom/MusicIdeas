package com.musicideas

import com.musicideas.audio.recording.AudioRecorder
import com.musicideas.audio.recording.JavaSoundRecorder

class AppContainer {
    val audioRecorder: AudioRecorder = JavaSoundRecorder()
    // val audioLibrary: AudioLibrary = AudioLibraryImpl()
    // val settings: Settings = SettingsImpl()
    // val cloudStorage: CloudStorage = CloudStorageImpl()
}