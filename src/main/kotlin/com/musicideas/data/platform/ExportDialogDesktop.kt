// data/platform/SaveDialogDesktop.kt
package com.musicideas.data.platform

import com.musicideas.core.repository.ExportDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame

class ExportDialogDesktop : ExportDialog {
    override suspend fun show(): String? = withContext(Dispatchers.IO) {
        FileDialog(null as Frame?, "Export As", FileDialog.SAVE).apply {
            file = "*.mp3"
            setFilenameFilter { _, name ->
                name.endsWith(".wav") || name.endsWith(".mp3")
            }
            isVisible = true
        }.let { dialog ->
            if (dialog.file != null && dialog.directory != null) {
                dialog.directory + dialog.file
            } else null
        }
    }
}