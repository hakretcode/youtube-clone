package com.hakretcode.youtube

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class YoutubePlayer(private val context: Context, private val holder: SurfaceHolder, private val youtubeEventListener: YoutubeEventListener) : SurfaceHolder.Callback {
    private var mediaPlayer: SimpleExoPlayer? = null

    init {
        checkMediaPlayer()
        holder.addCallback(this)
    }

    private fun checkMediaPlayer() { if (mediaPlayer == null) {
        mediaPlayer = SimpleExoPlayer.Builder(context).build()
        mediaPlayer?.setVideoSurfaceHolder(holder)
    } }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        checkMediaPlayer()
    }

    fun setUrl(url: String) {
        mediaPlayer?.apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(url)))
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    fun loop() {
                        if (isPlaying) {
                            youtubeEventListener.onTrackTime(currentPosition * 100 / duration, currentPosition)
                            Handler(Looper.myLooper()!!).postDelayed({ loop() }, 1000)
                        }
                    }
                    loop()
                }
            })
            playWhenReady = true
            prepare()
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) { release() }

    fun release() { mediaPlayer?.release() }

    fun pause() { mediaPlayer?.pause() }

    fun setProgressChanged(progress: Long) {
        if (progress > 0) {
            mediaPlayer?.apply {
                seekTo(progress * duration / 100)
            }
        }
    }

    interface  YoutubeEventListener {
        fun onTrackTime(progress: Long, currentTime: Long)
    }
}
