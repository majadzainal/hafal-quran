package com.hafalquran.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.hafalquran.app.data.model.Ayah
import com.hafalquran.app.data.model.MemorizationStats
import com.hafalquran.app.data.model.MemorizeConfig
import com.hafalquran.app.data.model.Surah
import com.hafalquran.app.data.model.TargetTask
import com.hafalquran.app.data.remote.FirestoreSyncManager
import com.hafalquran.app.data.repository.QuranRepository
import com.hafalquran.app.player.QuranPlayerManager
import com.hafalquran.app.ui.components.PlayerMiniBottomBar
import com.hafalquran.app.ui.screens.AboutScreen
import com.hafalquran.app.ui.screens.DonationScreen
import com.hafalquran.app.ui.screens.HomeScreen
import com.hafalquran.app.ui.screens.MemorizeScreen
import com.hafalquran.app.ui.screens.MyTargetScreen
import com.hafalquran.app.ui.screens.ProgressScreen
import com.hafalquran.app.ui.theme.EmeraldDark
import com.hafalquran.app.ui.theme.GoldAccent
import com.hafalquran.app.ui.theme.HafalQuranTheme
import com.hafalquran.app.ui.theme.QuranBgDark
import com.hafalquran.app.ui.theme.TextMuted
import com.hafalquran.app.ui.theme.TextWhite
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: QuranRepository
    private lateinit var playerManager: QuranPlayerManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize components
        val app = application as HafalQuranApp
        val syncManager = FirestoreSyncManager(this)
        repository = QuranRepository(
            surahDao = app.database.surahDao(),
            ayahDao = app.database.ayahDao(),
            targetTaskDao = app.database.targetTaskDao(),
            murojaahDao = app.database.murojaahDao(),
            firestoreSync = syncManager
        )
        playerManager = QuranPlayerManager(this)

        // Request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            HafalQuranTheme {
                MainAppScreen(
                    repository = repository,
                    playerManager = playerManager
                )
            }
        }
    }

    override fun onDestroy() {
        playerManager.release()
        super.onDestroy()
    }
}

enum class NavigationTab {
    QURAN,
    MY_TARGET,
    PROGRESS,
    DONATION,
    ABOUT
}

