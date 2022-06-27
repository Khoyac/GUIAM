package com.example.guiam.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lists(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    var placesList: String
)
