package com.chinnu.pocketplayer

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : Activity() {
    private lateinit var playerView: PlayerView
    private lateinit var titleView: TextView
    private var player: ExoPlayer? = null
    private var startIndex = 0
    private var startPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playerView = findViewById(R.id.playerView)
        titleView = findViewById(R.id.videoTitle)
        startIndex = intent.getIntExtra("index", 0)
        hideSystemBars()
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    private fun initializePlayer() {
        if (player != null) return
        val uris = intent.getStringArrayExtra("playlist")?.toList().orEmpty()
        val titles = intent.getStringArrayExtra("titles")?.toList().orEmpty()
        val fallback = intent.getStringExtra("video_uri")
        val mediaUris = if (uris.isNotEmpty()) uris else listOfNotNull(fallback)

        val exo = ExoPlayer.Builder(this).build()
        player = exo
        playerView.player = exo
        exo.setMediaItems(mediaUris.map { MediaItem.fromUri(Uri.parse(it)) }, startIndex.coerceIn(0, (mediaUris.size - 1).coerceAtLeast(0)), startPosition)
        exo.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val i = exo.currentMediaItemIndex
                titleView.text = titles.getOrNull(i) ?: intent.getStringExtra("video_title") ?: "Video"
            }
        })
        titleView.text = titles.getOrNull(startIndex) ?: intent.getStringExtra("video_title") ?: "Video"
        exo.prepare()
        exo.playWhenReady = true
    }

    override fun onStop() {
        player?.let {
            startIndex = it.currentMediaItemIndex
            startPosition = it.currentPosition
            it.release()
        }
        player = null
        playerView.player = null
        super.onStop()
    }

    private fun hideSystemBars() {
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }
}
