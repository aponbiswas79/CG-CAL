package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.User
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val userRepository = UserRepository(database.userDao(), database.academicDao(), application)

    val activeUser: StateFlow<User?> = userRepository.activeUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<User>> = userRepository.allUsersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Loading states
    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authSuccessMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage: StateFlow<String?> = _authSuccessMessage.asStateFlow()

    // Form inputs for login/registration
    val loginUsernameOrEmail = MutableStateFlow("")
    val loginPassword = MutableStateFlow("")
    val isRememberMe = MutableStateFlow(true)

    val registerFullName = MutableStateFlow("")
    val registerEmail = MutableStateFlow("")
    val registerPhone = MutableStateFlow("")
    val registerUsername = MutableStateFlow("")
    val registerPassword = MutableStateFlow("")
    val registerConfirmPassword = MutableStateFlow("")
    
    // Default academic choices for profile
    val profileSchool = MutableStateFlow("School of Science, Engineering and Technology")
    val profileDiscipline = MutableStateFlow("CSE")
    val profileBatch = MutableStateFlow("24")

    private val _exportedSpreadsheetUri = MutableStateFlow<String?>(null)
    val exportedSpreadsheetUri: StateFlow<String?> = _exportedSpreadsheetUri.asStateFlow()

    fun exportDatabaseAsExcel() {
        viewModelScope.launch {
            try {
                val file = userRepository.getExportedFile()
                _exportedSpreadsheetUri.value = file.absolutePath
            } catch (e: Exception) {
                _authError.value = "Failed to export data: ${e.message}"
            }
        }
    }

    fun clearMessages() {
        _authError.value = null
        _authSuccessMessage.value = null
    }

    fun login() {
        val input = loginUsernameOrEmail.value.trim()
        val pass = loginPassword.value

        if (input.isEmpty() || pass.isEmpty()) {
            _authError.value = "Username and password cannot be empty!"
            return
        }

        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            
            val result = userRepository.loginUser(input, pass)
            result.onSuccess { user ->
                _authSuccessMessage.value = "Welcome back, ${user.fullName}! 👋"
                // reset input fields after login
                loginUsernameOrEmail.value = ""
                loginPassword.value = ""
            }.onFailure { exception ->
                _authError.value = exception.message ?: "Authentication failed."
            }
            _isAuthLoading.value = false
        }
    }

    fun register() {
        val fullName = registerFullName.value.trim()
        val email = registerEmail.value.trim()
        val phone = registerPhone.value.trim()
        val username = registerUsername.value.trim()
        val password = registerPassword.value
        val confirmPassword = registerConfirmPassword.value
        val school = profileSchool.value
        val discipline = profileDiscipline.value
        val batch = profileBatch.value

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            _authError.value = "All registration fields are required!"
            return
        }

        if (password != confirmPassword) {
            _authError.value = "Passwords do not match!"
            return
        }

        if (password.length < 6) {
            _authError.value = "Password must be at least 6 characters!"
            return
        }

        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null

            val result = userRepository.registerUser(
                fullName = fullName,
                email = email,
                phone = phone,
                username = username,
                passwordRaw = password,
                school = school,
                discipline = discipline,
                batch = batch
            )

            result.onSuccess { user ->
                _authSuccessMessage.value = "Account created & logged in! Welcome to CG-CAL!"
                // clear registration inputs
                registerFullName.value = ""
                registerEmail.value = ""
                registerPhone.value = ""
                registerUsername.value = ""
                registerPassword.value = ""
                registerConfirmPassword.value = ""
            }.onFailure { exception ->
                _authError.value = exception.message ?: "Registration failed."
            }
            _isAuthLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logoutUser()
            _authSuccessMessage.value = "You have been logged out successfully."
        }
    }

    fun updateProfile(fullName: String, email: String, phone: String, school: String, discipline: String, batch: String) {
        val current = activeUser.value ?: return
        val updated = current.copy(
            fullName = fullName.trim(),
            email = email.trim(),
            phone = phone.trim(),
            school = school,
            discipline = discipline,
            batch = batch
        )

        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            
            val result = userRepository.updateProfile(updated)
            result.onSuccess {
                _authSuccessMessage.value = "Profile updated and synchronized successfully!"
            }.onFailure { exception ->
                _authError.value = exception.message ?: "Could not update profile"
            }
            
            _isAuthLoading.value = false
        }
    }

    suspend fun getExportedFile(): File {
        return userRepository.getExportedFile()
    }
}
