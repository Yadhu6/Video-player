package com.chinnu.pocketplayer

import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class VideoAdapter(
    private val activity: Activity,
    private val items: List<VideoItem>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<VideoAdapter.Holder>() {

    private val thumbExecutor = Executors.newFixedThreadPool(3)

    class Holder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
    ) {
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        val title: TextView = itemView.findViewById(R.id.title)
        val details: TextView = itemView.findViewById(R.id.details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(parent)
    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.title.text = item.name
        holder.details.text = "${formatDuration(item.durationMs)}  •  ${formatSize(item.sizeBytes)}"
        holder.thumbnail.setImageDrawable(null)
        holder.itemView.setOnClickListener { onClick(holder.bindingAdapterPosition) }

        if (Build.VERSION.SDK_INT >= 29) {
            val boundUri = item.uri
            holder.thumbnail.tag = boundUri.toString()
            thumbExecutor.execute {
                try {
                    val bitmap: Bitmap = activity.contentResolver.loadThumbnail(boundUri, Size(420, 260), null)
                    activity.runOnUiThread {
                        if (holder.thumbnail.tag == boundUri.toString()) holder.thumbnail.setImageBitmap(bitmap)
                    }
                } catch (_: Exception) { }
            }
        }
    }

    private fun formatDuration(ms: Long): String {
        val total = ms / 1000
        val h = total / 3600
        val m = (total % 3600) / 60
        val s = total % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
    }

    private fun formatSize(bytes: Long): String {
        val mb = bytes / 1024.0 / 1024.0
        return if (mb >= 1024) "%.1f GB".format(mb / 1024) else "${mb.roundToInt()} MB"
    }
}
