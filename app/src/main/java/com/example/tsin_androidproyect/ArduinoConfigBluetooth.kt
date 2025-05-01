package com.example.tsin_androidproyect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tsin_androidproyect.ui.theme.TSIN_ProyectAndroidTheme

class ArduinoConfigBluetooth : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TSIN_ProyectAndroidTheme {
                ArduinoConfigBluetoothScreen(onBackPressed = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArduinoConfigBluetoothScreen(onBackPressed: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Configuración de Arduino Bluetooth"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        BluetoothForm(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun BluetoothForm(modifier: Modifier = Modifier) {
    var fieldOne by remember { mutableStateOf("") }
    var fieldTwo by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = fieldOne,
            onValueChange = { fieldOne = it },
            label = { Text("Nombre de la red") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = fieldTwo,
            onValueChange = { fieldTwo = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {  }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BluetoothFormPreview() {
    TSIN_ProyectAndroidTheme {
        ArduinoConfigBluetoothScreen(onBackPressed = {})
    }
}
