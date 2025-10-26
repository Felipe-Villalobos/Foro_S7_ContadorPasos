package com.nrc3319.foro_s7_contadorpasos

import android.os.Bundle
import androidx.activity.ComponentActivity // Se cambia de AppCompatActivity a ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nrc3319.foro_s7_contadorpasos.ui.theme.Foro_S7_ContadorPasosTheme // Tema autogenerado

class MainActivity : ComponentActivity() { // <- ¡Fíjate en el cambio aquí!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ya no usamos setContentView(R.layout.activity_main)
        // Usamos setContent para construir la UI con Compose
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
    