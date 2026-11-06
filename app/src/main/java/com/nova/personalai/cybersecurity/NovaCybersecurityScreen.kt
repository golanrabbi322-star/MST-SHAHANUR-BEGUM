package com.nova.personalai.cybersecurity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NovaThemePreset
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

data class SecurityModule(
    val title: String,
    val description: String,
    val lessons: List<String>,
    val checklist: List<String>,
    val icon: ImageVector = Icons.Default.Security,
    val tag: String = "DEFENSE"
)

val securityModules = listOf(
    SecurityModule(
        title = "Network Security",
        description = "নিজের নেটওয়ার্ক বুঝুন ও সুরক্ষিত রাখুন।",
        lessons = listOf(
            "IP address, DNS ও gateway কী",
            "TCP, UDP এবং port-এর ধারণা",
            "Router ও Wi-Fi নিরাপত্তা",
            "Network scan-এর অনুমোদিত ব্যবহার"
        ),
        checklist = listOf(
            "Router-এর admin password পরিবর্তন",
            "WPA2-AES বা WPA3 ব্যবহার",
            "Router firmware আপডেট",
            "অপরিচিত ডিভাইস যাচাই"
        ),
        icon = Icons.Default.Wifi,
        tag = "NETWORK"
    ),
    SecurityModule(
        title = "Ethical Hacking",
        description = "অনুমোদিত ল্যাবে নিরাপত্তা পরীক্ষা শিখুন।",
        lessons = listOf(
            "Ethical hacking-এর নিয়ম ও অনুমতি",
            "Reconnaissance-এর মৌলিক ধারণা",
            "Vulnerability ও risk assessment",
            "Finding লিখে security report তৈরি"
        ),
        checklist = listOf(
            "ল্যাবের মালিকের অনুমতি নিশ্চিত",
            "পরীক্ষার scope নির্ধারণ",
            "Production data সুরক্ষিত রাখা",
            "প্রতিটি finding-এর সমাধান লেখা"
        ),
        icon = Icons.Default.BugReport,
        tag = "LABS"
    ),
    SecurityModule(
        title = "Website / PHP Security",
        description = "নিজের ওয়েবসাইট ও backend সুরক্ষিত করুন।",
        lessons = listOf(
            "SQL injection কী ও প্রতিরোধ",
            "XSS এবং output encoding",
            "PHP input validation",
            "Password hashing ও secure sessions",
            "Firestore ও database access rules"
        ),
        checklist = listOf(
            "Prepared statements ব্যবহার",
            "Server-side input validation",
            "Password plain text-এ না রাখা",
            "Database rules পরীক্ষা",
            "Secrets source code-এ না রাখা"
        ),
        icon = Icons.Default.Language,
        tag = "WEB/BACKEND"
    ),
    SecurityModule(
        title = "Android Security",
        description = "Android অ্যাপের permission ও privacy বুঝুন।",
        lessons = listOf(
            "Android permissions কীভাবে কাজ করে",
            "APK permission review",
            "App data ও backup সুরক্ষা",
            "Unknown APK-এর ঝুঁকি",
            "Accessibility permission-এর নিরাপদ ব্যবহার"
        ),
        checklist = listOf(
            "অপ্রয়োজনীয় permission বন্ধ",
            "বিশ্বস্ত উৎস থেকে অ্যাপ ইনস্টল",
            "Android security update পরীক্ষা",
            "সন্দেহজনক অ্যাপ পর্যালোচনা"
        ),
        icon = Icons.Default.Android,
        tag = "MOBILE"
    ),
    SecurityModule(
        title = "Account & Phishing Defense",
        description = "Email, social media ও অনলাইন অ্যাকাউন্ট রক্ষা করুন।",
        lessons = listOf(
            "Phishing link শনাক্ত করা",
            "Password manager-এর ব্যবহার",
            "Two-factor authentication",
            "অ্যাকাউন্ট পুনরুদ্ধারের নিরাপদ পদ্ধতি"
        ),
        checklist = listOf(
            "প্রতিটি সাইটে আলাদা password",
            "2-step verification চালু",
            "Login activity পরীক্ষা",
            "Recovery email ও phone আপডেট"
        ),
        icon = Icons.Default.Lock,
        tag = "PRIVACY"
    ),
    SecurityModule(
        title = "Security Tools & Labs",
        description = "নিরাপদ টুল ব্যবহার ও ল্যাব অনুশীলন।",
        lessons = listOf(
            "Termux-এর মৌলিক ব্যবহার",
            "Network Analyzer দিয়ে নিজের নেটওয়ার্ক দেখা",
            "PCAPdroid দিয়ে অনুমোদিত traffic পর্যবেক্ষণ",
            "PortSwigger Web Security Academy",
            "রিপোর্ট ও remediation শেখা"
        ),
        checklist = listOf(
            "শুধু অনুমোদিত নেটওয়ার্ক পরীক্ষা",
            "টুলের permission যাচাই",
            "বাস্তব তথ্য ও credentials গোপন রাখা",
            "পরীক্ষার ফলাফল সংরক্ষণ"
        ),
        icon = Icons.Default.Terminal,
        tag = "TOOLS"
    )
)

