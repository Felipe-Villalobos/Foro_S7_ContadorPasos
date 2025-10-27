package com.nrc3319.foro_s7_contadorpasos

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.nrc3319.foro_s7_contadorpasos.ui.theme.Foro_S7_ContadorPasosTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity(), SensorEventListener {
    private val stepsState = mutableStateOf(0)
    private val goalSteps = 100000
    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null
    private lateinit var stepDataManager: StepDataManager
    private var totalStepsFromSensor = 0
    private var lastSavedSteps = 0
    private val showHistory = mutableStateOf(false)


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity_Permission", "Permiso ACTIVITY_RECOGNITION concedido.")
                setupStepCounter()

            } else {
                Log.d("MainActivity_Permission", "Permiso ACTIVITY_RECOGNITION denegado.")
                stepsState.value = -1
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stepDataManager = StepDataManager(this)
        
        if (stepDataManager.isNewDay()) {
            stepDataManager.resetForNewDay()
            lastSavedSteps = 0
        } else {
            lastSavedSteps = stepDataManager.getTodaySteps()
        }
        
        stepsState.value = lastSavedSteps
        checkAndRequestPermission()

        setContent {
            Foro_S7_ContadorPasosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showHistory.value) {
                        HistoryScreen(
                            stepDataManager = stepDataManager,
                            onBackClick = { showHistory.value = false }
                        )
                    } else {
                        PantallaPodometro(
                            steps = stepsState.value, 
                            onHistoryClick = { showHistory.value = true }
                        )
                    }
                }
            }
        }
    }


    private fun checkAndRequestPermission() {
        val permission = android.Manifest.permission.ACTIVITY_RECOGNITION

        when {
            // Caso 1: El permiso YA ESTÁ concedido.
            // La pantalla se quedará en "0", que es lo correcto para empezar a contar.
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("MainActivity_Permission", "El permiso ya estaba concedido.")
                // Aquí, iniciaremos el contador de pasos.
                setupStepCounter()

            }

            shouldShowRequestPermissionRationale(permission) -> {
                Log.d("MainActivity_Permission", "Mostrando justificación para el permiso.")
                stepsState.value = -2
                requestPermissionLauncher.launch(permission)
            }

            // Caso 3: Pedir el permiso por primera vez.
            else -> {
                Log.d("MainActivity_Permission", "Lanzando solicitud de permiso por primera vez.")
                requestPermissionLauncher.launch(permission)
            }
        }
    }


    private fun setupStepCounter() {
        Log.d("MainActivity_Sensor", "Configurando el contador de pasos...")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounterSensor == null) {
            Log.e("MainActivity_Sensor", "Este dispositivo no tiene sensor de contador de pasos.")
            stepsState.value = -3
        } else {
            Log.d("MainActivity_Sensor", "Sensor encontrado, registrando listener...")
            val success = sensorManager?.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
            if (success == true) {
                Log.d("MainActivity_Sensor", "Listener registrado exitosamente")
            } else {
                Log.e("MainActivity_Sensor", "Error al registrar listener")
                stepsState.value = -4
            }
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                totalStepsFromSensor = it.values[0].toInt()
                val todaySteps = totalStepsFromSensor - lastSavedSteps
                Log.d("MainActivity_Sensor", "Sensor ha cambiado. Pasos totales: $totalStepsFromSensor, Hoy: $todaySteps")
                
                if (todaySteps > 0) {
                    stepsState.value = todaySteps
                    stepDataManager.saveTodaySteps(todaySteps)
                }
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        Log.d("MainActivity_Sensor", "La precisión del sensor ha cambiado a: $accuracy")
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPodometro(steps: Int, onHistoryClick: () -> Unit = {}) {
    val progress = if (steps >= 0) (steps.toFloat() / 100000f).coerceAtMost(1f) else 0f
    val calories = (steps * 0.04).toInt()
    val distance = (steps * 0.0008).toFixed(1)
    val time = (steps * 0.5).toInt()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Default.Settings, contentDescription = "History", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Today",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (steps) {
                -1 -> Text("Permiso Requerido", color = Color.Red, fontSize = 18.sp)
                -2 -> Text("Permiso es necesario para contar pasos", color = Color.Red, fontSize = 18.sp)
                -3 -> Text("No tiene sensor", color = Color.Red, fontSize = 18.sp)
                -4 -> Text("Error al conectar sensor", color = Color.Red, fontSize = 18.sp)
                else -> {
                    Box(
                        modifier = Modifier.size(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = progress,
                            modifier = Modifier.size(280.dp),
                            strokeWidth = 12.dp,
                            color = Color(0xFF4FC3F7),
                            trackColor = Color(0xFF424242)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = steps.toString(),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "GOAL 100,000",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricCard(
                    icon = "🔥",
                    value = "${calories} KCAL",
                    color = Color(0xFF4FC3F7)
                )
                MetricCard(
                    icon = "📏",
                    value = "${distance} MI",
                    color = Color(0xFF4FC3F7)
                )
                MetricCard(
                    icon = "⏱️",
                    value = "${time} MIN",
                    color = Color(0xFF4FC3F7)
                )
            }
        }
    }
}

@Composable
fun MetricCard(icon: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

fun Double.toFixed(digits: Int) = "%.${digits}f".format(this)


    