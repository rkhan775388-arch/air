package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiModelInfo
import com.example.data.repository.AirNineRepository
import com.example.ui.components.MarkdownText
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceVariant
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun ModelArenaScreen(
    repository: AirNineRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val availableModels = repository.availableModels
    val arenaResult by repository.arenaResult.collectAsState()
    val isArenaRunning by repository.isArenaRunning.collectAsState()

    var modelA by remember { mutableStateOf(availableModels[0]) }
    var modelB by remember { mutableStateOf(availableModels.getOrElse(1) { availableModels[0] }) }

    var showMenuA by remember { mutableStateOf(false) }
    var showMenuB by remember { mutableStateOf(false) }

    var promptInput by remember { mutableStateOf("Explain quantum computing in simple terms for high school students.") }
    var selectedComparisonTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "AI Model Arena",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = CyanAccent
        )
        Text(
            text = "Benchmark two configured models side-by-side with identical prompts",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Model Selection Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Model A Selector
            Box(modifier = Modifier.weight(1f)) {
                ModelPickerCard(
                    label = "Model A",
                    model = modelA,
                    onClick = { showMenuA = true }
                )
                DropdownMenu(
                    expanded = showMenuA,
                    onDismissRequest = { showMenuA = false },
                    modifier = Modifier.background(NavySurfaceVariant)
                ) {
                    availableModels.forEach { m ->
                        DropdownMenuItem(
                            text = { Text(m.displayName, color = TextPrimary) },
                            onClick = {
                                modelA = m
                                showMenuA = false
                            }
                        )
                    }
                }
            }

            // Model B Selector
            Box(modifier = Modifier.weight(1f)) {
                ModelPickerCard(
                    label = "Model B",
                    model = modelB,
                    onClick = { showMenuB = true }
                )
                DropdownMenu(
                    expanded = showMenuB,
                    onDismissRequest = { showMenuB = false },
                    modifier = Modifier.background(NavySurfaceVariant)
                ) {
                    availableModels.forEach { m ->
                        DropdownMenuItem(
                            text = { Text(m.displayName, color = TextPrimary) },
                            onClick = {
                                modelB = m
                                showMenuB = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Prompt input
        OutlinedTextField(
            value = promptInput,
            onValueChange = { promptInput = it },
            label = { Text("Comparison Prompt", color = TextSecondary) },
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag("arena_prompt_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Run Arena Benchmark Button
        Button(
            onClick = {
                if (promptInput.isNotBlank()) {
                    scope.launch {
                        repository.runArenaComparison(promptInput, modelA, modelB)
                    }
                }
            },
            enabled = !isArenaRunning && promptInput.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("run_arena_benchmark_button")
        ) {
            if (isArenaRunning) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CyanAccent, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Evaluating Models in Parallel...")
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = "Run", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Run Arena Duel", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Results Section
        if (arenaResult != null) {
            val res = arenaResult!!

            TabRow(
                selectedTabIndex = selectedComparisonTab,
                containerColor = NavySurface,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedComparisonTab]),
                        color = CyanAccent
                    )
                }
            ) {
                Tab(
                    selected = selectedComparisonTab == 0,
                    onClick = { selectedComparisonTab = 0 },
                    text = { Text(res.modelAName, color = if (selectedComparisonTab == 0) CyanAccent else TextSecondary) }
                )
                Tab(
                    selected = selectedComparisonTab == 1,
                    onClick = { selectedComparisonTab = 1 },
                    text = { Text(res.modelBName, color = if (selectedComparisonTab == 1) CyanAccent else TextSecondary) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val currentModelName = if (selectedComparisonTab == 0) res.modelAName else res.modelBName
            val currentResponse = if (selectedComparisonTab == 0) res.modelAResponse else res.modelBResponse
            val currentLatency = if (selectedComparisonTab == 0) res.modelALatencyMs else res.modelBLatencyMs

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavySurface)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currentModelName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Text(
                            text = "Latency: ${currentLatency}ms • Length: ${currentResponse.length} chars",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Arena Result", currentResponse))
                                Toast.makeText(context, "Copied $currentModelName response", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyanAccent)
                        }

                        IconButton(
                            onClick = {
                                repository.voteArenaWinner(currentModelName)
                                Toast.makeText(context, "Voted for $currentModelName", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                Icons.Default.ThumbUp,
                                contentDescription = "Vote Winner",
                                tint = if (res.selectedWinner == currentModelName) NeonTeal else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                MarkdownText(text = currentResponse)
            }
        }
    }
}

@Composable
private fun ModelPickerCard(
    label: String,
    model: AiModelInfo,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavySurface)
            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = model.displayName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CyanAccent
        )
        Text(text = model.latencyTier, fontSize = 11.sp, color = TextSecondary)
    }
}
