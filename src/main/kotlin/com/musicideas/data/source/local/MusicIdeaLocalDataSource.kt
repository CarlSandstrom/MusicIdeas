package com.musicideas.data.source.local

import com.musicideas.domain.model.MusicIdea

interface MusicIdeaLocalDataSource {
    suspend fun saveMusicIdea(musicIdea: MusicIdea)
    suspend fun getMusicIdea(id: String): MusicIdea?
    suspend fun getAllMusicIdeas(): List<MusicIdea>
    suspend fun deleteMusicIdea(id: String)
}