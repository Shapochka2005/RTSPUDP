package com.example.rtspudp

data class StreamDataModel(val name: String, val url: String, val protocol: String)

object StreamData {
    private var streams = mutableListOf<StreamDataModel>()

    fun init(initialStreams: List<StreamDataModel>) {
        streams.clear()
        streams.addAll(initialStreams)
    }

    fun getAll(): List<StreamDataModel> = streams

    fun getByPosition(position: Int): StreamDataModel? {
        if (position < 0 || position >= streams.count()) {
            return null
        }
        return streams[position]
    }

    fun addStream(stream: StreamDataModel) {
        streams.add(stream)
    }

    fun deleteStream(index: Int) {
        if (index in 0 until streams.size) {
            streams.removeAt(index)
        }
    }
}