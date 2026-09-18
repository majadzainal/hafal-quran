package com.hafalquran.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import com.hafalquran.app.data.model.Ayah
import com.hafalquran.app.data.model.MemorizeLevel
import com.hafalquran.app.data.model.Surah
import com.hafalquran.app.player.PlayerUiState
import com.hafalquran.app.ui.theme.*

@Composable
fun SurahCard(
    surah: Surah,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = QuranCardDark
        ),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Number badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldContainer)
                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = surah.number.toString(),
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Info
                Column {
                    Text(
                        text = surah.nameLatin,
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${surah.nameTranslation} • ${surah.numberOfAyahs} Ayat",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            // Arabic name
            Text(
                text = surah.nameArabic,
                color = GoldAccent,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AyahItem(
    ayah: Ayah,
    isPlaying: Boolean,
    currentIteration: Int,
    targetRepeat: Int,
    onPlayClick: () -> Unit,
    onLevelChange: (MemorizeLevel) -> Unit,
    modifier: Modifier = Modifier,
    showLatin: Boolean = true,
    showTranslation: Boolean = true,
    showAudioButton: Boolean = true,
    showLevelBadge: Boolean = true,
    arabicFontSize: Float = 28f,
    isMushafMode: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (isMushafMode) 20.dp else 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) EmeraldContainer.copy(alpha = 0.7f) else if (isMushafMode) QuranSurfaceDark.copy(alpha = 0.85f) else QuranCardDark
        ),
        border = if (isPlaying) {
            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldAccent))
        } else if (isMushafMode) {
            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldAccent.copy(alpha = 0.25f)))
        } else {
            CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
        }
    ) {
        Column(
            modifier = Modifier.padding(if (isMushafMode) 18.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(if (isMushafMode) 14.dp else 12.dp)
        ) {
            // Header: Ayah number & Action buttons (only show if not pure clean or if level/badge wanted)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isMushafMode) 36.dp else 32.dp)
                            .clip(CircleShape)
                            .background(if (isMushafMode) GoldAccent.copy(alpha = 0.15f) else EmeraldDark)
                            .border(if (isMushafMode) 1.dp else 0.dp, GoldAccent.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ayah.ayahNumber.toString(),
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isMushafMode) 14.sp else 12.sp
                        )
                    }

                    if (isPlaying) {
                        Text(
                            text = "Diputar ($currentIteration/${if (targetRepeat == 0) "∞" else targetRepeat}x)",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Memorization Level Badge (shown if showLevelBadge is true)
                if (showLevelBadge && !isMushafMode) {
                    LevelBadge(level = ayah.memorizationLevel) { nextLevel ->
                        onLevelChange(nextLevel)
                    }
                }
            }

            // Arabic Text (Mushaf style with dynamic large font size)
            Text(
                text = ayah.textArabic,
                color = TextWhite,
                fontSize = arabicFontSize.sp,
                lineHeight = (arabicFontSize * 1.85f).sp,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )

            // Latin & Translation (Conditional based on settings)
            if (showLatin || showTranslation) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (showLatin && ayah.textLatin.isNotBlank()) {
                        Text(
                            text = ayah.textLatin,
                            color = GoldLight.copy(alpha = 0.9f),
                            fontSize = 13.5.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 19.sp
                        )
                    }
                    if (showTranslation && ayah.translationId.isNotBlank()) {
                        Text(
                            text = ayah.translationId,
                            color = TextMuted,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Play / Loop button (Conditional)
            if (showAudioButton) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) GoldAccent else EmeraldDark)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play Ayah",
                            tint = if (isPlaying) QuranBgDark else TextWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LevelBadge(
    level: MemorizeLevel,
    onToggle: (MemorizeLevel) -> Unit
) {
    val (bgColor, textColor, text) = when (level) {
        MemorizeLevel.MUTQIN -> Triple(MutqinGreen.copy(alpha = 0.2f), MutqinGreen, "Mutqin (Lancar)")
        MemorizeLevel.LEARNING -> Triple(LearningBlue.copy(alpha = 0.2f), LearningBlue, "Sedang Hafal")
        MemorizeLevel.NEED_REVIEW -> Triple(ReviewOrange.copy(alpha = 0.2f), ReviewOrange, "Muroja'ah")
        MemorizeLevel.NOT_STARTED -> Triple(QuranSurfaceDark, TextMuted, "Belum Hafal")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable {
                val next = when (level) {
                    MemorizeLevel.NOT_STARTED -> MemorizeLevel.LEARNING
                    MemorizeLevel.LEARNING -> MemorizeLevel.MUTQIN
                    MemorizeLevel.MUTQIN -> MemorizeLevel.NEED_REVIEW
                    MemorizeLevel.NEED_REVIEW -> MemorizeLevel.NOT_STARTED
                }
                onToggle(next)
            }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PlayerMiniBottomBar(
    playerState: PlayerUiState,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onOpenDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (playerState.currentAyah == null) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() },
        color = EmeraldDark,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val titleText = if (playerState.isIntroPreamble || playerState.currentAyahNumber == 0) {
                    if (playerState.currentAyah?.id == -1L) "Pembuka: Ta'awwudz" else "Pembuka: Basmalah"
                } else {
                    "${playerState.currentSurahName} : Ayat ${playerState.currentAyahNumber}"
                }

                val subtitleText = if (playerState.isIntroPreamble || playerState.currentAyahNumber == 0) {
                    "${playerState.currentSurahName} • Adab Membaca Al-Qur'an"
                } else {
                    "Putaran Set ke-${playerState.currentSetRound} • Ulangi ${playerState.currentIteration}/${if (playerState.targetRepeatCount == 0) "∞" else "${playerState.targetRepeatCount}x"}" +
                            if (playerState.isPausedForRepeat) " (Jeda ${playerState.pauseSeconds}s)" else ""
                }

                Text(
                    text = titleText,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = subtitleText,
                    color = GoldLight,
                    fontSize = 12.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onPrev) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Prev", tint = TextWhite)
                }
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(GoldAccent)
                ) {
                    Icon(
                        imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = QuranBgDark
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = TextWhite)
                }
            }
        }
    }
}

