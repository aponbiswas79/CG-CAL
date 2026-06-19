package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AcademicRepository
import com.example.util.GeminiPredictor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AcademicViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val academicRepository = AcademicRepository(database.academicDao())

    // List of Khulna University Schools and Disciplines
    val kuSchoolsAndDisciplines = mapOf(
        "School of Science, Engineering and Technology" to listOf(
            "Computer Science and Engineering (CSE)",
            "Electronics and Communication Engineering (ECE)",
            "Mathematics (MATH)",
            "Statistics (STAT)",
            "Physics (PHYS)",
            "Chemistry (CHEM)",
            "Architecture (ARCH)",
            "Urban and Rural Planning (URP)"
        ),
        "School of Life Science" to listOf(
            "Biotechnology and Genetic Engineering (BGE)",
            "Pharmacy (PHARM)",
            "Environmental Science (ES)",
            "Fisheries and Marine Resource Technology (FMRT)",
            "Forestry and Wood Technology (FWT)",
            "Agrotechnology (AT)",
            "Soil, Water and Environment (SWE)"
        ),
        "School of Social Science" to listOf(
            "Economics (ECON)",
            "Sociology (SOC)",
            "Development Studies (DS)",
            "Mass Communication and Journalism (MCJ)"
        ),
        "School of Arts and Humanities" to listOf(
            "English (ENG)",
            "Bangla (BAN)",
            "History and Civilization (HC)"
        ),
        "School of Management and Business Administration" to listOf(
            "Business Administration (BA)",
            "Human Resource Management (HRM)"
        ),
        "School of Law" to listOf(
            "Law (LAW)"
        ),
        "School of Fine Arts" to listOf(
            "Drawing and Painting (DP)",
            "Printmaking (PM)",
            "Sculpture (SCULP)"
        )
    )

    // Current Active User Context session
    private val _userId = MutableStateFlow(-1)
    val userId: StateFlow<Int> = _userId.asStateFlow()

    fun setUserId(id: Int) {
        if (_userId.value != id) {
            _userId.value = id
        }
    }

    // Reactive Grade Scales
    val gradeScales: StateFlow<List<GradeScale>> = academicRepository.getGradeScalesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactive Semesters
    val semesters: StateFlow<List<Semester>> = userId
        .flatMapLatest { id ->
            if (id != -1) academicRepository.getSemestersForUser(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved theory & lab predictions
    val savedCalculations: StateFlow<List<SavedCalculation>> = userId
        .flatMapLatest { id ->
            if (id != -1) academicRepository.getSavedCalculationsForUser(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User activity logs
    val userLogs: StateFlow<List<ActivityLog>> = userId
        .flatMapLatest { id ->
            if (id != -1) academicRepository.getActivityLogsForUser(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminLogs: StateFlow<List<ActivityLog>> = academicRepository.getAllActivityLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Initializer
    init {
        viewModelScope.launch {
            academicRepository.initDefaultGradeScalesIfEmpty()
        }
    }

    // ----------------------------------------------------
    // THEORY CALCULATOR STATES
    // ----------------------------------------------------
    val theoryCourseName = MutableStateFlow("")
    val theoryCt1 = MutableStateFlow("")
    val theoryCt2 = MutableStateFlow("")
    val theoryCt3 = MutableStateFlow("")
    val theoryCt4 = MutableStateFlow("")
    val theoryAttendance = MutableStateFlow("")
    
    // Prediction states
    private val _theoryPredictionResult = MutableStateFlow<GeminiPredictor.AiPredictionResult?>(null)
    val theoryPredictionResult: StateFlow<GeminiPredictor.AiPredictionResult?> = _theoryPredictionResult.asStateFlow()

    private val _isPredictionLoading = MutableStateFlow(false)
    val isPredictionLoading: StateFlow<Boolean> = _isPredictionLoading.asStateFlow()

    fun calculateAndPredictTheory(discipline: String, school: String, targetGrade: String) {
        val ct1Val = theoryCt1.value.toDoubleOrNull() ?: 0.0
        val ct2Val = theoryCt2.value.toDoubleOrNull() ?: 0.0
        val ct3Val = theoryCt3.value.toDoubleOrNull() ?: 0.0
        val ct4Val = theoryCt4.value.toDoubleOrNull() ?: 0.0
        val attVal = theoryAttendance.value.toDoubleOrNull() ?: 0.0

        // Rule: Best of CT1 vs CT2 + Best of CT3 vs CT4
        val bestA = maxOf(ct1Val, ct2Val)
        val bestB = maxOf(ct3Val, ct4Val)
        val carry = (bestA + bestB) / 2.0 + attVal

        viewModelScope.launch {
            _isPredictionLoading.value = true
            val currentScales = academicRepository.getGradeScalesList()
            
            // Get prediction via Gemini helper (handles automated offline fallbacks)
            val result = GeminiPredictor.getPrediction(
                discipline = discipline,
                school = school,
                carryMarks = carry,
                targetGradeStr = targetGrade,
                gradeScales = currentScales
            )
            _theoryPredictionResult.value = result
            _isPredictionLoading.value = false
        }
    }

    fun saveTheoryCalculation() {
        val uId = userId.value
        if (uId == -1) return
        val result = theoryPredictionResult.value ?: return
        
        val ct1Val = theoryCt1.value.toDoubleOrNull() ?: 0.0
        val ct2Val = theoryCt2.value.toDoubleOrNull() ?: 0.0
        val ct3Val = theoryCt3.value.toDoubleOrNull() ?: 0.0
        val ct4Val = theoryCt4.value.toDoubleOrNull() ?: 0.0
        val attVal = theoryAttendance.value.toDoubleOrNull() ?: 0.0
        val bestA = maxOf(ct1Val, ct2Val)
        val bestB = maxOf(ct3Val, ct4Val)
        val carry = (bestA + bestB) / 2.0 + attVal

        viewModelScope.launch {
            val scales = academicRepository.getGradeScalesList()
            val matchGrade = scales.find { it.grade == result.targetGrade }
            val point = matchGrade?.gradePoint ?: 0.0

            academicRepository.insertSavedCalculation(
                SavedCalculation(
                    userId = uId,
                    courseType = "THEORY",
                    courseName = theoryCourseName.value.ifBlank { "Theory Course" },
                    ct1 = ct1Val,
                    ct2 = ct2Val,
                    ct3 = ct3Val,
                    ct4 = ct4Val,
                    attendance = attVal,
                    finalExam = result.minFinalRequired,
                    targetGrade = result.targetGrade,
                    calculatedGrade = result.safeGrade, // Represents advice fallback
                    gradePoint = point
                )
            )
        }
    }

    // ----------------------------------------------------
    // LAB CALCULATOR STATES
    // ----------------------------------------------------
    val labCourseName = MutableStateFlow("")
    val labAttendance = MutableStateFlow("") // 10
    val labViva = MutableStateFlow("")       // 30
    val labFinal = MutableStateFlow("")       // 60

    private val _labCalcResult = MutableStateFlow<Pair<String, Double>?>(null) // Grade, Point
    val labCalcResult: StateFlow<Pair<String, Double>?> = _labCalcResult.asStateFlow()

    fun calculateLabResult() {
        val att = labAttendance.value.toDoubleOrNull() ?: 0.0
        val vivaVal = labViva.value.toDoubleOrNull() ?: 0.0
        val finVal = labFinal.value.toDoubleOrNull() ?: 0.0
        val total = att + vivaVal + finVal

        viewModelScope.launch {
            val result = academicRepository.calculateGradeForTotal(total)
            _labCalcResult.value = result
        }
    }

    fun saveLabCalculation() {
        val uId = userId.value
        if (uId == -1) return
        val res = labCalcResult.value ?: return

        val att = labAttendance.value.toDoubleOrNull() ?: 0.0
        val vivaVal = labViva.value.toDoubleOrNull() ?: 0.0
        val finVal = labFinal.value.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            academicRepository.insertSavedCalculation(
                SavedCalculation(
                    userId = uId,
                    courseType = "LAB",
                    courseName = labCourseName.value.ifBlank { "Lab Course" },
                    attendance = att,
                    viva = vivaVal,
                    finalExam = finVal,
                    calculatedGrade = res.first,
                    gradePoint = res.second
                )
            )
        }
    }

    // Delete calc history
    fun deleteSavedCalc(calcId: Int) {
        val uId = userId.value
        if (uId == -1) return
        viewModelScope.launch {
            academicRepository.deleteSavedCalculation(uId, calcId)
        }
    }

    // ----------------------------------------------------
    // SEMESTER & DYNAMIC GPA MANAGEMENT
    // ----------------------------------------------------
    val newSemesterName = MutableStateFlow("")
    val newCourseName = MutableStateFlow("")
    val newCourseCredit = MutableStateFlow("")
    val newCourseGrade = MutableStateFlow("A+")

    fun addSemester() {
        val uId = userId.value
        if (uId == -1) return
        val name = newSemesterName.value.trim()
        if (name.isEmpty()) return

        viewModelScope.launch {
            academicRepository.insertSemester(
                Semester(userId = uId, name = name)
            )
            newSemesterName.value = ""
        }
    }

    fun deleteSemester(semester: Semester) {
        viewModelScope.launch {
            academicRepository.deleteSemester(semester)
        }
    }

    fun addCourseToSemester(semesterId: Int) {
        val uId = userId.value
        if (uId == -1) return
        val cName = newCourseName.value.trim().ifBlank { "Course" }
        val credit = newCourseCredit.value.toDoubleOrNull() ?: 3.0
        val grade = newCourseGrade.value

        viewModelScope.launch {
            val scales = academicRepository.getGradeScalesList()
            val match = scales.find { it.grade.equals(grade, ignoreCase = true) }
            val point = match?.gradePoint ?: 4.00

            academicRepository.insertCourse(
                Course(
                    semesterId = semesterId,
                    userId = uId,
                    name = cName,
                    creditHours = credit,
                    grade = grade,
                    gradePoint = point
                )
            )

            // Reset course inputs
            newCourseName.value = ""
            newCourseCredit.value = ""
            newCourseGrade.value = "A+"
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            academicRepository.deleteCourse(course)
        }
    }

    // ----------------------------------------------------
    // FUTURE PLANNER SIMULATOR
    // ----------------------------------------------------
    val simulatedNextSemesterGpa = MutableStateFlow("3.80")
    val simulatedNextSemesterCredits = MutableStateFlow("15")

    private val _simulatedCgpaPrediction = MutableStateFlow<Double?>(null)
    val simulatedCgpaPrediction: StateFlow<Double?> = _simulatedCgpaPrediction.asStateFlow()

    fun simulateFutureCgpa() {
        val futGpa = simulatedNextSemesterGpa.value.toDoubleOrNull() ?: 3.8
        val futCredits = simulatedNextSemesterCredits.value.toDoubleOrNull() ?: 15.0

        val currentSemesters = semesters.value
        var currentTotalPoints = 0.0
        var currentTotalCredits = 0.0

        currentSemesters.forEach {
            currentTotalPoints += (it.gpa * it.totalCredits)
            currentTotalCredits += it.totalCredits
        }

        val totalPointsSim = currentTotalPoints + (futGpa * futCredits)
        val totalCreditsSim = currentTotalCredits + futCredits

        _simulatedCgpaPrediction.value = if (totalCreditsSim > 0) totalPointsSim / totalCreditsSim else 0.0
    }

    // ----------------------------------------------------
    // DYNAMIC GRADE SCALE ADMIN OPERATIONS
    // ----------------------------------------------------
    fun updateGradeThreshold(scale: GradeScale, newMin: Double, newMax: Double, newPoint: Double) {
        viewModelScope.launch {
            academicRepository.updateGradeScale(
                scale.copy(
                    minMark = newMin,
                    maxMark = newMax,
                    gradePoint = newPoint
                )
            )
        }
    }

    fun resetGradeScales() {
        viewModelScope.launch {
            academicRepository.restoreDefaultGradeScales()
        }
    }

    fun getCoursesForSemester(semesterId: Int): Flow<List<Course>> {
        return academicRepository.getCoursesForSemester(semesterId)
    }
}
