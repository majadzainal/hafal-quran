package com.hafalquran.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.google.firebase.firestore.FirebaseFirestore
import com.hafalquran.app.ui.theme.*

data class BankDonationItem(
    val id: String = "",
    val bankName: String = "",
    val accountNumber: String = "",
    val accountHolder: String = ""
)

@Composable
fun DonationScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var bankList by remember {
        mutableStateOf(
            listOf(
                BankDonationItem(
                    id = "default",
                    bankName = "Bank Muamalat",
                    accountNumber = "3190006043",
                    accountHolder = "Madinatul Hijaz Yayasan"
                )
            )
        )
    }
    var whatsappNumber by remember { mutableStateOf("") }
    var whatsappMessage by remember { 
        mutableStateOf("Assalamu'alaikum Warahmatullahi Wabarakatuh, saya ingin konfirmasi infaq/donasi untuk pengembangan aplikasi Hafal Qur'an.") 
    }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("donation")
                .addSnapshotListener { snapshot, error ->
                    isLoading = false
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val items = mutableListOf<BankDonationItem>()
                        for (doc in snapshot.documents) {
                            // Cek apakah ada nomor WA & template pesan WA di field dokumen
                            val waNum = doc.getString("whatsapp_number") ?: doc.getString("wa_number") ?: doc.getString("whatsapp")
                            val waMsg = doc.getString("whatsapp_message") ?: doc.getString("wa_message") ?: doc.getString("message")
                            if (!waNum.isNullOrBlank()) {
                                whatsappNumber = waNum
                            }
                            if (!waMsg.isNullOrBlank()) {
                                whatsappMessage = waMsg
                            }

                            val bankName = doc.getString("bank_name") ?: doc.getString("bankName") ?: ""
                            val accountNumber = doc.getString("bank_account_number") ?: doc.getString("accountNumber") ?: ""
                            val accountHolder = doc.getString("bank_account_name") ?: doc.getString("accountHolder") ?: ""
                            if (bankName.isNotBlank() && accountNumber.isNotBlank()) {
                                items.add(
                                    BankDonationItem(
                                        id = doc.id,
                                        bankName = bankName,
                                        accountNumber = accountNumber,
                                        accountHolder = accountHolder
                                    )
                                )
                            }
                        }
                        if (items.isNotEmpty()) {
                            bankList = items
                        }
                    }
                }
        } catch (e: Exception) {
            isLoading = false
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label disalin ke clipboard", Toast.LENGTH_SHORT).show()
    }

    fun openWhatsApp() {
        val cleanPhone = whatsappNumber.trim().replace("+", "").replace("-", "").replace(" ", "")
        val url = if (cleanPhone.isNotBlank()) {
            "https://api.whatsapp.com/send?phone=$cleanPhone&text=" + Uri.encode(whatsappMessage)
        } else {
            "https://api.whatsapp.com/send?text=" + Uri.encode(whatsappMessage)
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Aplikasi WhatsApp tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuranBgDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Infaq & Donasi Pengembangan 🤲",
            color = TextWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = "Dukung keberlangsungan server audio & pengembangan aplikasi bebas iklan",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // 1. Banner Hadits Sedekah Jariyah
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldAccent.copy(alpha = 0.5f)))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(22.dp))
                            Text(
                                text = "Sedekah Jariyah & Wakaf Al-Qur'an",
                                color = GoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "\"Jika seseorang meninggal dunia, maka terputuslah amalannya kecuali tiga perkara: sedekah jariyah, ilmu yang bermanfaat, dan anak saleh yang mendoakannya.\"",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 18.sp
                        )
                        Text(
                            text = "(HR. Muslim no. 1631)",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 2. Transparansi Dana
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = QuranCardDark),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "💡 Alokasi Infaq Anda:",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "• Operasional Server Audio Streaming CDN Qari Internasional\n• Pemeliharaan API & Sinkronisasi Database Al-Qur'an\n• Pembaruan Fitur Baru Muroja'ah, Target, & Rekam Hafalan\n• 100% Bebas Iklan selamanya untuk kenyamanan seluruh kaum muslimin.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // 3. Pilihan Rekening Donasi (Sinkron Otomatis dari Cloud Firestore)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pilihan Rekening Pembayaran",
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = GoldAccent,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            items(bankList, key = { it.id.ifEmpty { it.accountNumber } }) { bank ->
                BankTransferCard(
                    bankName = bank.bankName,
                    accountNumber = bank.accountNumber,
                    accountHolder = bank.accountHolder,
                    onCopy = { copyToClipboard("No. Rekening ${bank.bankName}", bank.accountNumber) }
                )
            }

            // 4. Konfirmasi WhatsApp Button
            item {
                Button(
                    onClick = { openWhatsApp() },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Konfirmasi Donasi via WhatsApp",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // 5. Ucapan Do'a
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "جَزَاكُمُ اللهُ خَيْرًا كَثِيْرًا\nSemoga menjadi amal jariyah yang pahalanya terus mengalir tiada henti. Aamiin.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BankTransferCard(
    bankName: String,
    accountNumber: String,
    accountHolder: String,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = QuranCardDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(QuranCardBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(EmeraldDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                }

                Column {
                    Text(
                        text = bankName,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = accountNumber,
                        color = GoldLight,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "a.n. $accountHolder",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            OutlinedButton(
                onClick = onCopy,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Salin",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
