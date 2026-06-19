package com.example.data.dao

import androidx.room.*
import com.example.data.model.Semester
import com.example.data.model.Course
import com.example.data.model.ActivityLog
import com.example.data.model.SavedCalculation
import com.example.data.model.GradeScale
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicDao {
    // Semesters
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: Semester): Long

    @Update
    suspend fun updateSemester(semester: Semester)

    @Delete
    suspend fun deleteSemester(semester: Semester)

    @Query("SELECT * FROM semesters WHERE userId = :userId ORDER BY id ASC")
    fun getSemestersForUser(userId: Int): Flow<List<Semester>>

    @Query("SELECT * FROM semesters WHERE userId = :userId ORDER BY id ASC")
    suspend fun getSemestersForUserOneShot(userId: Int): List<Semester>

    // Courses
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("SELECT * FROM courses WHERE semesterId = :semesterId ORDER BY id ASC")
    fun getCoursesForSemester(semesterId: Int): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE semesterId = :semesterId ORDER BY id ASC")
    suspend fun getCoursesForSemesterOneShot(semesterId: Int): List<Course>

    @Query("SELECT * FROM courses WHERE userId = :userId")
    fun getCoursesForUser(userId: Int): Flow<List<Course>>

    @Query("DELETE FROM courses WHERE semesterId = :semesterId")
    suspend fun deleteCoursesForSemester(semesterId: Int)

    // Saved Calculations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedCalculation(calc: SavedCalculation): Long

    @Query("DELETE FROM saved_calculations WHERE id = :id")
    suspend fun deleteSavedCalculationById(id: Int)

    @Query("SELECT * FROM saved_calculations WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSavedCalculationsForUser(userId: Int): Flow<List<SavedCalculation>>

    // Activity Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLog): Long

    @Query("SELECT * FROM activity_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT 50")
    fun getActivityLogsForUser(userId: Int): Flow<List<ActivityLog>>

    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllActivityLogs(): Flow<List<ActivityLog>>

    // Grade Scales
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGradeScale(scale: GradeScale): Long

    @Query("DELETE FROM grade_scales")
    suspend fun clearGradeScales()

    @Query("SELECT * FROM grade_scales ORDER BY minMark DESC")
    fun getGradeScalesFlow(): Flow<List<GradeScale>>

    @Query("SELECT * FROM grade_scales ORDER BY minMark DESC")
    suspend fun getGradeScales(): List<GradeScale>

    @Update
    suspend fun updateGradeScale(scale: GradeScale)
}
