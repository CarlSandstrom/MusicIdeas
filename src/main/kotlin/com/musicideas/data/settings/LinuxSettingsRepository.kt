package com.musicideas.data.settings

import com.musicideas.core.model.AppSettings
import com.musicideas.core.repository.SettingsRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class LinuxSettingsRepository : SettingsRepository {
    private val settingsFile = File(
        System.getProperty("user.home"),
        ".config/musicideas/settings.json"
    )

    override fun read(): AppSettings {
        if (!settingsFile.exists()) return AppSettings()
        return try {
            Json.decodeFromString(settingsFile.readText())
        } catch (e: Exception) {
            println("Failed to read settings, using defaults: ${e.message}")
            AppSettings()
        }
    }

    override fun save(settings: AppSettings) {
        settingsFile.parentFile.mkdirs()
        settingsFile.writeText(Json.encodeToString(settings))
    }
}
