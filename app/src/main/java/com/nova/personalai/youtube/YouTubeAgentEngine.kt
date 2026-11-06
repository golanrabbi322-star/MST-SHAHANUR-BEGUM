package com.nova.personalai.youtube

data class HookFormula(
    val id: String,
    val name: String,
    val template: String,
    val example: String,
    val category: String = "CURIOSITY"
)

val hookFormulas21 = listOf(
    HookFormula("hf_1", "The Counter-Intuitive Truth", "অধিকাংশ মানুষ ভাবে [X], কিন্তু বাস্তবতা হলো [Y]...", "অধিকাংশ মানুষ ভাবে বেশি ভিডিও বানালেই ভিউ বাড়ে, কিন্তু বাস্তবতা হলো প্রথম ৫ সেকেন্ডই সবকিছু!", "CURIOSITY"),
    HookFormula("hf_2", "The Cost of Inaction", "যদি আপনি এখনই [X] না বদলান, তবে [Y] হারিয়ে ফেলবেন...", "যদি আপনি এখনই থাম্বনেইল স্ট্র্যাটেজি না বদলান, তবে ৯০% দর্শক ক্লিক করার আগেই স্ক্রোল করে চলে যাবে...", "URGENCY"),
    HookFormula("hf_3", "The Negative Hook", "কখনোই [X] করবেন না, যদি না আপনি [Y] চান...", "কখনোই ইউটিউবে প্রতিদিন আপলোড করবেন না, যদি না আপনার স্ক্রিপ্টে স্ট্রং হুক থাকে...", "WARNING"),
    HookFormula("hf_4", "The Secret Vulnerability", "আমি গত [X] সময় ধরে ভুলভাবে [Y] করছিলাম...", "আমি গত দুই বছর ধরে ভুলভাবে টাইটেল লিখছিলাম, যতক্ষণ না এই ফর্মুলা পেয়েছি...", "RELATABLE"),
    HookFormula("hf_5", "The Proof / Result First", "এই একটি পরিবর্তন আমার ভিউ [X] থেকে [Y]-এ নিয়ে গেছে...", "এই একটি মাত্র ট্রিক আমার ওয়াচ টাইম ২ মিনিট থেকে ৭ মিনিটে নিয়ে গেছে...", "PROOF"),
    HookFormula("hf_6", "The Direct Challenge", "আপনি কি ১ মিনিটে [X] করতে পারেন? বাজি ধরে বলছি পারেন না!", "আপনি কি ১৫ সেকেন্ডে দর্শকের দৃষ্টি ধরে রাখতে পারেন? এই রুলটি না জানলে পারবেন না!", "CHALLENGE"),
    HookFormula("hf_7", "The Specific Number", "২০২৬ সালে সফল ইউটিউবার হওয়ার [৩/৫/৭]টি নিয়ম...", "ইউটিউব অ্যালগরিদমের ৩টি গোপন সংকেত যা কেউ আপনাকে বলবে না...", "SPECIFICITY"),
    HookFormula("hf_8", "The Curiosity Gap", "সবাই [X] নিয়ে কথা বলছে, কিন্তু আসল রহস্য লুকিয়ে আছে [Y]-তে...", "সবাই ক্যামেরা নিয়ে কথা বলছে, কিন্তু আসল ভিউ নির্ভর করে অডিও ও হুকের ওপর...", "CURIOSITY"),
    HookFormula("hf_9", "The Common Enemy", "ইউটিউব অ্যালগরিদম আসলে আপনার শত্রু নয়, শত্রু হলো [X]...", "অ্যালগরিদম খারাপ নয়, আপনার ইন্ট্রোতে ১৫ সেকেন্ডের ভরাট কথা দর্শককে তাড়াচ্ছে...", "PERSPECTIVE"),
    HookFormula("hf_10", "The Authority Statement", "শীর্ষ ১% ক্রিয়েটররা ভিডিও শুরুর আগে এই কাজটি করেন...", "মিস্টার বিস্ট প্রতি ভিডিওর শুরুতে এই ১টি মনস্তাত্ত্বিক ফ্রেম ব্যবহার করেন...", "AUTHORITY"),
    HookFormula("hf_11", "The \"If You Want X, Stop Doing Y\"", "যদি দ্রুত ১ লাখ সাবস্ক্রাইবার চান, আজই [X] বন্ধ করুন...", "যদি ওয়াচ টাইম বাড়াতে চান, ইন্ট্রো অ্যানিমেশন দেখানো আজই বন্ধ করুন...", "DIRECT"),
    HookFormula("hf_12", "The Bold Claim", "এটিই ইউটিউবে [X] করার সবচেয়ে সহজ ও দ্রুততম উপায়...", "এটিই মোবাইল দিয়ে ৪কে স্টুডিও কোয়ালিটি ভিডিও বানানোর সবচেয়ে সহজ উপায়...", "BOLD"),
    HookFormula("hf_13", "The Relatable Pain", "আপনি কি ঘণ্টার পর ঘণ্টা এডিট করেও ভিউ পাচ্ছেন না?...", "ঘণ্টার পর ঘণ্টা এডিট করেও ১০-২০ ভিউয়ে আটকে আছেন? সমাধান শুনুন...", "PAIN"),
    HookFormula("hf_14", "The Speed / Ease Hook", "মাত্র ১০ মিনিটে কীভাবে [X] স্ক্রিপ্ট লিখবেন...", "মাত্র ৫ মিনিটে এআই দিয়ে স্ক্রিপ্ট ও ভয়েসওভার বানানোর সঠিক গাইড...", "SPEED"),
    HookFormula("hf_15", "The Question with Presupposition", "কেন ৯৫% নতুন চ্যানেল প্রথম ৩ মাসেই বন্ধ হয়ে যায়?...", "কেন ৯৫% নতুন ক্রিয়েটর প্রথম ১০০ ভিডিওর পর হাল ছেড়ে দেন? কারণটি জেনে নিন...", "QUESTION"),
    HookFormula("hf_16", "The Timeline / Transformation", "০ থেকে [X] দিনে কীভাবে [Y] ফলাফল পেলাম...", "শূন্য থেকে ৩০ দিনে যেভাবে আমার ইউটিউব চ্যানেল মনিটাইজ হলো...", "STORY"),
    HookFormula("hf_17", "The Warning of a Common Trap", "এই ফাঁদে পড়লে আপনার চ্যানেল ডেড হয়ে যেতে পারে...", "সাব ফর সাব বা ফেক ভিউয়ের এই ফাঁদে পড়লে আপনার চ্যানেল ডেড হয়ে যাবে...", "WARNING"),
    HookFormula("hf_18", "The Framework Tease", "আমি একে বলি [X] মেথড, যা আপনার [Y] দ্বিগুণ করবে...", "আমি একে বলি '৩-সেকেন্ড গোল্ডেন রুল', যা রিটেনশন ৮০% বাড়িয়ে দেয়...", "FRAMEWORK"),
    HookFormula("hf_19", "The Unpopular Opinion", "আনপপুলার ওপিনিয়ন: এসইও আর কাজ করে না, যা কাজ করে তা হলো [X]...", "বিতর্কিত হলেও সত্যি: ট্যাগ বা ডেসক্রিপশন নয়, থাম্বনেইল ও প্রথম বাক্যই আসল...", "CONTRARIAN"),
    HookFormula("hf_20", "The Mystery Box", "ভিডিওর শেষে এমন ১টি তথ্য আছে যা সবকিছু বদলে দেবে...", "এই ভিডিওর ৩ নম্বর পয়েন্টটি জানলে আপনার পরবর্তী ভিডিও ভাইরাল হতে বাধ্য...", "MYSTERY"),
    HookFormula("hf_21", "The Micro-Story Hook", "গত মঙ্গলবার আমার চ্যানেলটি প্রায় ব্যান হতে যাচ্ছিল...", "একটি ভুলের কারণে আমার সম্পূর্ণ চ্যানেলটি গত সপ্তাহে ফ্রিজ হয়ে গিয়েছিল...", "NARRATIVE")
)

