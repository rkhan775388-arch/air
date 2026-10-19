package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AirNineRepository
import com.example.ui.components.MarkdownText
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun ToolsScreen(
    repository: AirNineRepository,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedToolTab by remember { mutableIntStateOf(0) } // 0: Coding, 1: Creative, 2: Image Studio

    var toolInput by remember { mutableStateOf("") }
    var toolOutput by remember { mutableStateOf<String?>(null) }
    var isRunning by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = "Tools", tint = CyanAccent, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Air Nine Tools Hub",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
                Text(
                    text = "Specialized AI reasoning assistants and generators",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tool Selector Tabs
        TabRow(
            selectedTabIndex = selectedToolTab,
            containerColor = NavySurface,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedToolTab]),
                    color = CyanAccent
                )
            }
        ) {
            Tab(
                selected = selectedToolTab == 0,
                onClick = {
                    selectedToolTab = 0
                    toolInput = "Refactor this Kotlin coroutine function to handle exceptions gracefully:"
                },
                text = { Text("Code", color = if (selectedToolTab == 0) CyanAccent else TextSecondary) },
                icon = { Icon(Icons.Default.Code, contentDescription = "Coding", modifier = Modifier.size(16.dp), tint = if (selectedToolTab == 0) CyanAccent else TextMuted) }
            )
            Tab(
                selected = selectedToolTab == 1,
                onClick = {
                    selectedToolTab = 1
                    toolInput = "Write a compelling product launch announcement for an enterprise AI workspace:"
                },
                text = { Text("Creative", color = if (selectedToolTab == 1) CyanAccent else TextSecondary) },
                icon = { Icon(Icons.Default.Brush, contentDescription = "Creative", modifier = Modifier.size(16.dp), tint = if (selectedToolTab == 1) CyanAccent else TextMuted) }
            )
            Tab(
                selected = selectedToolTab == 2,
                onClick = {
                    selectedToolTab = 2
                    toolInput = "Dark navy futuristic server room with holographic cyan data streams"
                },
                text = { Text("Visual Prompt", color = if (selectedToolTab == 2) CyanAccent else TextSecondary) },
                icon = { Icon(Icons.Default.Image, contentDescription = "Image Studio", modifier = Modifier.size(16.dp), tint = if (selectedToolTab == 2) CyanAccent else TextMuted) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val label = when (selectedToolTab) {
            0 -> "Code Snippet or Programming Query"
            1 -> "Topic, Tone, or Draft Context"
            else -> "Visual Concept to Enhance"
        }

        OutlinedTextField(
            value = toolInput,
            onValueChange = { toolInput = it },
            label = { Text(label, color = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = NavySurface,
                unfocusedContainerColor = NavySurface,
                focusedBorderColor = CyanAccent,
                unfocusedBorderColor = NavyBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = CyanAccent
            ),
            shape = RoundedCornerShape(12.dp),
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tool_input_field")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (toolInput.isNotBlank()) {
                    isRunning = true
                    scope.launch {
                        val prompt = when (selectedToolTab) {
                            0 -> "You are an expert software engineer. Provide clean, documented code and explanation for:\n$toolInput"
                            1 -> "You are a senior creative copywriter. Craft exceptional, evocative prose for:\n$toolInput"
                            else -> "You are a generative image prompt engineer. Expand this raw concept into a highly detailed cinematic prompt with lighting, atmosphere, style, and camera angles:\n$toolInput"
                        }
                        val res = repository.sendMessage(prompt)
                        toolOutput = res.getOrElse { "Execution error: ${it.message}" }
                        isRunning = false
                    }
                }
            },
            enabled = !isRunning && toolInput.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("run_tool_button")
        ) {
            if (isRunning) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CyanAccent, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generating...")
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = "Execute", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                val btnText = when (selectedToolTab) {
                    0 -> "Analyze & Generate Code"
                    1 -> "Generate Creative Copy"
                    else -> "Synthesize Master Prompt"
                }
                Text(btnText, fontWeight = FontWeight.SemiBold)
            }
        }

        if (toolOutput != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavySurface)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "Generated Output",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
                Spacer(modifier = Modifier.height(8.dp))
                MarkdownText(text = toolOutput!!)
            }
        }
    }
}
