package com.example.rtspudp

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class StreamDataModel(val name: String, val url: String, val protocol: String)

object StreamData {
    private var _streams: SnapshotStateList<StreamDataModel> = mutableStateListOf()
    val streams: List<StreamDataModel> get() = _streams

    private val defaultStreams = listOf(
        StreamDataModel(
            name = "RTSP protocol",
            url = "rtsp://192.168.19.217:554/live",
            protocol = "RTSP"
        ),
        StreamDataModel(
            name = "RTMP protocol",
            url = "rtmp://192.168.19.217:1935/live/aboba",
            protocol = "RTMP"
        )
    )

    fun init(context: Context) {
        val savedStreams = SharedPrefManager.loadStreams(context)
        _streams.clear()
        _streams.addAll(if (savedStreams.isNotEmpty()) savedStreams else defaultStreams)
    }

    fun addStream(context: Context, stream: StreamDataModel) {
        _streams.add(stream)
        SharedPrefManager.saveStreams(context, _streams)
    }

    fun deleteStream(context: Context, index: Int) {
        if (index in _streams.indices) {
            _streams.removeAt(index)
            SharedPrefManager.saveStreams(context, _streams)
        }
    }

    fun getByPosition(position: Int): StreamDataModel? {
        return _streams.getOrNull(position)
    }
}