package com.example.mobilelogbook.model

import com.google.gson.annotations.SerializedName


data class Aircraft(
    val id: Long,
    @SerializedName("registration")
    val registration: String,
    val model: String? = null
)
