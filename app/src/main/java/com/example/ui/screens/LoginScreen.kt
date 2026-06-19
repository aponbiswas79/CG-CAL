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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    val usernameOrEmail by authViewModel.loginUsernameOrEmail.collectAsState()
    val password by authViewModel.loginPassword.collectAsState()
    val rememberMe by authViewModel.isRememberMe.collectAsState()
    
    val isLoading by authViewModel.isAuthLoading.collectAsState()
    val error by authViewModel.authError.collectAsState()
    val success by authViewModel.authSuccessMessage.collectAsState()

    var showPassword by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmailInput by remember { mutableStateOf("") }
    var forgotDialogSuccessMsg by remember { mutableStateOf<String?>(null) }

    AppGradientBackground(isDark = isDark) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0x3338BDF8) else Color(0x272563EB)),
                modifier = Modifier.size(80.dp),
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "CG-CAL Logo",
                        tint = if (isDark) Sky400 else Blue600,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CG-CAL",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else Slate900,
                textAlign = TextAlign.Center,
                fontSize = 32.sp
            )
            
            Text(
                text = "Khulna University Academic Portal",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Sky400 else Blue600,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
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

            if (success != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x2610B981)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Success", tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = success!!, color = if (isDark) Color.White else Color(0xFF064E3B), fontSize = 13.sp)
                    }
                }
            }

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "SECURE SIGN IN",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isDark) Color.LightGray else Slate700,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = usernameOrEmail,
                    onValueChange = { authViewModel.loginUsernameOrEmail.value = it },
                    label = { Text("Email Address or Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Sky400 else Blue600,
                        focusedLabelColor = if (isDark) Sky400 else Blue600
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { authViewModel.loginPassword.value = it },
                    label = { Text("Secret Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Warning else Icons.Default.Lock,
                                contentDescription = if (showPassword) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) Sky400 else Blue600,
                        focusedLabelColor = if (isDark) Sky400 else Blue600
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { authViewModel.isRememberMe.value = it },
                            colors = CheckboxDefaults.colors(checkedColor = if (isDark) Sky400 else Blue600)
                        )
                        Text(
                            text = "Remember Me",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) Color.LightGray else Slate700
                        )
                    }

                    TextButton(onClick = { 
                        showForgotDialog = true 
                        forgotDialogSuccessMsg = null
                    }) {
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Sky400 else Blue600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { authViewModel.login() },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Sky400 else Blue600),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("SIGN IN SECURELY", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
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
                    text = "Don't have an account yet?",
                    color = if (isDark) Color.LightGray else Slate700,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Register Now",
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Sky400 else Blue600,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Disclaimer: An independent secure calculation solution designed for students of Khulna University (KU), Bangladesh.",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) Color.Gray else Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = {
                Text(text = "Reset Password Request", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Enter your registered email address and we'll simulate a secure sandbox password recovery link.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) Color.LightGray else Slate700,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    OutlinedTextField(
                        value = forgotEmailInput,
                        onValueChange = { forgotEmailInput = it },
                        label = { Text("Registered Email Address") },
                        leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (forgotDialogSuccessMsg != null) {
                        Text(
                            text = forgotDialogSuccessMsg!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotEmailInput.trim().isEmpty()) {
                            forgotDialogSuccessMsg = "Please enter a valid email address!"
                        } else {
                            forgotDialogSuccessMsg = "Reset link sent successfully to ${forgotEmailInput.trim()}! Please check your inbox (sandboxed Simulation)."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Sky400 else Blue600)
                ) {
                    Text("Send Request", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
