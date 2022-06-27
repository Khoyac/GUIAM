package com.example.guiam.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Places(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val desc: String,
    val coord: String,
    val city: String
)
