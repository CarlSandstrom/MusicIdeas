package com.musicideas.data.storage

import com.musicideas.core.model.MusicIdea

interface MusicIdeaStorage {
    suspend fun saveMusicIdea(musicIdea: MusicIdea)
    suspend fun getMusicIdea(id: String): MusicIdea?
    suspend fun getAllMusicIdeas(): List<MusicIdea>
    suspend fun deleteMusicIdea(id: String)
}