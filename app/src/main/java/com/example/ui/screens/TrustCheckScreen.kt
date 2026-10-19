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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AirNineRepository
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDarkBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.launch

@Composable
fun TrustCheckScreen(
    repository: AirNineRepository,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val trustAnalysis by repository.trustAnalysis.collectAsState()
    val isAnalyzing by repository.isAnalyzingTrust.collectAsState()

    var statementInput by remember {
        mutableStateOf("Gemini 2.5 uses native multi-modal reasoning and supports over 1 million tokens of context.")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Security,
                contentDescription = "Trust Check",
                tint = CyanAccent,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Air Nine Trust Check",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
                Text(
                    text = "Audit statements for factual grounding, sources, and certainty",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mandatory Disclaimer Notice
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(WarningAmber.copy(alpha = 0.12f))
                .border(1.dp, WarningAmber.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Disclaimer",
                    tint = WarningAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI outputs can contain inaccuracies or hallucinated claims. Always review critical assertions before decision making.",
                    fontSize = 12.sp,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = statementInput,
            onValueChange = { statementInput = it },
            label = { Text("Statement or Claim to Audit", color = TextSecondary) },
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
                .testTag("trust_check_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (statementInput.isNotBlank()) {
                    scope.launch {
                        repository.analyzeTrust(statementInput)
                    }
                }
            },
            enabled = !isAnalyzing && statementInput.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("run_trust_audit_button")
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CyanAccent, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Evaluating Factuality & Uncertainty...")
            } else {
                Text("Analyze Statement Reliability", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (trustAnalysis != null) {
            val analysis = trustAnalysis!!

            // Score Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavySurface)
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Trust Confidence Score",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "${analysis.trustScore} / 100",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = if (analysis.trustScore >= 80) SuccessGreen else WarningAmber
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(NavyCard)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = analysis.uncertaintyScore,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyanAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Verdict: ${analysis.verdict}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Verified Points
                Text(
                    text = "Supported Elements:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SuccessGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                analysis.verifiedClaims.forEach { point ->
                    Row(modifier = Modifier.padding(vertical = 3.dp)) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Supported",
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = point, fontSize = 13.sp, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Potential Risks
                Text(
                    text = "Unverified / Extrapolated Claims:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WarningAmber
                )
                Spacer(modifier = Modifier.height(6.dp))
                analysis.unsupportedClaims.forEach { risk ->
                    Row(modifier = Modifier.padding(vertical = 3.dp)) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Risk",
                            tint = WarningAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = risk, fontSize = 13.sp, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sources
                Text(
                    text = "Reference Sources:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyanAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                analysis.sources.forEach { src ->
                    Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Link,
                            contentDescription = "Source",
                            tint = CyanAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = src, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}
