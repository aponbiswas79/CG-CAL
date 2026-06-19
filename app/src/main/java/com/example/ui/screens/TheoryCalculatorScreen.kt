package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.components.*
import com.example.ui.viewmodel.AcademicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheoryCalculatorScreen(
    user: User,
    academicViewModel: AcademicViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    val courseName by academicViewModel.theoryCourseName.collectAsState()
    val ct1 by academicViewModel.theoryCt1.collectAsState()
    val ct2 by academicViewModel.theoryCt2.collectAsState()
    val ct3 by academicViewModel.theoryCt3.collectAsState()
    val ct4 by academicViewModel.theoryCt4.collectAsState()
    val attendance by academicViewModel.theoryAttendance.collectAsState()

    val predictionResult by academicViewModel.theoryPredictionResult.collectAsState()
    val isPredictionLoading by academicViewModel.isPredictionLoading.collectAsState()

    var selectedTargetGrade by remember { mutableStateOf("A+") }
    var scaleExpanded by remember { mutableStateOf(false) }

    val gradeTargets = listOf("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F")

    val ct1Val = ct1.toDoubleOrNull() ?: 0.0
    val ct2Val = ct2.toDoubleOrNull() ?: 0.0
    val ct3Val = ct3.toDoubleOrNull() ?: 0.0
    val ct4Val = ct4.toDoubleOrNull() ?: 0.0
    val attVal = attendance.toDoubleOrNull() ?: 0.0

    val bestA = maxOf(ct1Val, ct2Val)
    val bestB = maxOf(ct3Val, ct4Val)
    val carryMarks = (bestA + bestB) / 2.0 + attVal

    val (feedbackMsg, feedbackColor, feedbackIcon) = when {
        carryMarks >= 38.0 -> Triple("Outstanding Performance 🔥", Color(0xFF10B981), Icons.Default.Check)
        carryMarks >= 35.0 -> Triple("Very Good Performance ✅", Color(0xFF059669), Icons.Default.Check)
        carryMarks >= 30.0 -> Triple("Good Performance 👍", Color(0xFF2563EB), Icons.Default.ThumbUp)
        carryMarks >= 25.0 -> Triple("Needs Improvement ⚠️", Color(0xFFD97706), Icons.Default.Warning)
        else -> Triple("Very Low Carry Marks ❌", Color(0xFFDC2626), Icons.Default.Close)
    }

    AppGradientBackground(isDark = isDark) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDark) Sky400 else Blue600
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "THEORY COURSE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Sky400 else Blue600
                    )
                    Text(
                        text = "Marks Evaluator & AI Predictor",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else Slate900
                    )
                }
            }

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = "COURSE DETAIL",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Sky400 else Blue600,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { academicViewModel.theoryCourseName.value = it },
                    label = { Text("Course Name or Code (e.g. CSE-3101)") },
                    leadingIcon = { Icon(Icons.Default.Create, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = "INTERNAL MARKS ASSESSMENTS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Sky400 else Blue600,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Section A Class Tests (Best of counts)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.LightGray else Slate700,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = ct1,
                        onValueChange = { academicViewModel.theoryCt1.value = it },
                        label = { Text("CT 1 (Max 30)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = ct2,
                        onValueChange = { academicViewModel.theoryCt2.value = it },
                        label = { Text("CT 2 (Max 30)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Text(
                    text = "Section B Class Tests (Best of counts)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.LightGray else Slate700,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = ct3,
                        onValueChange = { academicViewModel.theoryCt3.value = it },
                        label = { Text("CT 3 (Max 30)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = ct4,
                        onValueChange = { academicViewModel.theoryCt4.value = it },
                        label = { Text("CT 4 (Max 30)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Text(
                    text = "Class Attendance (Assessed out of 10)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.LightGray else Slate700,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = attendance,
                    onValueChange = { academicViewModel.theoryAttendance.value = it },
                    label = { Text("Attendance Score (0 to 10)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = "CARRY MARKS SUMMARY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.LightGray else Color.Gray,
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${"%.2f".format(carryMarks)} / 40.00",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = feedbackColor
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                            Icon(
                                imageVector = feedbackIcon,
                                contentDescription = null,
                                tint = feedbackColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = feedbackMsg,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = feedbackColor
                            )
                        }
                    }

                    Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                        PerformanceMeter(
                            score = carryMarks,
                            maxScore = 40.0,
                            isDark = isDark,
                            scoreLabel = "Carry",
                            modifier = Modifier.size(76.dp)
                        )
                    }
                }

                CustomProgressBar(
                    progress = (carryMarks / 40.0).toFloat(),
                    color = feedbackColor
                )
            }

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Text(
                    text = "TARGET GRADE PREDICTOR",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Sky400 else Blue600,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    OutlinedTextField(
                        value = selectedTargetGrade,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Target Term Grade") },
                        trailingIcon = {
                            IconButton(onClick = { scaleExpanded = !scaleExpanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = scaleExpanded,
                        onDismissRequest = { scaleExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        gradeTargets.forEach { grade ->
                            DropdownMenuItem(
                                text = { Text("Grade $grade") },
                                onClick = {
                                    selectedTargetGrade = grade
                                    scaleExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        academicViewModel.calculateAndPredictTheory(
                            discipline = user.discipline,
                            school = user.school,
                            targetGrade = selectedTargetGrade
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Sky400 else Blue600),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !isPredictionLoading
                ) {
                    if (isPredictionLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("RUN SMART GRADE PREDICTION", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            if (predictionResult != null) {
                val res = predictionResult!!
                val isImpossible = res.probabilityIndicator == "Impossible"

                GlassmorphicCard(
                    isDark = isDark,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PREDICTION ANALYSIS RESULT",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Sky400 else Blue600
                        )
                        
                        IconButton(
                            onClick = {
                                academicViewModel.saveTheoryCalculation()
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0x1910B981))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save result",
                                tint = Color(0xFF10B981)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isImpossible) {
                            "Grade ${res.targetGrade} is mathematically impossible from your carry marks."
                        } else {
                            "At least ${"%.1f".format(res.minFinalRequired)} marks out of 60 standard required in the final semester exam."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Slate900,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0x11FFFFFF) else Color(0x06000000)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "Safe Grade Guidance", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Text(text = res.safeGrade, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = if (isDark) Sky400 else Blue600)
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0x11FFFFFF) else Color(0x06000000)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "Preparation Risk Level", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Text(text = res.riskLevel, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (isImpossible) Color.Red else Color.Unspecified)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Probability of Achievement:", style = MaterialTheme.typography.bodyMedium, color = if (isDark) Color.LightGray else Slate700)
                        Text(text = res.probabilityIndicator, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = if (isImpossible) Color.Red else Color(0xFF10B981))
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isDark) Color(0x3338BDF8) else Color(0x1C2563EB))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isDark) Sky400 else Blue600,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ACADEMIC AI COUNSEL",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDark) Sky400 else Blue600,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = res.boosterAdvice,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) Color.White else Slate900,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
