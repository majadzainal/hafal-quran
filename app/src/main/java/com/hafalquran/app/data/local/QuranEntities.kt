package com.hafalquran.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hafalquran.app.data.model.MemorizeLevel

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val number: Int,
    val nameArabic: String,
    val nameLatin: String,
    val nameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String,
    val juzNumber: Int
)

@Entity(
    tableName = "ayahs",
    primaryKeys = ["surahNumber", "ayahNumber"]
)
data class AyahEntity(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textArabic: String,
    val textLatin: String = "",
    val translationId: String,
    val audioUrl: String,
    val localAudioPath: String? = null,
    val isMemorized: Boolean = false,
    val memorizationLevel: String = MemorizeLevel.NOT_STARTED.name,
    val reviewCount: Int = 0,
    val lastReviewedTimestamp: Long = 0L
)

@Entity(tableName = "target_tasks")
data class TargetTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val surahNumber: Int,
    val surahName: String,
    val startAyah: Int,
    val endAyah: Int,
    val repeatPerAyah: Int = 3,
    val isLoopEntireSet: Boolean = true,
    val pauseSeconds: Float = 0.5f,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "murojaah_history")
data class MurojaahHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahNumber: Int,
    val startAyah: Int,
    val endAyah: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val totalRepetitions: Int
)