data class HookScore(
    val totalScore: Int, // 0 - 100
    val brevity: Int, // 0 - 20
    val clarity: Int, // 0 - 20
    val curiosity: Int, // 0 - 20
    val urgency: Int, // 0 - 20
    val grade: String, // A+, A, B, C, Need Rework
    val feedback: String
)

data class PackagingAudit(
    val score: Int, // 0 - 100
    val titleLength: Int,
    val isTruncatedMobile: Boolean,
    val hasDuplication: Boolean,
    val thumbnailComplement: String,
    val recommendation: String
)

data class YTScriptResult(
    val topic: String,
    val selectedFormula: HookFormula,
    val generatedHook: String,
    val hookScore: HookScore,
    val retentionScript: String,
    val chapters: List<Pair<String, String>>, // "00:00" to "Title"
    val editDecisionList: List<String>,
    val shortsCuts: List<String>
)

object YouTubeAgentEngine {

    fun scoreHook(hookText: String): HookScore {
        val wordCount = hookText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        val charCount = hookText.length

        // 1. Brevity (under 15 words is prime)
        val brevity = when {
            wordCount in 6..14 -> 20
            wordCount in 4..5 || wordCount in 15..18 -> 16
            wordCount in 19..24 -> 10
            else -> 6
        }

        // 2. Clarity (simple direct vocabulary)
        val clarity = if (!hookText.contains("হয়তো") && !hookText.contains("সম্ভবত") && charCount > 15) 20 else 12

        // 3. Curiosity trigger words
        val curiosityTriggers = listOf("কেন", "গোপন", "রহস্য", "বাস্তবতা", "ভুল", "ট্রিক", "কেউ বলবে না", "কারণ", "সত্য")
        val curiosityMatches = curiosityTriggers.count { hookText.contains(it) }
        val curiosity = (10 + curiosityMatches * 5).coerceAtMost(20)

        // 4. Urgency & Stakes
        val urgencyTriggers = listOf("আজই", "এখনই", "কখনোই না", "থামুন", "হারাবেন", "সবচেয়ে", "১টি", "৩টি", "ভুল")
        val urgencyMatches = urgencyTriggers.count { hookText.contains(it) }
        val urgency = (10 + urgencyMatches * 5).coerceAtMost(20)

        val total = (brevity + clarity + curiosity + urgency + 20).coerceIn(0, 100)
        val grade = when {
            total >= 90 -> "A+ (Viral Tier)"
            total >= 80 -> "A (Strong Hook)"
            total >= 70 -> "B (Solid)"
            total >= 60 -> "C (Acceptable)"
            else -> "Needs Rework"
        }

        val feedback = when {
            total >= 85 -> "চমৎকার হুক! শব্দ সংখ্যা সঠিক এবং দর্শকের কৌতূহল ও মনোযোগ ধরে রাখার মতো শক্তিশালী।"
            wordCount > 18 -> "হুকটি কিছুটা দীর্ঘ। প্রথম বাক্যটি ১২-১৪ শব্দের মধ্যে রাখলে স্ক্রোল-স্টপ রেট অনেক বাড়বে।"
            curiosityMatches == 0 -> "হুকে আরও কৌতূহল জাগাতে 'কেন' বা 'গোপন কারণ'-এর মতো মনস্তাত্ত্বিক শব্দ যোগ করুন।"
            else -> "হুকটি গ্রহণযোগ্য। সরাসরি দর্শকের প্রধান সমস্যায় আঘাত করলে সিটিআর আরও বাড়বে।"
        }

        return HookScore(total, brevity, clarity, curiosity, urgency, grade, feedback)
    }

