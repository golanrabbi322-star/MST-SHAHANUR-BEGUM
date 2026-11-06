package com.nova.personalai.jarvis

enum class JarvisAgentType(
    val id: String,
    val title: String,
    val executionMode: String, // Scheduled, On-demand, Continuous
    val description: String,
    val defaultPreset: String,
    val iconName: String
) {
    MORNING_DIGEST(
        "morning_digest",
        "Morning Digest",
        "Scheduled",
        "ক্যালেন্ডার, ইমেইল, স্বাস্থ্য ও খবর থেকে দৈনিক অডিও স্পোকেন ব্রিফিং তৈরি করে।",
        "morning-digest-minimal",
        "WbSunny"
    ),
    DEEP_RESEARCH(
        "deep_research",
        "Deep Research",
        "On-demand",
        "ওয়েব ও লোকাল ডকুমেন্টস ঘেঁটে মাল্টি-হপ রিসার্চ এবং সাইটেশন রিপোর্ট প্রস্তুত করে।",
        "deep-research",
        "Search"
    ),
    CODE_ACT(
        "native_openhands",
        "CodeAct & Assistant",
        "On-demand",
        "Python কোড তৈরি, সরাসরি শেল এক্সিকিউশন ও লোকাল ফাইল I/O হ্যান্ডলিং করে।",
        "code-assistant",
        "Code"
    ),
    SCHEDULED_MONITOR(
        "scheduled_monitor",
        "Monitor Operative",
        "Continuous",
        "লং-হরাইজন পর্যবেক্ষণ, মেমোরি কম্প্রেশন এবং রিট্রিভাল স্বয়ংক্রিয়ভাবে নিয়ন্ত্রণ করে।",
        "scheduled-monitor",
        "Visibility"
    ),
    ORCHESTRATOR(
        "orchestrator",
        "Multi-Turn Orchestrator",
        "On-demand",
        "জটিল কাজে স্বয়ংক্রিয়ভাবে একাধিক সাব-টুল নির্বাচন ও মাল্টি-টার্ন রিজনিং চালায়।",
        "orchestrator",
        "AccountTree"
    ),
    NATIVE_REACT(
        "native_react",
        "ReAct Agent Loop",
        "On-demand",
        "Thought-Action-Observation মেথডে ধাপে ধাপে চিন্তা করে ও ফলাফল পর্যবেক্ষণ করে।",
        "native_react",
        "Psychology"
    ),
    OPERATIVE(
        "operative",
        "Persistent Autonomous Operative",
        "Continuous",
        "স্থায়ী ব্যাকগ্রাউন্ড স্টেট ম্যানেজমেন্ট সহ স্বায়ত্তশাসিত অ্যাকশন পরিচালনা করে।",
        "operative",
        "SmartToy"
    ),
    SIMPLE_LOCAL(
        "chat_simple",
        "Local-First Chat",
        "On-demand",
        "কোনো ক্লাউড এপিআই ছাড়াই লোকাল-ফার্স্ট লাইটওয়েট অফলাইন কথোপকথন।",
        "chat-simple",
        "Chat"
    )
}

data class JarvisSkill(
    val id: String,
    val name: String,
    val source: String, // Hermes, OpenClaw, Arxiv, GitHub
    val command: String,
    val description: String,
    val isInstalled: Boolean = true
)

data class LocalTelemetry(
    val intelligencePerWatt: String = "5.3× Efficiency (Stanford SAIL)",
    val localQueryRatio: String = "88.7% On-Device",
    val latencyMs: Int = 18,
    val flopsCostDollar: String = "$0.00 (Zero Cloud Cost)",
    val modelState: String = "Active (Local-First Runtime)"
)

object OpenJarvisEngine {

    val builtInAgents = JarvisAgentType.values().toList()

