package com.nova.personalai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import com.nova.personalai.cybersecurity.NovaCybersecurityScreen
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.CyberNeonPink
import com.example.ui.theme.CyberNeonPurple
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.LocalNovaTheme
import com.example.ui.theme.NovaThemePreset
import kotlinx.coroutines.launch

data class AcademyLesson(
    val title: String,
    val explanation: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val practicalTask: String
)

data class AcademyCourse(
    val title: String,
    val description: String,
    val lessons: List<AcademyLesson>,
    val badge: String = "ACADEMY",
    val accentColor: Color = CyberNeonCyan
)

val novaCourses = listOf(
    AcademyCourse(
        title = "Networking ও Internet",
        description = "IP, DNS, router, server, ports ও network protocols",
        badge = "NETWORKING",
        accentColor = CyberNeonCyan,
        lessons = listOf(
            AcademyLesson(
                title = "IP Address কী?",
                explanation = "IP address হলো নেটওয়ার্কে ডিভাইস বা network interface শনাক্ত করতে ব্যবহৃত ঠিকানা। IPv4 ও IPv6 প্রচলিত সংস্করণ। লোকালহোস্ট IP হলো 127.0.0.1।",
                question = "IP address-এর প্রধান কাজ কী?",
                options = listOf(
                    "ডিভাইসকে নেটওয়ার্কে শনাক্ত করতে সাহায্য করা",
                    "ফোনের ব্যাটারি চার্জ করা",
                    "স্ক্রিনের আলো বাড়ানো"
                ),
                correctAnswer = 0,
                practicalTask = "ফোনের Settings থেকে Wi-Fi network-এর details খুলে IP address কোথায় দেখায় খুঁজে বের করো।"
            ),
            AcademyLesson(
                title = "DNS কী?",
                explanation = "DNS (Domain Name System) সাধারণ মানুষের পঠনযোগ্য domain name (যেমন google.com)-কে সংশ্লিষ্ট IP address-এর সঙ্গে মেলাতে সাহায্য করে।",
                question = "DNS প্রধানত কী করে?",
                options = listOf(
                    "ওয়েবসাইটের নাম থেকে সার্ভারের ঠিকানা খুঁজতে সাহায্য করে",
                    "ফোনের ছবি সম্পাদনা করে",
                    "পাসওয়ার্ড স্বয়ংক্রিয়ভাবে বদলায়"
                ),
                correctAnswer = 0,
                practicalTask = "একটি browser-এ পরিচিত website-এর domain name লেখো এবং পেজটি কীভাবে লোড হয় লক্ষ্য করো।"
            )
        )
    ),
    AcademyCourse(
        title = "Ethical Hacking ও Cybersecurity",
        description = "Threat, vulnerability, phishing ও নিরাপদ testing ল্যাব",
        badge = "CYBERSECURITY",
        accentColor = CyberNeonPink,
        lessons = listOf(
            AcademyLesson(
                title = "Ethical Hacking কী?",
                explanation = "অনুমতি ও নির্ধারিত সীমার মধ্যে সিস্টেম পরীক্ষা করে দুর্বলতা শনাক্ত করা এবং সেগুলো ঠিক করতে সাহায্য করাই Ethical Hacking। সাদা টুপির (White Hat) হ্যাকাররা সর্বদা অনুমোদনের ভিত্তিতে কাজ করেন।",
                question = "নিরাপত্তা পরীক্ষা করার আগে কী প্রয়োজন?",
                options = listOf(
                    "সিস্টেমের মালিকের অনুমতি ও নির্দিষ্ট scope",
                    "অন্যের পাসওয়ার্ড",
                    "গোপনে প্রবেশের চেষ্টা"
                ),
                correctAnswer = 0,
                practicalTask = "একটি নিজের test project বা অনুমোদিত training lab বেছে নিয়ে তার scope লিখে রাখো।"
            ),
            AcademyLesson(
                title = "Phishing চিনবে কীভাবে?",
                explanation = "Phishing হলো ভুয়া বার্তা বা website দিয়ে মানুষকে পাসওয়ার্ড, verification code বা অন্য গোপন তথ্য দিতে প্রলুব্ধ করার চেষ্টা। ভুয়া URL, প্রেরকের সন্দেহজনক ইমেইল ও অযৌক্তিক জরুরিতা এর প্রধান লক্ষণ।",
                question = "সন্দেহজনক login link পেলে কী করবে?",
                options = listOf(
                    "লিংক যাচাই না করে পাসওয়ার্ড দেবে",
                    "অফিসিয়াল app বা website নিজে খুলে যাচাই করবে",
                    "verification code অন্যকে পাঠাবে"
                ),
                correctAnswer = 1,
                practicalTask = "একটি সন্দেহজনক বার্তায় sender, domain, জরুরি চাপ এবং অস্বাভাবিক অনুরোধ আছে কি না পরীক্ষা করো।"
            )
        )
    ),
    AcademyCourse(
        title = "Wi-Fi ও Android Security",
        description = "Router, WPA2/WPA3, permissions ও device protection",
        badge = "DEFENSE",
        accentColor = CyberNeonGreen,
        lessons = listOf(
            AcademyLesson(
                title = "Wi-Fi সুরক্ষিত রাখো",
                explanation = "শক্তিশালী unique Wi-Fi password, WPA2-AES বা WPA3, router firmware update এবং admin password পরিবর্তন নিরাপত্তা বাড়ায়। ডিফল্ট পাসওয়ার্ড ব্যবহার অত্যন্ত বিপজ্জনক।",
                question = "কোনটি ভালো নিরাপত্তা অভ্যাস?",
                options = listOf(
                    "সবার সঙ্গে router admin password ভাগ করা",
                    "ডিফল্ট admin password রেখে দেওয়া",
                    "শক্তিশালী password ও আধুনিক security mode ব্যবহার করা"
                ),
                correctAnswer = 2,
                practicalTask = "নিজের router-এর security settings দেখো। পরিবর্তনের আগে ISP বা router নির্দেশিকা যাচাই করো।"
            )
        )
    ),
    AcademyCourse(
        title = "Website, PHP ও Database",
        description = "HTML, PHP, SQL, authentication এবং web application security",
        badge = "PHP & DB",
        accentColor = CyberNeonPurple,
        lessons = listOf(
            AcademyLesson(
                title = "PHP কী?",
                explanation = "PHP একটি server-side programming language। এটি form handling, database interaction এবং dynamic web page ও API তৈরিতে ব্যবহার করা যায়।",
                question = "PHP সাধারণত কোথায় চলে?",
                options = listOf(
                    "Server-এ",
                    "শুধু router-এর ভেতরে",
                    "শুধু monitor-এ"
                ),
                correctAnswer = 0,
                practicalTask = "একটি নিরাপদ PHP tutorial থেকে একটি সাধারণ Hello World program পড়ো অথবা Termux-এ 'php -v' রান করে দেখো।"
            ),
            AcademyLesson(
                title = "SQL Injection প্রতিরোধ",
                explanation = "SQL Injection ঝুঁকি কমাতে parameterized queries বা prepared statements ব্যবহার করো। কখনো ব্যবহারকারীর ইনপুট সরাসরি SQL string-এ যুক্ত করবে না।",
                question = "SQL Injection প্রতিরোধে কোনটি উপযুক্ত?",
                options = listOf(
                    "User input সরাসরি SQL string-এ জোড়া লাগানো",
                    "Parameterized queries ব্যবহার করা",
                    "সব database user-কে admin করা"
                ),
                correctAnswer = 1,
                practicalTask = "নিজের test project-এ prepared statements-এর উদাহরণ দেখো। বাস্তব website-এ অনুমতি ছাড়া পরীক্ষা করবে না।"
            )
        )
    ),
    AcademyCourse(
        title = "Email ও Facebook Security",
        description = "Account protection, 2FA, login alerts ও recovery",
        badge = "IDENTITY",
        accentColor = CyberNeonCyan,
        lessons = listOf(
            AcademyLesson(
                title = "Account compromise শনাক্তকরণ",
                explanation = "অচেনা login alert, অজানা recovery change, নিজের করা নয় এমন message এবং অপ্রত্যাশিত session account compromise-এর লক্ষণ হতে পারে। সর্বদা Two-Factor Authentication (2FA) চালু রাখুন।",
                question = "সন্দেহজনক login দেখলে কী করবে?",
                options = listOf(
                    "সন্দেহজনক session উপেক্ষা করবে",
                    "অফিসিয়াল security settings থেকে session পরীক্ষা ও password সুরক্ষিত করবে",
                    "OTP অন্যকে দেবে"
                ),
                correctAnswer = 1,
                practicalTask = "নিজের account-এর official security page-এ গিয়ে recent activity ও recovery options পরীক্ষা করো।"
            )
        )
    ),
    AcademyCourse(
        title = "Digital Marketing ও YouTube SEO",
        description = "Keyword research, titles, thumbnails ও analytics",
        badge = "MARKETING",
        accentColor = Color(0xFFFFB300),
        lessons = listOf(
            AcademyLesson(
                title = "YouTube SEO-এর ভিত্তি",
                explanation = "প্রাসঙ্গিক title, সঠিক description, আকর্ষণীয় thumbnail, দর্শক ধরে রাখার মতো content এবং analytics ভিডিও খুঁজে পেতে ও বুঝতে সাহায্য করে। ক্লীকবেইট নয়, কাজের তথ্যই আসল।",
                question = "ভালো YouTube SEO-এর অংশ কোনটি?",
                options = listOf(
                    "ভিডিওর সঙ্গে সম্পর্কহীন keyword ভরে দেওয়া",
                    "দর্শকের প্রয়োজন অনুযায়ী title ও content তৈরি করা",
                    "ভুয়া views কেনা"
                ),
                correctAnswer = 1,
                practicalTask = "একটি ভিডিওর বিষয় লিখে তিনটি প্রাসঙ্গিক keyword এবং দুটি সৎ title তৈরি করো।"
            )
        )
    ),
    AcademyCourse(
        title = "Automation ও AI Workflows",
        description = "Workflow steps, approvals, APIs এবং task status",
        badge = "AUTOMATION",
        accentColor = CyberNeonGreen,
        lessons = listOf(
            AcademyLesson(
                title = "Workflow Automation কী?",
                explanation = "Workflow automation নির্ধারিত ধাপগুলো ক্রমানুসারে চালায়, ফলাফল সংরক্ষণ করে এবং প্রয়োজন হলে মানুষের অনুমতি চায়। এটি জটিল কাজকে নির্ভুল ও দ্রুত করে তোলে।",
                question = "গুরুত্বপূর্ণ কাজ প্রকাশের আগে কী রাখা উচিত?",
                options = listOf(
                    "অনুমতি ও যাচাইয়ের ধাপ (Review & Approval)",
                    "কোনো error handling না রাখা",
                    "সব গোপন key public করা"
                ),
                correctAnswer = 0,
                practicalTask = "NOVA-র জন্য একটি workflow লেখো: idea → draft → review → approval → publish।"
            )
        )
    )
)

