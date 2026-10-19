package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.VoiceState
import com.example.data.repository.AirNineRepository
import com.example.speech.VoiceManager
import com.example.ui.navigation.AirNineScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DocumentWorkspaceScreen
import com.example.ui.screens.ModelArenaScreen
import com.example.ui.screens.ProjectSpacesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TaskFlowScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.TrustCheckScreen
import com.example.ui.screens.VoiceModeScreen
import com.example.ui.screens.WebResearchScreen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirNineApp(
    repository: AirNineRepository,
    voiceManager: VoiceManager
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var currentScreen by remember { mutableStateOf(AirNineScreen.CHAT) }

    val appLanguage by repository.appLanguage.collectAsState()
    val voiceState by voiceManager.voiceState.collectAsState()
    val selectedModel by repository.selectedModel.collectAsState()

    // Handle back button: if drawer is open, close it; if not on chat screen, return to chat
    BackHandler(enabled = drawerState.isOpen || currentScreen != AirNineScreen.CHAT) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            currentScreen = AirNineScreen.CHAT
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpanded = maxWidth >= 720.dp

        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = !isExpanded,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = NavySurface,
                    drawerContentColor = TextPrimary,
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight()
                        .border(1.dp, NavyBorder)
                ) {
                    // Drawer Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavySurfaceVariant)
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ElectricBlue)
                                    .border(1.dp, CyanAccent, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "9",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Air Nine",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent
                                )
                                Text(
                                    text = "AI Workspace",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = NavyBorder)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Drawer Navigation Items
                    AirNineScreen.values().forEach { screen ->
                        val isSelected = currentScreen == screen
                        val title = if (appLanguage == AppLanguage.HINDI) screen.titleHi else screen.titleEn

                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    screen.icon,
                                    contentDescription = title,
                                    tint = if (isSelected) CyanAccent else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CyanAccent else TextPrimary
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                currentScreen = screen
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = ElectricBlue.copy(alpha = 0.2f),
                                unselectedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 2.dp)
                                .testTag(screen.testTag)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Bottom active model indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NavyCard)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(text = "Active Engine", fontSize = 11.sp, color = TextMuted)
                            Text(text = selectedModel.displayName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CyanAccent)
                        }
                    }
                }
            }
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Tablet / Desktop Navigation Rail
                if (isExpanded) {
                    NavigationRail(
                        containerColor = NavySurface,
                        contentColor = TextPrimary,
                        header = {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ElectricBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("9", fontWeight = FontWeight.Black, fontSize = 22.sp, color = TextPrimary)
                            }
                        },
                        modifier = Modifier.border(1.dp, NavyBorder)
                    ) {
                        AirNineScreen.values().forEach { screen ->
                            val isSelected = currentScreen == screen
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { currentScreen = screen },
                                icon = { Icon(screen.icon, contentDescription = screen.titleEn) },
                                label = { Text(screen.titleEn, fontSize = 10.sp) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = CyanAccent,
                                    selectedTextColor = CyanAccent,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = ElectricBlue.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }

                // Main Content Area
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Air Nine",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NavyCard)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        val screenTitle = if (appLanguage == AppLanguage.HINDI) currentScreen.titleHi else currentScreen.titleEn
                                        Text(
                                            text = screenTitle,
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("app_menu_drawer_button")
                                ) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "Open Navigation Menu",
                                        tint = TextPrimary
                                    )
                                }
                            },
                            actions = {
                                // Voice Mode quick tap
                                IconButton(
                                    onClick = { currentScreen = AirNineScreen.VOICE },
                                    modifier = Modifier.testTag("topbar_voice_button")
                                ) {
                                    Icon(
                                        Icons.Default.Mic,
                                        contentDescription = "Voice Mode",
                                        tint = if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) NeonTeal else CyanAccent
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = NavySurface,
                                titleContentColor = CyanAccent
                            )
                        )
                    },
                    bottomBar = {
                        // Mobile Bottom Navigation Bar (Core Workspaces)
                        if (!isExpanded) {
                            val bottomNavItems = listOf(
                                AirNineScreen.CHAT,
                                AirNineScreen.VOICE,
                                AirNineScreen.ARENA,
                                AirNineScreen.TRUST,
                                AirNineScreen.PROJECTS
                            )

                            NavigationBar(
                                containerColor = NavySurface,
                                modifier = Modifier
                                    .border(1.dp, NavyBorder)
                                    .navigationBarsPadding()
                                    .testTag("airnine_bottom_navigation")
                            ) {
                                bottomNavItems.forEach { screen ->
                                    val isSelected = currentScreen == screen
                                    val label = if (appLanguage == AppLanguage.HINDI) screen.titleHi else screen.titleEn

                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentScreen = screen },
                                        icon = {
                                            Icon(
                                                screen.icon,
                                                contentDescription = label,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = label,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = CyanAccent,
                                            selectedTextColor = CyanAccent,
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted,
                                            indicatorColor = ElectricBlue.copy(alpha = 0.35f)
                                        ),
                                        modifier = Modifier.testTag("bottom_nav_${screen.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    },
                    containerColor = NavyDarkBackground
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            AirNineScreen.CHAT -> ChatScreen(
                                repository = repository,
                                voiceManager = voiceManager,
                                onNavigateToVoice = { currentScreen = AirNineScreen.VOICE }
                            )
                            AirNineScreen.VOICE -> VoiceModeScreen(
                                repository = repository,
                                voiceManager = voiceManager
                            )
                            AirNineScreen.ARENA -> ModelArenaScreen(
                                repository = repository
                            )
                            AirNineScreen.TRUST -> TrustCheckScreen(
                                repository = repository
                            )
                            AirNineScreen.PROJECTS -> ProjectSpacesScreen(
                                repository = repository,
                                onOpenChatWithProject = { currentScreen = AirNineScreen.CHAT }
                            )
                            AirNineScreen.TASKFLOW -> TaskFlowScreen(
                                repository = repository
                            )
                            AirNineScreen.DOCS -> DocumentWorkspaceScreen(
                                repository = repository
                            )
                            AirNineScreen.RESEARCH -> WebResearchScreen(
                                repository = repository
                            )
                            AirNineScreen.TOOLS -> ToolsScreen(
                                repository = repository
                            )
                            AirNineScreen.SETTINGS -> SettingsScreen(
                                repository = repository,
                                voiceManager = voiceManager
                            )
                        }
                    }
                }
            }
        }
    }
}
