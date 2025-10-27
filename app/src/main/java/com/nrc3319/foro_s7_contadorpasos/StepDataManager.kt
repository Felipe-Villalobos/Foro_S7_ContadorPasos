package com.nrc3319.foro_s7_contadorpasos

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

data class DayStepData(
    val date: String,
    val steps: Int,
    val calories: Int,
    val distance: Double,
    val time: Int
)

class StepDataManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("step_data", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun saveTodaySteps(steps: Int) {
        val today = getCurrentDate()
        val calories = (steps * 0.04).toInt()
        val distance = steps * 0.0008
        val time = (steps * 0.5).toInt()
        
        prefs.edit().apply {
            putString("${today}_steps", steps.toString())
            putString("${today}_calories", calories.toString())
            putString("${today}_distance", distance.toString())
            putString("${today}_time", time.toString())
            putString("last_update", today)
            apply()
        }
    }
    
    fun getTodaySteps(): Int {
        val today = getCurrentDate()
        return prefs.getString("${today}_steps", "0")?.toIntOrNull() ?: 0
    }
    
    fun getTodayData(): DayStepData {
        val today = getCurrentDate()
        val steps = prefs.getString("${today}_steps", "0")?.toIntOrNull() ?: 0
        val calories = prefs.getString("${today}_calories", "0")?.toIntOrNull() ?: 0
        val distance = prefs.getString("${today}_distance", "0.0")?.toDoubleOrNull() ?: 0.0
        val time = prefs.getString("${today}_time", "0")?.toIntOrNull() ?: 0
        
        return DayStepData(today, steps, calories, distance, time)
    }
    
    fun getLast7Days(): List<DayStepData> {
        val data = mutableListOf<DayStepData>()
        val calendar = Calendar.getInstance()
        
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = dateFormat.format(calendar.time)
            
            val steps = prefs.getString("${date}_steps", "0")?.toIntOrNull() ?: 0
            val calories = prefs.getString("${date}_calories", "0")?.toIntOrNull() ?: 0
            val distance = prefs.getString("${date}_distance", "0.0")?.toDoubleOrNull() ?: 0.0
            val time = prefs.getString("${date}_time", "0")?.toIntOrNull() ?: 0
            
            data.add(DayStepData(date, steps, calories, distance, time))
        }
        
        return data
    }
    
    fun getLast30Days(): List<DayStepData> {
        val data = mutableListOf<DayStepData>()
        val calendar = Calendar.getInstance()
        
        for (i in 29 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = dateFormat.format(calendar.time)
            
            val steps = prefs.getString("${date}_steps", "0")?.toIntOrNull() ?: 0
            val calories = prefs.getString("${date}_calories", "0")?.toIntOrNull() ?: 0
            val distance = prefs.getString("${date}_distance", "0.0")?.toDoubleOrNull() ?: 0.0
            val time = prefs.getString("${date}_time", "0")?.toIntOrNull() ?: 0
            
            data.add(DayStepData(date, steps, calories, distance, time))
        }
        
        return data
    }
    
    fun getTotalSteps(): Int {
        val last7Days = getLast7Days()
        return last7Days.sumOf { it.steps }
    }
    
    fun getAverageSteps(): Int {
        val last7Days = getLast7Days()
        return if (last7Days.isNotEmpty()) last7Days.sumOf { it.steps } / last7Days.size else 0
    }
    
    private fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }
    
    fun isNewDay(): Boolean {
        val lastUpdate = prefs.getString("last_update", "")
        val today = getCurrentDate()
        return lastUpdate != today
    }
    
    fun resetForNewDay() {
        val today = getCurrentDate()
        prefs.edit().putString("last_update", today).apply()
    }
}
