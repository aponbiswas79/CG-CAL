package com.example.data.repository

import android.content.Context
import com.example.data.dao.UserDao
import com.example.data.dao.AcademicDao
import com.example.data.model.User
import com.example.data.model.ActivityLog
import com.example.util.HashUtils
import com.example.util.ExcelExportHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class UserRepository(
    private val userDao: UserDao,
    private val academicDao: AcademicDao,
    private val context: Context
) {
    val activeUser: Flow<User?> = userDao.getActiveUser()
    val allUsersFlow: Flow<List<User>> = userDao.getAllUsersFlow()

    suspend fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        username: String,
        passwordRaw: String,
        school: String,
        discipline: String,
        batch: String
    ): Result<User> = withContext(Dispatchers.IO) {
        // Validate uniqueness of username and email
        val existingUserByName = userDao.getUserByUsername(username)
        if (existingUserByName != null) {
            return@withContext Result.failure(Exception("Username already exists"))
        }

        val existingUserByEmail = userDao.getUserByEmail(email)
        if (existingUserByEmail != null) {
            return@withContext Result.failure(Exception("Email already exists"))
        }

        val passwordHash = HashUtils.sha256(passwordRaw)
        val newUser = User(
            fullName = fullName,
            email = email,
            phone = phone,
            username = username,
            passwordHash = passwordHash,
            school = school,
            discipline = discipline,
            batch = batch,
            isLoggedIn = true, // Log them in immediately after register
            registrationDate = System.currentTimeMillis(),
            lastLoginDate = System.currentTimeMillis()
        )

        try {
            userDao.logoutAllUsers() // First clear any other active sessions
            val id = userDao.insertUser(newUser)
            val createdUser = newUser.copy(id = id.toInt())
            
            // Log security history
            academicDao.insertActivityLog(
                ActivityLog(
                    userId = createdUser.id,
                    activityType = "REGISTER",
                    description = "Account successfully registered for user ${createdUser.username}"
                )
            )
            // Log automatic initial login
            academicDao.insertActivityLog(
                ActivityLog(
                    userId = createdUser.id,
                    activityType = "LOGIN",
                    description = "Automatic login session started for user ${createdUser.username}"
                )
            )

            // Dynamic Excel-compatible sync
            syncUserExcelInternal()

            Result.success(createdUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(usernameOrEmail: String, passwordRaw: String): Result<User> = withContext(Dispatchers.IO) {
        val user = if (usernameOrEmail.contains("@")) {
            userDao.getUserByEmail(usernameOrEmail)
        } else {
            userDao.getUserByUsername(usernameOrEmail)
        }

        if (user == null) {
            return@withContext Result.failure(Exception("Username or Email not found"))
        }

        val hash = HashUtils.sha256(passwordRaw)
        if (user.passwordHash != hash) {
            return@withContext Result.failure(Exception("Invalid password. Please try again!"))
        }

        try {
            userDao.logoutAllUsers()
            val updatedUser = user.copy(
                isLoggedIn = true,
                lastLoginDate = System.currentTimeMillis()
            )
            userDao.updateUser(updatedUser)

            // Log activity
            academicDao.insertActivityLog(
                ActivityLog(
                    userId = updatedUser.id,
                    activityType = "LOGIN",
                    description = "User logged in successfully"
                )
            )

            syncUserExcelInternal()

            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logoutUser(): Unit = withContext(Dispatchers.IO) {
        val active = userDao.getActiveUserOneShot()
        if (active != null) {
            academicDao.insertActivityLog(
                ActivityLog(
                    userId = active.id,
                    activityType = "LOGOUT",
                    description = "User session ended (logged out)"
                )
            )
            val loggedOut = active.copy(isLoggedIn = false)
            userDao.updateUser(loggedOut)
        } else {
            userDao.logoutAllUsers()
        }
    }

    suspend fun updateProfile(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            userDao.updateUser(user)
            
            academicDao.insertActivityLog(
                ActivityLog(
                    userId = user.id,
                    activityType = "PROFILE_UPDATE",
                    description = "Profile and academic school/discipline settings updated"
                )
            )

            syncUserExcelInternal()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchUsers(query: String): Flow<List<User>> = withContext(Dispatchers.IO) {
        userDao.searchUsers(query)
    }

    suspend fun getExportedFile(): File = withContext(Dispatchers.IO) {
        val list = userDao.getAllUsers()
        ExcelExportHelper.syncUserDatabaseToExcel(context, list)
    }

    private suspend fun syncUserExcelInternal() {
        val list = userDao.getAllUsers()
        ExcelExportHelper.syncUserDatabaseToExcel(context, list)
    }
}
