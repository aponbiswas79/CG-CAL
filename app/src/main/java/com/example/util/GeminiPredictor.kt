package com.example.util

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.GradeScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiPredictor {
    private const val TAG = "GeminiPredictor"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    data class AiPredictionResult(
        val targetGrade: String,
        val minFinalRequired: Double,
        val maxFinalPossible: Double,
        val probabilityIndicator: String, // "High", "Medium", "Low", "Impossible"
        val safeGrade: String,
        val riskLevel: String,
        val boosterAdvice: String
    )

    // Formulates a highly personalized academic counselling session
    suspend fun getPrediction(
        discipline: String,
        school: String,
        carryMarks: Double,
        targetGradeStr: String,
        gradeScales: List<GradeScale>
    ): AiPredictionResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // Calculate exact mathematical limits locally first
        val targetScale = gradeScales.find { it.grade.equals(targetGradeStr, ignoreCase = true) }
        val dTargetMin = targetScale?.minMark ?: 80.0
        val finalExamMinRequired = (dTargetMin - carryMarks).coerceAtLeast(0.0)
        
        // Final is out of 60 marks
        val isPossible = finalExamMinRequired <= 60.0
        val minFinal = if (isPossible) finalExamMinRequired else 60.00
        val maxFinal = 60.0
        
        val maxPotentialTotal = carryMarks + 60.0
        val bestScaleMatch = gradeScales
            .filter { maxPotentialTotal >= it.minMark }
            .maxByOrNull { it.gradePoint }
        val maxPossibleGrade = bestScaleMatch?.grade ?: "F"

        val prob = when {
            !isPossible -> "Impossible"
            finalExamMinRequired <= 25 -> "Very High 🔥"
            finalExamMinRequired <= 35 -> "High ✅"
            finalExamMinRequired <= 45 -> "Medium ⚠️"
            finalExamMinRequired <= 55 -> "Low (Extremely Challenging) ⚡"
            else -> "Borderline 🛡️"
        }

        val risk = when {
            finalExamMinRequired > 50 -> "Critical Risk 🚨"
            finalExamMinRequired > 40 -> "High Risk ⚠️"
            finalExamMinRequired > 30 -> "Moderate Risk 👍"
            else -> "Low Risk (Safe) 🟢"
        }

        val safeMatch = gradeScales
            .filter { (carryMarks + 30.0) >= it.minMark }
            .maxByOrNull { it.gradePoint }
        val safeGrade = safeMatch?.grade ?: "D"

        val localAdvice = if (!isPossible) {
            "Achieving a Grade $targetGradeStr is mathematically impossible from your current carry marks ($carryMarks/40). We advise adjusting your target to $maxPossibleGrade, and maximizing your final preparations!"
        } else {
            "To achieve Grade $targetGradeStr, you need at least ${"%.1f".format(minFinal)} out of 60 in the Final Semester Exam. Practice past term papers for Section A and Section B, maintain focused revision sheets!"
        }

        val fallbackResult = AiPredictionResult(
            targetGrade = targetGradeStr,
            minFinalRequired = minFinal,
            maxFinalPossible = maxFinal,
            probabilityIndicator = prob,
            safeGrade = safeGrade,
            riskLevel = risk,
            boosterAdvice = localAdvice
        )

        // If no credentials or offline, return local math-based analysis immediately
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.startsWith("placeholder", ignoreCase = true)) {
            Log.d(TAG, "No valid api key found, using local predictor engine.")
            return@withContext fallbackResult
        }

        try {
            // Construct a highly detailed system instruction and prompt for Gemini
            val scalesDesc = gradeScales.joinToString(", ") { "${it.grade}: ${it.minMark}-${it.maxMark} (GP: ${it.gradePoint})" }
            val prompt = """
                You are CG-CAL Academic AI, the official student counsellor for Khulna University (KU).
                Provide a friendly, highly specific motivational booster advice for a student in the '$discipline' discipline under '$school'.
                Student Status:
                - Carry Marks (Internal Theory): $carryMarks out of 40 (based on Section A and B Best CTs + Class Attendance)
                - Target Final Course Grade: $targetGradeStr
                - Grade Scale Rules: $scalesDesc
                
                Identify:
                - Minimum Final marks out of 60 required: ${"%.1f".format(minFinal)}
                - Max possible grade potential: $maxPossibleGrade
                - Safe Grade: $safeGrade
                - Risk Level: $risk
                
                Write exactly a 1-sentence boosterAdvice that matches their risk category, specifically referencing things useful for KU '$discipline' students (like lab work, math problems, research, theory or design, depending on the course). Keep it concise, professional, encouraging, and clear.
                
                Respond ONLY with a valid JSON in this exact structure:
                {
                   "boosterAdvice": "Your custom motivational sentence here."
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", org.json.JSONArray().put(
                    JSONObject().put("parts", org.json.JSONArray().put(
                        JSONObject().put("text", prompt)
                    ))
                ))
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val jsonResponse = JSONObject(bodyString)
                    val candidates = jsonResponse.getJSONArray("candidates")
                    val textContent = candidates.getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                    // Clean clean json blocks from response text
                    val cleanedJson = textContent.trim()
                        .removePrefix("```json")
                        .removePrefix("```")
                        .removeSuffix("```")
                        .trim()

                    val advisorJson = JSONObject(cleanedJson)
                    val aiAdvice = advisorJson.getString("boosterAdvice")
                    
                    return@withContext fallbackResult.copy(boosterAdvice = aiAdvice)
                } else {
                    Log.e(TAG, "Gemini API error code: ${response.code}, message: ${response.message}")
                    return@withContext fallbackResult
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API exception: ${e.message}", e)
            return@withContext fallbackResult
        }
    }
}
