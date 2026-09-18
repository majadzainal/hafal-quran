package com.hafalquran.app.data.remote

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hafalquran.app.data.model.MemorizeLevel
import com.hafalquran.app.data.model.TargetTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import com.google.firebase.messaging.FirebaseMessaging

class FirestoreSyncManager(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val deviceId: String by lazy {
        try {
            val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            if (id.isNullOrBlank()) "unknown_device" else id
        } catch (e: Exception) {
            "unknown_device"
        }
    }

    init {
        // Otomatis sinkronkan info perangkat saat aplikasi dibuka
        syncUserProfile()
        initFirebaseMessaging()
    }

    private fun initFirebaseMessaging() {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            FirebaseMessaging.getInstance().subscribeToTopic("daily_reminder")
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    if (!token.isNullOrBlank()) {
                        scope.launch {
                            try {
                                db.collection("users").document(deviceId).set(
                                    mapOf(
                                        "fcm_token" to token,
                                        "token_updated_at" to FieldValue.serverTimestamp()
                                    ),
                                    SetOptions.merge()
                                ).await()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun syncUserProfile() {
        scope.launch {
            try {
                val userDoc = db.collection("users").document(deviceId)
                val data = mapOf(
                    "device_id" to deviceId,
                    "device_model" to "${Build.MANUFACTURER} ${Build.MODEL}",
                    "os_version" to "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                    "app_version" to "1.4.0",
                    "last_active" to FieldValue.serverTimestamp()
                )
                userDoc.set(data, SetOptions.merge()).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun syncTargetTask(task: TargetTask) {
        scope.launch {
            try {
                val targetDoc = db.collection("users")
                    .document(deviceId)
                    .collection("targets")
                    .document(task.id.toString())

                val data = mapOf(
                    "id" to task.id,
                    "title" to task.title,
                    "surah_number" to task.surahNumber,
                    "surah_name" to task.surahName,
                    "start_ayah" to task.startAyah,
                    "end_ayah" to task.endAyah,
                    "repeat_per_ayah" to task.repeatPerAyah,
                    "is_loop_entire_set" to task.isLoopEntireSet,
                    "pause_seconds" to task.pauseSeconds,
                    "is_completed" to task.isCompleted,
                    "created_at" to task.createdAt,
                    "updated_at" to FieldValue.serverTimestamp()
                )
                targetDoc.set(data, SetOptions.merge()).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTargetTask(taskId: Long) {
        scope.launch {
            try {
                db.collection("users")
                    .document(deviceId)
                    .collection("targets")
                    .document(taskId.toString())
                    .delete()
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun syncAyahProgress(surahNumber: Int, ayahNumber: Int, level: MemorizeLevel) {
        scope.launch {
            try {
                val docId = "${surahNumber}_${ayahNumber}"
                val ayahDoc = db.collection("users")
                    .document(deviceId)
                    .collection("ayah_progress")
                    .document(docId)

                val data = mapOf(
                    "surah_number" to surahNumber,
                    "ayah_number" to ayahNumber,
                    "level" to level.name,
                    "is_memorized" to (level == MemorizeLevel.MUTQIN),
                    "last_reviewed_at" to FieldValue.serverTimestamp()
                )
                ayahDoc.set(data, SetOptions.merge()).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
