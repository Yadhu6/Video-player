package com.chinnu.pocketplayer

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private lateinit var recycler: RecyclerView
    private lateinit var subtitle: TextView
    private lateinit var permissionButton: Button
    private val io = Executors.newSingleThreadExecutor()
    private val permissionCode = 77

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.videoList)
        subtitle = findViewById(R.id.subtitle)
        permissionButton = findViewById(R.id.permissionButton)
        recycler.layoutManager = LinearLayoutManager(this)
        permissionButton.setOnClickListener { requestVideoPermission() }

        if (hasVideoPermission()) loadVideos() else {
            subtitle.text = "Allow access to show videos stored on this phone"
            permissionButton.visibility = View.VISIBLE
            requestVideoPermission()
        }
    }

    private fun hasVideoPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO
        else Manifest.permission.READ_EXTERNAL_STORAGE
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestVideoPermission() {
        val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO
        else Manifest.permission.READ_EXTERNAL_STORAGE
        requestPermissions(arrayOf(permission), permissionCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            permissionButton.visibility = View.GONE
            loadVideos()
        } else {
            subtitle.text = "Video permission is required. You can allow it from Settings."
            permissionButton.visibility = View.VISIBLE
        }
    }

    private fun loadVideos() {
        subtitle.text = "Scanning videos…"
        io.execute {
            val videos = mutableListOf<VideoItem>()
            val collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
            )
            val sort = "${MediaStore.Video.Media.DATE_ADDED} DESC"

            contentResolver.query(collection, projection, null, null, sort)?.use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    videos += VideoItem(
                        uri = ContentUris.withAppendedId(collection, id),
                        name = cursor.getString(nameIndex) ?: "Video",
                        durationMs = cursor.getLong(durationIndex),
                        sizeBytes = cursor.getLong(sizeIndex)
                    )
                }
            }

            runOnUiThread {
                recycler.adapter = VideoAdapter(this, videos) { index ->
                    val intent = Intent(this, PlayerActivity::class.java).apply {
                        putExtra("video_uri", videos[index].uri.toString())
                        putExtra("video_title", videos[index].name)
                        putExtra("playlist", videos.map { it.uri.toString() }.toTypedArray())
                        putExtra("titles", videos.map { it.name }.toTypedArray())
                        putExtra("index", index)
                    }
                    startActivity(intent)
                }
                subtitle.text = if (videos.isEmpty()) "No videos found" else "${videos.size} videos on this phone"
            }
        }
    }

    override fun onDestroy() {
        io.shutdownNow()
        super.onDestroy()
    }
}