@Composable
fun BackgroundAudioInfoDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = QuranCardDark,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Headphones,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Tips Audio Layar Mati",
                    color = GoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Info Box 1: Penyebab audio berhenti
                Card(
                    colors = CardDefaults.cardColors(containerColor = QuranSurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "❓ Kenapa audio berhenti saat layar mati 10-15 menit?",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                        Text(
                            text = "Sistem operasi Android memiliki fitur penghemat baterai (Doze Mode). Ketika layar HP terkunci/mati lama, Android secara otomatis menidurkan proses latar belakang untuk menghemat daya.",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Info Box 2: Pause app activity if unused
                Card(
                    colors = CardDefaults.cardColors(containerColor = QuranSurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "ℹ️ Tentang \"Pause app activity if unused\":",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                        Text(
                            text = "Fitur bawaan Android ini diset ON secara default oleh sistem untuk semua aplikasi baru. Fitur ini hanya bekerja jika aplikasi tidak pernah dibuka selama 90 hari (3 bulan). Boleh dimatikan (OFF) di menu izin agar izin aplikasi tidak dicabut.",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Info Box 3: Solusi agar audio lancar
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "✅ Solusi Agar Audio Lancar Non-Stop:",
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                        Text(
                            text = "1. Tekan tombol \"Buka Info & Izin Aplikasi\" di bawah.\n2. Masuk ke menu \"Baterai\" (Battery Usage).\n3. Pilih opsi \"Tidak Dibatasi\" (Unrestricted / No Restrictions).",
                            color = TextWhite.copy(alpha = 0.9f),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        try {
                            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (e2: Exception) {}
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = QuranBgDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Buka Info & Izin Aplikasi",
                    color = QuranBgDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Tutup", color = TextMuted, fontSize = 12.sp)
            }
        }
    )
}
