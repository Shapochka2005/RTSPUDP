package com.example.rtspudp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPrefManager {
    private const val PREFS_NAME = "stream_prefs"
    private const val STREAMS_KEY = "saved_streams"

    fun saveStreams(context: Context, streams: List<StreamDataModel>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(streams)
        editor.putString(STREAMS_KEY, json)
        editor.apply()
    }

    fun loadStreams(context: Context): List<StreamDataModel> {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(STREAMS_KEY, null)
            if (json != null) {
                val type = object : TypeToken<List<StreamDataModel>>() {}.type
                Gson().fromJson(json, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}