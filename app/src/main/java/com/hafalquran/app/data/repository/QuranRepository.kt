package com.hafalquran.app.data.repository

import com.hafalquran.app.data.local.*
import com.hafalquran.app.data.model.*
import com.hafalquran.app.data.remote.FirestoreSyncManager
import com.hafalquran.app.data.remote.QuranApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class QuranRepository(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val targetTaskDao: TargetTaskDao,
    private val murojaahDao: MurojaahDao,
    private val apiService: QuranApiService = QuranApiService.create(),
    private val firestoreSync: FirestoreSyncManager? = null
) {

    val allSurahs: Flow<List<Surah>> = surahDao.getAllSurahs().map { list ->
        if (list.isEmpty()) {
            getAll114Surahs()
        } else {
            list.map { it.toDomain() }
        }
    }

    val allTargetTasks: Flow<List<TargetTask>> = targetTaskDao.getAllTasks().map { list ->
        list.map { it.toDomain() }
    }

    val statsFlow: Flow<MemorizationStats> = combine(
        ayahDao.getCountByLevel(MemorizeLevel.MUTQIN.name),
        ayahDao.getCountByLevel(MemorizeLevel.LEARNING.name),
        ayahDao.getCountByLevel(MemorizeLevel.NEED_REVIEW.name),
        ayahDao.getJuz30MutqinCount(),
        targetTaskDao.getTotalTasksCount(),
        targetTaskDao.getCompletedTasksCount()
    ) { args: Array<Any> ->
        val mutqin = args[0] as Int
        val learning = args[1] as Int
        val needReview = args[2] as Int
        val juz30Mutqin = args[3] as Int
        val totalTasks = args[4] as Int
        val completedTasks = args[5] as Int
        MemorizationStats(
            totalSurahs = 114,
            completedSurahs = 0,
            totalAyahs = 6236,
            mutqinAyahs = mutqin,
            learningAyahs = learning,
            needReviewAyahs = needReview,
            notStartedAyahs = (6236 - mutqin - learning - needReview).coerceAtLeast(0),
            juz30MutqinAyahs = juz30Mutqin,
            juz30TotalAyahs = 564,
            totalTasks = totalTasks,
            completedTasks = completedTasks
        )
    }

    val recentMurojaahHistory: Flow<List<MurojaahHistoryEntity>> = murojaahDao.getRecentHistory()

    fun getAyahs(surahNumber: Int): Flow<List<Ayah>> =
        ayahDao.getAyahsBySurah(surahNumber).map { list ->
            list.map { it.toDomain() }
        }

    suspend fun getAyahsRange(surahNumber: Int, start: Int, end: Int): List<Ayah> = withContext(Dispatchers.IO) {
        val expectedCount = end - start + 1
        var list = ayahDao.getAyahsRange(surahNumber, start, end)

        // Jika jumlah ayat di database lokal kurang dari yang diminta, otomatis sinkronkan dari API
        if (list.size < expectedCount) {
            syncSurahAyahs(surahNumber, start, end)
            list = ayahDao.getAyahsRange(surahNumber, start, end)
        }

        // Fallback jika offline dan belum terunduh
        if (list.isEmpty()) {
            if (surahNumber == 1) {
                getPreloadedAlFatihah().filter { it.ayahNumber in start..end }
            } else if (surahNumber == 112) {
                getPreloadedAlIkhlas().filter { it.ayahNumber in start..end }
            } else {
                emptyList()
            }
        } else {
            list.map { it.toDomain() }
        }
    }

    suspend fun hasAyahs(surahNumber: Int): Boolean = withContext(Dispatchers.IO) {
        ayahDao.getAyahsCountForSurah(surahNumber) > 0
    }

    suspend fun syncSurahAyahs(surahNumber: Int, startAyah: Int = 1, endAyah: Int? = null): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSurahWithAyahs(surahNumber)
            if (response.code != 200 || response.data.isEmpty()) {
                return@withContext Result.failure(Exception("Gagal mengambil data dari API (${response.status})"))
            }

            val editions = response.data
            val uthmaniEdition = editions.find { it.edition.identifier.contains("uthmani") } ?: editions[0]
            val translationEdition = editions.find { it.edition.identifier.contains("id.") || it.edition.language == "id" }
            val audioEdition = editions.find { it.edition.identifier.contains("alafasy") || it.edition.type == "versebyverse" }

            val totalAvailable = uthmaniEdition.ayahs.size
            val effectiveEnd = (endAyah ?: totalAvailable).coerceIn(startAyah, totalAvailable)

            val ayahEntities = mutableListOf<AyahEntity>()

            for (i in 0 until totalAvailable) {
                val ayahNumberInSurah = i + 1
                if (ayahNumberInSurah in startAyah..effectiveEnd) {
                    val arabText = uthmaniEdition.ayahs.getOrNull(i)?.text ?: ""
                    val translationText = translationEdition?.ayahs?.getOrNull(i)?.text ?: "Terjemahan ayat ke-$ayahNumberInSurah"
                    val audioUrl = audioEdition?.ayahs?.getOrNull(i)?.audio
                        ?: "https://cdn.islamic.network/quran/audio/128/ar.alafasy/${uthmaniEdition.ayahs.getOrNull(i)?.numberInQuran ?: (surahNumber * 100 + ayahNumberInSurah)}.mp3"

                    ayahEntities.add(
                        AyahEntity(
                            surahNumber = surahNumber,
                            ayahNumber = ayahNumberInSurah,
                            textArabic = arabText,
                            textLatin = "",
                            translationId = translationText,
                            audioUrl = audioUrl,
                            isMemorized = false,
                            memorizationLevel = MemorizeLevel.NOT_STARTED.name
                        )
                    )
                }
            }

            ayahDao.insertAyahs(ayahEntities)
            Result.success(ayahEntities.size)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback for Al-Fatihah offline
            if (surahNumber == 1) {
                val preloaded = getPreloadedAlFatihah()
                ayahDao.insertAyahs(preloaded.map { it.toEntity() })
                Result.success(preloaded.size)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun addTargetTask(task: TargetTask): Long = withContext(Dispatchers.IO) {
        val id = targetTaskDao.insertTask(task.toEntity())
        val taskWithId = task.copy(id = id)
        firestoreSync?.syncTargetTask(taskWithId)
        id
    }

    suspend fun updateTargetTask(task: TargetTask) = withContext(Dispatchers.IO) {
        targetTaskDao.updateTask(task.toEntity())
        firestoreSync?.syncTargetTask(task)
    }

    suspend fun updateTaskStatus(id: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        targetTaskDao.updateTaskStatus(id, isCompleted)
        val current = targetTaskDao.getTaskById(id)
        if (current != null) {
            firestoreSync?.syncTargetTask(current.toDomain())
        }
    }

    suspend fun deleteTask(id: Long) = withContext(Dispatchers.IO) {
        targetTaskDao.deleteTask(id)
        firestoreSync?.deleteTargetTask(id)
    }

    suspend fun updateMemorization(surahNumber: Int, ayahNumber: Int, level: MemorizeLevel) = withContext(Dispatchers.IO) {
        val isMemorized = (level == MemorizeLevel.MUTQIN)
        ayahDao.updateMemorizationStatus(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            level = level.name,
            isMemorized = isMemorized,
            timestamp = System.currentTimeMillis()
        )
        firestoreSync?.syncAyahProgress(surahNumber, ayahNumber, level)
    }

    suspend fun logMurojaah(surahNumber: Int, startAyah: Int, endAyah: Int, count: Int) = withContext(Dispatchers.IO) {
        murojaahDao.insertHistory(
            MurojaahHistoryEntity(
                surahNumber = surahNumber,
                startAyah = startAyah,
                endAyah = endAyah,
                totalRepetitions = count
            )
        )
    }

    suspend fun initDefaultDataIfNeeded() = withContext(Dispatchers.IO) {
        val staticList = getAll114Surahs()
        surahDao.insertSurahs(staticList.map { it.toEntity() })

        // Preload seluruh 7 ayat Al-Fatihah dan Al-Ikhlas
        if (ayahDao.getAyahsCountForSurah(1) < 7) {
            ayahDao.insertAyahs(getPreloadedAlFatihah().map { it.toEntity() })
        }
        if (ayahDao.getAyahsCountForSurah(112) < 4) {
            ayahDao.insertAyahs(getPreloadedAlIkhlas().map { it.toEntity() })
        }
    }

    private fun getPreloadedAlFatihah(): List<Ayah> = listOf(
        Ayah(1, 1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Bismillāhir-raḥmānir-raḥīm", "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang.", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3"),
        Ayah(2, 1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Al-ḥamdu lillāhi rabbil-'ālamīn", "Segala puji bagi Allah, Tuhan seluruh alam.", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/2.mp3"),
        Ayah(3, 1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "Ar-raḥmānir-raḥīm", "Yang Maha Pengasih, Maha Penyayang.", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/3.mp3"),
        Ayah(4, 1, 4, "مَالِكِ يَوْمِ الدِّينِ", "Māliki yaumid-dīn", "Pemilik hari pembalasan.", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/4.mp3"),
        Ayah(5, 1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Iyyāka na'budu wa iyyāka nasta'īn", "Hanya kepada Engkaulah kami menyembah dan hanya kepada Engkaulah kami memohon pertolongan.", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/5.mp3"),
        Ayah(6, 1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Ihdinaṣ-ṣirāṭal-mustaqīm", "Tunjukilah kami jalan yang lurus,", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6.mp3"),
        Ayah(7, 1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "Ṣirāṭallażīna an'amta 'alaihim gairil-magḍūbi 'alaihim wa laḍ-ḍāllīn", "(yaitu) jalan orang-orang yang telah Engkau beri nikmat kepadanya; bukan (jalan) mereka yang dimurkai, dan bukan (pula jalan) mereka yang sesat.", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/7.mp3")
    )

    private fun getPreloadedAlIkhlas(): List<Ayah> = listOf(
        Ayah(1, 112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Qul huwallāhu aḥad", "Katakanlah (Muhammad), \"Dialah Allah, Yang Maha Esa.\"", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6222.mp3"),
        Ayah(2, 112, 2, "اللَّهُ الصَّمَدُ", "Allāhuṣ-ṣamad", "Allah tempat meminta segala sesuatu.", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6223.mp3"),
        Ayah(3, 112, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Lam yalid wa lam yūlad", "(Allah) tidak beranak dan tidak pula diperanakkan,", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6224.mp3"),
        Ayah(4, 112, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Wa lam yakul lahū kufuwan aḥad", "dan tidak ada sesuatu yang setara dengan Dia.", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6225.mp3")
    )

    fun getAll114Surahs(): List<Surah> {
        return listOf(
            Surah(1, "الفاتحة", "Al-Fatihah", "Pembukaan", 7, "Makkiyah", 1),
            Surah(2, "البقرة", "Al-Baqarah", "Sapi Betina", 286, "Madaniyah", 1),
            Surah(3, "آل عمران", "Ali 'Imran", "Keluarga Imran", 200, "Madaniyah", 3),
            Surah(4, "النساء", "An-Nisa'", "Wanita", 176, "Madaniyah", 4),
            Surah(5, "المائدة", "Al-Ma'idah", "Hidangan", 120, "Madaniyah", 6),
            Surah(6, "الأنعام", "Al-An'am", "Binatang Ternak", 165, "Makkiyah", 7),
            Surah(7, "الأعراف", "Al-A'raf", "Tempat Tertinggi", 206, "Makkiyah", 8),
            Surah(8, "الأنفال", "Al-Anfal", "Rampasan Perang", 75, "Madaniyah", 9),
            Surah(9, "التوبة", "At-Taubah", "Pengampunan", 129, "Madaniyah", 10),
            Surah(10, "يونس", "Yunus", "Nabi Yunus", 109, "Makkiyah", 11),
            Surah(11, "هود", "Hud", "Nabi Hud", 123, "Makkiyah", 11),
            Surah(12, "يوسف", "Yusuf", "Nabi Yusuf", 111, "Makkiyah", 12),
            Surah(13, "الرعد", "Ar-Ra'd", "Guruh", 43, "Madaniyah", 13),
            Surah(14, "ابراهيم", "Ibrahim", "Nabi Ibrahim", 52, "Makkiyah", 13),
            Surah(15, "الحجر", "Al-Hijr", "Gunung Al-Hijr", 99, "Makkiyah", 14),
            Surah(16, "النحل", "An-Nahl", "Lebah", 128, "Makkiyah", 14),
            Surah(17, "الإسراء", "Al-Isra'", "Perjalanan Malam", 111, "Makkiyah", 15),
            Surah(18, "الكهف", "Al-Kahf", "Gua", 110, "Makkiyah", 15),
            Surah(19, "مريم", "Maryam", "Siti Maryam", 98, "Makkiyah", 16),
            Surah(20, "طه", "Taha", "Tha-Ha", 135, "Makkiyah", 16),
            Surah(21, "الأنبياء", "Al-Anbiya'", "Para Nabi", 112, "Makkiyah", 17),
            Surah(22, "الحج", "Al-Hajj", "Haji", 78, "Madaniyah", 17),
            Surah(23, "المؤمنون", "Al-Mu'minun", "Orang-Orang Mukmin", 118, "Makkiyah", 18),
            Surah(24, "النور", "An-Nur", "Cahaya", 64, "Madaniyah", 18),
            Surah(25, "الفرقان", "Al-Furqan", "Pembeda", 77, "Makkiyah", 18),
            Surah(26, "الشعراء", "Ash-Shu'ara'", "Penyair", 227, "Makkiyah", 19),
            Surah(27, "النمل", "An-Naml", "Semut", 93, "Makkiyah", 19),
            Surah(28, "القصص", "Al-Qasas", "Kisah-Kisah", 88, "Makkiyah", 20),
            Surah(29, "العنكبوت", "Al-'Ankabut", "Laba-Laba", 69, "Makkiyah", 20),
            Surah(30, "الروم", "Ar-Rum", "Bangsa Romawi", 60, "Makkiyah", 21),
            Surah(31, "لقمان", "Luqman", "Keluarga Luqman", 34, "Makkiyah", 21),
            Surah(32, "السجدة", "As-Sajdah", "Sujud", 30, "Makkiyah", 21),
            Surah(33, "الأحزاب", "Al-Ahzab", "Golongan Bersekutu", 73, "Madaniyah", 21),
            Surah(34, "سبإ", "Saba'", "Kaum Saba'", 54, "Makkiyah", 22),
            Surah(35, "فاطر", "Fatir", "Pencipta", 45, "Makkiyah", 22),
            Surah(36, "يس", "Ya-Sin", "Yaasiin", 83, "Makkiyah", 22),
            Surah(37, "الصافات", "As-Saffat", "Barisan-Barisan", 182, "Makkiyah", 23),
            Surah(38, "ص", "Sad", "Shaad", 88, "Makkiyah", 23),
            Surah(39, "الزمر", "Az-Zumar", "Rombongan-Rombongan", 75, "Makkiyah", 23),
            Surah(40, "غافر", "Ghafir", "Maha Pengampun", 85, "Makkiyah", 24),
            Surah(41, "فصلت", "Fussilat", "Yang Dijelaskan", 54, "Makkiyah", 24),
            Surah(42, "الشورى", "Ash-Shura", "Musyawarah", 53, "Makkiyah", 25),
            Surah(43, "الزخرف", "Az-Zukhruf", "Perhiasan", 89, "Makkiyah", 25),
            Surah(44, "الدخان", "Ad-Dukhan", "Kabut / Asap", 59, "Makkiyah", 25),
            Surah(45, "الجاثية", "Al-Jasiyah", "Yang Berlutut", 37, "Makkiyah", 25),
            Surah(46, "الأحقاف", "Al-Ahqaf", "Bukit-Bukit Pasir", 35, "Makkiyah", 26),
            Surah(47, "محمد", "Muhammad", "Nabi Muhammad", 38, "Madaniyah", 26),
            Surah(48, "الفتح", "Al-Fath", "Kemenangan", 29, "Madaniyah", 26),
            Surah(49, "الحجرات", "Al-Hujurat", "Kamar-Kamar", 18, "Madaniyah", 26),
            Surah(50, "ق", "Qaf", "Qaaf", 45, "Makkiyah", 26),
            Surah(51, "الذاريات", "Az-Zariyat", "Angin Menerbangkan", 60, "Makkiyah", 26),
            Surah(52, "الطور", "At-Tur", "Bukit Tursina", 49, "Makkiyah", 27),
            Surah(53, "النجم", "An-Najm", "Bintang", 62, "Makkiyah", 27),
            Surah(54, "القمر", "Al-Qamar", "Bulan", 55, "Makkiyah", 27),
            Surah(55, "الرحمن", "Ar-Rahman", "Maha Pemurah", 78, "Madaniyah", 27),
            Surah(56, "الواقعة", "Al-Waqi'ah", "Hari Kiamat", 96, "Makkiyah", 27),
            Surah(57, "الحديد", "Al-Hadid", "Besi", 29, "Madaniyah", 27),
            Surah(58, "المجادلة", "Al-Mujadilah", "Gugatan", 22, "Madaniyah", 28),
            Surah(59, "الحشر", "Al-Hasyr", "Pengusiran", 24, "Madaniyah", 28),
            Surah(60, "الممتحنة", "Al-Mumtahanah", "Wanita Yang Diuji", 13, "Madaniyah", 28),
            Surah(61, "الصف", "As-Saff", "Barisan", 14, "Madaniyah", 28),
            Surah(62, "الجمعة", "Al-Jumu'ah", "Hari Jumat", 11, "Madaniyah", 28),
            Surah(63, "المنافقون", "Al-Munafiqun", "Orang-Orang Munafik", 11, "Madaniyah", 28),
            Surah(64, "التغابن", "At-Taghabun", "Hari Dinampakkan Kesalahan", 18, "Madaniyah", 28),
            Surah(65, "الطلاق", "At-Talaq", "Talak / Perceraian", 12, "Madaniyah", 28),
            Surah(66, "التحريم", "At-Tahrim", "Pengharaman", 12, "Madaniyah", 28),
            Surah(67, "الملك", "Al-Mulk", "Kerajaan", 30, "Makkiyah", 29),
            Surah(68, "القلم", "Al-Qalam", "Pena", 52, "Makkiyah", 29),
            Surah(69, "الحاقة", "Al-Haqqah", "Hari Kiamat Pasti", 52, "Makkiyah", 29),
            Surah(70, "المعارج", "Al-Ma'arij", "Tempat Naik", 44, "Makkiyah", 29),
            Surah(71, "نوح", "Nuh", "Nabi Nuh", 28, "Makkiyah", 29),
            Surah(72, "الجن", "Al-Jinn", "Bangsa Jin", 28, "Makkiyah", 29),
            Surah(73, "المزمل", "Al-Muzzammil", "Orang Berselimut", 20, "Makkiyah", 29),
            Surah(74, "المدثر", "Al-Muddassir", "Orang Berkemul", 56, "Makkiyah", 29),
            Surah(75, "القيامة", "Al-Qiyamah", "Hari Kiamat", 40, "Makkiyah", 29),
            Surah(76, "الإنسان", "Al-Insan", "Manusia", 31, "Madaniyah", 29),
            Surah(77, "المرسلات", "Al-Mursalat", "Malaikat Diutus", 50, "Makkiyah", 29),
            Surah(78, "النبأ", "An-Naba'", "Berita Besar", 40, "Makkiyah", 30),
            Surah(79, "النازعات", "An-Nazi'at", "Malaikat Mencabut", 46, "Makkiyah", 30),
            Surah(80, "عبس", "'Abasa", "Bermuka Masam", 42, "Makkiyah", 30),
            Surah(81, "التكوير", "At-Takwir", "Menggulung", 29, "Makkiyah", 30),
            Surah(82, "الانفطار", "Al-Infitar", "Terbelah", 19, "Makkiyah", 30),
            Surah(83, "المطففين", "Al-Mutaffifin", "Orang Curang", 36, "Makkiyah", 30),
            Surah(84, "الانشقاق", "Al-Inshiqaq", "Terbelah", 25, "Makkiyah", 30),
            Surah(85, "البروج", "Al-Buruj", "Gugusan Bintang", 22, "Makkiyah", 30),
            Surah(86, "الطارق", "At-Tariq", "Datang di Malam Hari", 17, "Makkiyah", 30),
            Surah(87, "الأعلى", "Al-A'la", "Maha Tinggi", 19, "Makkiyah", 30),
            Surah(88, "الغاشية", "Al-Ghashiyah", "Hari Pembalasan", 26, "Makkiyah", 30),
            Surah(89, "الفجر", "Al-Fajr", "Fajar", 30, "Makkiyah", 30),
            Surah(90, "البلد", "Al-Balad", "Negeri", 20, "Makkiyah", 30),
            Surah(91, "الشمس", "Ash-Shams", "Matahari", 15, "Makkiyah", 30),
            Surah(92, "الليل", "Al-Lail", "Malam", 21, "Makkiyah", 30),
            Surah(93, "الضحى", "Ad-Duha", "Waktu Dhuha", 11, "Makkiyah", 30),
            Surah(94, "الشرح", "Ash-Sharh", "Kelapangan", 8, "Makkiyah", 30),
            Surah(95, "التين", "At-Tin", "Buah Tin", 8, "Makkiyah", 30),
            Surah(96, "العلق", "Al-'Alaq", "Segumpal Darah", 19, "Makkiyah", 30),
            Surah(97, "القدر", "Al-Qadr", "Kemuliaan", 5, "Makkiyah", 30),
            Surah(98, "البينة", "Al-Bayyinah", "Bukti Nyata", 8, "Madaniyah", 30),
            Surah(99, "الزلزلة", "Az-Zalzalah", "Kegoncangan", 8, "Madaniyah", 30),
            Surah(100, "العاديات", "Al-'Adiyat", "Kuda Berlari Kencang", 11, "Makkiyah", 30),
            Surah(101, "القارعة", "Al-Qari'ah", "Hari Kiamat", 11, "Makkiyah", 30),
            Surah(102, "التكاثر", "At-Takathur", "Bermegah-megahan", 8, "Makkiyah", 30),
            Surah(103, "العصر", "Al-'Asr", "Masa / Waktu", 3, "Makkiyah", 30),
            Surah(104, "الهمزة", "Al-Humazah", "Pengumpat", 9, "Makkiyah", 30),
            Surah(105, "الفيل", "Al-Fil", "Gajah", 5, "Makkiyah", 30),
            Surah(106, "قريش", "Quraysh", "Suku Quraisy", 4, "Makkiyah", 30),
            Surah(107, "الماعون", "Al-Ma'un", "Barang Berguna", 7, "Makkiyah", 30),
            Surah(108, "الكوثر", "Al-Kawthar", "Nikmat Berlimpah", 3, "Makkiyah", 30),
            Surah(109, "الكافرون", "Al-Kafirun", "Orang-Orang Kafir", 6, "Makkiyah", 30),
            Surah(110, "النصر", "An-Nasr", "Pertolongan", 3, "Madaniyah", 30),
            Surah(111, "المسد", "Al-Masad", "Gejolak Api / Sabut", 5, "Makkiyah", 30),
            Surah(112, "الإخلاص", "Al-Ikhlas", "Kemurnian Keesaan Allah", 4, "Makkiyah", 30),
            Surah(113, "الفلق", "Al-Falaq", "Waktu Subuh", 5, "Makkiyah", 30),
            Surah(114, "الناس", "An-Nas", "Manusia", 6, "Makkiyah", 30)
        )
    }

    private fun SurahEntity.toDomain() = Surah(
        number = number,
        nameArabic = nameArabic,
        nameLatin = nameLatin,
        nameTranslation = nameTranslation,
        numberOfAyahs = numberOfAyahs,
        revelationType = revelationType,
        juzNumber = juzNumber
    )

    private fun Surah.toEntity() = SurahEntity(
        number = number,
        nameArabic = nameArabic,
        nameLatin = nameLatin,
        nameTranslation = nameTranslation,
        numberOfAyahs = numberOfAyahs,
        revelationType = revelationType,
        juzNumber = juzNumber
    )

    private fun AyahEntity.toDomain() = Ayah(
        id = ((surahNumber.toLong() shl 16) or ayahNumber.toLong()),
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        textArabic = textArabic,
        textLatin = textLatin,
        translationId = translationId,
        audioUrl = audioUrl,
        localAudioPath = localAudioPath,
        isMemorized = isMemorized,
        memorizationLevel = try { MemorizeLevel.valueOf(memorizationLevel) } catch (e: Exception) { MemorizeLevel.NOT_STARTED },
        repeatCount = reviewCount
    )

    private fun Ayah.toEntity() = AyahEntity(
        surahNumber = surahNumber,
        ayahNumber = ayahNumber,
        textArabic = textArabic,
        textLatin = textLatin,
        translationId = translationId,
        audioUrl = audioUrl,
        localAudioPath = localAudioPath,
        isMemorized = isMemorized,
        memorizationLevel = memorizationLevel.name,
        reviewCount = repeatCount
    )

    private fun TargetTaskEntity.toDomain() = TargetTask(
        id = id,
        title = title,
        surahNumber = surahNumber,
        surahName = surahName,
        startAyah = startAyah,
        endAyah = endAyah,
        repeatPerAyah = repeatPerAyah,
        isLoopEntireSet = isLoopEntireSet,
        pauseSeconds = pauseSeconds,
        isCompleted = isCompleted,
        createdAt = createdAt
    )

    private fun TargetTask.toEntity() = TargetTaskEntity(
        id = id,
        title = title,
        surahNumber = surahNumber,
        surahName = surahName,
        startAyah = startAyah,
        endAyah = endAyah,
        repeatPerAyah = repeatPerAyah,
        isLoopEntireSet = isLoopEntireSet,
        pauseSeconds = pauseSeconds,
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}
