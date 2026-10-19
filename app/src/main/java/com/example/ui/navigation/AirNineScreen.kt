package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AirNineScreen(
    val titleEn: String,
    val titleHi: String,
    val icon: ImageVector,
    val testTag: String
) {
    CHAT("AI Chat", "एआई चैट", Icons.Default.QuestionAnswer, "nav_chat"),
    VOICE("Voice Mode", "वॉइस मोड", Icons.Default.Mic, "nav_voice"),
    ARENA("Model Arena", "मॉडल एरीना", Icons.AutoMirrored.Filled.CompareArrows, "nav_arena"),
    TRUST("Trust Check", "ट्रस्ट चेक", Icons.Default.CheckCircle, "nav_trust"),
    PROJECTS("Project Spaces", "प्रोजेक्ट स्पेस", Icons.Default.Folder, "nav_projects"),
    TASKFLOW("TaskFlow", "टास्क फ्लो", Icons.Default.Analytics, "nav_taskflow"),
    DOCS("Documents", "दस्तावेज़", Icons.Default.Description, "nav_docs"),
    RESEARCH("Web Research", "वेब रिसर्च", Icons.Default.Search, "nav_research"),
    TOOLS("Tools Hub", "टूल्स हब", Icons.Default.AutoAwesome, "nav_tools"),
    SETTINGS("Settings", "सेटिंग्स", Icons.Default.Settings, "nav_settings")
}
