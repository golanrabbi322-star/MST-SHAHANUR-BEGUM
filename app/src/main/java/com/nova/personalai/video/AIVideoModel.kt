package com.nova.personalai.video

import java.util.UUID

enum class CameraMotionType {
    ZOOM_IN,
    ZOOM_OUT,
    PAN_LEFT,
    PAN_RIGHT,
    SLOW_DRIFT,
    PULSE_EXPAND
}

data class VideoScene(
    val sceneNumber: Int,
    val title: String,
    val visualPrompt: String,
    val narrationText: String,
    val subtitleText: String,
    val durationSeconds: Float = 5.0f,
    val startSeconds: Float = 0.0f,
    val cameraEffect: CameraMotionType = CameraMotionType.ZOOM_IN,
    val primaryColorHex: Long = 0xFF0D1B2A,
    val secondaryColorHex: Long = 0xFF00E5FF,
    val visualTag: String = "Cosmic Vision"
)

data class AIVideoProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val topic: String,
    val aspectRatio: String = "9:16 (YouTube Shorts)",
    val totalDurationSeconds: Float = 30.0f,
    val scenes: List<VideoScene> = emptyList(),
    val tags: List<String> = emptyList(),
    val description: String = "",
    val youtubeTitle: String = "",
    val voiceTrackName: String = "Nova Neural Bengali (bn-BD)",
    val isRendered: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
