package com.hafalquran.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
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
import com.hafalquran.app.data.model.Surah
import com.hafalquran.app.data.model.TargetTask
import com.hafalquran.app.player.PlayerUiState
import com.hafalquran.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTargetScreen(
    tasks: List<TargetTask>,
    surahs: List<Surah>,
    playerState: PlayerUiState,
    onPlayTarget: (TargetTask) -> Unit,
    onToggleTaskStatus: (TargetTask) -> Unit,
    onDeleteTask: (TargetTask) -> Unit,
    onAddNewTask: (TargetTask) -> Unit,
    onEditTask: (TargetTask) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TargetTask?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuranBgDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "My Target Hafalan 🎯",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Playlist hafalan yang di-loop terus di perjalanan",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    taskToEdit = null
                    showAddDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Target", tint = QuranBgDark, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Buat Target", color = QuranBgDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tasks.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp),
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.PlaylistAddCheck,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "Belum Ada Target Hafalan",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Buat target hafalan dengan memilih surat dan rentang ayat (misal: Ali 'Imran Ayat 1-2, lalu nanti bisa diedit menjadi 1-3). Tiap ayat akan diulang dan playlist di-loop otomatis.",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Button(
                            onClick = {
                                taskToEdit = null
                                showAddDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Buat Target Pertama Sekarang", color = TextWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TargetTaskCard(
                        task = task,
                        isPlaying = playerState.currentSurahNumber == task.surahNumber &&
                                playerState.currentAyahNumber in task.startAyah..task.endAyah &&
                                playerState.isPlaying,
                        currentRound = if (playerState.currentSurahNumber == task.surahNumber) playerState.currentSetRound else 1,
                        onPlayClick = { onPlayTarget(task) },
                        onToggleComplete = { onToggleTaskStatus(task) },
                        onEditClick = {
                            taskToEdit = task
                            showAddDialog = true
                        },
                        onDeleteClick = {
                            onDeleteTask(task)
                            Toast.makeText(context, "Target \"${task.title}\" dihapus", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        TargetTaskDialog(
            surahs = surahs,
            initialTask = taskToEdit,
            onDismiss = {
                showAddDialog = false
                taskToEdit = null
            },
            onSave = { savedTask ->
                showAddDialog = false
                if (taskToEdit != null) {
                    onEditTask(savedTask)
                    Toast.makeText(context, "Target \"${savedTask.title}\" berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                } else {
                    onAddNewTask(savedTask)
                    Toast.makeText(context, "Target \"${savedTask.title}\" berhasil dibuat & disinkronkan!", Toast.LENGTH_SHORT).show()
                }
                taskToEdit = null
            }
        )
    }
}

@Composable
fun TargetTaskCard(
    task: TargetTask,
    isPlaying: Boolean,
    currentRound: Int,
    onPlayClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) EmeraldContainer else QuranCardDark
        ),
        border = if (isPlaying) {
            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldAccent))
        } else {
            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (task.isCompleted) MutqinGreen.copy(alpha = 0.2f) else EmeraldDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${task.surahNumber}",
                            color = if (task.isCompleted) MutqinGreen else GoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Column {
                        Text(
                            text = task.title,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "${task.surahName} • Ayat ${task.startAyah} - ${task.endAyah}",
                            color = GoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Action buttons: Edit & Delete
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Target", tint = GoldAccent, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Info Loop & Repeat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val pauseLabel = if (task.pauseSeconds == 0.5f) "0.5s" else if (task.pauseSeconds == 0f) "0s" else "${task.pauseSeconds.toInt()}s"
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "🔁 Ulang: ${if (task.repeatPerAyah == 0) "Loop ∞" else "${task.repeatPerAyah}x / Ayat"} | ⏱️ Jeda: $pauseLabel",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = if (task.isLoopEntireSet) "🔄 Loop Playlist: Berulang Terus (Infinite)" else "⏹️ Sekali Putar",
                        color = if (task.isLoopEntireSet) GoldLight else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Status Badge
                Text(
                    text = if (task.isCompleted) "✓ Selesai Mutqin" else (if (isPlaying) "▶ Putaran ke-$currentRound" else "🎯 Sedang Dihafal"),
                    color = if (task.isCompleted) MutqinGreen else if (isPlaying) GoldAccent else LearningBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            HorizontalDivider(color = QuranCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

            // Bottom Actions: Play Loop Playlist & Toggle Complete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onToggleComplete) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (task.isCompleted) MutqinGreen else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (task.isCompleted) "Tandai Belum Selesai" else "Tandai Selesai",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onPlayClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) GoldAccent else EmeraldPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (isPlaying) QuranBgDark else TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "Sedang Diputar (Loop)" else "Play Loop Playlist",
                        color = if (isPlaying) QuranBgDark else TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetTaskDialog(
    surahs: List<Surah>,
    initialTask: TargetTask? = null,
    onDismiss: () -> Unit,
    onSave: (TargetTask) -> Unit
) {
    val defaultSurah = remember(initialTask, surahs) {
        if (initialTask != null) {
            surahs.find { it.number == initialTask.surahNumber } ?: surahs.firstOrNull() ?: Surah(1, "الفاتحة", "Al-Fatihah", "Pembukaan", 7, "Makkiyah")
        } else {
            surahs.firstOrNull() ?: Surah(1, "الفاتحة", "Al-Fatihah", "Pembukaan", 7, "Makkiyah")
        }
    }

    var title by remember { mutableStateOf(initialTask?.title ?: "Target Hafalan") }
    var selectedSurah by remember { mutableStateOf(defaultSurah) }
    var startAyahText by remember { mutableStateOf(initialTask?.startAyah?.toString() ?: "1") }
    var endAyahText by remember { mutableStateOf(initialTask?.endAyah?.toString() ?: minOf(3, defaultSurah.numberOfAyahs).toString()) }
    var repeatCount by remember { mutableStateOf(initialTask?.repeatPerAyah ?: 3) }
    var isLoopEntireSet by remember { mutableStateOf(initialTask?.isLoopEntireSet ?: true) }
    var pauseSeconds by remember { mutableStateOf(initialTask?.pauseSeconds ?: 0.5f) }
    var showSurahPicker by remember { mutableStateOf(false) }

    val isEditing = (initialTask != null)

    if (showSurahPicker) {
        SurahPickerDialog(
            surahs = surahs,
            selectedSurahNumber = selectedSurah.number,
            onSurahSelected = { surah ->
                selectedSurah = surah
                startAyahText = "1"
                endAyahText = minOf(3, surah.numberOfAyahs).toString()
                showSurahPicker = false
            },
            onDismiss = { showSurahPicker = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = QuranCardDark,
        title = {
            Text(
                text = if (isEditing) "✏️ Edit Target Hafalan" else "➕ Buat Target & Playlist Hafalan",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Target / Catatan", color = TextMuted, fontSize = 12.sp) },
                    placeholder = { Text("misal: Hafalan Subuh, Juz 30", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = QuranCardBorder
                    )
                )

                // 1. Pilih Surat (Clickable Selector Card with Picker Modal)
                Text(text = "1. Pilih Surat:", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showSurahPicker = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = QuranSurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${selectedSurah.number}",
                                    color = QuranBgDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = selectedSurah.nameLatin,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${selectedSurah.nameTranslation} • ${selectedSurah.numberOfAyahs} Ayat",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedSurah.nameArabic,
                                color = GoldLight,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Ganti Surat",
                                tint = GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // 2. Pilih Rentang Ayat
                Text(text = "2. Rentang Ayat (Total ${selectedSurah.numberOfAyahs} Ayat):", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = startAyahText,
                        onValueChange = { startAyahText = it },
                        label = { Text("Dari Ayat", color = TextMuted, fontSize = 11.sp) },
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
                        label = { Text("Sampai Ayat", color = TextMuted, fontSize = 11.sp) },
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

                // 3. Setel Pengulangan per Ayat
                Text(text = "3. Pengulangan Tiap Ayat:", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1, 3, 5, 10, 0).forEach { count ->
                        val isSelected = (repeatCount == count)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GoldAccent else EmeraldContainer)
                                .clickable { repeatCount = count }
                                .padding(vertical = 6.dp),
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

                // 4. Jeda Antar Ayat
                Text(text = "4. Jeda Hening Antar Ayat:", color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(0f, 0.5f, 1f, 2f, 3f).forEach { sec ->
                        val isSelected = (pauseSeconds == sec)
                        val label = if (sec == 0f) "0s" else if (sec == 0.5f) "0.5s" else "${sec.toInt()}s"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldLight else QuranSurfaceDark)
                                .clickable { pauseSeconds = sec }
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

                // 5. Mode Pengulangan Seluruh Rentang Playlist (Infinite Loop)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isLoopEntireSet = !isLoopEntireSet },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLoopEntireSet) EmeraldContainer else QuranSurfaceDark
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (isLoopEntireSet) GoldAccent else QuranCardBorder)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🔄 Loop Terus-Menerus",
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Ulang lagi dari ayat ${startAyahText.ifBlank { "1" }} sampai di-stop manual.",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = isLoopEntireSet,
                            onCheckedChange = { isLoopEntireSet = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = EmeraldDark)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val start = startAyahText.toIntOrNull()?.coerceIn(1, selectedSurah.numberOfAyahs) ?: 1
                    val end = endAyahText.toIntOrNull()?.coerceIn(start, selectedSurah.numberOfAyahs) ?: selectedSurah.numberOfAyahs
                    onSave(
                        TargetTask(
                            id = initialTask?.id ?: 0L,
                            title = if (title.isBlank()) "Target ${selectedSurah.nameLatin}" else title,
                            surahNumber = selectedSurah.number,
                            surahName = selectedSurah.nameLatin,
                            startAyah = start,
                            endAyah = end,
                            repeatPerAyah = repeatCount,
                            isLoopEntireSet = isLoopEntireSet,
                            pauseSeconds = pauseSeconds,
                            isCompleted = initialTask?.isCompleted ?: false,
                            createdAt = initialTask?.createdAt ?: System.currentTimeMillis()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text(if (isEditing) "Simpan Perubahan" else "Simpan & Mulai", color = QuranBgDark, fontWeight = FontWeight.Bold)
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
fun SurahPickerDialog(
    surahs: List<Surah>,
    selectedSurahNumber: Int,
    onSurahSelected: (Surah) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredSurahs = remember(searchQuery, surahs) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.nameLatin.contains(searchQuery, ignoreCase = true) ||
            it.nameTranslation.contains(searchQuery, ignoreCase = true) ||
            it.number.toString() == searchQuery.trim() ||
            it.nameArabic.contains(searchQuery)
        }
    }

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
                    text = "📖 Pilih Surat Target",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 260.dp, max = 450.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Ketik nama / nomor surat...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = QuranCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (filteredSurahs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Surat \"$searchQuery\" tidak ditemukan",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredSurahs, key = { it.number }) { surah ->
                            val isSelected = (surah.number == selectedSurahNumber)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSurahSelected(surah) },
                                color = if (isSelected) EmeraldContainer else QuranSurfaceDark,
                                shape = RoundedCornerShape(10.dp),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, GoldAccent) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) GoldAccent else EmeraldDark),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${surah.number}",
                                                color = if (isSelected) QuranBgDark else TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = surah.nameLatin,
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "${surah.nameTranslation} • ${surah.numberOfAyahs} Ayat",
                                                color = TextMuted,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Text(
                                        text = surah.nameArabic,
                                        color = GoldLight,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = TextMuted)
            }
        }
    )
}
