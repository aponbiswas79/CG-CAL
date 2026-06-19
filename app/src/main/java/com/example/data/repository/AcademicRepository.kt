package com.example.data.repository

import com.example.data.dao.AcademicDao
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AcademicRepository(private val academicDao: AcademicDao) {

    // Semesters
    fun getSemestersForUser(userId: Int): Flow<List<Semester>> = academicDao.getSemestersForUser(userId)
    
    suspend fun insertSemester(semester: Semester): Long = withContext(Dispatchers.IO) {
        val id = academicDao.insertSemester(semester)
        academicDao.insertActivityLog(
            ActivityLog(
                userId = semester.userId,
                activityType = "SEMESTER_ADD",
                description = "Added new semester: ${semester.name}"
            )
        )
        id
    }

    suspend fun updateSemester(semester: Semester) = withContext(Dispatchers.IO) {
        academicDao.updateSemester(semester)
    }

    suspend fun deleteSemester(semester: Semester) = withContext(Dispatchers.IO) {
        academicDao.deleteCoursesForSemester(semester.id)
        academicDao.deleteSemester(semester)
        academicDao.insertActivityLog(
            ActivityLog(
                userId = semester.userId,
                activityType = "SEMESTER_DELETE",
                description = "Deleted semester ${semester.name} and all its course evaluations"
            )
        )
    }

    // Courses
    fun getCoursesForSemester(semesterId: Int): Flow<List<Course>> = academicDao.getCoursesForSemester(semesterId)
    fun getCoursesForUser(userId: Int): Flow<List<Course>> = academicDao.getCoursesForUser(userId)

    suspend fun insertCourse(course: Course): Long = withContext(Dispatchers.IO) {
        val id = academicDao.insertCourse(course)
        recalculateSemesterGpa(course.semesterId, course.userId)
        id
    }

    suspend fun updateCourse(course: Course) = withContext(Dispatchers.IO) {
        academicDao.updateCourse(course)
        recalculateSemesterGpa(course.semesterId, course.userId)
    }

    suspend fun deleteCourse(course: Course) = withContext(Dispatchers.IO) {
        academicDao.deleteCourse(course)
        recalculateSemesterGpa(course.semesterId, course.userId)
    }

    // Recalculates semester GPA and credit totals reactively when courses change
    private suspend fun recalculateSemesterGpa(semesterId: Int, userId: Int) {
        val courses = academicDao.getCoursesForSemesterOneShot(semesterId)
        val semestersList = academicDao.getSemestersForUserOneShot(userId)
        val targetSemester = semestersList.find { it.id == semesterId } ?: return

        var sumCreditPoints = 0.0
        var totalCredits = 0.0

        courses.forEach { course ->
            sumCreditPoints += (course.creditHours * course.gradePoint)
            totalCredits += course.creditHours
        }

        val gpa = if (totalCredits > 0) sumCreditPoints / totalCredits else 0.0
        academicDao.updateSemester(
            targetSemester.copy(
                gpa = gpa,
                totalCredits = totalCredits
            )
        )
    }

    // Saved Calculations (Theory/Lab Evaluations)
    fun getSavedCalculationsForUser(userId: Int): Flow<List<SavedCalculation>> = academicDao.getSavedCalculationsForUser(userId)

    suspend fun insertSavedCalculation(calc: SavedCalculation): Long = withContext(Dispatchers.IO) {
        val id = academicDao.insertSavedCalculation(calc)
        academicDao.insertActivityLog(
            ActivityLog(
                userId = calc.userId,
                activityType = "CALC_SAVE",
                description = "Saved academic calculation for course: ${calc.courseName} (${calc.courseType})"
            )
        )
        id
    }

    suspend fun deleteSavedCalculation(userId: Int, calcId: Int) = withContext(Dispatchers.IO) {
        academicDao.deleteSavedCalculationById(calcId)
        academicDao.insertActivityLog(
            ActivityLog(
                userId = userId,
                activityType = "CALC_DELETE",
                description = "Removed historical calculation ID $calcId"
            )
        )
    }

    // Activity Logs
    fun getActivityLogsForUser(userId: Int): Flow<List<ActivityLog>> = academicDao.getActivityLogsForUser(userId)
    fun getAllActivityLogs(): Flow<List<ActivityLog>> = academicDao.getAllActivityLogs()

    suspend fun insertActivityLog(log: ActivityLog) = withContext(Dispatchers.IO) {
        academicDao.insertActivityLog(log)
    }

    // Grade Scale System (with Dynamic customizable limits)
    fun getGradeScalesFlow(): Flow<List<GradeScale>> = academicDao.getGradeScalesFlow()
    
    suspend fun getGradeScalesList(): List<GradeScale> = withContext(Dispatchers.IO) {
        academicDao.getGradeScales()
    }

    suspend fun updateGradeScale(scale: GradeScale) = withContext(Dispatchers.IO) {
        academicDao.updateGradeScale(scale)
    }

    suspend fun restoreDefaultGradeScales() = withContext(Dispatchers.IO) {
        academicDao.clearGradeScales()
        val defaults = getDefKUStyleScales()
        defaults.forEach { academicDao.insertGradeScale(it) }
    }

    suspend fun initDefaultGradeScalesIfEmpty() = withContext(Dispatchers.IO) {
        val existing = academicDao.getGradeScales()
        if (existing.isEmpty()) {
            val defaults = getDefKUStyleScales()
            defaults.forEach { academicDao.insertGradeScale(it) }
        }
    }

    private fun getDefKUStyleScales(): List<GradeScale> {
        return listOf(
            GradeScale(grade = "A+", minMark = 80.0, maxMark = 100.0, gradePoint = 4.00),
            GradeScale(grade = "A", minMark = 75.0, maxMark = 79.99, gradePoint = 3.75),
            GradeScale(grade = "A-", minMark = 70.0, maxMark = 74.99, gradePoint = 3.50),
            GradeScale(grade = "B+", minMark = 65.0, maxMark = 69.99, gradePoint = 3.25),
            GradeScale(grade = "B", minMark = 60.0, maxMark = 64.99, gradePoint = 3.00),
            GradeScale(grade = "B-", minMark = 55.0, maxMark = 59.99, gradePoint = 2.75),
            GradeScale(grade = "C+", minMark = 50.0, maxMark = 54.99, gradePoint = 2.50),
            GradeScale(grade = "C", minMark = 45.0, maxMark = 49.99, gradePoint = 2.25),
            GradeScale(grade = "D", minMark = 40.0, maxMark = 44.99, gradePoint = 2.00),
            GradeScale(grade = "F", minMark = 0.0, maxMark = 39.99, gradePoint = 0.00)
        )
    }

    // Helper functions to work out marks according to current configured grade scales
    suspend fun calculateGradeForTotal(totalMarks: Double): Pair<String, Double> {
        val scales = academicDao.getGradeScales()
        val match = scales.find { totalMarks >= it.minMark && totalMarks <= it.maxMark }
        return if (match != null) {
            Pair(match.grade, match.gradePoint)
        } else if (totalMarks >= 80.0) {
            Pair("A+", 4.00) // fallback base default
        } else if (totalMarks < 40.0) {
            Pair("F", 0.00)
        } else {
            Pair("D", 2.0)
        }
    }
}
