package com.musicideas.data.storage

import com.musicideas.core.model.MusicIdea
import java.io.File

interface MusicIdeaStorage {
    suspend fun saveMusicIdea(musicIdea: MusicIdea)
    suspend fun getMusicIdea(id: String): MusicIdea?
    suspend fun getAllMusicIdeas(): List<MusicIdea>
    suspend fun deleteMusicIdea(id: String)
    fun getAudioFile(id: String): File?
}