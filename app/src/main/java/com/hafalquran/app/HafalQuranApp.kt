package com.hafalquran.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.hafalquran.app.data.local.QuranDatabase

class HafalQuranApp : Application() {

    lateinit var database: QuranDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = QuranDatabase.getDatabase(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "quran_audio_channel"
        lateinit var instance: HafalQuranApp
            private set
    }
}
