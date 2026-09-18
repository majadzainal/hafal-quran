package com.hafalquran.app.player

import android.content.ComponentName
import android.content.Context
import android.os.PowerManager
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.hafalquran.app.data.model.Ayah
import com.hafalquran.app.data.model.MemorizeConfig
import com.hafalquran.app.service.QuranAudioService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurahNumber: Int = 1,
    val currentSurahName: String = "Al-Fatihah",
    val currentAyahNumber: Int = 1,
    val currentAyah: Ayah? = null,
    val playlist: List<Ayah> = emptyList(),
    val currentIteration: Int = 1,
    val targetRepeatCount: Int = 1,       // 0 = infinite loop pada ayat yang sama
    val isLoopEntireSet: Boolean = true,  // Loop seluruh playlist tanpa henti
    val currentSetRound: Int = 1,         // Putaran ke-1, ke-2, ke-3 dari seluruh rentang ayat
    val pauseSeconds: Float = 0.5f,
    val isPausedForRepeat: Boolean = false,
    val isIntroPreamble: Boolean = false  // True saat sedang melantunkan Ta'awwudz / Basmalah
)

class QuranPlayerManager(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var controller: MediaController? = null

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
    private var wakeLock: PowerManager.WakeLock? = null
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? android.net.wifi.WifiManager
    private var wifiLock: android.net.wifi.WifiManager.WifiLock? = null

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var currentConfig = MemorizeConfig()
    private var currentPlaylist: List<Ayah> = emptyList()
    private var currentAyahIndex: Int = 0
    private var currentIterationCount: Int = 1
    private var currentSetRoundCount: Int = 1

    // Antrean pembuka tilawah (Ta'awwudz & Bismillah)
    private val introQueue = ArrayDeque<Ayah>()
    private var currentIntroAyah: Ayah? = null

    init {
        initializeMediaController()
    }

    private fun acquireWakeLock() {
        try {
            if (wakeLock == null) {
                wakeLock = powerManager?.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "HafalQuran:PlaybackWakeLock"
                )?.apply { setReferenceCounted(false) }
            }
            if (wakeLock?.isHeld == false) {
                wakeLock?.acquire()
            }
            if (wifiLock == null) {
                wifiLock = wifiManager?.createWifiLock(
                    android.net.wifi.WifiManager.WIFI_MODE_FULL_HIGH_PERF,
                    "HafalQuran:PlaybackWifiLock"
                )?.apply { setReferenceCounted(false) }
            }
            if (wifiLock?.isHeld == false) {
                wifiLock?.acquire()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
            if (wifiLock?.isHeld == true) {
                wifiLock?.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initializeMediaController() {
        val sessionToken = SessionToken(context, ComponentName(context, QuranAudioService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            controller = controllerFuture.get().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                _uiState.value = _uiState.value.copy(isLoading = true)
                            }
                            Player.STATE_READY -> {
                                _uiState.value = _uiState.value.copy(isLoading = false)
                            }
                            Player.STATE_ENDED -> {
                                handleAyahPlaybackEnded()
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        error.printStackTrace()
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        // Lanjutkan ke ayat berikutnya jika terjadi kegagalan jaringan agar audio tidak macet
                        scope.launch {
                            delay(1500L)
                            handleAyahPlaybackEnded()
                        }
                    }
                })
            }
        }, MoreExecutors.directExecutor())
    }

    fun playAyahs(
        surahNumber: Int,
        surahName: String,
        ayahs: List<Ayah>,
        startIndex: Int = 0,
        config: MemorizeConfig = currentConfig,
        playWithIntro: Boolean = (startIndex == 0)
    ) {
        if (ayahs.isEmpty()) return
        currentPlaylist = ayahs
        currentAyahIndex = startIndex.coerceIn(0, ayahs.size - 1)
        currentConfig = config
        currentIterationCount = 1
        currentSetRoundCount = 1
        introQueue.clear()
        currentIntroAyah = null

        val currentAyah = currentPlaylist[currentAyahIndex]
        _uiState.value = _uiState.value.copy(
            currentSurahNumber = surahNumber,
            currentSurahName = surahName,
            currentAyahNumber = currentAyah.ayahNumber,
            currentAyah = currentAyah,
            playlist = currentPlaylist,
            targetRepeatCount = config.repeatPerAyah,
            isLoopEntireSet = config.isLoopEntireSet,
            pauseSeconds = config.pauseSecondsBetweenAyahs,
            currentIteration = 1,
            currentSetRound = 1,
            isIntroPreamble = false
        )

        // Siapkan lantunan Ta'awwudz dan Bismillah saat pertama kali memulai putaran
        if (playWithIntro) {
            // 1. Ta'awwudz (أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ)
            introQueue.add(
                Ayah(
                    id = -1L,
                    surahNumber = surahNumber,
                    ayahNumber = 0,
                    textArabic = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ",
                    textLatin = "A'udzu billahi minasy-syaithanir-rajim",
                    translationId = "Aku berlindung kepada Allah dari godaan setan yang terkutuk",
                    audioUrl = "https://archive.org/download/Muhammad-Taha-Al-Junaid-Juzz30/Muhammad%20Taha%20Al-Junaid%20-%20000%20-%20Taawudz%20Basmalah.mp3"
                )
            )

            // 2. Bismillah (بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ)
            // Catatan Fiqih: Surah At-Taubah (9) tidak memakai Bismillah; Surah 1 (Al-Fatihah) ayat 1 sudah merupakan Bismillah.
            if (surahNumber != 9 && !(surahNumber == 1 && currentAyah.ayahNumber == 1)) {
                introQueue.add(
                    Ayah(
                        id = -2L,
                        surahNumber = surahNumber,
                        ayahNumber = 0,
                        textArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        textLatin = "Bismillahirrahmanirrahim",
                        translationId = "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang",
                        audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3"
                    )
                )
            }

            if (introQueue.isNotEmpty()) {
                currentIntroAyah = introQueue.removeFirst()
                playIntroAyah(currentIntroAyah!!)
                return
            }
        }

        playCurrentAyah()
    }

    private fun playIntroAyah(introAyah: Ayah) {
        val isTaawudz = introAyah.id == -1L
        val title = if (isTaawudz) "Pembuka: Ta'awwudz" else "Pembuka: Basmalah"

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist("${_uiState.value.currentSurahName} • Adab Tilawah")
            .setDisplayTitle(introAyah.textArabic)
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(introAyah.audioUrl)
            .setMediaMetadata(mediaMetadata)
            .build()

        acquireWakeLock()

        controller?.apply {
            stop()
            setMediaItem(mediaItem, /* resetPosition = */ true)
            prepare()
            play()
        }

        _uiState.value = _uiState.value.copy(
            currentAyahNumber = 0,
            currentAyah = introAyah,
            currentIteration = 1,
            isIntroPreamble = true,
            isPausedForRepeat = false
        )
    }

    private fun playCurrentAyah() {
        if (currentAyahIndex !in currentPlaylist.indices) return
        val ayah = currentPlaylist[currentAyahIndex]

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle("Ayat ${ayah.ayahNumber} - ${_uiState.value.currentSurahName}")
            .setArtist("Putaran $currentSetRoundCount | Ulangi $currentIterationCount/${if (currentConfig.repeatPerAyah == 0) "∞" else "${currentConfig.repeatPerAyah}x"}")
            .setDisplayTitle(ayah.textArabic)
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(ayah.audioUrl)
            .setMediaMetadata(mediaMetadata)
            .build()

        acquireWakeLock()

        controller?.apply {
            stop()
            setMediaItem(mediaItem, /* resetPosition = */ true)
            prepare()
            play()
        }

        _uiState.value = _uiState.value.copy(
            currentAyahNumber = ayah.ayahNumber,
            currentAyah = ayah,
            currentIteration = currentIterationCount,
            currentSetRound = currentSetRoundCount,
            isIntroPreamble = false,
            isPausedForRepeat = false
        )
    }

    private fun handleAyahPlaybackEnded() {
        // Jika sedang melantunkan pembuka (Ta'awwudz / Bismillah)
        if (currentIntroAyah != null) {
            if (introQueue.isNotEmpty()) {
                currentIntroAyah = introQueue.removeFirst()
                scope.launch {
                    delay(300L)
                    currentIntroAyah?.let { playIntroAyah(it) }
                }
            } else {
                // Selesai pembuka -> masuk ke ayat pertama dari hafalan target
                currentIntroAyah = null
                scope.launch {
                    delay(400L)
                    playCurrentAyah()
                }
            }
            return
        }

        val target = currentConfig.repeatPerAyah
        val pauseMs = (currentConfig.pauseSecondsBetweenAyahs * 1000L).toLong()

        // 1. Jika masih perlu mengulang ayat yang sama (misal ayat 1 diulang 3x)
        if (target == 0 || currentIterationCount < target) {
            currentIterationCount++
            acquireWakeLock()
            scope.launch {
                if (pauseMs > 0) {
                    _uiState.value = _uiState.value.copy(isPausedForRepeat = true)
                    delay(pauseMs)
                }
                playCurrentAyah()
            }
        } else {
            // 2. Ayat saat ini sudah selesai diulang sebanyak target (misal Ayat 1 selesai 1x atau 3x)
            if (currentConfig.isAutoNextAyah && currentAyahIndex < currentPlaylist.size - 1) {
                // Lanjut ke ayat berikutnya dalam rentang playlist (misal Ayat 1 -> Ayat 2 -> Ayat 3 ...)
                currentAyahIndex++
                currentIterationCount = 1
                acquireWakeLock()
                scope.launch {
                    if (pauseMs > 0) {
                        _uiState.value = _uiState.value.copy(isPausedForRepeat = true)
                        delay(pauseMs)
                    }
                    playCurrentAyah()
                }
            } else if (currentConfig.isLoopEntireSet && currentPlaylist.isNotEmpty()) {
                // 3. Seluruh rentang ayat dalam target (misal Ayat 1-7) telah selesai diputar
                // KEMBALI KE AYAT PERTAMA DAN ULANGI LAGI DARI AYAT 1 (PUTARAN SET BERIKUTNYA) TANPA HENTI!
                currentAyahIndex = 0
                currentIterationCount = 1
                currentSetRoundCount++
                acquireWakeLock()
                scope.launch {
                    if (pauseMs > 0) {
                        _uiState.value = _uiState.value.copy(isPausedForRepeat = true)
                        delay(pauseMs)
                    }
                    playCurrentAyah()
                }
            } else {
                // Selesai seluruh playlist (jika loop set dimatikan)
                _uiState.value = _uiState.value.copy(isPlaying = false, isPausedForRepeat = false)
                releaseWakeLock()
            }
        }
    }

    fun togglePlayPause() {
        controller?.let {
            if (it.isPlaying) {
                it.pause()
                releaseWakeLock()
            } else {
                if (it.playbackState == Player.STATE_ENDED) {
                    currentIterationCount = 1
                    if (currentIntroAyah != null) {
                        playIntroAyah(currentIntroAyah!!)
                    } else {
                        playCurrentAyah()
                    }
                } else {
                    acquireWakeLock()
                    it.play()
                }
            }
        }
    }

    fun nextAyah() {
        if (currentIntroAyah != null) {
            introQueue.clear()
            currentIntroAyah = null
            playCurrentAyah()
            return
        }

        if (currentAyahIndex < currentPlaylist.size - 1) {
            currentAyahIndex++
            currentIterationCount = 1
            playCurrentAyah()
        } else if (currentConfig.isLoopEntireSet && currentPlaylist.isNotEmpty()) {
            currentAyahIndex = 0
            currentIterationCount = 1
            currentSetRoundCount++
            playCurrentAyah()
        }
    }

    fun previousAyah() {
        if (currentIntroAyah != null) {
            introQueue.clear()
            currentIntroAyah = null
            playCurrentAyah()
            return
        }

        if (currentAyahIndex > 0) {
            currentAyahIndex--
            currentIterationCount = 1
            playCurrentAyah()
        }
    }

    fun setRepeatCount(count: Int) {
        currentConfig = currentConfig.copy(repeatPerAyah = count)
        _uiState.value = _uiState.value.copy(targetRepeatCount = count)
    }

    fun setLoopEntireSet(loop: Boolean) {
        currentConfig = currentConfig.copy(isLoopEntireSet = loop)
        _uiState.value = _uiState.value.copy(isLoopEntireSet = loop)
    }

    fun setPauseSeconds(seconds: Float) {
        currentConfig = currentConfig.copy(pauseSecondsBetweenAyahs = seconds)
        _uiState.value = _uiState.value.copy(pauseSeconds = seconds)
    }

    fun release() {
        releaseWakeLock()
        controller?.release()
        controller = null
    }
}
