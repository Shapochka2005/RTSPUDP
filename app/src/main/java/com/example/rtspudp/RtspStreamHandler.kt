package com.example.rtspudp

import android.content.Context
import android.net.Uri
import android.util.Log
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

class RtspStreamHandler(
    private val context: Context,
    private val mediaPlayer: MediaPlayer,
    private val libVLC: LibVLC
) {
    fun playStream(streamUrl: String) {
        try {
            val uri = Uri.parse(streamUrl)
            Media(libVLC, uri).apply {
                setHWDecoderEnabled(true, false)
                addOption(":network-caching=150")
                addOption(":rtsp-frame-buffer-size=1000000")
                addOption(":rtsp-tcp")
                mediaPlayer.media = this
            }.release()

            mediaPlayer.play()
        } catch (e: Exception) {
            Log.e("RtspStreamHandler", "Error playing RTSP stream", e)
        }
    }

    companion object {
        fun createLibVLC(context: Context): LibVLC {
            return LibVLC(context, arrayListOf(
                "--rtsp-tcp",
                "--network-caching=500",
                "--demux=ts",
                "--no-drop-late-frames",
                "--no-skip-frames",
                "--rtsp-frame-buffer-size=1000000",
                "--avcodec-codec=h264",
                "--file-caching=500",
                "--live-caching=500",
                "--clock-jitter=0",
                "--verbose=2"
            ))
        }
    }
}