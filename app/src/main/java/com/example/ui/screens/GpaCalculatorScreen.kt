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
import com.example.data.model.Semester
import com.example.data.model.Course
import com.example.ui.components.*
import com.example.ui.viewmodel.AcademicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaCalculatorScreen(
    user: User,
    academicViewModel: AcademicViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    val semesters by academicViewModel.semesters.collectAsState()
    
    val newSemName by academicViewModel.newSemesterName.collectAsState()

    val cName by academicViewModel.newCourseName.collectAsState()
    val cCredit by academicViewModel.newCourseCredit.collectAsState()
    val cGrade by academicViewModel.newCourseGrade.collectAsState()

    val simGpa by academicViewModel.simulatedNextSemesterGpa.collectAsState()
    val simCredits by academicViewModel.simulatedNextSemesterCredits.collectAsState()
    val simulatedCgpa by academicViewModel.simulatedCgpaPrediction.collectAsState()

    var activeCourseSemesterId by remember { mutableStateOf<Int?>(null) }
    var gradeMenuExpanded by remember { mutableStateOf(false) }

    val gradingOptions = listOf("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F")

    var totalCredits = 0.0
    var pointMultiplierSum = 0.0
    semesters.forEach {
        totalCredits += it.totalCredits
        pointMultiplierSum += (it.gpa * it.totalCredits)
    }
    val overallCgpa = if (totalCredits > 0) pointMultiplierSum / totalCredits else 0.0

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
                        text = "ACADEMIC TERM HUB",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Sky400 else Blue600
                    )
                    Text(
                        text = "GPA & CGPA Calculator",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LIVE CGPA SUMMARY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%.3f CGPA".format(overallCgpa),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Sky400 else Blue600
                        )
                        Text(
                            text = "Based on ${"%.1f".format(totalCredits)} accumulated credits",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Box(modifier = Modifier.size(70.dp), contentAlignment = Alignment.Center) {
                        PerformanceMeter(
                            score = overallCgpa,
                            maxScore = 4.0,
                            isDark = isDark,
                            scoreLabel = "CGPA",
                            modifier = Modifier.size(66.dp)
                        )
                    }
                }
            }

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Text(
                    text = "ADD ACADEMIC SEMESTER / TERM",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Sky400 else Blue600,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newSemName,
                        onValueChange = { academicViewModel.newSemesterName.value = it },
                        label = { Text("e.g. Year 1 Term I") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    Button(
                        onClick = { academicViewModel.addSemester() },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Sky400 else Blue600),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("TERM", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Text(
                text = "SEMESTER COURSE DETAILS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.LightGray else Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (semesters.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0x0CFFFFFF) else Color(0x1AFFFFFF))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No semesters listed. Create your first term above!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                semesters.forEach { semester ->
                    val coursesFlow = academicViewModel.getCoursesForSemester(semester.id).collectAsState(initial = emptyList())
                    val courses = coursesFlow.value

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = semester.name.uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = if (isDark) Color.White else Slate900
                                    )
                                    Text(
                                        text = "Term GPA: %.2f | Credit Hours: %.1f".format(semester.gpa, semester.totalCredits),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Sky400 else Blue600
                                    )
                                }

                                IconButton(
                                    onClick = { academicViewModel.deleteSemester(semester) },
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0x19EF4444))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Collapse semester",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = if (isDark) Color(0x1FFFFFFF) else Color(0x0F000000))

                            if (courses.isEmpty()) {
                                Text(
                                    text = "No course cards added yet.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                                )
                            } else {
                                courses.forEach { course ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp, horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = course.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) Color.White else Slate900
                                            )
                                            Text(
                                                text = "Credit: ${course.creditHours} | Grade: ${course.grade} (${course.gradePoint} GP)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                        }

                                        IconButton(
                                            onClick = { academicViewModel.deleteCourse(course) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove course",
                                                tint = Color.Red,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (activeCourseSemesterId == semester.id) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Add Course Evaluation",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Sky400 else Blue600
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = cName,
                                            onValueChange = { academicViewModel.newCourseName.value = it },
                                            label = { Text("Course Name") },
                                            modifier = Modifier.weight(1.5f),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = cCredit,
                                            onValueChange = { academicViewModel.newCourseCredit.value = it },
                                            label = { Text("Credit") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.weight(1.5f)) {
                                            OutlinedTextField(
                                                value = cGrade,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("Grade") },
                                                trailingIcon = {
                                                    IconButton(onClick = { gradeMenuExpanded = !gradeMenuExpanded }) {
                                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            DropdownMenu(
                                                expanded = gradeMenuExpanded,
                                                onDismissRequest = { gradeMenuExpanded = false }
                                            ) {
                                                gradingOptions.forEach { grade ->
                                                    DropdownMenuItem(
                                                        text = { Text(grade) },
                                                        onClick = {
                                                            academicViewModel.newCourseGrade.value = grade
                                                            gradeMenuExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                academicViewModel.addCourseToSemester(semester.id)
                                                activeCourseSemesterId = null
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).height(50.dp)
                                        ) {
                                            Text("Save", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        activeCourseSemesterId = semester.id
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ADD COURSE EVALUATION", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Text(
                text = "ACADEMIC SIMULATION PLANNER",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.LightGray else Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
            )

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Text(
                    text = "FUTURE TERM CGPA SIMULATOR",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Sky400 else Blue600,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Project your future CGPA standing by simulating upcoming semester results.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = simGpa,
                        onValueChange = { academicViewModel.simulatedNextSemesterGpa.value = it },
                        label = { Text("What-If GPA?") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = simCredits,
                        onValueChange = { academicViewModel.simulatedNextSemesterCredits.value = it },
                        label = { Text("Sim Credit Hours") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Button(
                    onClick = { academicViewModel.simulateFutureCgpa() },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Sky400 else Blue600),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("RUN SIMULATION CALCULATION", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }

                if (simulatedCgpa != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0x3310B981) else Color(0x2710B981)),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        border = BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "SIMULATED FUTURE CGPA OUTCOME",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your Overall CGPA will become: %.3f".format(simulatedCgpa!!),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Black,
                                color = if (isDark) Color.White else Slate900
                            )
                            Text(
                                text = "By appending ${simCredits} credits of GPA ${simGpa} to your current academic profile.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
