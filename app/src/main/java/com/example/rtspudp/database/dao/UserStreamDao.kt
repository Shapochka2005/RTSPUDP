package com.example.rtspudp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.rtspudp.database.entities.UserStream

@Dao
interface UserStreamDao {
    @Insert
    suspend fun insert(stream: UserStream)

    @Delete
    suspend fun delete(stream: UserStream)

    @Query("SELECT * FROM user_streams WHERE userId = :userId")
    suspend fun getStreamsForUser(userId: Int): List<UserStream>

    @Query("SELECT * FROM user_streams")
    suspend fun getAllStreams(): List<UserStream>
}