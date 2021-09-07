package com.hakretcode.youtube

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.main_rv_item.view.*

class MainAdapter(private val videos: List<Video>, private val onClick: (Video) -> Unit) : RecyclerView.Adapter<MainAdapter.Holder>() {

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(video: Video) = with(itemView) {
            cl_video_container.setOnClickListener { onClick(video) }
        val publisher = video.publisher
        Glide.with(context).apply {
            load(video.thumbnailUrl).into(iv_video_thumbnail)
            load(publisher.pictureProfileUrl).into(iv_author)
        }
            tv_video_title.text = video.title
            tv_info.text = context.getString(
                R.string.info,
                publisher.name,
                video.viewsCountLabel,
                video.publishedAt.formatted()
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(LayoutInflater.from(parent.context).inflate(R.layout.main_rv_item, parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(videos[position])

    override fun getItemCount(): Int = videos.size
}