@Composable
fun MainAppScreen(
    repository: QuranRepository,
    playerManager: QuranPlayerManager
) {
    var selectedTab by remember { mutableStateOf(NavigationTab.QURAN) }
    var selectedSurahForMemorize by remember { mutableStateOf<Surah?>(null) }
    val scope = rememberCoroutineScope()

    val surahs by repository.allSurahs.collectAsState(initial = emptyList())
    val targetTasks by repository.allTargetTasks.collectAsState(initial = emptyList())
    val stats by repository.statsFlow.collectAsState(initial = MemorizationStats())
    val recentHistory by repository.recentMurojaahHistory.collectAsState(initial = emptyList())
    val playerState by playerManager.uiState.collectAsState()

    // Initialize default database data
    LaunchedEffect(Unit) {
        repository.initDefaultDataIfNeeded()
    }

    Scaffold(
        bottomBar = {
            Column {
                // Mini Player Floating Bar if an ayah is loaded
                if (playerState.currentAyah != null && selectedSurahForMemorize == null) {
                    PlayerMiniBottomBar(
                        playerState = playerState,
                        onPlayPause = { playerManager.togglePlayPause() },
                        onNext = { playerManager.nextAyah() },
                        onPrev = { playerManager.previousAyah() },
                        onOpenDetail = {
                            val targetSurah = surahs.find { it.number == playerState.currentSurahNumber }
                            if (targetSurah != null) {
                                selectedSurahForMemorize = targetSurah
                            }
                        }
                    )
                }

                // Bottom Navigation Bar
                NavigationBar(
                    containerColor = EmeraldDark,
                    contentColor = TextWhite
                ) {
                    NavigationBarItem(
                        selected = selectedTab == NavigationTab.QURAN && selectedSurahForMemorize == null,
                        onClick = {
                            selectedTab = NavigationTab.QURAN
                            selectedSurahForMemorize = null
                        },
                        icon = { Icon(Icons.Default.Book, contentDescription = "Al-Quran") },
                        label = { Text("Al-Quran") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = QuranBgDark,
                            selectedTextColor = GoldAccent,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == NavigationTab.MY_TARGET && selectedSurahForMemorize == null,
                        onClick = {
                            selectedTab = NavigationTab.MY_TARGET
                            selectedSurahForMemorize = null
                        },
                        icon = { Icon(Icons.Default.TrackChanges, contentDescription = "My Target") },
                        label = { Text("My Target") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = QuranBgDark,
                            selectedTextColor = GoldAccent,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == NavigationTab.PROGRESS && selectedSurahForMemorize == null,
                        onClick = {
                            selectedTab = NavigationTab.PROGRESS
                            selectedSurahForMemorize = null
                        },
                        icon = { Icon(Icons.Default.AutoGraph, contentDescription = "Statistik") },
                        label = { Text("Statistik") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = QuranBgDark,
                            selectedTextColor = GoldAccent,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == NavigationTab.DONATION && selectedSurahForMemorize == null,
                        onClick = {
                            selectedTab = NavigationTab.DONATION
                            selectedSurahForMemorize = null
                        },
                        icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = "Donasi") },
                        label = { Text("Donasi") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = QuranBgDark,
                            selectedTextColor = GoldAccent,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val activeSurah = selectedSurahForMemorize
            if (activeSurah != null) {
                val ayahs by repository.getAyahs(activeSurah.number).collectAsState(initial = emptyList())

                MemorizeScreen(
                    surah = activeSurah,
                    ayahs = ayahs,
                    playerState = playerState,
                    onBackClick = { selectedSurahForMemorize = null },
                    onPlayAyah = { ayah, config ->
                        val targetIndex = ayahs.indexOfFirst { it.ayahNumber == ayah.ayahNumber }
                        playerManager.playAyahs(
                            surahNumber = activeSurah.number,
                            surahName = activeSurah.nameLatin,
                            ayahs = ayahs,
                            startIndex = if (targetIndex >= 0) targetIndex else 0,
                            config = config
                        )
                        scope.launch {
                            repository.logMurojaah(activeSurah.number, ayah.ayahNumber, ayah.ayahNumber, config.repeatPerAyah)
                        }
                    },
                    onPlayAll = { config ->
                        playerManager.playAyahs(
                            surahNumber = activeSurah.number,
                            surahName = activeSurah.nameLatin,
                            ayahs = ayahs,
                            startIndex = 0,
                            config = config
                        )
                        scope.launch {
                            repository.logMurojaah(activeSurah.number, config.startAyah, config.endAyah, config.repeatPerAyah)
                        }
                    },
                    onLevelChange = { ayah, newLevel ->
                        scope.launch {
                            repository.updateMemorization(activeSurah.number, ayah.ayahNumber, newLevel)
                        }
                    },
                    onRepeatCountChange = { count ->
                        playerManager.setRepeatCount(count)
                    },
                    onPauseSecondsChange = { sec ->
                        playerManager.setPauseSeconds(sec)
                    },
                    onSyncAyahs = { start, end ->
                        repository.syncSurahAyahs(activeSurah.number, start, end)
                    }
                )
            } else {
                when (selectedTab) {
                    NavigationTab.QURAN -> {
                        HomeScreen(
                            surahs = surahs,
                            onSurahClick = { surah ->
                                selectedSurahForMemorize = surah
                            },
                            onQuickMurojaahClick = {
                                selectedTab = NavigationTab.MY_TARGET
                            },
                            onAboutClick = {
                                selectedTab = NavigationTab.ABOUT
                            }
                        )
                    }
                    NavigationTab.MY_TARGET -> {
                        MyTargetScreen(
                            tasks = targetTasks,
                            surahs = surahs,
                            playerState = playerState,
                            onPlayTarget = { task ->
                                scope.launch {
                                    var targetAyahs = repository.getAyahsRange(task.surahNumber, task.startAyah, task.endAyah)
                                    if (targetAyahs.isEmpty()) {
                                        repository.syncSurahAyahs(task.surahNumber, task.startAyah, task.endAyah)
                                        targetAyahs = repository.getAyahsRange(task.surahNumber, task.startAyah, task.endAyah)
                                    }

                                    if (targetAyahs.isNotEmpty()) {
                                        val config = MemorizeConfig(
                                            repeatPerAyah = task.repeatPerAyah,
                                            isLoopEntireSet = task.isLoopEntireSet,
                                            pauseSecondsBetweenAyahs = task.pauseSeconds,
                                            startAyah = task.startAyah,
                                            endAyah = task.endAyah
                                        )
                                        playerManager.playAyahs(
                                            surahNumber = task.surahNumber,
                                            surahName = task.surahName,
                                            ayahs = targetAyahs,
                                            startIndex = 0,
                                            config = config
                                        )
                                        repository.logMurojaah(task.surahNumber, task.startAyah, task.endAyah, task.repeatPerAyah)
                                    }
                                }
                            },
                            onToggleTaskStatus = { task ->
                                scope.launch {
                                    repository.updateTaskStatus(task.id, !task.isCompleted)
                                }
                            },
                            onDeleteTask = { task ->
                                scope.launch {
                                    repository.deleteTask(task.id)
                                }
                            },
                            onAddNewTask = { newTask ->
                                scope.launch {
                                    repository.addTargetTask(newTask)
                                    repository.syncSurahAyahs(newTask.surahNumber, newTask.startAyah, newTask.endAyah)
                                }
                            },
                            onEditTask = { updatedTask ->
                                scope.launch {
                                    repository.updateTargetTask(updatedTask)
                                    repository.syncSurahAyahs(updatedTask.surahNumber, updatedTask.startAyah, updatedTask.endAyah)
                                }
                            }
                        )
                    }
                    NavigationTab.PROGRESS -> {
                        ProgressScreen(
                            stats = stats,
                            recentHistory = recentHistory,
                            surahs = surahs
                        )
                    }
                    NavigationTab.DONATION -> {
                        DonationScreen()
                    }
                    NavigationTab.ABOUT -> {
                        AboutScreen()
                    }
                }
            }
        }
    }
}
