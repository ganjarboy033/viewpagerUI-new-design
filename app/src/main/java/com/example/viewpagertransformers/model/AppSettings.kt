package com.example.viewpagertransformers.model

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    SUNDANESE("su", "Sunda", "Basa Sunda"),
    INDONESIAN("id", "Indonesia", "Bahasa Indonesia"),
    ENGLISH("en", "English", "English")
}

enum class CardStyle(val labelKey: String) {
    MODERN("modern"),
    CLASSIC("classic")
}

enum class PagerOrientation(val labelKey: String) {
    HORIZONTAL("horizontal"),
    VERTICAL("vertical")
}

enum class CameraDepth(val value: Float, val labelKey: String) {
    SUBTLE(8f, "depth_subtle"),
    STANDARD(14f, "depth_standard"),
    DEEP(22f, "depth_deep")
}

data class AppSettings(
    val language: AppLanguage = AppLanguage.SUNDANESE,
    val cardStyle: CardStyle = CardStyle.MODERN,
    val orientation: PagerOrientation = PagerOrientation.HORIZONTAL,
    val cameraDepth: CameraDepth = CameraDepth.STANDARD,
    val pageSpacingDp: Int = 0,
    val clipPages: Boolean = false,
    val autoPlaySpeedMs: Long = 2200L
)
