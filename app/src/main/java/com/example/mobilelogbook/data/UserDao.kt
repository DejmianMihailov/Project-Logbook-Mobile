package com.example.mobilelogbook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mobilelogbook.model.AppUser

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AppUser)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): AppUser?
}
