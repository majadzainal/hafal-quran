package com.hafalquran.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.google.firebase.firestore.FirebaseFirestore
import com.hafalquran.app.data.model.Surah
import com.hafalquran.app.ui.components.BackgroundAudioInfoDialog
import com.hafalquran.app.ui.components.SurahCard
import com.hafalquran.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    surahs: List<Surah>,
    onSurahClick: (Surah) -> Unit,
    onQuickMurojaahClick: () -> Unit,
    onAboutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showBatteryInfoDialog by remember { mutableStateOf(false) }
    var announcementTitle by remember { mutableStateOf<String?>(null) }
    var announcementBody by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("app_announcements")
                .whereEqualTo("is_active", true)
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null && !snapshot.isEmpty) {
                        val doc = snapshot.documents.firstOrNull()
                        if (doc != null) {
                            announcementTitle = doc.getString("title")
                            announcementBody = doc.getString("body")
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    val filteredSurahs = remember(searchQuery, surahs) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.nameLatin.contains(searchQuery, ignoreCase = true) ||
            it.nameTranslation.contains(searchQuery, ignoreCase = true) ||
            it.number.toString() == searchQuery.trim()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuranBgDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // App Header with Info / About Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hafal Qur'an ✨",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Companion Tahfidz & Muroja'ah Offline",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            IconButton(
                onClick = onAboutClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(QuranCardDark)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Tentang Aplikasi",
                    tint = GoldAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Muroja'ah Banner (Fitur Praktis Berkendara/Layar Mati)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldDark)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Mode Muroja'ah Cepat 🎧",
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Putar otomatis & berulang di latar belakang",
                            color = TextWhite.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }

                Button(
                    onClick = onQuickMurojaahClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Murojaah",
                        tint = QuranBgDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mulai Muroja'ah Juz 30 (Layar Mati)",
                        color = QuranBgDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showBatteryInfoDialog = true }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tips audio lancar saat layar HP mati 💡",
                        color = GoldLight,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (showBatteryInfoDialog) {
            BackgroundAudioInfoDialog(onDismiss = { showBatteryInfoDialog = false })
        }

        // Live Announcement Card from Cloud Firestore
        if (!announcementTitle.isNullOrBlank() && !announcementBody.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(GoldAccent.copy(alpha = 0.6f), EmeraldLight.copy(alpha = 0.3f))
                    )
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = announcementTitle ?: "",
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                        Text(
                            text = announcementBody ?: "",
                            color = TextWhite.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari Surah (contoh: Al-Mulk, An-Naba, 78)", color = TextMuted, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldAccent) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = QuranCardDark,
                unfocusedContainerColor = QuranCardDark,
                focusedBorderColor = GoldAccent,
                unfocusedBorderColor = QuranCardBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title Daftar Surah
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daftar Surah",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${filteredSurahs.size} Surah",
                color = GoldLight,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(filteredSurahs, key = { it.number }) { surah ->
                SurahCard(
                    surah = surah,
                    onClick = { onSurahClick(surah) }
                )
            }
        }
    }
}