    fun auditPackaging(title: String, thumbnailText: String): PackagingAudit {
        val titleLen = title.length
        val isTruncated = titleLen > 55
        
        val titleWords = title.lowercase().split(Regex("[^a-zA-Z0-9\u0980-\u09FF]+")).filter { it.length > 2 }
        val thumbWords = thumbnailText.lowercase().split(Regex("[^a-zA-Z0-9\u0980-\u09FF]+")).filter { it.length > 2 }
        val duplicates = titleWords.intersect(thumbWords.toSet())
        val hasDuplication = duplicates.size >= 2

        var score = 95
        if (isTruncated) score -= 20
        if (hasDuplication) score -= 25
        if (thumbnailText.length > 25) score -= 15
        if (title.isBlank()) score = 0

        val rec = buildString {
            if (isTruncated) append("⚠️ টাইটেল ৫৫ অক্ষরের বেশি হওয়ায় মোবাইলে কেটে যেতে পারে (Truncated)। ")
            if (hasDuplication) append("⚠️ টাইটেল ও থাম্বনেইলে একই শব্দ বারবার ব্যবহার করবেন না! থাম্বনেইল কৌতূহল বাড়াবে এবং টাইটেল বিষয়টি নিশ্চিত করবে। ")
            if (!isTruncated && !hasDuplication && title.isNotBlank()) append("✅ প্যাকেজিং পারফেক্ট! টাইটেল ও থাম্বনেইলের পেয়ারিং দর্শকের সর্বোচ্চ ক্লিক আকর্ষণ করবে।")
        }

        return PackagingAudit(
            score = score.coerceIn(0, 100),
            titleLength = titleLen,
            isTruncatedMobile = isTruncated,
            hasDuplication = hasDuplication,
            thumbnailComplement = if (hasDuplication) "ভিন্ন শব্দ ব্যবহার করুন (Visual Punch)" else "পরিপূরক টেক্সট ঠিক আছে",
            recommendation = rec
        )
    }

