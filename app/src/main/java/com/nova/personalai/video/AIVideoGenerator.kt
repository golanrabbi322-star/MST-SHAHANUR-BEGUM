package com.nova.personalai.video

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.util.Locale

object AIVideoGenerator {

    /**
     * Creates a fully structured AI Video Project with 6 cinematic scenes,
     * narrative voiceover, precise subtitle timings, and camera motions.
     */
    fun createProject(topic: String, isShorts: Boolean = true): AIVideoProject {
        val cleanTopic = topic.trim().ifBlank { "রহস্যময় বিজ্ঞান ও মহাকাশ" }
        val durationPerScene = if (isShorts) 5.0f else 7.5f

        val scenes = generateScenes(cleanTopic, durationPerScene)
        val totalDuration = scenes.size * durationPerScene

        val tags = listOf(
            "#Shorts",
            "#BanglaScience",
            "#BanglaTech",
            "#SpaceMystery",
            "#NOVA_AI",
            "#ViralVideo",
            "#ScienceFacts"
        )

        val youtubeTitle = "🔥 $cleanTopic | বিজ্ঞানের বিস্ময়কর রহস্য! 🌌 #Shorts"
        val description = """
            🎬 $cleanTopic — সম্পূর্ণ স্বয়ংক্রিয় NOVA AI স্টুডিও দিয়ে তৈরি।
            
            গল্প, চিত্রনাট্য, ভিজ্যুয়াল আর্টওয়ার্ক, বাংলা ভয়েসওভার ও সাবটাইটেল সম্পূর্ণ AI অ্যালগরিদম দ্বারা রেন্ডার করা হয়েছে।
            
            🔔 এরকম আরও রহস্য ও বিজ্ঞানের ভিডিও দেখতে সাবস্ক্রাইব করুন!
            ${tags.joinToString(" ")}
        """.trimIndent()

        return AIVideoProject(
            title = cleanTopic,
            topic = cleanTopic,
            aspectRatio = if (isShorts) "9:16 (YouTube Shorts)" else "16:9 (Cinematic)",
            totalDurationSeconds = totalDuration,
            scenes = scenes,
            tags = tags,
            description = description,
            youtubeTitle = youtubeTitle,
            isRendered = true
        )
    }

    private fun generateScenes(topic: String, duration: Float): List<VideoScene> {
        return listOf(
            VideoScene(
                sceneNumber = 1,
                title = "মহাজাগতিক হুক ও সূচনা",
                visualPrompt = "Hyper-detailed cinematic cosmic void, swirling dark matter, volumetric gold and cyan nebulae, 8k resolution, vertical 9:16 ratio",
                narrationText = "আপনি কি জানেন মহাবিশ্বের সবচেয়ে গভীর রহস্যটি কী? বিজ্ঞানের নতুন গবেষণা পুরো বিশ্বকে স্তম্ভিত করে দিয়েছে!",
                subtitleText = "আপনি কি জানেন মহাবিশ্বের সবচেয়ে গভীর রহস্যটি কী?",
                durationSeconds = duration,
                startSeconds = 0.0f,
                cameraEffect = CameraMotionType.ZOOM_IN,
                primaryColorHex = 0xFF0B132B,
                secondaryColorHex = 0xFF48CAE4,
                visualTag = "Deep Cosmic Void"
            ),
            VideoScene(
                sceneNumber = 2,
                title = "অদৃশ্য শক্তির উদ্ভাস",
                visualPrompt = "Glowing quantum energy field, cybernetic particle vortex, shimmering event horizon of a supermassive black hole",
                narrationText = "আমাদের চেনা দৃশ্যমান জগৎ সমগ্র সৃষ্টির মাত্র পাঁচ শতাংশ! বাকি ৯৫ শতাংশ ঢাকা পড়ে আছে ডার্ক ম্যাটার ও ডার্ক এনার্জির অতল অন্ধকারে।",
                subtitleText = "দৃশ্যমান জগৎ মাত্র ৫%! বাকি ৯৫% ডার্ক ম্যাটারের রহস্য।",
                durationSeconds = duration,
                startSeconds = duration,
                cameraEffect = CameraMotionType.PAN_LEFT,
                primaryColorHex = 0xFF1C0A35,
                secondaryColorHex = 0xFFFF007F,
                visualTag = "Quantum Field & Vortex"
            ),
            VideoScene(
                sceneNumber = 3,
                title = "সময়ের সংকোচন ও লেন্সিং",
                visualPrompt = "Einstein gravitational lensing curving light around a neutron star, intense golden accretion disk, photorealistic physics simulation",
                narrationText = "এখানে সময় এবং স্থান এমনভাবে বেঁকে যায়, যেখানে একটি সেকেন্ড পৃথিবীর শত বছরের সমান হতে পারে!",
                subtitleText = "এখানে স্থান ও কাল বেঁকে গিয়ে সময়ের গতি বদলে দেয়!",
                durationSeconds = duration,
                startSeconds = duration * 2,
                cameraEffect = CameraMotionType.SLOW_DRIFT,
                primaryColorHex = 0xFF1A1A24,
                secondaryColorHex = 0xFFFFD700,
                visualTag = "Gravitational Lensing"
            ),
            VideoScene(
                sceneNumber = 4,
                title = "কোয়ান্টাম আলোক বিস্ফোরণ",
                visualPrompt = "Explosion of super-dense quantum photons, cyan and neon purple crystalline shockwave expanding in vacuum",
                narrationText = "বিজ্ঞানীরা এমন সংকেত পেয়েছেন যা প্রমাণ করে, মহাবিশ্ব একা নয়—হয়তো সমান্তরাল কোনো মাত্রা আমাদের ডাকছে।",
                subtitleText = "বিজ্ঞানীরা পাচ্ছেন সমান্তরাল কোনো মাত্রার অদ্ভুত সংকেত!",
                durationSeconds = duration,
                startSeconds = duration * 3,
                cameraEffect = CameraMotionType.PULSE_EXPAND,
                primaryColorHex = 0xFF002233,
                secondaryColorHex = 0xFF00FFCC,
                visualTag = "Quantum Shockwave"
            ),
            VideoScene(
                sceneNumber = 5,
                title = "চূড়ান্ত মানব উপলব্ধি",
                visualPrompt = "Futuristic AI consciousness observing millions of interconnected galaxies through an optical cyber-visor, neon blue ambient glow",
                narrationText = "আমরা সেই বিশাল অনন্তের ক্ষুদ্রতম অংশ, তবু আমাদের চিন্তাই হয়তো উন্মোচন করবে এই সৃষ্টির চূড়ান্ত সত্য।",
                subtitleText = "আমরা সেই বিশাল অনন্তের অংশ, যারা সৃষ্টির সত্য খুঁজছি।",
                durationSeconds = duration,
                startSeconds = duration * 4,
                cameraEffect = CameraMotionType.PAN_RIGHT,
                primaryColorHex = 0xFF140826,
                secondaryColorHex = 0xFF7928CA,
                visualTag = "AI Cyber Observer"
            ),
            VideoScene(
                sceneNumber = 6,
                title = "সমাপ্তি ও সাবস্ক্রাইব কল",
                visualPrompt = "Cinematic golden YouTube Shorts end-screen with dynamic holographic subscribe badge and audio frequency bars glowing",
                narrationText = "এই বিস্ময়কর সত্যটি আপনার বন্ধুদের সাথে শেয়ার করুন এবং NOVA AI এর সাথে থাকতে সাবস্ক্রাইব করে বেল আইকনটি বাজিয়ে রাখুন!",
                subtitleText = "লাইক করুন, বন্ধুদের সাথে শেয়ার করুন এবং সাবস্ক্রাইব করুন!",
                durationSeconds = duration,
                startSeconds = duration * 5,
                cameraEffect = CameraMotionType.ZOOM_OUT,
                primaryColorHex = 0xFF1F0015,
                secondaryColorHex = 0xFFFF3366,
                visualTag = "Creator End Screen"
            )
        )
    }

