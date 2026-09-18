package com.hafalquran.app.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hafalquran.app.data.model.Ayah
import com.hafalquran.app.data.model.MemorizeConfig
import com.hafalquran.app.data.model.MemorizeLevel
import com.hafalquran.app.data.model.Surah
import com.hafalquran.app.player.PlayerUiState
import com.hafalquran.app.ui.components.AyahItem
import com.hafalquran.app.ui.components.BackgroundAudioInfoDialog
import com.hafalquran.app.ui.theme.*
import kotlinx.coroutines.launch

private const val PREF_NAME = "hafal_quran_display_prefs"
private const val KEY_MUSHAF_MODE = "is_mushaf_mode"
private const val KEY_SHOW_LATIN = "show_latin"
private const val KEY_SHOW_TRANSLATION = "show_translation"
private const val KEY_SHOW_AUDIO_BUTTON = "show_audio_button"
private const val KEY_SHOW_REPEAT_PANEL = "show_repeat_panel"
private const val KEY_ARABIC_FONT_SIZE = "arabic_font_size"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorizeScreen(
    surah: Surah,
    ayahs: List<Ayah>,
    playerState: PlayerUiState,
    onBackClick: () -> Unit,
    onPlayAyah: (ayah: Ayah, config: MemorizeConfig) -> Unit,
    onPlayAll: (config: MemorizeConfig) -> Unit,
    onLevelChange: (Ayah, MemorizeLevel) -> Unit,
    onRepeatCountChange: (Int) -> Unit,
    onPauseSecondsChange: (Float) -> Unit,
    onSyncAyahs: suspend (startAyah: Int, endAyah: Int?) -> Result<Int>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    val scope = rememberCoroutineScope()

    // Preferences State
    var isMushafMode by remember { mutableStateOf(prefs.getBoolean(KEY_MUSHAF_MODE, false)) }
    var showLatin by remember { mutableStateOf(prefs.getBoolean(KEY_SHOW_LATIN, true)) }
    var showTranslation by remember { mutableStateOf(prefs.getBoolean(KEY_SHOW_TRANSLATION, true)) }
    var showAudioButton by remember { mutableStateOf(prefs.getBoolean(KEY_SHOW_AUDIO_BUTTON, true)) }
    var showRepeatPanel by remember { mutableStateOf(prefs.getBoolean(KEY_SHOW_REPEAT_PANEL, true)) }
    var arabicFontSize by remember { mutableStateOf(prefs.getFloat(KEY_ARABIC_FONT_SIZE, 28f)) }

    // Repetition state
    var selectedRepeat by remember { mutableStateOf(3) }
    var selectedPause by remember { mutableStateOf(0.5f) }
    var showSyncDialog by remember { mutableStateOf(false) }
    var showDisplaySettingsDialog by remember { mutableStateOf(false) }
    var showBatteryTipsDialog by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }

    val repeatOptions = listOf(1, 3, 5, 10, 0) // 0 = Loop tak terhingga
    val pauseOptions = listOf(0f, 0.5f, 1f, 2f, 3f, 5f)

    fun savePrefs(
        mushaf: Boolean = isMushafMode,
        latin: Boolean = showLatin,
        trans: Boolean = showTranslation,
        audio: Boolean = showAudioButton,
        repeatPanel: Boolean = showRepeatPanel,
        fontSize: Float = arabicFontSize
    ) {
        prefs.edit().apply {
            putBoolean(KEY_MUSHAF_MODE, mushaf)
            putBoolean(KEY_SHOW_LATIN, latin)
            putBoolean(KEY_SHOW_TRANSLATION, trans)
            putBoolean(KEY_SHOW_AUDIO_BUTTON, audio)
            putBoolean(KEY_SHOW_REPEAT_PANEL, repeatPanel)
            putFloat(KEY_ARABIC_FONT_SIZE, fontSize)
            apply()
        }
    }

    fun toggleMushafMode(active: Boolean) {
        isMushafMode = active
        if (active) {
            // Mode Baca Mushaf: Sembunyikan Latin, Terjemahan, Tombol Audio, perbesar font
            showLatin = false
            showTranslation = false
            showAudioButton = false
            showRepeatPanel = false
            if (arabicFontSize < 32f) arabicFontSize = 32f
        } else {
            // Mode Standar / Hafalan: Aktifkan kembali
            showLatin = true
            showTranslation = true
            showAudioButton = true
            showRepeatPanel = true
            if (arabicFontSize > 30f) arabicFontSize = 28f
        }
        savePrefs(
            mushaf = active,
            latin = showLatin,
            trans = showTranslation,
            audio = showAudioButton,
            repeatPanel = showRepeatPanel,
            fontSize = arabicFontSize
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuranBgDark)
    ) {
        // Top App Bar
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${surah.nameLatin} (${surah.nameArabic})",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "${surah.numberOfAyahs} Ayat • ${surah.revelationType}${if (isMushafMode) " • Mode Mushaf" else ""}",
                        color = GoldLight,
                        fontSize = 12.sp
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }
            },
            actions = {
                // Quick Display Settings
                IconButton(onClick = { showDisplaySettingsDialog = true }) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Pengaturan Tampilan",
                        tint = if (isMushafMode) GoldAccent else TextWhite
                    )
                }
                // Sync Ayat Button
                IconButton(onClick = { showSyncDialog = true }) {
                    Icon(Icons.Default.CloudDownload, contentDescription = "Sync Ayat", tint = GoldAccent)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = QuranSurfaceDark
            )
        )

        if (isSyncing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = GoldAccent,
                trackColor = QuranSurfaceDark
            )
        }

        if (ayahs.isEmpty() && !isSyncing) {
            // Empty state: Data belum disinkronkan
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "Data Ayat Belum Tersedia",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Ayat untuk ${surah.nameLatin} belum ada di database lokal Anda. Anda dapat menyinkronkan seluruh ayat atau memilih rentang ayat tertentu.",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = { showSyncDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = QuranBgDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sinkronkan Ayat Sekarang",
                                color = QuranBgDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(if (isMushafMode) 16.dp else 14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp)
            ) {
                // Quick Controls Header Bar (Mode Baca Mushaf & Atur Tampilan)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMushafMode) EmeraldDark.copy(alpha = 0.6f) else QuranSurfaceDark
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(if (isMushafMode) GoldAccent else QuranCardBorder)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Mushaf Mode Toggle Chip
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isMushafMode) GoldAccent else EmeraldContainer)
                                    .clickable { toggleMushafMode(!isMushafMode) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = null,
                                    tint = if (isMushafMode) QuranBgDark else GoldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isMushafMode) "Mode Mushaf: AKTIF" else "Mode Baca Mushaf",
                                    color = if (isMushafMode) QuranBgDark else TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            // Atur Tampilan Button
                            OutlinedButton(
                                onClick = { showDisplaySettingsDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(
                                    Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Atur Tampilan",
                                    color = GoldAccent,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Panel Pengaturan Hafalan (Muroja'ah Settings)
                if (showRepeatPanel) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚙️ Pengaturan Pengulangan",
                                        color = GoldLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f, fill = false),
                                        maxLines = 1
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedButton(
                                            onClick = { showBatteryTipsDialog = true },
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldLight.copy(alpha = 0.6f)),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(Icons.Default.Info, contentDescription = null, tint = GoldLight, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Tips Audio", color = GoldLight, fontSize = 11.sp, maxLines = 1, softWrap = false)
                                        }
                                        OutlinedButton(
                                            onClick = { showSyncDialog = true },
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f)),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(Icons.Default.Sync, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Sinkron Ulang", color = GoldAccent, fontSize = 11.sp, maxLines = 1, softWrap = false)
                                        }
                                    }
                                }

                                // Repeat Count selector
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Ulangi Tiap Ayat:",
                                        color = TextMuted,
                                        fontSize = 12.sp
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        repeatOptions.forEach { count ->
                                            val isSelected = (selectedRepeat == count)
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (isSelected) GoldAccent else EmeraldContainer)
                                                    .clickable {
                                                        selectedRepeat = count
                                                        onRepeatCountChange(count)
                                                    }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (count == 0) "Loop ∞" else "${count}x",
                                                    color = if (isSelected) QuranBgDark else TextWhite,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                // Pause interval selector
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Jeda Hening Antar Ayat (untuk Menirukan):",
                                        color = TextMuted,
                                        fontSize = 12.sp
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        pauseOptions.forEach { sec ->
                                            val isSelected = (selectedPause == sec)
                                            val label = if (sec == 0f) "0s" else if (sec == 0.5f) "0.5s" else "${sec.toInt()}s"
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (isSelected) EmeraldLight else QuranSurfaceDark)
                                                    .clickable {
                                                        selectedPause = sec
                                                        onPauseSecondsChange(sec)
                                                    }
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    color = if (isSelected) QuranBgDark else TextWhite,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                // Quick Button "Putar Surah Ini Berulang"
                                Button(
                                    onClick = {
                                        val config = MemorizeConfig(
                                            repeatPerAyah = selectedRepeat,
                                            pauseSecondsBetweenAyahs = selectedPause,
                                            startAyah = ayahs.firstOrNull()?.ayahNumber ?: 1,
                                            endAyah = ayahs.lastOrNull()?.ayahNumber ?: surah.numberOfAyahs
                                        )
                                        onPlayAll(config)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = TextWhite)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Putar Ayat di Atas Berulang (Background)",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Ayat List
                items(ayahs, key = { it.ayahNumber }) { ayah ->
                    val isCurrentPlaying = (playerState.currentSurahNumber == surah.number && playerState.currentAyahNumber == ayah.ayahNumber && playerState.isPlaying)
                    AyahItem(
                        ayah = ayah,
                        isPlaying = isCurrentPlaying,
                        currentIteration = playerState.currentIteration,
                        targetRepeat = selectedRepeat,
                        showLatin = showLatin,
                        showTranslation = showTranslation,
                        showAudioButton = showAudioButton,
                        showLevelBadge = !isMushafMode,
                        arabicFontSize = arabicFontSize,
                        isMushafMode = isMushafMode,
                        onPlayClick = {
                            val config = MemorizeConfig(
                                repeatPerAyah = selectedRepeat,
                                pauseSecondsBetweenAyahs = selectedPause,
                                startAyah = ayah.ayahNumber,
                                endAyah = ayahs.lastOrNull()?.ayahNumber ?: surah.numberOfAyahs
                            )
                            onPlayAyah(ayah, config)
                        },
                        onLevelChange = { nextLevel ->
                            onLevelChange(ayah, nextLevel)
                        }
                    )
                }
            }
        }
    }

    // Modal Dialog Pengaturan Tampilan & Ukuran Font
    if (showDisplaySettingsDialog) {
        DisplaySettingsDialog(
            isMushafMode = isMushafMode,
            showLatin = showLatin,
            showTranslation = showTranslation,
            showAudioButton = showAudioButton,
            showRepeatPanel = showRepeatPanel,
            arabicFontSize = arabicFontSize,
            onDismiss = { showDisplaySettingsDialog = false },
            onOpenBatteryTips = {
                showDisplaySettingsDialog = false
                showBatteryTipsDialog = true
            },
            onSaveSettings = { newMushaf, newLatin, newTrans, newAudio, newPanel, newFont ->
                isMushafMode = newMushaf
                showLatin = newLatin
                showTranslation = newTrans
                showAudioButton = newAudio
                showRepeatPanel = newPanel
                arabicFontSize = newFont
                savePrefs(newMushaf, newLatin, newTrans, newAudio, newPanel, newFont)
                showDisplaySettingsDialog = false
            }
        )
    }

    // Modal Dialog Tips Audio Background & Layar Mati
    if (showBatteryTipsDialog) {
        BackgroundAudioInfoDialog(onDismiss = { showBatteryTipsDialog = false })
    }

    // Modal Dialog Sinkronisasi Ayat
    if (showSyncDialog) {
        SyncAyahDialog(
            surah = surah,
            onDismiss = { showSyncDialog = false },
            onConfirmSync = { start, end ->
                showSyncDialog = false
                isSyncing = true
                scope.launch {
                    val res = onSyncAyahs(start, end)
                    isSyncing = false
                    res.onSuccess { count ->
                        Toast.makeText(context, "Berhasil menyinkronkan $count ayat untuk ${surah.nameLatin}", Toast.LENGTH_LONG).show()
                    }.onFailure { err ->
                        Toast.makeText(context, "Gagal sinkron: ${err.localizedMessage ?: "Cek koneksi internet"}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}

@Composable
fun DisplaySettingsDialog(
    isMushafMode: Boolean,
    showLatin: Boolean,
    showTranslation: Boolean,
    showAudioButton: Boolean,
    showRepeatPanel: Boolean,
    arabicFontSize: Float,
    onDismiss: () -> Unit,
    onOpenBatteryTips: () -> Unit = {},
    onSaveSettings: (isMushaf: Boolean, latin: Boolean, trans: Boolean, audio: Boolean, panel: Boolean, font: Float) -> Unit
) {
    var tempMushaf by remember { mutableStateOf(isMushafMode) }
    var tempLatin by remember { mutableStateOf(showLatin) }
    var tempTranslation by remember { mutableStateOf(showTranslation) }
    var tempAudioButton by remember { mutableStateOf(showAudioButton) }
    var tempRepeatPanel by remember { mutableStateOf(showRepeatPanel) }
    var tempFontSize by remember { mutableStateOf(arabicFontSize) }

    val fontPresets = listOf(
        Pair("Normal", 24f),
        Pair("Sedang", 28f),
        Pair("Besar", 32f),
        Pair("Sangat Besar", 36f),
        Pair("Ekstra Besar", 40f)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = QuranCardDark,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚙️ Pengaturan Mode & Tampilan",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Preset Mode Mushaf Switch Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (tempMushaf) EmeraldContainer else QuranSurfaceDark
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (tempMushaf) GoldAccent else QuranCardBorder)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📖 Mode Baca Mushaf",
                                color = if (tempMushaf) GoldLight else TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Tampilkan hanya ayat Arab berukuran besar (sembunyikan latin, terjemahan, & audio).",
                                color = TextMuted,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        }
                        Switch(
                            checked = tempMushaf,
                            onCheckedChange = { checked ->
                                tempMushaf = checked
                                if (checked) {
                                    tempLatin = false
                                    tempTranslation = false
                                    tempAudioButton = false
                                    tempRepeatPanel = false
                                    if (tempFontSize < 32f) tempFontSize = 32f
                                } else {
                                    tempLatin = true
                                    tempTranslation = true
                                    tempAudioButton = true
                                    tempRepeatPanel = true
                                    if (tempFontSize > 30f) tempFontSize = 28f
                                }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = EmeraldDark)
                        )
                    }
                }

                HorizontalDivider(color = QuranCardBorder.copy(alpha = 0.5f))

                // Pengaturan Ukuran Teks Arab / Mushaf
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ukuran Huruf Arab / Mushaf:",
                            color = GoldLight,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${tempFontSize.toInt()} sp",
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Font Size Slider
                    Slider(
                        value = tempFontSize,
                        onValueChange = { tempFontSize = it },
                        valueRange = 22f..42f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldAccent,
                            activeTrackColor = GoldAccent,
                            inactiveTrackColor = QuranSurfaceDark
                        )
                    )

                    // Preset Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        fontPresets.forEach { (label, size) ->
                            val isSelected = (tempFontSize == size)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GoldAccent else QuranSurfaceDark)
                                    .clickable { tempFontSize = size }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) QuranBgDark else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Live Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(QuranSurfaceDark)
                            .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            color = TextWhite,
                            fontSize = tempFontSize.sp,
                            lineHeight = (tempFontSize * 1.7f).sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                HorizontalDivider(color = QuranCardBorder.copy(alpha = 0.5f))

                // 1. Tampilkan / Hide Latin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "1. Tampilkan Teks Latin (Transliterasi)",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Teks latin ejaan bahasa Indonesia",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = tempLatin,
                        onCheckedChange = {
                            tempLatin = it
                            if (it) tempMushaf = false
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = EmeraldDark)
                    )
                }

                // 2. Tampilkan / Hide Terjemahan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "2. Tampilkan Terjemahan Kemenag",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Arti & terjemahan resmi Kemenag RI",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = tempTranslation,
                        onCheckedChange = {
                            tempTranslation = it
                            if (it) tempMushaf = false
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = EmeraldDark)
                    )
                }

                // 3. Tampilkan / Hide Tombol Audio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "3. Tampilkan Tombol Audio (Play) per Ayat",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Tombol putar audio langsung di samping ayat",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = tempAudioButton,
                        onCheckedChange = {
                            tempAudioButton = it
                            if (it) tempMushaf = false
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = EmeraldDark)
                    )
                }

                // 4. Tampilkan / Hide Panel Muroja'ah Atas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "4. Tampilkan Panel Pengulangan Atas",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Kotak pengatur repeat count & jeda di atas daftar ayat",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = tempRepeatPanel,
                        onCheckedChange = { tempRepeatPanel = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = EmeraldDark)
                    )
                }

                HorizontalDivider(color = QuranCardBorder.copy(alpha = 0.5f))

                // Tips Pemutaran Audio Layar Mati
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenBatteryTips() },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Headphones, contentDescription = null, tint = GoldLight, modifier = Modifier.size(18.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("💡 Tips Audio Saat Layar HP Mati", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Kenapa audio berhenti 10-15 menit & solusinya", color = TextWhite.copy(alpha = 0.85f), fontSize = 10.5.sp)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveSettings(
                        tempMushaf,
                        tempLatin,
                        tempTranslation,
                        tempAudioButton,
                        tempRepeatPanel,
                        tempFontSize
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Terapkan Tampilan", color = QuranBgDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextMuted)
            }
        }
    )
}

