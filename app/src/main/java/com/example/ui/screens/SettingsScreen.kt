package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.repository.AirNineRepository
import com.example.speech.VoiceManager
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun SettingsScreen(
    repository: AirNineRepository,
    voiceManager: VoiceManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appLanguage by repository.appLanguage.collectAsState()
    val isMuted by voiceManager.isMuted.collectAsState()
    var isKeyConfigured by remember { mutableStateOf(repository.isApiKeyConfigured()) }
    var apiKeyInput by remember { mutableStateOf(repository.getApiKey()) }
    var showApiKey by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (appLanguage == AppLanguage.HINDI) "सेटिंग्स" else "Settings",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = CyanAccent
        )
        Text(
            text = if (appLanguage == AppLanguage.HINDI)
                "सिस्टम प्राथमिकताएं, भाषा और वॉइस कॉन्फ़िगरेशन"
            else
                "System preferences, localization, and voice configuration",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Language Section
        SettingsCard(title = if (appLanguage == AppLanguage.HINDI) "भाषा (Language)" else "Interface Language") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppLanguage.values().forEach { lang ->
                    val isSelected = appLanguage == lang
                    OutlinedButton(
                        onClick = {
                            repository.setAppLanguage(lang)
                            voiceManager.setLanguage(lang)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) ElectricBlue.copy(alpha = 0.25f) else NavyCard,
                            contentColor = if (isSelected) CyanAccent else TextSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyanAccent else NavyBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("lang_btn_${lang.code}")
                    ) {
                        Text(lang.displayName, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Voice Preferences
        SettingsCard(title = if (appLanguage == AppLanguage.HINDI) "वॉइस और स्पीच सेटिंग्स" else "Voice & Speech Settings") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (appLanguage == AppLanguage.HINDI) "वॉइस उत्तर म्यूट करें" else "Mute Spoken Responses",
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = if (appLanguage == AppLanguage.HINDI) "AI उत्तर केवल टेक्स्ट में दिखाएं" else "Output responses as text without speech audio",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
                Switch(
                    checked = isMuted,
                    onCheckedChange = { voiceManager.setMuted(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyanAccent,
                        checkedTrackColor = ElectricBlue,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = NavyCard
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val testSpeech = if (appLanguage == AppLanguage.HINDI)
                        "यह एयर नाइन की आवाज़ का परीक्षण है। सब कुछ सही काम कर रहा है।"
                    else
                        "This is a speech synthesis test from Air Nine. Audio output is fully operational."
                    voiceManager.speak(testSpeech)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Test Speech", tint = CyanAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (appLanguage == AppLanguage.HINDI) "वॉइस आउटपुट टेस्ट करें" else "Test Voice Speech Output", color = TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // API Key Status & Configuration
        SettingsCard(title = if (appLanguage == AppLanguage.HINDI) "एआई इंजन और एपीआई की" else "AI Engine & API Key") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isKeyConfigured) SuccessGreen else WarningAmber)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isKeyConfigured) "Gemini API Active (Connected)" else "API Key Configuration Required",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isKeyConfigured) SuccessGreen else WarningAmber
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (appLanguage == AppLanguage.HINDI)
                    "एयर नाइन गूगल जेमिनी मॉडल का उपयोग करता है। आप अपनी GEMINI_API_KEY यहाँ दर्ज कर सकते हैं या AI Studio Secrets पैनल में कॉन्फ़िगर कर सकते हैं।"
                else
                    "Air Nine connects to Google Gemini models. You can enter your GEMINI_API_KEY below or configure it in the AI Studio Secrets panel.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = apiKeyInput,
                onValueChange = { apiKeyInput = it },
                label = { Text("GEMINI_API_KEY", color = TextSecondary, fontSize = 12.sp) },
                placeholder = { Text("AIzaSy...", color = TextMuted) },
                visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showApiKey = !showApiKey }) {
                        Icon(
                            imageVector = if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showApiKey) "Hide key" else "Show key",
                            tint = TextSecondary
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = NavyDarkBackground,
                    unfocusedContainerColor = NavyDarkBackground,
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = NavyBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_api_key_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val clean = apiKeyInput.trim()
                        repository.setCustomApiKey(clean)
                        isKeyConfigured = repository.isApiKeyConfigured()
                        Toast.makeText(context, "API Key saved successfully", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_api_key_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (appLanguage == AppLanguage.HINDI) "की सुरक्षित करें" else "Save Key")
                }

                if (apiKeyInput.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            apiKeyInput = ""
                            repository.setCustomApiKey("")
                            isKeyConfigured = repository.isApiKeyConfigured()
                            Toast.makeText(context, "Custom API Key cleared", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("clear_api_key_button")
                    ) {
                        Text(if (appLanguage == AppLanguage.HINDI) "हटाएं" else "Clear")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data & Privacy
        SettingsCard(title = if (appLanguage == AppLanguage.HINDI) "डेटा और गोपनीयता" else "Data & Privacy") {
            Text(
                text = "Air Nine stores your conversation history and project contexts locally on your device. API credentials remain securely contained.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    repository.clearChat()
                    Toast.makeText(context, "Chat history cleared", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("clear_history_settings_button")
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = ErrorRed, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (appLanguage == AppLanguage.HINDI) "चैट इतिहास साफ़ करें" else "Clear Chat History", color = ErrorRed)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Info (Must strictly display Air Nine only!)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Air Nine",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CyanAccent
            )
            Text(
                text = "Version 1.0.0 • Modern AI Workspace",
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NavySurface)
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyanAccent
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}
