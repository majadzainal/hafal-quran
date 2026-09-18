package com.hafalquran.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext
import com.hafalquran.app.ui.theme.*

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuranBgDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tentang Aplikasi 📖",
            color = TextWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = "Visi, rujukan data, dan fitur aplikasi Tahfidz offline-first",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // ==========================================
            // 1. Header & Visi Personal
            // ==========================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(GoldAccent.copy(alpha = 0.6f), EmeraldLight.copy(alpha = 0.3f)))
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // App Icon Badge
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(GoldAccent, EmeraldDark)
                                    )
                                )
                                .border(2.dp, GoldLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = "App Logo",
                                tint = QuranBgDark,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // App Title
                        Text(
                            text = "Hafal Qur'an",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Tahfidz Ride & Loop • Offline First Companion",
                            color = GoldLight,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        // Badges Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FeatureBadge(text = "🛡️ Offline-First")
                            FeatureBadge(text = "🏍️ Background Loop")
                            FeatureBadge(text = "✨ 100% Bebas Iklan")
                        }

                        HorizontalDivider(
                            color = QuranCardBorder.copy(alpha = 0.6f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Vision Description
                        Text(
                            text = "Aplikasi ini dikembangkan secara mandiri dengan niat tulus untuk memfasilitasi hafalan pribadi (tadabbur & muroja'ah) yang dioptimalkan secara khusus untuk pemutaran audio di latar belakang (background playback) saat berkendara (motor/mobil), berolahraga, maupun waktu luang tanpa ketergantungan koneksi internet.",
                            color = TextWhite.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 19.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ==========================================
            // 2. Bagian Sumber Data & Rujukan
            // ==========================================
            item {
                SectionHeader(
                    icon = Icons.Default.LibraryBooks,
                    title = "Sumber Data & Rujukan Autentik"
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReferenceCard(
                        icon = Icons.Default.MenuBook,
                        title = "Teks Al-Qur'an & Terjemahan",
                        subtitle = "Standar Rasm Uthmani dari Quran.com API (v4) & Terjemahan resmi Bahasa Indonesia (Kementerian Agama RI)."
                    )
                    ReferenceCard(
                        icon = Icons.Default.Headphones,
                        title = "Audio Murottal Per Ayat",
                        subtitle = "Lantunan ayat per ayat (*ayah-by-ayah*) bersumber dari CDN resmi Qari Internasional: Syaikh Mishary Rashid Alafasy."
                    )
                    ReferenceCard(
                        icon = Icons.Default.FormatQuote,
                        title = "Tafsir & Hadis Keutamaan",
                        subtitle = "\"Sebaik-baik kalian adalah orang yang mempelajari Al-Qur'an dan mengajarkannya.\" (HR. Bukhari no. 5027)."
                    )
                }
            }

            // ==========================================
            // 3. Bagian Fitur Utama
            // ==========================================
            item {
                SectionHeader(
                    icon = Icons.Default.Stars,
                    title = "Fitur Unggulan Aplikasi"
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HighlightCard(
                        icon = Icons.Default.Storage,
                        iconTint = EmeraldLight,
                        title = "Offline-First Architecture",
                        description = "Ayat yang telah disinkronkan tersimpan rapi di database lokal perangkat (Room Database). Aplikasi dapat digunakan sepenuhnya tanpa internet."
                    )
                    HighlightCard(
                        icon = Icons.Default.CloudDownload,
                        iconTint = LearningBlue,
                        title = "Partial Sync (Hemat Kuota)",
                        description = "Sinkronisasi fleksibel per rentang ayat target (misal Ayat 1-5 saja), menghemat kuota data dan memori internal perangkat Anda."
                    )
                    HighlightCard(
                        icon = Icons.Default.Repeat,
                        iconTint = GoldAccent,
                        title = "Custom Looping & Pause Hening",
                        description = "Atur pengulangan per ayat (1x, 3x, 5x, 10x, atau Loop ∞), jeda jeda hening untuk menirukan bacaan, serta loop seluruh playlist target tanpa henti."
                    )
                    HighlightCard(
                        icon = Icons.Default.PhoneAndroid,
                        iconTint = MutqinGreen,
                        title = "Background Audio & Lockscreen Control",
                        description = "Tetap berputar lancar saat layar HP mati atau saat membuka aplikasi navigasi/maps dengan integrasi resmi Android Media3 & MediaSession."
                    )
                    HighlightCard(
                        icon = Icons.Default.Mosque,
                        iconTint = GoldLight,
                        title = "Adab Tilawah Ta'awwudz & Bismillah",
                        description = "Otomatis mengawali pemutaran dengan lantunan isti'adzah dan basmalah sesuai kaidah tilawah sebelum memasuki ayat hafalan."
                    )
                }
            }

            // ==========================================
            // 4. Panduan Audio Background & Izin Baterai
            // ==========================================
            item {
                SectionHeader(
                    icon = Icons.Default.Headphones,
                    title = "Panduan Audio Layar Mati / Background"
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(GoldAccent.copy(alpha = 0.5f), EmeraldLight.copy(alpha = 0.3f)))
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "💡 Mengapa Audio Bisa Terhenti Saat Layar Mati?",
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )

                        Text(
                            text = "Sistem operasi Android memiliki fitur penghemat daya bawaan (Doze Mode). Jika layar HP mati lebih dari 10-15 menit, sistem dapat membatasi aktivitas latar belakang secara otomatis.",
                            color = TextWhite.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = QuranSurfaceDark)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "ℹ️ Tentang \"Pause app activity if unused\":",
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                )
                                Text(
                                    text = "Fitur bawaan Android ini diset ON secara default oleh sistem dan baru aktif jika aplikasi tidak dibuka selama 90 hari (3 bulan). Opsi ini aman dimatikan (OFF) di menu izin aplikasi.",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Text(
                            text = "✅ Solusi Agar Audio Muroja'ah Lancar Non-Stop:\n1. Buka Info Aplikasi di bawah ini.\n2. Masuk ke menu 'Penggunaan Baterai' (Battery).\n3. Pilih 'Tidak Dibatasi' (Unrestricted / No Restrictions).",
                            color = EmeraldLight,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp
                        )

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
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = QuranBgDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Buka Info & Izin Baterai HP",
                                color = QuranBgDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 5. Footer & Informasi Teknis
            // ==========================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = QuranSurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Versi Aplikasi: v1.0.0-release",
                                color = GoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = "Dibuat dengan Jetpack Compose & Android Media3",
                            color = TextMuted,
                            fontSize = 11.sp
                        )

                        HorizontalDivider(color = QuranCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                        Text(
                            text = "\"Segala kebenaran, kesempurnaan, dan kemuliaan hanyalah milik Allah Subhanahu wa Ta'ala. Segala kekurangan, kealpaan, dan kekhilafan murni dari kelemahan pengembang. Semoga aplikasi ini menjadi amal jariyah dan washilah kemudahan menghafal Al-Qur'an bagi kaum muslimin.\"",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldAccent,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            color = GoldLight,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ReferenceCard(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = QuranCardDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(EmeraldContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun HighlightCard(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = QuranCardDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = title,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = description,
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun FeatureBadge(text: String) {
    Surface(
        color = EmeraldContainer,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            color = GoldLight,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
        )
    }
}
