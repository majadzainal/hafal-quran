package com.hafalquran.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hafalquran.app.data.local.MurojaahHistoryEntity
import com.hafalquran.app.data.model.MemorizationStats
import com.hafalquran.app.data.model.Surah
import com.hafalquran.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProgressScreen(
    stats: MemorizationStats,
    recentHistory: List<MurojaahHistoryEntity>,
    surahs: List<Surah>,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))

    val juz30Percentage = if (stats.juz30TotalAyahs > 0) {
        ((stats.juz30MutqinAyahs.toFloat() / stats.juz30TotalAyahs.toFloat()) * 100f).toInt()
    } else 0

    val totalPercentage = if (stats.totalAyahs > 0) {
        ((stats.mutqinAyahs.toFloat() / stats.totalAyahs.toFloat()) * 100f)
    } else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuranBgDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Statistik & Progres Hafalan 📊",
            color = TextWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = "Data riil pencapaian hafalan dan muroja'ah Anda",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // 1. Ringkasan 3 Status Riil
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mutqin Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MutqinGreen.copy(alpha = 0.5f)))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MutqinGreen, modifier = Modifier.size(20.dp))
                            Text(
                                text = "${stats.mutqinAyahs} Ayat",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(text = "Mutqin (Lancar)", color = MutqinGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Sedang Menghafal Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(LearningBlue.copy(alpha = 0.5f)))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = LearningBlue, modifier = Modifier.size(20.dp))
                            Text(
                                text = "${stats.learningAyahs} Ayat",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(text = "Sedang Hafal", color = LearningBlue, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Perlu Murojaah Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ReviewOrange.copy(alpha = 0.5f)))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Loop, contentDescription = null, tint = ReviewOrange, modifier = Modifier.size(20.dp))
                            Text(
                                text = "${stats.needReviewAyahs} Ayat",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(text = "Muroja'ah", color = ReviewOrange, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // 2. Progress Juz 30 Card (Real Dynamic)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.AutoGraph, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Progres Juz 30 (Juz 'Amma)",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Text(
                                text = "$juz30Percentage%",
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }

                        LinearProgressIndicator(
                            progress = { (stats.juz30MutqinAyahs.toFloat() / stats.juz30TotalAyahs.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = GoldAccent,
                            trackColor = QuranSurfaceDark,
                        )

                        Text(
                            text = "${stats.juz30MutqinAyahs} dari ${stats.juz30TotalAyahs} Ayat Juz 30 berstatus Mutqin.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 3. Status Target Task (Real Dynamic)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Pencapaian Target Hafalan",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${stats.completedTasks} dari ${stats.totalTasks} playlist target telah diselesaikan",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EmeraldDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${stats.completedTasks}/${stats.totalTasks}",
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // 4. Riwayat Muroja'ah Terakhir
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Muroja'ah Terakhir",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${recentHistory.size} sesi",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            if (recentHistory.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = QuranSurfaceDark)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada riwayat muroja'ah. Mulai putar hafalan sekarang!",
                                color = TextMuted,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(recentHistory, key = { it.id }) { history ->
                    val surahName = surahs.find { it.number == history.surahNumber }?.nameLatin ?: "Surat ke-${history.surahNumber}"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Headphones, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                                }
                                Column {
                                    Text(
                                        text = surahName,
                                        color = TextWhite,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Ayat ${history.startAyah} - ${history.endAyah} • Diulang ${if (history.totalRepetitions == 0) "∞" else "${history.totalRepetitions}x"}",
                                        color = GoldLight,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Text(
                                text = dateFormat.format(Date(history.timestamp)),
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
