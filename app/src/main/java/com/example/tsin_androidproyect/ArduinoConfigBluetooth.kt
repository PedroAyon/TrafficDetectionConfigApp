package com.example.tsin_androidproyect

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class ArduinoConfigBluetooth : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val device: BluetoothDevice? = intent.getParcelableExtra("bluetooth_device")

        setContent {
            TSIN_ProyectAndroidTheme {
                ArduinoConfigBluetoothScreen(
                    bluetoothDevice = device,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun ArduinoConfigBluetoothScreen(
    bluetoothDevice: BluetoothDevice?,
    onBackPressed: () -> Unit
) {
    val name = bluetoothDevice?.name ?: "Desconocido"
    val address = bluetoothDevice?.address ?: "n/a"
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val scope = rememberCoroutineScope()
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Configuración de Arduino Bluetooth") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Dispositivo: $name", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "MAC: $address",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            BluetoothForm { ssid, password ->
                bluetoothDevice?.let { device ->
                    scope.launch {
                        statusMessage = "Enviando configuración..."
                        val result = sendConfigToDevice(device, ssid, password)
                        statusMessage = if (result) "¡Configuración enviada!" else "Error al enviar."
                    }
                }
            }

            statusMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = msg, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun BluetoothForm(onSaveClick: (ssid: String, password: String) -> Unit) {
    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = ssid,
            onValueChange = { ssid = it },
            label = { Text("Nombre de la red (SSID)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onSaveClick(ssid, password) }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar")
        }
    }
}

// Helper to send data over RFCOMM
@SuppressLint("MissingPermission")
suspend fun sendConfigToDevice(
    device: BluetoothDevice,
    ssid: String,
    password: String
): Boolean {
    return withContext(Dispatchers.IO) {
        val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var socket: BluetoothSocket? = null
        return@withContext try {
            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()
            val output = socket.outputStream
            val message = "${ssid.trim()},${password.trim()}\n"
            output.write(message.toByteArray())
            output.flush()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            try { socket?.close() } catch (_: IOException) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BluetoothFormPreview() {
    TSIN_ProyectAndroidTheme {
        ArduinoConfigBluetoothScreen(
            bluetoothDevice = null,
            onBackPressed = {}
        )
    }
}
