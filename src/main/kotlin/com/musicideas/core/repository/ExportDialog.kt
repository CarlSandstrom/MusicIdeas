package com.musicideas.core.repository

interface ExportDialog
{
    suspend fun show(): String?
}