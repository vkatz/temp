package com.test.movies.data.database

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.test.movies.data.Movie

@Database(entities = [Movie::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}