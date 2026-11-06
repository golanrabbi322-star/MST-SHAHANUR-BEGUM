package com.nova.personalai.phone

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import java.net.URLEncoder
import java.util.Locale

data class PhoneActionResult(
    val handled: Boolean,
    val feedbackMessage: String,
    val actionType: String = ""
)

data class AppItem(
    val label: String,
    val packageName: String,
    val iconRes: Int? = null
)

class PhoneActionManager(
    private val context: Context
) {
    // In-memory cache of installed apps for sub-millisecond lookups
    private var cachedInstalledApps: List<AppItem>? = null

    private fun getInstalledApps(forceRefresh: Boolean = false): List<AppItem> {
        if (!forceRefresh && cachedInstalledApps != null) {
            return cachedInstalledApps!!
        }

        return try {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolvedList: List<ResolveInfo> = pm.queryIntentActivities(mainIntent, 0)
            val list = resolvedList.mapNotNull { resolveInfo ->
                val label = resolveInfo.loadLabel(pm).toString().trim()
                val pkg = resolveInfo.activityInfo?.packageName
                if (!pkg.isNullOrBlank() && label.isNotBlank()) {
                    AppItem(label = label, packageName = pkg)
                } else null
            }.distinctBy { it.packageName }

            cachedInstalledApps = list
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun launchPackage(packageName: String): Boolean {
        return try {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                context.startActivity(launchIntent)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    fun openPlayStoreForApp(packageName: String? = null, query: String? = null): Boolean {
        return try {
            val uri = when {
                !packageName.isNullOrBlank() -> Uri.parse("market://details?id=$packageName")
                !query.isNullOrBlank() -> Uri.parse("market://search?q=${URLEncoder.encode(query, "UTF-8")}")
                else -> Uri.parse("market://search?q=apps")
            }
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            // Fallback to web Play Store
            try {
                val webUrl = when {
                    !packageName.isNullOrBlank() -> "https://play.google.com/store/apps/details?id=$packageName"
                    !query.isNullOrBlank() -> "https://play.google.com/store/search?q=${URLEncoder.encode(query, "UTF-8")}&c=apps"
                    else -> "https://play.google.com/store"
                }
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun openYouTube(query: String? = null): Boolean {
        return try {
            val intent = if (!query.isNullOrBlank()) {
                val encoded = URLEncoder.encode(query, "UTF-8")
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$encoded")).apply {
                    setPackage("com.google.android.youtube")
                }
            } else {
                context.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                    ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://m.youtube.com"))
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
                true
            } catch (_: Exception) {
                // Fallback to web browser
                val webUrl = if (!query.isNullOrBlank()) {
                    "https://www.youtube.com/results?search_query=${URLEncoder.encode(query, "UTF-8")}"
                } else {
                    "https://m.youtube.com"
                }
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
                true
            }
        } catch (_: Exception) {
            false
        }
    }

    fun openWebSearch(query: String): Boolean {
        return try {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val url = "https://www.google.com/search?q=$encoded"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openBrowser(url: String = "https://www.google.com"): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openCamera(): Boolean {
        return try {
            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            try {
                val fallback = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun openGallery(): Boolean {
        val galleryPackages = listOf(
            "com.google.android.apps.photos",
            "com.android.gallery3d",
            "com.miui.gallery",
            "com.sec.android.gallery3d"
        )
        for (pkg in galleryPackages) {
            if (launchPackage(pkg)) return true
        }
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "image/*"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openDialer(phoneNumber: String? = null): Boolean {
        return try {
            val uri = if (phoneNumber.isNullOrBlank()) Uri.parse("tel:") else Uri.parse("tel:$phoneNumber")
            val intent = Intent(Intent.ACTION_DIAL, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openCalculator(): Boolean {
        val calcPackages = listOf(
            "com.google.android.calculator",
            "com.android.calculator2",
            "com.sec.android.app.popupcalculator",
            "com.miui.calculator"
        )
        for (pkg in calcPackages) {
            if (launchPackage(pkg)) return true
        }
        return openWebSearch("calculator")
    }

    fun openClock(): Boolean {
        return try {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openMaps(place: String? = null): Boolean {
        return try {
            val uri = if (!place.isNullOrBlank()) {
                Uri.parse("geo:0,0?q=${URLEncoder.encode(place, "UTF-8")}")
            } else {
                Uri.parse("geo:0,0?q=")
            }
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openGmail(): Boolean {
        if (launchPackage("com.google.android.gm")) return true
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            openBrowser("https://mail.google.com")
        }
    }

    fun openMessages(): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_MESSAGING)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openFiles(): Boolean {
        val filePackages = listOf(
            "com.google.android.apps.nbu.files",
            "com.google.android.documentsui",
            "com.sec.android.app.myfiles",
            "com.mi.android.globalFileexplorer"
        )
        for (pkg in filePackages) {
            if (launchPackage(pkg)) return true
        }
        return try {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun createNote(text: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Note সংরক্ষণ করুন").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Attempts to find and open any installed application by matching name or package.
     */
    fun openAppByName(appNameQuery: String): PhoneActionResult {
        val query = appNameQuery.lowercase().trim()
        if (query.isBlank()) {
            return PhoneActionResult(false, "")
        }

        val installedApps = getInstalledApps()

        // 1. Direct label match
        val matchedApp = installedApps.firstOrNull {
            it.label.lowercase() == query
        } ?: installedApps.firstOrNull {
            it.label.lowercase().contains(query) || query.contains(it.label.lowercase())
        } ?: installedApps.firstOrNull {
            it.packageName.lowercase().contains(query)
        }

        if (matchedApp != null) {
            val success = launchPackage(matchedApp.packageName)
            if (success) {
                return PhoneActionResult(
                    handled = true,
                    feedbackMessage = "⚡ '${matchedApp.label}' অ্যাপটি চালু করা হয়েছে!",
                    actionType = "APP_LAUNCHED"
                )
            }
        }

        // 2. Well-known third-party apps fallback with web or play store
        when {
            query.contains("facebook") || query.contains("ফেসবুক") || query.contains("fb") -> {
                if (launchPackage("com.facebook.katana") || launchPackage("com.facebook.lite")) {
                    return PhoneActionResult(true, "⚡ ফেসবুক অ্যাপ চালু করা হয়েছে!", "FACEBOOK")
                }
                openBrowser("https://m.facebook.com")
                return PhoneActionResult(true, "ফেসবুক ব্রাউজারে খুলে দেওয়া হয়েছে।", "FACEBOOK")
            }
            query.contains("whatsapp") || query.contains("হোয়াটসঅ্যাপ") || query.contains("হোয়াটস") -> {
                if (launchPackage("com.whatsapp") || launchPackage("com.whatsapp.w4b")) {
                    return PhoneActionResult(true, "⚡ WhatsApp অ্যাপ চালু করা হয়েছে!", "WHATSAPP")
                }
                openPlayStoreForApp("com.whatsapp")
                return PhoneActionResult(true, "WhatsApp অ্যাপটি ফোনে নেই, Play Store-এ নিয়ে যাওয়া হচ্ছে।", "WHATSAPP")
            }
            query.contains("messenger") || query.contains("মেসেঞ্জার") -> {
                if (launchPackage("com.facebook.orca") || launchPackage("com.facebook.mlite")) {
                    return PhoneActionResult(true, "⚡ Messenger অ্যাপ চালু করা হয়েছে!", "MESSENGER")
                }
                openPlayStoreForApp("com.facebook.orca")
                return PhoneActionResult(true, "Messenger Play Store-এ খোঁজা হচ্ছে।", "MESSENGER")
            }
            query.contains("instagram") || query.contains("ইনস্টাগ্রাম") || query.contains("insta") -> {
                if (launchPackage("com.instagram.android") || launchPackage("com.instagram.lite")) {
                    return PhoneActionResult(true, "⚡ Instagram অ্যাপ চালু করা হয়েছে!", "INSTAGRAM")
                }
                openBrowser("https://www.instagram.com")
                return PhoneActionResult(true, "Instagram ব্রাউজারে খুলে দেওয়া হয়েছে।", "INSTAGRAM")
            }
            query.contains("telegram") || query.contains("টেলিগ্রাম") -> {
                if (launchPackage("org.telegram.messenger") || launchPackage("org.telegram.messenger.web")) {
                    return PhoneActionResult(true, "⚡ Telegram অ্যাপ চালু করা হয়েছে!", "TELEGRAM")
                }
                openPlayStoreForApp("org.telegram.messenger")
                return PhoneActionResult(true, "Telegram Play Store-এ নিয়ে যাওয়া হচ্ছে।", "TELEGRAM")
            }
            query.contains("tiktok") || query.contains("টিকটক") -> {
                if (launchPackage("com.zhiliaoapp.musically") || launchPackage("com.zhiliaoapp.musically.go")) {
                    return PhoneActionResult(true, "⚡ TikTok চালু করা হয়েছে!", "TIKTOK")
                }
                openBrowser("https://www.tiktok.com")
                return PhoneActionResult(true, "TikTok ব্রাউজারে চালু করা হয়েছে।", "TIKTOK")
            }
            query.contains("play store") || query.contains("playstore") || query.contains("প্লে স্টোর") -> {
                openPlayStoreForApp()
                return PhoneActionResult(true, "Google Play Store চালু করা হয়েছে।", "PLAY_STORE")
            }
        }

        return PhoneActionResult(false, "")
    }

    /**
     * Master command processor for phone actions.
     * Evaluates commands in English, Bengali script, and Banglish (Romanized Bengali).
     */
    fun executeAction(command: String): PhoneActionResult {
        val raw = command.trim()
        val text = raw.lowercase(Locale.getDefault())

        // 1. YouTube actions
        if (text.contains("youtube") || text.contains("ইউটিউব") ||
            (text.contains("গান") && (text.contains("চালাও") || text.contains("শুনবো") || text.contains("chalao") || text.contains("play")))
        ) {
            val searchQuery = text
                .replace("open", "")
                .replace("kholo", "")
                .replace("khulo", "")
                .replace("chalu koro", "")
                .replace("chalao", "")
                .replace("youtube", "")
                .replace("ইউটিউব", "")
                .replace("খোলো", "")
                .replace("চালু করো", "")
                .replace("চালাও", "")
                .replace("গান", "")
                .replace("play", "")
                .replace("search", "")
                .trim()
            val success = openYouTube(if (searchQuery.isNotBlank()) searchQuery else null)
            return if (success) {
                PhoneActionResult(true, "⚡ ইউটিউব চালু করা হয়েছে!", "YOUTUBE")
            } else {
                PhoneActionResult(true, "ইউটিউব খুলতে পারছি না।", "ERROR")
            }
        }

        // 2. Camera actions
        if (text.contains("camera") || text.contains("ক্যামেরা") || text.contains("ছবি তোলো") ||
            text.contains("photo tolo") || text.contains("pic tolo") || text.contains("selfie")
        ) {
            val success = openCamera()
            return if (success) {
                PhoneActionResult(true, "📷 ক্যামেরা চালু করা হয়েছে!", "CAMERA")
            } else {
                PhoneActionResult(true, "ক্যামেরা চালু করতে সমস্যা হয়েছে।", "ERROR")
            }
        }

        // 3. Gallery / Photos actions
        if (text.contains("gallery") || text.contains("গ্যালারি") || text.contains("গ্যালারী") ||
            (text.contains("ছবি") && (text.contains("দেখাও") || text.contains("খোলো") || text.contains("dekhao"))) ||
            text.contains("photos")
        ) {
            val success = openGallery()
            return if (success) {
                PhoneActionResult(true, "🖼️ গ্যালারি চালু করা হয়েছে!", "GALLERY")
            } else {
                PhoneActionResult(true, "গ্যালারি ওপেন করা সম্ভব হয়নি।", "ERROR")
            }
        }

        // 4. Settings actions
        if (text.contains("settings") || text.contains("সেটিংস") || text.contains("setting") || text.contains("সেটিং")) {
            val success = openSettings()
            return if (success) {
                PhoneActionResult(true, "⚙️ ফোনের সেটিংস চালু করা হয়েছে!", "SETTINGS")
            } else {
                PhoneActionResult(true, "সেটিংস খুলতে পারছি না।", "ERROR")
            }
        }

        // 5. Calculator actions
        if (text.contains("calculator") || text.contains("ক্যালকুলেটর") || text.contains("cal") ||
            (text.contains("হিসাব") && (text.contains("খোলো") || text.contains("করবো")))
        ) {
            val success = openCalculator()
            return if (success) {
                PhoneActionResult(true, "🧮 ক্যালকুলেটর চালু করা হয়েছে!", "CALCULATOR")
            } else {
                PhoneActionResult(true, "ক্যালকুলেটর পাওয়া যায়নি।", "ERROR")
            }
        }

        // 6. Clock & Alarm
        if (text.contains("alarm") || text.contains("অ্যালার্ম") || text.contains("ঘড়ি") ||
            text.contains("ঘড়ি") || text.contains("clock") || text.contains("সময়") || text.contains("time")
        ) {
            val success = openClock()
            return if (success) {
                PhoneActionResult(true, "⏰ ঘড়ি ও অ্যালার্ম চালু করা হয়েছে!", "CLOCK")
            } else {
                PhoneActionResult(true, "ঘড়ি চালু করা যায়নি।", "ERROR")
            }
        }

        // 7. Phone Call / Dialer
        if (text.contains("dialer") || text.contains("ডায়ালার") || text.contains("ডায়ালার") ||
            text.contains("call") || text.contains("কল করো") || text.contains("ফোন লাগাও") || text.contains("phone")
        ) {
            val phoneRegex = Regex("\\b\\d{5,14}\\b")
            val numberMatch = phoneRegex.find(raw)?.value
            val success = openDialer(numberMatch)
            return if (success) {
                val msg = if (numberMatch != null) "📞 $numberMatch নম্বরে ডায়ালার ওপেন করা হয়েছে!" else "📞 ফোন ডায়ালার চালু করা হয়েছে!"
                PhoneActionResult(true, msg, "DIALER")
            } else {
                PhoneActionResult(true, "ডায়ালার ওপেন করা যায়নি।", "ERROR")
            }
        }

        // 8. SMS / Messages
        if (text.contains("sms") || text.contains("মেসেজ") || text.contains("messages") || text.contains("message")) {
            val success = openMessages()
            return if (success) {
                PhoneActionResult(true, "💬 মেসেজ অ্যাপ চালু করা হয়েছে!", "MESSAGES")
            } else {
                PhoneActionResult(true, "মেসেজ অ্যাপ পাওয়া যায়নি।", "ERROR")
            }
        }

        // 9. Files / Storage
        if (text.contains("files") || text.contains("file manager") || text.contains("ফাইল") || text.contains("my files")) {
            val success = openFiles()
            return if (success) {
                PhoneActionResult(true, "📁 ফাইল ম্যানেজার চালু করা হয়েছে!", "FILES")
            } else {
                PhoneActionResult(true, "ফাইল ম্যানেজার পাওয়া যায়নি।", "ERROR")
            }
        }

        // 10. Gmail / Email
        if (text.contains("gmail") || text.contains("ইমেইল") || text.contains("মেইল") || text.contains("email")) {
            val success = openGmail()
            return if (success) {
                PhoneActionResult(true, "📧 জিমেইল চালু করা হয়েছে!", "GMAIL")
            } else {
                PhoneActionResult(true, "জিমেইল ওপেন করা সম্ভব হয়নি।", "ERROR")
            }
        }

        // 11. Google Maps
        if (text.contains("maps") || text.contains("ম্যাপ") || text.contains("লোকেশন") || text.contains("location")) {
            val place = text
                .replace("maps", "")
                .replace("ম্যাপ", "")
                .replace("খোলো", "")
                .replace("kholo", "")
                .replace("open", "")
                .replace("লোকেশন", "")
                .trim()
            val success = openMaps(if (place.isNotBlank()) place else null)
            return if (success) {
                PhoneActionResult(true, "🗺️ ম্যাপ চালু করা হয়েছে!", "MAPS")
            } else {
                PhoneActionResult(true, "ম্যাপ ওপেন করা সম্ভব হয়নি।", "ERROR")
            }
        }

        // 12. Chrome / Browser / Web Search
        if (text.contains("chrome") || text.contains("ব্রাউজার") || text.contains("browser") ||
            text.contains("গুগল করো") || text.contains("search") || text.contains("সার্চ")
        ) {
            val query = text
                .replace("chrome", "")
                .replace("ব্রাউজার", "")
                .replace("browser", "")
                .replace("খোলো", "")
                .replace("kholo", "")
                .replace("open", "")
                .replace("গুগল করো", "")
                .replace("সার্চ করো", "")
                .replace("search", "")
                .trim()
            return if (query.isNotBlank()) {
                openWebSearch(query)
                PhoneActionResult(true, "🌐 '$query' গুগলে সার্চ করা হচ্ছে।", "BROWSER")
            } else {
                openBrowser()
                PhoneActionResult(true, "🌐 ব্রাউজার চালু করা হয়েছে!", "BROWSER")
            }
        }

        // 13. Note creation
        if (text.contains("note") || text.contains("নোট") || text.contains("স্মারকলিপি")) {
            val noteText = text
                .replace("নোট তৈরি করো", "")
                .replace("create note", "")
                .replace("একটি নোট লেখো", "")
                .replace("নোট", "")
                .trim()
            val content = if (noteText.isNotBlank()) noteText else "NOVA Note: ${java.util.Date()}"
            createNote(content)
            return PhoneActionResult(true, "📝 নোট তৈরি করার অপশন খোলা হয়েছে।", "NOTE")
        }

        // 14. Generic App Opening: "Open <App>", "<App> kholo", "<App> open koro", "<App> খোলো"
        val openPrefixes = listOf("open ", "launch ", "start ", "run ")
        for (prefix in openPrefixes) {
            if (text.startsWith(prefix)) {
                val appTarget = text.removePrefix(prefix).trim()
                val result = openAppByName(appTarget)
                if (result.handled) return result
            }
        }

        val openSuffixes = listOf(
            " kholo", " khulo", " open koro", " open", " chalu koro", " chalao",
            " খোলো", " চালু করো", " চালাও", " ওপেন করো", " ওপেন", " খুলুন", " চালু করুন"
        )
        for (suffix in openSuffixes) {
            if (text.endsWith(suffix)) {
                val appTarget = text.removeSuffix(suffix).trim()
                val result = openAppByName(appTarget)
                if (result.handled) return result
            }
        }

        // 15. Finally try single word matching with installed apps
        val words = text.split(" ").filter { it.isNotBlank() }
        for (word in words) {
            if (word.length >= 3) {
                val result = openAppByName(word)
                if (result.handled) return result
            }
        }

        return PhoneActionResult(false, "")
    }

    fun process(command: String): Boolean {
        val result = executeAction(command)
        return result.handled
    }
}
