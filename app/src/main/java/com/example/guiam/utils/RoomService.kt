package com.example.guiam.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.guiam.dao.ListDAO
import com.example.guiam.dao.PlacesDAO
import com.example.guiam.models.Cities
import com.example.guiam.models.Lists
import com.example.guiam.models.Places

class RoomService interface RoomDatabase {
    @Database(
        entities = [Cities::class, Places::class, Lists::class],
        version = 1
    )
    abstract class ListDb : RoomDatabase() {
        abstract fun ListDAO(): ListDAO
        abstract fun PlacesDAO(): PlacesDAO
    }
}