    fun generateFullScriptPackage(topic: String): YTScriptResult {
        val cleanTopic = topic.ifBlank { "ইউটিউব গ্রোথ ও অ্যালগরিদম হ্যাক" }
        val formula = hookFormulas21.random()
        val hook = "অধিকাংশ ক্রিয়েটর ভাবে $cleanTopic নিয়ে ভিউ পাওয়া কঠিন, কিন্তু আসল গোপন ট্রিক লুকিয়ে আছে এই ৩টি সেকেন্ডে!"
        val score = scoreHook(hook)

        val script = """
            [00:00 - 00:05] 🎯 HOOK (Formula: ${formula.name}):
            "$hook"
            
            [00:05 - 00:15] ⚡ RE-HOOK & STAKES (দর্শকের মনোযোগ লক করা):
            "যদি আপনি এই ভিডিওটি শেষ পর্যন্ত না দেখেন, তবে আপনার পরবর্তী ৫টি ভিডিওর পরিশ্রমও দর্শক ২০ সেকেন্ড না দেখে বন্ধ করে দেবে।"
            
            [00:15 - 00:45] 📊 POINT 1: সমস্যার মূল কারণ:
            "$cleanTopic-এর ক্ষেত্রে ৯৫% মানুষ যে ভুলটি করে তা হলো অপ্রয়োজনীয় ইন্ট্রো। ইউটিউবে ওয়াচটাইম গ্রাফের প্রথম ড্রপটাই পুরো ইম্প্রেশন নষ্ট করে দেয়।"
            
            [00:45 - 01:30] 💡 POINT 2: গোপন ফ্রেমওয়ার্ক:
            "প্রতি ৩০ সেকেন্ডে ভিজ্যুয়াল প্যাটার্ন ইন্টারাপ্ট করুন (জুম ইন, সাউন্ড এফেক্ট, টেক্সট পপ-আপ)। এতে মস্তিষ্কের ডোপামিন রিসেট হয়।"
            
            [01:30 - 02:15] 🚀 POINT 3: আল্টিমেট অ্যাকশন স্টেপ:
            "এখনই আপনার অ্যানালিটিক্সে গিয়ে অডিয়েন্স রিটেনশন চার্ট খুলুন। যেখানে ড্রপ হয়েছে, পরবর্তী ভিডিওতে সেই অংশটি বাদ দিন।"
            
            [02:15 - 02:30] 🎬 LOOP CALL-TO-ACTION:
            "যদি আপনার থাম্বনেইল এখনও লো-সিটিআর দেয়, তবে ডেসক্রিপশনের লিংকে ক্লিক করে আমাদের প্যাকেজিং মাস্টারক্লাস দেখে নিন!"
        """.trimIndent()

        val chapters = listOf(
            "00:00" to "Introduction & The Hook",
            "00:15" to "Why Most Creators Fail with $cleanTopic",
            "00:45" to "The Retention Pattern Interrupt",
            "01:30" to "Fixing Your Audience Retention",
            "02:15" to "Final Summary & Next Video Loop"
        )

        val edl = listOf(
            "✂️ [00:04] কাট: হুকের শেষে কোনো নিঃশ্বাস বা নীরবতা (Dead Air) রাখবেন না।",
            "⚡ [00:18] প্যাটার্ন ইন্টারাপ্ট: স্ক্রিনে দ্রুত গ্রাফিক্স বা টেক্সট পপ-আপ দিন।",
            "✂️ [00:52] কাট: 'উম...', 'মানে...' জাতীয় সব ফিলার সাউন্ড কেটে ফেলুন।",
            "🎵 [01:10] সাউন্ড লেয়ার: পয়েন্ট পরিবর্তনের সময় 'Whoosh' বা 'Sub-bass' ট্রানজিশন যোগ করুন।"
        )

        val shorts = listOf(
            "📱 Shorts 1 (00:00 - 00:40): 'অধিকাংশ ক্রিয়েটর $cleanTopic নিয়ে কেন ভুল করে?'",
            "📱 Shorts 2 (00:45 - 01:25): 'প্রতি ৩০ সেকেন্ডে দর্শকের মন ধরে রাখার ৩টি এডিটিং ট্রিক!'"
        )

        return YTScriptResult(
            topic = cleanTopic,
            selectedFormula = formula,
            generatedHook = hook,
            hookScore = score,
            retentionScript = script,
            chapters = chapters,
            editDecisionList = edl,
            shortsCuts = shorts
        )
    }
}
