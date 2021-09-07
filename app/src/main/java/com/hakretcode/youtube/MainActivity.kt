package com.hakretcode.youtube

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_rv_item.*
import kotlinx.android.synthetic.main.main_rv_item.view.*
import kotlinx.android.synthetic.main.video_collapsed_content.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {
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
        motion_layout.getConstraintSet(R.id.collapse)
        /*.apply {
        val displayWidth = resources.displayMetrics.widthPixels
        val scaledViewWidth = displayWidth * 0.35F
        setTranslationX(R.id.video_player, -((displayWidth - scaledViewWidth) / 2) - 1)
        setMargin(R.id.cl_collapse_container, ConstraintSet.START, scaledViewWidth.toInt() -1)
    }*/
    }

    private fun showViewOverlay(video: Video) {
        tv_collapse_video_title.text = video.title
        tv_channel_name.text = video.publisher.name
        showVideoContent(View.VISIBLE)
        motion_layout.transitionToEnd()
    }

    private fun showVideoContent(mode: Int) {
        for (item in arrayOf(video_container, video_player, tv_collapse_video_title,
            tv_channel_name, iv_play_pause, iv_close)) item.visibility = mode
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