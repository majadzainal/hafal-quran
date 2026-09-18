package com.hafalquran.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Query("SELECT * FROM surahs ORDER BY number ASC")
    fun getAllSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE number = :surahNumber LIMIT 1")
    suspend fun getSurahByNumber(surahNumber: Int): SurahEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)
}

@Dao
interface AyahDao {
    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    fun getAyahsBySurah(surahNumber: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber AND ayahNumber BETWEEN :start AND :end ORDER BY ayahNumber ASC")
    suspend fun getAyahsRange(surahNumber: Int, start: Int, end: Int): List<AyahEntity>

    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber LIMIT 1")
    suspend fun getAyah(surahNumber: Int, ayahNumber: Int): AyahEntity?

    @Query("SELECT COUNT(*) FROM ayahs WHERE surahNumber = :surahNumber")
    suspend fun getAyahsCountForSurah(surahNumber: Int): Int

    @Query("UPDATE ayahs SET memorizationLevel = :level, isMemorized = :isMemorized, lastReviewedTimestamp = :timestamp WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber")
    suspend fun updateMemorizationStatus(surahNumber: Int, ayahNumber: Int, level: String, isMemorized: Boolean, timestamp: Long)

    @Query("UPDATE ayahs SET reviewCount = reviewCount + 1, lastReviewedTimestamp = :timestamp WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber")
    suspend fun incrementReviewCount(surahNumber: Int, ayahNumber: Int, timestamp: Long)

    @Query("UPDATE ayahs SET localAudioPath = :path WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber")
    suspend fun updateLocalAudioPath(surahNumber: Int, ayahNumber: Int, path: String)

    @Query("SELECT COUNT(*) FROM ayahs WHERE isMemorized = 1 OR memorizationLevel = 'MUTQIN'")
    fun getMemorizedAyahsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM ayahs WHERE memorizationLevel = :level")
    fun getCountByLevel(level: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM ayahs WHERE surahNumber >= 78 AND surahNumber <= 114 AND (isMemorized = 1 OR memorizationLevel = 'MUTQIN')")
    fun getJuz30MutqinCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM ayahs")
    fun getTotalAyahsInDb(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyahs(ayahs: List<AyahEntity>)
}

@Dao
interface TargetTaskDao {
    @Query("SELECT * FROM target_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TargetTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TargetTaskEntity): Long

    @Update
    suspend fun updateTask(task: TargetTaskEntity)

    @Query("UPDATE target_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, completed: Boolean)

    @Query("SELECT * FROM target_tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): TargetTaskEntity?

    @Query("DELETE FROM target_tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)

    @Query("SELECT COUNT(*) FROM target_tasks")
    fun getTotalTasksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM target_tasks WHERE isCompleted = 1")
    fun getCompletedTasksCount(): Flow<Int>
}

@Dao
interface MurojaahDao {
    @Query("SELECT * FROM murojaah_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentHistory(): Flow<List<MurojaahHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: MurojaahHistoryEntity)
}