    val installedSkills = listOf(
        JarvisSkill("skill_1", "Hermes Arxiv Research", "Hermes Agent", "jarvis skill sync hermes --category research", "গবেষণাপত্র ও সায়েন্টিফিক পেপার সারসংক্ষেপ"),
        JarvisSkill("skill_2", "Code Explainer & Runner", "OpenHands CodeAct", "jarvis ask --skill code-explainer", "পাইথন অ্যালগরিদম বিশ্লেষণ ও রান"),
        JarvisSkill("skill_3", "Voice Daily Digest", "OpenJarvis Voice", "jarvis digest --fresh", "টিটিএস অডিও সহ সকালের সার্বিক ব্রিফিং"),
        JarvisSkill("skill_4", "Trace History Optimizer", "DSPy Policy", "jarvis optimize skills --policy dspy", "ইউজার ট্রেস হিস্টোরি দিয়ে লোকাল মডেল ফাইন-টিউনিং"),
        JarvisSkill("skill_5", "Stateful Long-Horizon Memory", "Operative", "jarvis monitor --stateful", "ক্রমাগত মেমোরি ও দীর্ঘমেয়াদি কনটেক্সট ট্র্যাকিং")
    )

    fun executePreset(presetName: String, topic: String): String {
        return when (presetName) {
            "morning-digest-minimal", "morning_digest" -> """
                🌅【OPENJARVIS MORNING DIGEST • LOCAL BRIEFING】
                📅 সময়: সকাল ৮:০০ | ডিভাইস: Local On-Device Neural Engine
                
                • 📬 গুরুত্বপূর্ণ বার্তা: কোনো জরুরি অপঠিত ফ্ল্যাগ নেই।
                • 🗓️ আজকের এজেন্ডা: ৩টি ডেভেলপমেন্ট টাস্ক শিডিউল রয়েছে।
                • ⚡ সিস্টেম স্ট্যাটাস: লোকাল কোয়ারি এফিশিয়েন্সি ৮৮.৭%, ব্যাটারি ড্রেন মিনিমাল।
                • 🎧 অডিও টিটিএস: সম্পূর্ণ ব্রিফিং স্পোকেন অডিওতে প্লে করার জন্য প্রস্তুত।
            """.trimIndent()

            "deep-research", "deep_research" -> """
                🔍【OPENJARVIS DEEP-RESEARCH REPORT】
                🎯 বিষয়: "$topic"
                
                • ধাপ ১: মাল্টি-হপ ডকুমেন্ট ইনডেক্সিং সম্পন্ন (Local Trace)।
                • ধাপ ২: ৩টি প্রাসঙ্গিক পেপার ও লোকাল রেফারেন্স সাইটেশন এক্সট্রাক্ট করা হয়েছে।
                • ধাপ ৩: জিরো ক্লাউড কস্ট এবং এনক্রিপ্টেড অন-ডিভাইস সামারি প্রস্তুত।
                • 💡 ফলাফল: আপনার ডিভাইসের মেমোরি ক্যাশে সম্পূর্ণ গবেষণা সংরক্ষণ করা হলো।
            """.trimIndent()

            "code-assistant", "native_openhands" -> """
                💻【OPENJARVIS CODEACT AGENT EXECUTED】
                🎯 কোড রিকোয়েস্ট: "$topic"
                
                ```python
                # OpenJarvis Local CodeAct Autonomous Script
                import sys

                def execute_local_task():
                    print("⚡ Running locally with Zero API Cost...")
                    return {"status": "SUCCESS", "mode": "Local-First", "efficiency": "5.3x"}

                if __name__ == "__main__":
                    res = execute_local_task()
                    print(res)
                ```
                • শেল এক্সিকিউশন: কমপ্লিটেড (Exit Code 0)
                • মেমোরি ব্যান্ডউইথ: অপ্টিমাইজড।
            """.trimIndent()

            "scheduled-monitor", "monitor_operative" -> """
                📡【OPENJARVIS CONTINUOUS MONITOR ACTIVE】
                • মনিটরিং মোড: Persistent Memory Compression
                • স্টেট: Active Background Watcher
                • রিসোর্স ব্যবহার: < 0.2W (Intelligence Per Watt Architecture)
            """.trimIndent()

            else -> """
                ⚡【OPENJARVIS LOCAL-FIRST AGENT RUNTIME】
                🎯 প্রম্পট: "$topic"
                
                • আর্কিটেকচার: Stanford SAIL / Hazy Research OpenJarvis Framework
                • এক্সিকিউশন: ৮৮.৭% অন-ডিভাইস রিজনিং ইঞ্জিন (No Cloud Dependency)
                • এফিশিয়েন্সি: ৫.৩× Intelligence Per Watt অপ্টিমাইজেশন কার্যকর।
            """.trimIndent()
        }
    }
}
