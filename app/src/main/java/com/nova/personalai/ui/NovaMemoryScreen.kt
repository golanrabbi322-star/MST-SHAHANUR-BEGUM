package com.nova.personalai.ui

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberNeonRed
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.nova.personalai.FirebaseMemoryRepository
import com.nova.personalai.MemoryType
import com.nova.personalai.NovaMemory
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun NovaMemoryScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val theme = LocalNovaTheme.current
    val scope = rememberCoroutineScope()

    val auth: FirebaseAuth? = remember {
        try {
            Firebase.auth
        } catch (_: Exception) {
            null
        }
    }

    val repo = remember(context) {
        FirebaseMemoryRepository(context)
    }

    var currentUser by remember {
        mutableStateOf(auth?.currentUser)
    }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MemoryType.CHAT) }
    var filterType by remember { mutableStateOf<MemoryType?>(null) }
    var memories by remember { mutableStateOf(emptyList<NovaMemory>()) }
    var message by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    fun refresh() {
        scope.launch {
            busy = true
            try {
                memories = repo.list(filterType)
                message = if (currentUser != null) {
                    "✓ ক্লাউড ও লোকাল স্মৃতি লোড হয়েছে (${memories.size}টি আইটেম)।"
                } else {
                    "স্মৃতি লোড হয়েছে (${memories.size}টি লোকাল আইটেম)।"
                }
            } catch (e: Exception) {
                message = e.message ?: "Memory লোড করা যায়নি।"
            } finally {
                busy = false
            }
        }
    }

    LaunchedEffect(currentUser, filterType) {
        refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = theme.surface),
            border = BorderStroke(1.2.dp, Brush.horizontalGradient(theme.gradientColors)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(theme.primary.copy(alpha = 0.2f))
                            .border(1.2.dp, theme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = theme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NOVA MEMORY SYSTEM",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (currentUser != null) "Firebase Firestore ক্লাউড কানেক্টেড" else "লোকাল ও ক্লাউড স্মৃতি হাব",
                            fontSize = 11.sp,
                            color = CyberTextSecondary
                        )
                    }
                }

                if (currentUser != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberNeonGreen.copy(alpha = 0.15f))
                            .border(0.8.dp, CyberNeonGreen, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = CyberNeonGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "CLOUD SYNC",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberNeonGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberNeonAmber.copy(alpha = 0.15f))
                            .border(0.8.dp, CyberNeonAmber, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Cloud, contentDescription = null, tint = CyberNeonAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LOCAL ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberNeonAmber,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        if (busy) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = theme.primary,
                trackColor = theme.surface
            )
        }

        // Account status banner / Google Sign-In Card
        if (currentUser == null) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                border = BorderStroke(1.dp, theme.borderGlow.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = theme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ক্লাউড ব্যাকআপ ও মাল্টি-ডিভাইস সিঙ্ক",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )
                    }
                    Text(
                        text = "গুগল সাইন-ইন করলে আপনার চ্যাট, পছন্দ ও অটোমেশন ওয়ার্কফ্লো ক্লাউডে স্থায়ী থাকবে।",
                        fontSize = 11.sp,
                        color = CyberTextSecondary
                    )

                    Button(
                        onClick = {
                            val act = context as? Activity
                            if (act == null) {
                                message = "Activity পাওয়া যায়নি।"
                                return@Button
                            }
                            val clientId = try {
                                context.getString(R.string.default_web_client_id)
                            } catch (_: Exception) {
                                ""
                            }
                            if (clientId.isBlank()) {
                                message = "Google Client ID এখনও লোড হয়নি।"
                                return@Button
                            }

                            busy = true
                            scope.launch {
                                try {
                                    val credentialManager = CredentialManager.create(context)
                                    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
                                    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

                                    val result = credentialManager.getCredential(act, request)
                                    val credential = result.credential
                                    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                                        val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                                        auth?.signInWithCredential(authCredential)?.await()
                                        currentUser = auth?.currentUser
                                        message = "✓ Google দিয়ে সাইন ইন সফল হয়েছে!"
                                        refresh()
                                    } else {
                                        message = "অন্যান্য ক্রেডেনশিয়াল টাইপ পাওয়া গেছে।"
                                    }
                                } catch (e: GetCredentialCancellationException) {
                                    Log.w("Auth", "Google Sign-In cancelled: ${e.message}")
                                    message = "সাইন-ইন বাতিল করা হয়েছে।"
                                } catch (e: Exception) {
                                    Log.e("Auth", "Google Sign-In failed", e)
                                    message = "Google Sign-In: ${e.localizedMessage ?: "ব্যর্থ হয়েছে"}"
                                } finally {
                                    busy = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_google_button")
                    ) {
                        Text(
                            text = "Google দিয়ে সাইন ইন করুন",
                            color = Color(0xFF001524),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = theme.cardBg),
                border = BorderStroke(0.8.dp, theme.borderGlow.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = theme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentUser?.displayName ?: currentUser?.email ?: "Google User",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberTextPrimary
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                try {
                                    auth?.signOut()
                                    val credentialManager = CredentialManager.create(context)
                                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                                    currentUser = null
                                    message = "সাইন আউট সম্পন্ন হয়েছে।"
                                    refresh()
                                } catch (_: Exception) {}
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.8.dp, CyberNeonRed),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("memory_signout_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = CyberNeonRed, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sign Out", fontSize = 10.sp, color = CyberNeonRed)
                    }
                }
            }
        }

        // Memory Creator Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = theme.surface),
            border = BorderStroke(1.dp, theme.borderGlow.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "স্মৃতি ক্যাটাগরি নির্বাচন করুন:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.primary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MemoryType.values().forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = type },
                            label = { Text(type.titleBn, fontSize = 10.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = theme.primary.copy(alpha = 0.25f),
                                selectedLabelColor = theme.primary
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("স্মৃতির শিরোনাম (যেমন: পছন্দের ভাষা / প্রজেক্ট নোট)", fontSize = 11.sp, color = CyberTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("memory_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.primary,
                        unfocusedBorderColor = theme.borderGlow.copy(alpha = 0.4f),
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("স্মৃতির বিস্তারিত তথ্য...", fontSize = 11.sp, color = CyberTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("memory_content_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.primary,
                        unfocusedBorderColor = theme.borderGlow.copy(alpha = 0.4f),
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2,
                    maxLines = 4
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        enabled = !busy && title.isNotBlank() && content.isNotBlank(),
                        onClick = {
                            scope.launch {
                                busy = true
                                try {
                                    repo.save(selectedType, title, content)
                                    title = ""
                                    content = ""
                                    memories = repo.list(filterType)
                                    message = "✓ স্মৃতি সফলভাবে সংরক্ষিত হয়েছে।"
                                } catch (e: Exception) {
                                    message = e.message ?: "Save ব্যর্থ হয়েছে।"
                                } finally {
                                    busy = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("memory_save_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF001524), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("স্মৃতি সংরক্ষণ করুন", color = Color(0xFF001524), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { refresh() },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, theme.primary),
                        modifier = Modifier.testTag("memory_refresh_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = theme.primary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Memories List Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "সংরক্ষিত মেমোরি তালিকা (${memories.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = theme.primary,
                fontFamily = FontFamily.Monospace
            )
        }

        if (memories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.surface)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Cloud, contentDescription = null, tint = CyberTextSecondary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("এখনও কোনো স্মৃতি সংরক্ষণ করা হয়নি", fontSize = 12.sp, color = CyberTextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(memories, key = { it.id }) { memory ->
                    val badgeColor = when (memory.type) {
                        MemoryType.CHAT.name -> CyberNeonCyan
                        MemoryType.PREFERENCE.name -> CyberNeonAmber
                        MemoryType.WORKFLOW.name -> CyberNeonPurple
                        MemoryType.PROJECT.name -> CyberNeonGreen
                        else -> theme.primary
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = theme.surface),
                        border = BorderStroke(0.8.dp, badgeColor.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(badgeColor.copy(alpha = 0.18f))
                                            .border(0.6.dp, badgeColor, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = memory.type,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeColor,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = memory.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberTextPrimary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                repo.delete(memory.id)
                                                memories = repo.list(filterType)
                                                message = "স্মৃতি মুছে ফেলা হয়েছে।"
                                            } catch (e: Exception) {
                                                message = e.message ?: "Delete ব্যর্থ।"
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CyberNeonRed, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = memory.content,
                                fontSize = 11.sp,
                                color = CyberTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Status or Toast Message
        if (message.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.surface)
                    .border(0.8.dp, theme.borderGlow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = theme.primary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = message,
                        fontSize = 10.5.sp,
                        color = CyberTextSecondary
                    )
                }
            }
        }
    }
}
