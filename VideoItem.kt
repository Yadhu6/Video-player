package com.chinnu.pocketplayer

import android.net.Uri

data class VideoItem(
    val uri: Uri,
    val name: String,
    val durationMs: Long,
    val sizeBytes: Long
)
