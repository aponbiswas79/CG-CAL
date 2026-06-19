package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.AcademicViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()

        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        val academicViewModel = ViewModelProvider(this)[AcademicViewModel::class.java]

        setContent {
            MyApplicationTheme {
                CgCalAppRoot(
                    authViewModel = authViewModel,
                    academicViewModel = academicViewModel
                )
            }
        }
    }
}

sealed class AppScreen {
    object Login : AppScreen()
    object Register : AppScreen()
    object Dashboard : AppScreen()
    object TheoryCalc : AppScreen()
    object LabCalc : AppScreen()
    object GpaCalc : AppScreen()
    object AdminSettings : AppScreen()
    object Profile : AppScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgCalAppRoot(
    authViewModel: AuthViewModel,
    academicViewModel: AcademicViewModel
) {
    val isSystemDark = isSystemInDarkTheme()
    var isDarkThemeUserOverride by remember { mutableStateOf<Boolean?>(null) }
    val isCurrentlyDark = isDarkThemeUserOverride ?: isSystemDark

    val activeUser by authViewModel.activeUser.collectAsState()
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Login) }

    LaunchedEffect(activeUser) {
        val user = activeUser
        if (user != null) {
            academicViewModel.setUserId(user.id)
            currentScreen = AppScreen.Dashboard
        } else {
            academicViewModel.setUserId(-1)
            currentScreen = AppScreen.Login
        }
    }

    MyApplicationTheme(darkTheme = isCurrentlyDark) {
        if (activeUser == null) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Crossfade(targetState = currentScreen, label = "UnauthenticatedNavigation") { screen ->
                    when (screen) {
                        AppScreen.Register -> {
                            RegisterScreen(
                                authViewModel = authViewModel,
                                academicViewModel = academicViewModel,
                                onNavigateToLogin = { currentScreen = AppScreen.Login }
                            )
                        }
                        else -> {
                            LoginScreen(
                                authViewModel = authViewModel,
                                onNavigateToRegister = { currentScreen = AppScreen.Register }
                            )
                        }
                    }
                }
            }
        } else {
            val user = activeUser!!
            
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = if (isCurrentlyDark) Sky400 else Blue600),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CG-CAL",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { isDarkThemeUserOverride = !isCurrentlyDark }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Toggle Theme"
                                )
                            }

                            IconButton(onClick = { currentScreen = AppScreen.AdminSettings }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Admin Panel",
                                    tint = if (currentScreen is AppScreen.AdminSettings) (if (isCurrentlyDark) Sky400 else Blue600) else LocalContentColor.current
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        NavigationBarItem(
                            selected = currentScreen is AppScreen.Dashboard,
                            onClick = { currentScreen = AppScreen.Dashboard },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                            label = { Text("Portal", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.TheoryCalc,
                            onClick = { currentScreen = AppScreen.TheoryCalc },
                            icon = { Icon(Icons.Default.Create, contentDescription = "Theory Evaluator") },
                            label = { Text("Theory", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.LabCalc,
                            onClick = { currentScreen = AppScreen.LabCalc },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Lab Practical") },
                            label = { Text("Practical", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.GpaCalc,
                            onClick = { currentScreen = AppScreen.GpaCalc },
                            icon = { Icon(Icons.Default.Add, contentDescription = "GPA/CGPA Planner") },
                            label = { Text("GPA", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )

                        NavigationBarItem(
                            selected = currentScreen is AppScreen.Profile,
                            onClick = { currentScreen = AppScreen.Profile },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile Portfolio") },
                            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Crossfade(targetState = currentScreen, label = "AuthenticatedNavigation") { screen ->
                        when (screen) {
                            AppScreen.Dashboard -> {
                                DashboardScreen(
                                    authViewModel = authViewModel,
                                    academicViewModel = academicViewModel,
                                    onNavigate = { route ->
                                        when (route) {
                                            "THEORY_CALC" -> currentScreen = AppScreen.TheoryCalc
                                            "LAB_CALC" -> currentScreen = AppScreen.LabCalc
                                            "GPA_CALC" -> currentScreen = AppScreen.GpaCalc
                                            "PROFILE" -> currentScreen = AppScreen.Profile
                                        }
                                    }
                                )
                            }
                            AppScreen.TheoryCalc -> {
                                TheoryCalculatorScreen(
                                    user = user,
                                    academicViewModel = academicViewModel,
                                    onBack = { currentScreen = AppScreen.Dashboard }
                                )
                            }
                            AppScreen.LabCalc -> {
                                LabCalculatorScreen(
                                    user = user,
                                    academicViewModel = academicViewModel,
                                    onBack = { currentScreen = AppScreen.Dashboard }
                                )
                            }
                            AppScreen.GpaCalc -> {
                                GpaCalculatorScreen(
                                    user = user,
                                    academicViewModel = academicViewModel,
                                    onBack = { currentScreen = AppScreen.Dashboard }
                                )
                            }
                            AppScreen.Profile -> {
                                ProfileScreen(
                                    authViewModel = authViewModel,
                                    academicViewModel = academicViewModel,
                                    onBack = { currentScreen = AppScreen.Dashboard }
                                )
                            }
                            AppScreen.AdminSettings -> {
                                AdminSettingsScreen(
                                    authViewModel = authViewModel,
                                    academicViewModel = academicViewModel,
                                    onBack = { currentScreen = AppScreen.Dashboard }
                                )
                            }
                            else -> {
                                Text("Routing error.", modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                }
            }
        }
    }
}