private const val ACADEMY_PREFS = "nova_academy_progress"
private const val KEY_COMPLETED_LESSONS = "completed_lessons_set"

@Composable
fun NovaAcademyScreen(
    currentPreset: NovaThemePreset = LocalNovaTheme.current,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedCourse by remember { mutableStateOf<AcademyCourse?>(null) }
    var selectedLesson by remember { mutableStateOf<AcademyLesson?>(null) }
    var chosenAnswer by remember { mutableIntStateOf(-1) }
    var checkedAnswer by remember { mutableStateOf(false) }

    // Persistent Completed Lessons
    var completed by remember { mutableStateOf(setOf<String>()) }

    // AI Tutor States
    var isAskingTutor by remember { mutableStateOf(false) }
    var tutorExplanation by remember { mutableStateOf("") }
    var showTutorCard by remember { mutableStateOf(false) }

    // Load saved progress from SharedPreferences
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences(ACADEMY_PREFS, Context.MODE_PRIVATE)
        val saved = prefs.getStringSet(KEY_COMPLETED_LESSONS, emptySet()) ?: emptySet()
        completed = saved
    }

    // Save progress helper
    fun markLessonCompleted(key: String) {
        val updated = completed + key
        completed = updated
        val prefs = context.getSharedPreferences(ACADEMY_PREFS, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_COMPLETED_LESSONS, updated).apply()

        // Sync with Firebase Memory asynchronously if signed in
        scope.launch {
            try {
                FirebaseMemoryRepository(context).save(
                    type = MemoryType.PROJECT,
                    title = "Academy Progress: $key",
                    content = "Completed quiz and lesson: $key successfully."
                )
            } catch (_: Exception) {}
        }
    }

    // BackHandler for smooth stack unwinding
    BackHandler {
        when {
            selectedLesson != null -> {
                selectedLesson = null
                chosenAnswer = -1
                checkedAnswer = false
                showTutorCard = false
                tutorExplanation = ""
            }
            selectedCourse != null -> {
                selectedCourse = null
            }
            else -> {
                onNavigateBack()
            }
        }
    }

    val lessonKey = (selectedCourse?.title ?: "") + "|" + (selectedLesson?.title ?: "")
    val totalLessons = novaCourses.sumOf { it.lessons.size }
    val progressFraction = if (totalLessons > 0) completed.size.toFloat() / totalLessons.toFloat() else 0f

    var academyViewMode by remember { mutableIntStateOf(0) } // 0: Security Engine & Checklist, 1: Curriculum Courses

    if (selectedCourse == null && selectedLesson == null && academyViewMode == 0) {
        NovaCybersecurityScreen(
            modifier = Modifier.fillMaxSize(),
            currentPreset = currentPreset,
            onNavigateBack = onNavigateBack,
            onSwitchToCourses = { academyViewMode = 1 }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(currentPreset.background)
            .padding(16.dp)
    ) {
        // Academy Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedCourse != null || selectedLesson != null) {
                    IconButton(
                        onClick = {
                            if (selectedLesson != null) {
                                selectedLesson = null
                                chosenAnswer = -1
                                checkedAnswer = false
                                showTutorCard = false
                                tutorExplanation = ""
                            } else {
                                selectedCourse = null
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = currentPreset.primary
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(currentPreset.primary.copy(alpha = 0.15f))
                            .border(1.dp, currentPreset.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = currentPreset.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = "NOVA CYBER ACADEMY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = currentPreset.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Cybersecurity • Ethical Hacking • Networking",
                        fontSize = 10.sp,
                        color = CyberTextSecondary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = { academyViewMode = 0 },
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("switch_to_security_engine_btn"),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(currentPreset.primary, currentPreset.primary))
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = currentPreset.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Security",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentPreset.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D1B2A))
                        .border(1.dp, currentPreset.primary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${completed.size}/$totalLessons কুইজ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentPreset.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Overall Learning Progress Bar
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "শেখার অগ্রগতি",
                    fontSize = 11.sp,
                    color = CyberTextSecondary
                )
                Text(
                    text = "${(progressFraction * 100).toInt()}% সম্পন্ন",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberNeonGreen,
                    fontFamily = FontFamily.Monospace
                )
            }
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = CyberNeonGreen,
                trackColor = Color(0xFF1E293B)
            )
        }

        Spacer(Modifier.height(16.dp))

        // VIEW 1: LESSON DETAILS & QUIZ
        if (selectedLesson != null) {
            val lesson = selectedLesson!!
            val isAlreadyCompleted = lessonKey in completed

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    // Lesson Topic Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lesson.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = currentPreset.primary
                                )
                                if (isAlreadyCompleted) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberNeonGreen, modifier = Modifier.size(16.dp))
                                        Text("সম্পন্ন", fontSize = 11.sp, color = CyberNeonGreen, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Text(
                                text = lesson.explanation,
                                fontSize = 13.sp,
                                color = CyberTextPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // AI Tutor Button
                item {
                    OutlinedButton(
                        onClick = {
                            if (!showTutorCard) {
                                showTutorCard = true
                                if (tutorExplanation.isBlank()) {
                                    isAskingTutor = true
                                    scope.launch {
                                        try {
                                            val query = "NOVA Cyber Academy পাঠ: '${lesson.title}'.\n" +
                                                    "ব্যাখ্যা: ${lesson.explanation}\n" +
                                                    "একজন সাইবার সিকিউরিটি শিক্ষকের মতো সহজ বাংলায় আরও ২টি বাস্তব উদাহরণ ও হ্যাকিং প্রতিরোধে করণীয় সংক্ষেপে বুঝিয়ে বলো।"
                                            tutorExplanation = NovaApi.ask(query, "chat")
                                        } catch (e: Exception) {
                                            tutorExplanation = "⚠️ AI শিক্ষকের সাথে যোগাযোগ করা সম্ভব হয়নি: ${e.localizedMessage}"
                                        } finally {
                                            isAskingTutor = false
                                        }
                                    }
                                }
                            } else {
                                showTutorCard = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberNeonCyan)
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberNeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (showTutorCard) "🤖 এআই ব্যাখ্যা লুকান" else "🤖 NOVA AI শিক্ষকের কাছে আরও বুঝুন",
                            fontSize = 12.sp,
                            color = CyberNeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // AI Tutor Response Box
                if (showTutorCard) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF071B26)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberNeonCyan.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyberNeonCyan, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "NOVA AI TUTOR নোটস",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = CyberNeonCyan
                                    )
                                }

                                if (isAskingTutor) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberNeonCyan, strokeWidth = 2.dp)
                                        Text("AI ব্যাখ্যা প্রস্তুত করছে...", fontSize = 12.sp, color = CyberTextSecondary)
                                    }
                                } else {
                                    Text(
                                        text = tutorExplanation,
                                        fontSize = 12.5.sp,
                                        color = CyberTextPrimary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Quiz Section Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = CyberNeonPurple, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "কুইজ প্রশ্ন",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberNeonPurple,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = lesson.question,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextPrimary
                            )

                            // MCQ Options
                            lesson.options.forEachIndexed { index, option ->
                                val isSelected = chosenAnswer == index
                                val isCorrectOption = index == lesson.correctAnswer

                                val borderColor = when {
                                    !checkedAnswer && isSelected -> CyberNeonCyan
                                    checkedAnswer && isCorrectOption -> CyberNeonGreen
                                    checkedAnswer && isSelected && !isCorrectOption -> CyberNeonPink
                                    else -> Color(0xFF1E293B)
                                }

                                val bgColor = when {
                                    !checkedAnswer && isSelected -> Color(0xFF0F2537)
                                    checkedAnswer && isCorrectOption -> Color(0xFF09291A)
                                    checkedAnswer && isSelected && !isCorrectOption -> Color(0xFF2C0F1A)
                                    else -> CyberSurface
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(bgColor)
                                        .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                                        .clickable {
                                            if (!checkedAnswer) chosenAnswer = index
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        RadioButton(
                                            selected = chosenAnswer == index,
                                            onClick = {
                                                if (!checkedAnswer) chosenAnswer = index
                                            },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = if (checkedAnswer && !isCorrectOption) CyberNeonPink else CyberNeonCyan,
                                                unselectedColor = Color(0xFF64748B)
                                            )
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = option,
                                            fontSize = 12.5.sp,
                                            color = if (isSelected) CyberTextPrimary else CyberTextSecondary,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }

                            // Quiz Result Feedback
                            if (checkedAnswer) {
                                val correct = chosenAnswer == lesson.correctAnswer

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (correct) CyberNeonGreen.copy(alpha = 0.12f) else CyberNeonPink.copy(alpha = 0.12f))
                                        .border(1.dp, if (correct) CyberNeonGreen else CyberNeonPink, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(
                                            imageVector = if (correct) Icons.Default.Check else Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = if (correct) CyberNeonGreen else CyberNeonPink,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (correct) {
                                                "✓ সঠিক উত্তর! কুইজটি সফলভাবে সম্পন্ন হয়েছে।"
                                            } else {
                                                "ভুল উত্তর। সঠিক উত্তর: ${lesson.options[lesson.correctAnswer]}"
                                            },
                                            color = if (correct) CyberNeonGreen else CyberNeonPink,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Practical Task Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberNeonGreen, modifier = Modifier.size(14.dp))
                                                Text("হাতে-কলমে অনুশীলন (Lab Task)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = CyberNeonGreen)
                                            }
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                    val clip = ClipData.newPlainText("Lab Task", lesson.practicalTask)
                                                    clipboard?.setPrimaryClip(clip)
                                                    Toast.makeText(context, "অনুশীলন টাস্ক কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberTextSecondary, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        Text(
                                            text = lesson.practicalTask,
                                            fontSize = 12.sp,
                                            color = CyberTextPrimary,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        if (correct) {
                                            markLessonCompleted(lessonKey)
                                        }
                                        selectedLesson = null
                                        chosenAnswer = -1
                                        checkedAnswer = false
                                        showTutorCard = false
                                        tutorExplanation = ""
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("পাঠ তালিকায় ফিরে যাও", color = Color(0xFF021B0F), fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        checkedAnswer = true
                                        if (chosenAnswer == lesson.correctAnswer) {
                                            markLessonCompleted(lessonKey)
                                        }
                                    },
                                    enabled = chosenAnswer >= 0,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("verify_answer_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonCyan),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("উত্তর যাচাই করো", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    TextButton(
                        onClick = {
                            selectedLesson = null
                            chosenAnswer = -1
                            checkedAnswer = false
                            showTutorCard = false
                            tutorExplanation = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("← পরে করব / ফিরে যাও", color = CyberTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
        // VIEW 2: COURSE LESSONS LIST
        else if (selectedCourse != null) {
            val course = selectedCourse!!

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, course.accentColor.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(course.accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, course.accentColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = course.badge,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = course.accentColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = course.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextPrimary
                            )
                            Text(
                                text = course.description,
                                fontSize = 12.sp,
                                color = CyberTextSecondary
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "বিষয়সূচী ও পাঠ (${course.lessons.size}টি পাঠ)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = currentPreset.primary,
                        letterSpacing = 1.sp
                    )
                }

                items(course.lessons) { lesson ->
                    val key = course.title + "|" + lesson.title
                    val isDone = key in completed

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedLesson = lesson
                                chosenAnswer = -1
                                checkedAnswer = false
                                showTutorCard = false
                                tutorExplanation = ""
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDone) CyberNeonGreen.copy(alpha = 0.6f) else Color(0xFF1E293B)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = lesson.title,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTextPrimary
                                )
                                Text(
                                    text = lesson.explanation.take(65) + "...",
                                    fontSize = 11.sp,
                                    color = CyberTextMuted
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDone) CyberNeonGreen.copy(alpha = 0.15f) else CyberSurface)
                                    .border(1.dp, if (isDone) CyberNeonGreen else Color(0xFF334155), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isDone) "✓ সম্পন্ন" else "শুরু করুন",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) CyberNeonGreen else CyberNeonCyan
                                )
                            }
                        }
                    }
                }
            }
        }
        // VIEW 3: ALL COURSES DIRECTORY
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "সকল কোর্স ও প্রশিক্ষণ মডিউল (৭টি বিভাগ)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = currentPreset.primary,
                        letterSpacing = 1.sp
                    )
                }

                items(novaCourses) { course ->
                    val courseCompletedCount = course.lessons.count { (course.title + "|" + it.title) in completed }
                    val isAllCourseDone = courseCompletedCount == course.lessons.size && course.lessons.isNotEmpty()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedCourse = course },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAllCourseDone) CyberNeonGreen.copy(alpha = 0.5f) else Color(0xFF1E293B)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(course.accentColor.copy(alpha = 0.15f))
                                        .border(0.8.dp, course.accentColor, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = course.badge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = course.accentColor,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Text(
                                    text = "$courseCompletedCount/${course.lessons.size} সম্পন্ন",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAllCourseDone) CyberNeonGreen else CyberTextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = course.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTextPrimary
                            )

                            Text(
                                text = course.description,
                                fontSize = 11.5.sp,
                                color = CyberTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
