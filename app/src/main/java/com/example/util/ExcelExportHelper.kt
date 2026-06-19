package com.example.util

import android.content.Context
import android.os.Environment
import com.example.data.model.User
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExcelExportHelper {
    private const val FILE_NAME = "KU_CGCAL_Users_Database.csv"

    // Formats dates formatted like typical spreadsheet reports
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun syncUserDatabaseToExcel(context: Context, users: List<User>): File {
        // We write to the app's files directory or external files directory for durability
        val fileDir = context.getExternalFilesDir(null) ?: context.filesDir
        val file = File(fileDir, FILE_NAME)
        
        try {
            file.bufferedWriter().use { writer ->
                // Write Header matching exact requested Excel columns
                writer.write("User ID,Full Name,Email,Phone Number,Username,Registration Date,Last Login Date,Account Status,School,Discipline,Batch\n")
                
                // Write user lines
                users.forEach { user ->
                    val regDate = dateFormatter.format(Date(user.registrationDate))
                    val lastLoginStr = dateFormatter.format(Date(user.lastLoginDate))
                    
                    // Escape CSV values
                    val escapedId = user.id.toString()
                    val escapedName = escapeCsv(user.fullName)
                    val escapedEmail = escapeCsv(user.email)
                    val escapedPhone = escapeCsv(user.phone)
                    val escapedUsername = escapeCsv(user.username)
                    val escapedStatus = escapeCsv(user.accountStatus)
                    val escapedSchool = escapeCsv(user.school)
                    val escapedDiscipline = escapeCsv(user.discipline)
                    val escapedBatch = escapeCsv(user.batch)
                    
                    writer.write("$escapedId,$escapedName,$escapedEmail,$escapedPhone,$escapedUsername,$regDate,$lastLoginStr,$escapedStatus,$escapedSchool,$escapedDiscipline,$escapedBatch\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    private fun escapeCsv(value: String): String {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            val replaced = value.replace("\"", "\"\"")
            return "\"$replaced\""
        }
        return value
    }
}
