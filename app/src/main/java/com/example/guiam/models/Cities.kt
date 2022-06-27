package com.example.guiam.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cities(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val cords: String
)
