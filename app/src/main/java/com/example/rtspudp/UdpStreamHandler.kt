package com.example.rtspudp

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.SurfaceView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.UdpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

@UnstableApi
class UdpStreamHandler(
    private val context: Context,
    private val player: ExoPlayer,
    private val surfaceView: SurfaceView
) {
    fun playStream(streamUrl: String) {
        player.setVideoSurfaceView(surfaceView)
        val mediaItem = MediaItem.fromUri(Uri.parse(streamUrl))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_OFF
        Log.d("UdpStreamHandler", "Unicast UDP playback started")
    }

    companion object {
        fun createPlayer(context: Context): ExoPlayer {
            // Создаем DataSource.Factory на базе UdpDataSource
            val udpFactory: DataSource.Factory = DataSource.Factory {
                UdpDataSource(
                    /* maxPacketSize = */ UdpDataSource.DEFAULT_MAX_PACKET_SIZE,
                    /* socketTimeoutMillis = */ UdpDataSource.DEFAULT_SOCKET_TIMEOUT_MILLIS
                )
            }
            val dataSourceFactory = DefaultDataSource.Factory(context, udpFactory)

            return ExoPlayer.Builder(context)
                .setMediaSourceFactory(
                    DefaultMediaSourceFactory(dataSourceFactory)
                )
                .setLoadControl(
                    DefaultLoadControl.Builder()
                        .setBufferDurationsMs(
                            /* minBufferMs */ 1000,
                            /* maxBufferMs */ 2000,
                            /* bufferForPlaybackMs */ 500,
                            /* bufferForPlaybackAfterRebufferMs */ 1000
                        )
                        .build()
                )
                .build()
        }
    }
}