package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun WebResearchScreen(
    repository: AirNineRepository,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var researchTopic by remember { mutableStateOf("State of real-time conversational AI models 2026") }
    var isSearching by remember { mutableStateOf(false) }
    var researchSummary by remember { mutableStateOf<String?>(null) }
    var subQueries by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Language, contentDescription = "Research", tint = CyanAccent, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Web Research Hub",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
                Text(
                    text = "Multi-step source retrieval and structured synthesis",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = researchTopic,
            onValueChange = { researchTopic = it },
            label = { Text("Topic or Hypothesis to Investigate", color = TextSecondary) },
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
                .testTag("research_topic_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (researchTopic.isNotBlank()) {
                    isSearching = true
                    subQueries = listOf(
                        "Latest multi-modal streaming latency benchmarks 2026",
                        "Speech-to-speech neural architectures",
                        "Device-edge vs cloud synthesis trade-offs"
                    )
                    scope.launch {
                        val prompt = """
                        Conduct a structured research synthesis on: "$researchTopic"
                        Format with:
                        - Executive Overview
                        - Key Verified Findings
                        - Emerging Technical Trends
                        - Source References
                        """.trimIndent()
                        val res = repository.sendMessage(prompt)
                        researchSummary = res.getOrElse { "Research synthesis error: ${it.message}" }
                        isSearching = false
                    }
                }
            },
            enabled = !isSearching && researchTopic.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("run_research_button")
        ) {
            if (isSearching) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CyanAccent, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Synthesizing Research...")
            } else {
                Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Decompose & Synthesize Topic", fontWeight = FontWeight.SemiBold)
            }
        }

        if (subQueries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Decomposed Query Angles:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyanAccent
            )
            Spacer(modifier = Modifier.height(6.dp))
            subQueries.forEach { q ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavySurface)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Link, contentDescription = "Query", tint = ElectricBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = q, fontSize = 12.sp, color = TextPrimary)
                }
            }
        }

        if (researchSummary != null) {
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
                    text = "Synthesized Findings",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
                Spacer(modifier = Modifier.height(10.dp))
                MarkdownText(text = researchSummary!!)
            }
        }
    }
}
