package com.example.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.AcademicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    academicViewModel: AcademicViewModel,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    val fullName by authViewModel.registerFullName.collectAsState()
    val email by authViewModel.registerEmail.collectAsState()
    val phone by authViewModel.registerPhone.collectAsState()
    val username by authViewModel.registerUsername.collectAsState()
    val password by authViewModel.registerPassword.collectAsState()
    val confirmPassword by authViewModel.registerConfirmPassword.collectAsState()
    
    val selectedSchool by authViewModel.profileSchool.collectAsState()
    val selectedDiscipline by authViewModel.profileDiscipline.collectAsState()
    val selectedBatch by authViewModel.profileBatch.collectAsState()

    val isLoading by authViewModel.isAuthLoading.collectAsState()
    val error by authViewModel.authError.collectAsState()
    val success by authViewModel.authSuccessMessage.collectAsState()

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var schoolExpanded by remember { mutableStateOf(false) }
    var disciplineExpanded by remember { mutableStateOf(false) }

    val schools = academicViewModel.kuSchoolsAndDisciplines.keys.toList()
    val disciplines = academicViewModel.kuSchoolsAndDisciplines[selectedSchool] ?: emptyList()

    AppGradientBackground(isDark = isDark) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onNavigateToLogin) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDark) Sky400 else Blue600
                    )
                }
            }

            Text(
                text = "CREATE ACCOUNT",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else Slate900,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Join KU CG-CAL Grade Prediction & Management Platform",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) Color.LightGray else Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            if (error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x26EF4444)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Error", tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = error!!, color = if (isDark) Color.White else Color(0xFF7F1D1D), fontSize = 13.sp)
                    }
                }
            }

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ACADEMIC & SECURITY DETAILS",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isDark) Sky400 else Blue600,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { authViewModel.registerFullName.value = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Sky400 else Blue600,
                        focusedLabelColor = if (isDark) Sky400 else Blue600
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { authViewModel.registerEmail.value = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Sky400 else Blue600,
                        focusedLabelColor = if (isDark) Sky400 else Blue600
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { authViewModel.registerPhone.value = it },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Sky400 else Blue600,
                        focusedLabelColor = if (isDark) Sky400 else Blue600
                    )
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { authViewModel.registerUsername.value = it },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Sky400 else Blue600,
                        focusedLabelColor = if (isDark) Sky400 else Blue600
                    )
                )

                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    OutlinedTextField(
                        value = selectedSchool,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("KU School") },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                        trailingIcon = {
                            IconButton(onClick = { schoolExpanded = !schoolExpanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = schoolExpanded,
                        onDismissRequest = { schoolExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        schools.forEach { school ->
                            DropdownMenuItem(
                                text = { Text(school) },
                                onClick = {
                                    authViewModel.profileSchool.value = school
                                    val newDisciplines = academicViewModel.kuSchoolsAndDisciplines[school] ?: emptyList()
                                    if (newDisciplines.isNotEmpty()) {
                                        authViewModel.profileDiscipline.value = newDisciplines.first()
                                    }
                                    schoolExpanded = false
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    OutlinedTextField(
                        value = selectedDiscipline,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Discipline") },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                        trailingIcon = {
                            IconButton(onClick = { disciplineExpanded = !disciplineExpanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = disciplineExpanded,
                        onDismissRequest = { disciplineExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        disciplines.forEach { disc ->
                            DropdownMenuItem(
                                text = { Text(disc) },
                                onClick = {
                                    authViewModel.profileDiscipline.value = disc
                                    disciplineExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = selectedBatch,
                    onValueChange = { authViewModel.profileBatch.value = it },
                    label = { Text("Batch / Year (e.g. 24, 23)") },
                    leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Sky400 else Blue600,
                        focusedLabelColor = if (isDark) Sky400 else Blue600
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { authViewModel.registerPassword.value = it },
                    label = { Text("Secret Password (min 6 characters)") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Warning else Icons.Default.Lock,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Sky400 else Blue600,
                        focusedLabelColor = if (isDark) Sky400 else Blue600
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { authViewModel.registerConfirmPassword.value = it },
                    label = { Text("Confirm Secret Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) Icons.Default.Warning else Icons.Default.Lock,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Sky400 else Blue600,
                        focusedLabelColor = if (isDark) Sky400 else Blue600
                    )
                )

                Button(
                    onClick = { authViewModel.register() },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Sky400 else Blue600),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("CREATE ACCOUNT & ACTIVATE", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already registered?",
                    color = if (isDark) Color.LightGray else Slate700,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Sign In",
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Sky400 else Blue600,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