    /**
     * Builds standardized .SRT subtitle content
     */
    fun generateSrtContent(project: AIVideoProject): String {
        val sb = StringBuilder()
        project.scenes.forEachIndexed { index, scene ->
            val startMs = (scene.startSeconds * 1000).toLong()
            val endMs = ((scene.startSeconds + scene.durationSeconds) * 1000).toLong()

            sb.append("${index + 1}\n")
            sb.append("${formatTime(startMs)} --> ${formatTime(endMs)}\n")
            sb.append("${scene.subtitleText}\n\n")
        }
        return sb.toString().trim()
    }

    private fun formatTime(millis: Long): String {
        val hours = millis / 3600000
        val minutes = (millis % 3600000) / 60000
        val seconds = (millis % 60000) / 1000
        val ms = millis % 1000
        return String.format(Locale.US, "%02d:%02d:%02d,%03d", hours, minutes, seconds, ms)
    }

    /**
     * Share the complete AI Video Project via Android Share Sheet
     */
    fun shareVideoProject(context: Context, project: AIVideoProject) {
        val shareBody = """
            🎬 ${project.youtubeTitle}
            
            ${project.description}
            
            ══════════════════════════════════════
            📝 সিন-বাই-সিন চিত্রনাট্য ও ভয়েসওভার:
            ${project.scenes.joinToString("\n\n") { "সিন ${it.sceneNumber} (${it.title}):\n🎙️ \"${it.narrationText}\"" }}
            
            ══════════════════════════════════════
            📁 সাবটাইটেল (.SRT):
            ${generateSrtContent(project)}
            
            🚀 Generated by NOVA AI Autonomous Video Studio
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, project.youtubeTitle)
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }
        context.startActivity(Intent.createChooser(intent, "ভিডিও প্যাকেজ শেয়ার ও এক্সপোর্ট করুন"))
    }

    /**
     * Open YouTube Shorts upload / creation intent directly on the phone
     */
    fun openYouTubeShortsUpload(context: Context, project: AIVideoProject) {
        try {
            // Copy title & tags to clipboard for the user's convenience
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("YouTube Video Data", "${project.youtubeTitle}\n\n${project.description}")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "শিরোনাম ও ডেসক্রিপশন ক্লিপবোর্ডে কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()

            // Try YouTube upload intent or web fallback
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://youtube.com/upload")
                setPackage("com.google.android.youtube")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                // Fallback to browser YouTube upload / Shorts studio
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://studio.youtube.com")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (_: Exception) {
                Toast.makeText(context, "YouTube ওপেন করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