private const val CYBER_PREFS = "nova_cybersecurity_prefs"
private const val KEY_COMPLETED = "completed_checklist_items"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaCybersecurityScreen(
    modifier: Modifier = Modifier,
    currentPreset: NovaThemePreset = NovaThemePreset.IMPERIAL_GOLD,
    onNavigateBack: () -> Unit = {},
    onSwitchToCourses: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val primaryColor = currentPreset.primary
    val cardBg = currentPreset.cardBg
    val textColor = CyberTextPrimary
    val textSub = CyberTextSecondary

    var selectedModule by remember { mutableStateOf(securityModules.first()) }
    var searchText by remember { mutableStateOf("") }
    var completedItems by remember { mutableStateOf(setOf<String>()) }
    var showReport by remember { mutableStateOf(false) }

    // Load persistent checklist progress
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences(CYBER_PREFS, Context.MODE_PRIVATE)
        completedItems = prefs.getStringSet(KEY_COMPLETED, emptySet()) ?: emptySet()
    }

    fun toggleChecklistItem(itemKey: String) {
        val next = if (completedItems.contains(itemKey)) {
            completedItems - itemKey
        } else {
            completedItems + itemKey
        }
        completedItems = next
        val prefs = context.getSharedPreferences(CYBER_PREFS, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_COMPLETED, next).apply()
    }

    val visibleModules = securityModules.filter { module ->
        searchText.isBlank() ||
        module.title.contains(searchText, ignoreCase = true) ||
        module.description.contains(searchText, ignoreCase = true) ||
        module.tag.contains(searchText, ignoreCase = true) ||
        module.lessons.any { it.contains(searchText, ignoreCase = true) } ||
        module.checklist.any { it.contains(searchText, ignoreCase = true) }
    }

    val totalTasks = securityModules.sumOf { it.checklist.size }
    val progressFraction = if (totalTasks > 0) completedItems.size.toFloat() / totalTasks.toFloat() else 0f
    val progressPercent = (progressFraction * 100).toInt()

    val reportText = buildString {
        appendLine("🛡️ NOVA CYBERSECURITY LEARNING & DEFENSE REPORT")
        appendLine("--------------------------------------------------")
        appendLine("Modules Analyzed: ${securityModules.size}")
        appendLine("Checklist Status: ${completedItems.size} / $totalTasks completed ($progressPercent%)")
        appendLine("Active Focus: ${selectedModule.title} (${selectedModule.tag})")
        appendLine()
        appendLine("📌 COMPLETED DEFENSE ITEMS:")
        if (completedItems.isEmpty()) {
            appendLine("  [No items marked yet. Start your checklist below.]")
        } else {
            completedItems.forEach { item ->
                appendLine("  ✅ $item")
            }
        }
        appendLine()
        appendLine("🔒 ALL MODULES & TASKS SUMMARY:")
        securityModules.forEach { mod ->
            val modCompleted = mod.checklist.count { completedItems.contains("${mod.title}:$it") }
            appendLine("• ${mod.title} [$modCompleted/${mod.checklist.size}]")
        }
        appendLine()
        appendLine("⚠️ Legal & Ethical Disclaimer:")
        appendLine("This report is an educational security checklist and training tool.")
        appendLine("Always adhere to ethical hacking boundaries and legal authorization.")
        appendLine("Generated by NOVA Personal AI Engine v2.0")
    }

    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(currentPreset.background),
        containerColor = currentPreset.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(primaryColor.copy(alpha = 0.15f))
                                .border(1.dp, primaryColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Security Shield",
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "CYBERSECURITY ENGINE",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp,
                                    color = primaryColor
                                )
                            )
                            Text(
                                text = "Ethical Hacking & Network Defense",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = textSub
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("cyber_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = primaryColor
                        )
                    }
                },
                actions = {
                    if (onSwitchToCourses != null) {
                        OutlinedButton(
                            onClick = onSwitchToCourses,
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("switch_to_courses_button"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(primaryColor, primaryColor))
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = primaryColor
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Courses",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        }
                    }
                    IconButton(
                        onClick = { showReport = true },
                        modifier = Modifier.testTag("open_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "Security Report",
                            tint = primaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = currentPreset.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Stats Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cyber_progress_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBg
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(primaryColor.copy(alpha = 0.6f), Color(0xFF00FFCC).copy(alpha = 0.4f))
                        )
                    )
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Defense Readiness",
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "$progressPercent% Complete",
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = primaryColor,
                                fontSize = 13.sp
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = primaryColor,
                            trackColor = primaryColor.copy(alpha = 0.15f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tasks: ${completedItems.size} of $totalTasks",
                                fontSize = 11.sp,
                                color = textSub
                            )
                            Text(
                                text = "${securityModules.size} Active Modules",
                                fontSize = 11.sp,
                                color = textSub
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cyber_search_input"),
                    placeholder = {
                        Text(
                            text = "Search lessons, modules, or checklist...",
                            fontSize = 13.sp,
                            color = textSub.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = primaryColor
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { searchText = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = textSub
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = primaryColor.copy(alpha = 0.3f),
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg
                    )
                )
            }

            // Module Selector Chips (Horizontal Scroll)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SECURITY MODULES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = primaryColor,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        visibleModules.forEach { mod ->
                            val isSelected = mod.title == selectedModule.title
                            val chipBg by animateColorAsState(
                                targetValue = if (isSelected) primaryColor.copy(alpha = 0.2f) else cardBg,
                                label = "chipBg"
                            )
                            val borderCol = if (isSelected) primaryColor else primaryColor.copy(alpha = 0.2f)

                            Surface(
                                modifier = Modifier
                                    .clickable { selectedModule = mod }
                                    .testTag("module_chip_${mod.tag}"),
                                shape = RoundedCornerShape(20.dp),
                                color = chipBg,
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(borderCol, borderCol)))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = mod.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) primaryColor else textSub,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = mod.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) primaryColor else textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Selected Module Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(primaryColor.copy(alpha = 0.4f), Color.Transparent)
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedModule.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = primaryColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = selectedModule.tag,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = primaryColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = selectedModule.description,
                            fontSize = 13.sp,
                            color = textSub,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Lessons Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "LEARNING LESSONS (${selectedModule.lessons.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = primaryColor,
                        letterSpacing = 1.sp
                    )
                }
            }

            itemsIndexed(selectedModule.lessons) { index, lesson ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(primaryColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        }
                        Text(
                            text = lesson,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Interactive Checklist Section
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "ACTION CHECKLIST",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00E676),
                            letterSpacing = 1.sp
                        )
                    }

                    val moduleCompletedCount = selectedModule.checklist.count {
                        completedItems.contains("${selectedModule.title}:$it")
                    }
                    Text(
                        text = "$moduleCompletedCount / ${selectedModule.checklist.size}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = textSub
                    )
                }
            }

            items(selectedModule.checklist) { task ->
                val taskKey = "${selectedModule.title}:$task"
                val isDone = completedItems.contains(taskKey)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { toggleChecklistItem(taskKey) }
                        .testTag("checklist_item_${task.hashCode()}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDone) primaryColor.copy(alpha = 0.08f) else cardBg
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            if (isDone) listOf(Color(0xFF00E676).copy(alpha = 0.5f), Color(0xFF00E676).copy(alpha = 0.2f))
                            else listOf(primaryColor.copy(alpha = 0.15f), primaryColor.copy(alpha = 0.05f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(
                            checked = isDone,
                            onCheckedChange = { toggleChecklistItem(taskKey) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF00E676),
                                checkmarkColor = Color.Black,
                                uncheckedColor = textSub
                            )
                        )

                        Text(
                            text = task,
                            fontSize = 13.sp,
                            color = if (isDone) textSub else textColor,
                            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                            fontWeight = if (isDone) FontWeight.Normal else FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Bottom Spacer & Action Card
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showReport = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("view_report_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate Security Report (রিপোর্ট দেখুন)",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Security Report Dialog
    if (showReport) {
        AlertDialog(
            onDismissRequest = { showReport = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = primaryColor
                    )
                    Text(
                        text = "Security Learning Report",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            },
            text = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        item {
                            Text(
                                text = reportText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF00FFCC),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("NOVA Security Report", reportText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Report copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("copy_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy")
                    }

                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "NOVA Cybersecurity Learning Report")
                                putExtra(Intent.EXTRA_TEXT, reportText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Security Report"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.testTag("share_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", color = Color.Black)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showReport = false },
                    modifier = Modifier.testTag("close_report_button")
                ) {
                    Text("Close", color = textSub)
                }
            },
            containerColor = cardBg
        )
    }
}
