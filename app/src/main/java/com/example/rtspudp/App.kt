package com.example.rtspudp

import android.app.Application
import com.example.rtspudp.database.AppDatabase
import com.example.rtspudp.repository.StreamRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getDatabase(this)
        val repository = StreamRepository(
            database.userDao(),
            database.userStreamDao()
        )

        CoroutineScope(Dispatchers.IO).launch {
            if (repository.loginUser("admin", "admin") == null) {
                repository.registerUser("admin", "admin", true)
            }
        }
    }
}