@Composable
fun SyncAyahDialog(
    surah: Surah,
    onDismiss: () -> Unit,
    onConfirmSync: (startAyah: Int, endAyah: Int?) -> Unit
) {
    var isAllAyahs by remember { mutableStateOf(true) }
    var startAyahText by remember { mutableStateOf("1") }
    var endAyahText by remember { mutableStateOf(surah.numberOfAyahs.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = QuranCardDark,
        title = {
            Text(
                text = "📥 Sinkronisasi Ayat ${surah.nameLatin}",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Pilih opsi pengunduhan ayat, terjemahan resmi Kemenag, dan audio qori:",
                    color = TextMuted,
                    fontSize = 13.sp
                )

                // Radio Button Pilihan
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAllAyahs = true }
                ) {
                    RadioButton(
                        selected = isAllAyahs,
                        onClick = { isAllAyahs = true },
                        colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Semua Ayat (1 - ${surah.numberOfAyahs} Ayat)",
                        color = TextWhite,
                        fontSize = 14.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAllAyahs = false }
                ) {
                    RadioButton(
                        selected = !isAllAyahs,
                        onClick = { isAllAyahs = false },
                        colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pilih Rentang Ayat Tertentu",
                        color = TextWhite,
                        fontSize = 14.sp
                    )
                }

                if (!isAllAyahs) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = startAyahText,
                            onValueChange = { startAyahText = it },
                            label = { Text("Dari Ayat", color = TextMuted, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = QuranCardBorder
                            )
                        )
                        OutlinedTextField(
                            value = endAyahText,
                            onValueChange = { endAyahText = it },
                            label = { Text("Sampai Ayat", color = TextMuted, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = QuranCardBorder
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isAllAyahs) {
                        onConfirmSync(1, surah.numberOfAyahs)
                    } else {
                        val start = startAyahText.toIntOrNull()?.coerceIn(1, surah.numberOfAyahs) ?: 1
                        val end = endAyahText.toIntOrNull()?.coerceIn(start, surah.numberOfAyahs) ?: surah.numberOfAyahs
                        onConfirmSync(start, end)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text("Mulai Sinkron", color = QuranBgDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextMuted)
            }
        }
    )
}
