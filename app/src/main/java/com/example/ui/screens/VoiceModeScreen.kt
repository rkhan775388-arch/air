package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.AppLanguage
import com.example.data.model.VoiceState
import com.example.data.repository.AirNineRepository
import com.example.speech.VoiceManager
import com.example.ui.components.AirNineGlowOrb
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.MarkdownText
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.launch

@Composable
fun VoiceModeScreen(
    repository: AirNineRepository,
    voiceManager: VoiceManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val voiceState by voiceManager.voiceState.collectAsState()
    val recognizedText by voiceManager.recognizedText.collectAsState()
    val soundLevel by voiceManager.soundLevel.collectAsState()
    val errorMessage by voiceManager.errorMessage.collectAsState()
    val isMuted by voiceManager.isMuted.collectAsState()
    val appLanguage by repository.appLanguage.collectAsState()

    var lastAiSpokenResponse by remember { mutableStateOf<String?>(null) }
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            voiceManager.startListening()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top status row: Language switch + Mute toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language selector button
            OutlinedButton(
                onClick = {
                    val nextLang = if (appLanguage == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
                    repository.setAppLanguage(nextLang)
                    voiceManager.setLanguage(nextLang)
                },
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
                modifier = Modifier.testTag("voice_language_toggle")
            ) {
                Icon(
                    Icons.Default.Translate,
                    contentDescription = "Switch Language",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = appLanguage.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Mute / Unmute Button
            IconButton(
                onClick = { voiceManager.setMuted(!isMuted) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NavyCard)
                    .border(1.dp, NavyBorder, CircleShape)
                    .testTag("voice_mute_button")
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = if (isMuted) "Unmute voice responses" else "Mute voice responses",
                    tint = if (isMuted) TextMuted else CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Voice Visualizer & Status Badge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Status text
            val statusTitle = when (voiceState) {
                VoiceState.IDLE -> if (appLanguage == AppLanguage.HINDI) "बोलने के लिए दबाएं" else "Press to Speak"
                VoiceState.LISTENING -> if (appLanguage == AppLanguage.HINDI) "सुन रहा है..." else "Listening..."
                VoiceState.PROCESSING -> if (appLanguage == AppLanguage.HINDI) "सोच रहा है..." else "Processing with AI..."
                VoiceState.SPEAKING -> if (appLanguage == AppLanguage.HINDI) "बोल रहा है..." else "Speaking..."
                VoiceState.ERROR -> if (appLanguage == AppLanguage.HINDI) "त्रुटि हुई" else "Speech Error"
            }

            val statusColor = when (voiceState) {
                VoiceState.IDLE -> TextSecondary
                VoiceState.LISTENING -> CyanAccent
                VoiceState.PROCESSING -> ElectricBlue
                VoiceState.SPEAKING -> NeonTeal
                VoiceState.ERROR -> ErrorRed
            }

            Text(
                text = statusTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (appLanguage == AppLanguage.HINDI)
                    "पुश-टू-टॉक: माइक्रोफ़ोन दबाएं और बोलें"
                else
                    "Push-to-talk: Tap the orb and speak naturally",
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Glowing Orb
            AirNineGlowOrb(
                isActive = voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING,
                soundLevel = soundLevel,
                onClick = {
                    if (!hasMicPermission) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        when (voiceState) {
                            VoiceState.IDLE -> voiceManager.startListening()
                            VoiceState.LISTENING -> voiceManager.stopListening()
                            VoiceState.SPEAKING -> voiceManager.stopSpeaking()
                            VoiceState.PROCESSING -> {}
                            VoiceState.ERROR -> voiceManager.resetState()
                        }
                    }
                },
                modifier = Modifier.testTag("push_to_talk_orb")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Animated waveform
            AudioWaveformVisualizer(
                isListening = voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING,
                barCount = 14
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Transcript / AI Output Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(NavySurface)
                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = if (appLanguage == AppLanguage.HINDI) "वॉइस ट्रांसक्रिप्ट" else "Voice Transcript",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyanAccent
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (recognizedText.isNotBlank()) {
                Text(
                    text = recognizedText,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Submit Recognized Text to AI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val textToSend = recognizedText
                            voiceManager.setProcessing()
                            scope.launch {
                                val result = repository.sendMessage(textToSend)
                                if (result.isSuccess) {
                                    val reply = result.getOrNull() ?: ""
                                    lastAiSpokenResponse = reply
                                    voiceManager.speak(reply)
                                } else {
                                    val err = result.exceptionOrNull()?.message ?: "AI request failed"
                                    lastAiSpokenResponse = err
                                    voiceManager.resetState()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("submit_voice_text_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Submit",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (appLanguage == AppLanguage.HINDI) "AI को भेजें" else "Submit to AI",
                            fontSize = 13.sp
                        )
                    }
                }
            } else if (lastAiSpokenResponse != null) {
                Text(
                    text = "Air Nine: ${lastAiSpokenResponse!!}",
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            } else {
                Text(
                    text = if (appLanguage == AppLanguage.HINDI)
                        "जब आप बोलेंगे, यहाँ ट्रांसक्रिप्ट दिखाई देगी।"
                    else
                        "Speak to transcribe your voice in real time.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    fontSize = 12.sp,
                    color = ErrorRed
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (voiceState == VoiceState.SPEAKING) {
                Button(
                    onClick = { voiceManager.stopSpeaking() },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("stop_speaking_button")
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (appLanguage == AppLanguage.HINDI) "बोलना बंद करें" else "Stop Speaking")
                }
            } else if (voiceState == VoiceState.LISTENING) {
                Button(
                    onClick = { voiceManager.stopListening() },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("stop_listening_button")
                ) {
                    Icon(Icons.Default.MicOff, contentDescription = "Stop", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (appLanguage == AppLanguage.HINDI) "सुनना बंद करें" else "Finish Speech")
                }
            } else {
                Button(
                    onClick = {
                        if (!hasMicPermission) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            voiceManager.startListening()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("start_ptt_button")
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Mic", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (appLanguage == AppLanguage.HINDI) "पुश-टू-टॉक शुरू करें" else "Start Push-to-Talk")
                }
            }
        }
    }
}
