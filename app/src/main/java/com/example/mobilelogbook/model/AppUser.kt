package com.example.mobilelogbook.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class AppUser(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val username: String,
    val password: String
)
