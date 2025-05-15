package com.example.rtspudp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.rtspudp.database.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}