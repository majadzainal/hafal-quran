package com.hafalquran.app.data.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameLatin: String,
    val nameTranslation: String,
    val numberOfAyahs: Int,
    val revelationType: String, // Makkiyah / Madaniyah
    val juzNumber: Int = 1
)

data class Ayah(
    val id: Long = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val textArabic: String,
    val textLatin: String = "",
    val translationId: String,
    val audioUrl: String,
    val localAudioPath: String? = null,
    val isMemorized: Boolean = false,
    val memorizationLevel: MemorizeLevel = MemorizeLevel.NOT_STARTED,
    val repeatCount: Int = 0
)

enum class MemorizeLevel {
    NOT_STARTED, // Belum mulai
    LEARNING,    // Sedang menghafal
    NEED_REVIEW, // Perlu Muroja'ah
    MUTQIN       // Lancar / Mutqin
}

data class Reciter(
    val id: String,
    val name: String,
    val style: String,
    val baseUrl: String
)

data class MemorizeConfig(
    val repeatPerAyah: Int = 3,            // 1x, 3x, 5x, 10x, 20x, 0 (looping tak hingga per ayat)
    val isLoopEntireSet: Boolean = true,   // Ulangi dari ayat awal ke akhir terus menerus sampai distop manual
    val pauseSecondsBetweenAyahs: Float = 0.5f, // Jeda silent audio untuk menirukan (default 0.5 detik)
    val startAyah: Int = 1,
    val endAyah: Int = 7,
    val isAutoNextAyah: Boolean = true,
    val selectedReciterId: String = "mishary_alafasy"
)

data class TargetTask(
    val id: Long = 0,
    val title: String,
    val surahNumber: Int,
    val surahName: String,
    val startAyah: Int,
    val endAyah: Int,
    val repeatPerAyah: Int = 3,
    val isLoopEntireSet: Boolean = true, // Ulangi seluruh rentang ayat tanpa henti
    val pauseSeconds: Float = 0.5f,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class MemorizationStats(
    val totalSurahs: Int = 114,
    val completedSurahs: Int = 0,
    val totalAyahs: Int = 6236,
    val mutqinAyahs: Int = 0,
    val learningAyahs: Int = 0,
    val needReviewAyahs: Int = 0,
    val notStartedAyahs: Int = 0,
    val juz30MutqinAyahs: Int = 0,
    val juz30TotalAyahs: Int = 564,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0
)
