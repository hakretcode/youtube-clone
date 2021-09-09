package com.hakretcode.youtube

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_rv_item.*
import kotlinx.android.synthetic.main.main_rv_item.view.*
import kotlinx.android.synthetic.main.video_content_collapsed.*
import kotlinx.android.synthetic.main.video_content_expanded.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {
    private lateinit var youtubePlayer: YoutubePlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)
        supportActionBar?.title = null
        showVideoContent(View.GONE)
        val videos = mutableListOf<Video>()
        val adapter = MainAdapter(videos, this::showViewOverlay)
        CoroutineScope(Job() + Dispatchers.Main).launch {
            fl_motion_container.visibility = View.VISIBLE
            getVideos()?.data?.apply {
                videos.clear();videos.addAll(this)
                fl_motion_container.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }
        }; rv_main.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.VERTICAL, false
            )
        }
        preparePlayer()
    }

    private fun preparePlayer() {
        youtubePlayer = YoutubePlayer(this, video_surface.holder, object : YoutubePlayer.YoutubeEventListener{
            override fun onTrackTime(progress: Long, currentTime: Long) {
                seek_bar_video.progress = progress.toInt()
                tv_current_time.text = currentTime.formatTime()
            }
        })
        seek_bar_video.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(view: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) youtubePlayer.setProgressChanged(progress.toLong())
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        youtubePlayer.release()
    }

    override fun onPause() {
        super.onPause()
        youtubePlayer.release()
    }

    private fun showViewOverlay(video: Video) {
        val title = video.title
        tv_video_title_collapsed.text = title;tv_video_title_expanded.text = title
        val publisher = video.publisher
        val name = publisher.name
        tv_channel_name_collapsed.text = name;tv_channel_name_expanded.text = name
        tv_info_expanded.text = getString(
            R.string.info,
            name,
            video.viewsCountLabel,
            video.publishedAt.formatted()
        )
        Glide.with(this).load(publisher.pictureProfileUrl).into(iv_author_expanded)
        showVideoContent(View.VISIBLE)
        motion_layout.transitionToEnd()
        youtubePlayer.setUrl(video.videoUrl)
    }

    private fun showVideoContent(mode: Int) {
        for (item in arrayOf(video_container, video_surface, tv_video_title_collapsed,
            tv_channel_name_collapsed, iv_play_pause, iv_close)) item.visibility = mode
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private suspend fun getVideos(): ListVideo? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url("https://tiagoaguiar.co/api/youtube-videos").build()
            OkHttpClient().newCall(request).execute().use { response ->
                if (response.isSuccessful) GsonBuilder().create()
                    .fromJson(response.body?.string(), ListVideo::class.java)
                else null
            }
        } catch (e: Throwable) {
            null
        }
    }
}

private fun Long.formatTime(): CharSequence = String.format("%02d:%2d", this / 1000 / 60, this / 1000 % 60)
