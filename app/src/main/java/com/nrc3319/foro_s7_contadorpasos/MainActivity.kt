package com.nrc3319.foro_s7_contadorpasos

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity // Se cambia de AppCompatActivity a ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.nrc3319.foro_s7_contadorpasos.ui.theme.Foro_S7_ContadorPasosTheme // Tema autogenerado

class MainActivity : ComponentActivity() {
    // Variable de estado para guardar el texto que se mostrará en pantalla.
    // El valor inicial es "0".
    private val stepsState = mutableStateOf("0")

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // El usuario aceptó el permiso.Log.d("MainActivity_Permission", "Permiso ACTIVITY_RECOGNITION concedido.")
                // En el futuro, aquí llamaremos a la función que inicia el contador de pasos.
                // No necesitamos cambiar la UI, porque ya está en "0", lista para contar.

            } else {
                // ¡¡ESTA ES LA CORRECCIÓN!!
                // El usuario rechazó el permiso. Actualizamos el estado para que la UI reaccione.
                Log.d("MainActivity_Permission", "Permiso ACTIVITY_RECOGNITION denegado.")
                stepsState.value = "Permiso Requerido"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)        // En lugar de lanzar el permiso directamente, llamamos a nuestra función inteligente.
        checkAndRequestPermission()

        setContent {
            Foro_S7_ContadorPasosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaPodometro(displayText = stepsState.value)
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
                // Aquí, más tarde, iniciaremos el contador de pasos.
            }

            // Caso 2 (Opcional pero recomendado): Explicar por qué necesitas el permiso.
            // Esto ocurre si el usuario ya lo negó una vez.
            shouldShowRequestPermissionRationale(permission) -> {
                Log.d("MainActivity_Permission", "Mostrando justificación para el permiso.")
                // Mostramos un mensaje de error y luego pedimos el permiso de nuevo.
                stepsState.value = "Permiso es necesario para contar pasos."
                requestPermissionLauncher.launch(permission)
            }

            // Caso 3: Pedir el permiso por primera vez.
            else -> {
                Log.d("MainActivity_Permission", "Lanzando solicitud de permiso por primera vez.")
                requestPermissionLauncher.launch(permission)
            }
        }
    }

}

// Esta es nuestra nueva pantalla, definida como una función
@Composable
fun PantallaPodometro(displayText: String) {
    Text(text = displayText)
}


    