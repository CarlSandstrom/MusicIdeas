package com.musicideas.core.repository

import com.musicideas.core.model.AppSettings

interface SettingsRepository {
    fun read(): AppSettings
    fun save(settings: AppSettings)
}
