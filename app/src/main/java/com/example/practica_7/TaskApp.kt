package com.example.practica_7

import android.app.Application
import androidx.room.Room

class TaskApp:Application() {
    val room: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}