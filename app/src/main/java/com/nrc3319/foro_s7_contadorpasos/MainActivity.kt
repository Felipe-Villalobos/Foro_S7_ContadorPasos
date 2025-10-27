package com.nrc3319.foro_s7_contadorpasos

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nrc3319.foro_s7_contadorpasos.ui.theme.Foro_S7_ContadorPasosTheme // Tema autogenerado

class MainActivity : ComponentActivity() {    // 1. Declaramos el lanzador para la solicitud de permiso.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // El usuario aceptó el permiso.
                // Por ahora, solo mostraremos un mensaje en la consola (Logcat).
                Log.d("MainActivity_Permission", "Permiso ACTIVITY_RECOGNITION concedido.")
            } else {
                // El usuario rechazó el permiso.
                // También lo mostraremos en la consola por ahora.
                Log.d("MainActivity_Permission", "Permiso ACTIVITY_RECOGNITION denegado.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pedimos el permiso de actividad física
        requestPermissionLauncher.launch(android.Manifest.permission.ACTIVITY_RECOGNITION)

        setContent {
            Foro_S7_ContadorPasosTheme {
                // Un Surface es un contenedor básico con color de fondo del tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos a nuestra función Composable
                    PantallaPodometro()
                }
            }
        }
    }
}

// Esta es nuestra nueva pantalla, definida como una función
@Composable
fun PantallaPodometro() {
    Text(text = "¡Hola, Podómetro en Compose!")
}

// Esta es una vista previa para ver el diseño sin ejecutar la app
@Preview(showBackground = true)
@Composable
fun PantallaPodometroPreview() {
    Foro_S7_ContadorPasosTheme {
        PantallaPodometro()
    }
}
    