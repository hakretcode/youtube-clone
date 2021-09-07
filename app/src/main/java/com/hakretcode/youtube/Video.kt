package com.hakretcode.youtube

import java.text.SimpleDateFormat
import java.util.*

class Video(
    val id: String,
    val thumbnailUrl: String,
    val title: String,
    val viewsCount: Long,
    val publishedAt: Date,
    val viewsCountLabel: String,
    val duration: Int,
    val videoUrl: String,
    val publisher: Publisher
)

class Publisher(val id: String, val name: String, val pictureProfileUrl: String)

class ListVideo(val status: Int, val data: List<Video>)

fun Date.formatted(): String = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(this)