package com.example.rtspudp.repository

import com.example.rtspudp.database.dao.UserDao
import com.example.rtspudp.database.dao.UserStreamDao
import com.example.rtspudp.database.entities.User
import com.example.rtspudp.database.entities.UserStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class StreamRepository(
    private val userDao: UserDao,
    private val userStreamDao: UserStreamDao
) {
    suspend fun registerUser(username: String, password: String, isAdmin: Boolean = false) {
        withContext(Dispatchers.IO) {
            if (userDao.getUserByUsername(username) != null) {
                throw Exception("User already exists")
            }
            userDao.insert(User(username = username, password = password, isAdmin = isAdmin))
        }
    }

    suspend fun loginUser(username: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByUsername(username)?.takeIf { it.password == password }
        }
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        userDao.getAllUsers()
    }

    suspend fun addStreamForUser(userId: Int, name: String, url: String, protocol: String) {
        withContext(Dispatchers.IO) {
            userStreamDao.insert(UserStream(userId = userId, name = name, url = url, protocol = protocol))
        }
    }

    fun getStreamsForUser(userId: Int): Flow<List<UserStream>> = flow {
        emit(withContext(Dispatchers.IO) {
            userStreamDao.getStreamsForUser(userId)
        })
    }

    suspend fun getAllStreams(): List<UserStream> = withContext(Dispatchers.IO) {
        userStreamDao.getAllStreams()
    }

    suspend fun deleteStream(stream: UserStream) = withContext(Dispatchers.IO) {
        userStreamDao.delete(stream)
    }
}