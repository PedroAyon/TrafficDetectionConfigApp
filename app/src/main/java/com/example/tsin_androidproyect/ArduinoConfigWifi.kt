package com.example.tsin_androidproyect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import com.example.tsin_androidproyect.models.CamRequest
import com.example.tsin_androidproyect.models.RefLine
import com.example.tsin_androidproyect.models.TrafficCam
import com.example.tsin_androidproyect.repository.CamRepository
import com.example.tsin_androidproyect.ui.theme.TSIN_ProyectAndroidTheme
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class ArduinoConfigWifi : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1) Extract TrafficCam JSON from intent
        val camJson = intent.getStringExtra("cam_json") ?: ""
        val cam = Gson().fromJson(camJson, TrafficCam::class.java)

        setContent {
            TSIN_ProyectAndroidTheme {
                ArduinoConfigWifiScreen(
                    cam = cam,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArduinoConfigWifiScreen(
    cam: TrafficCam,
    onBackPressed: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("Configuración de Arduino Wifi") },
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
        WifiForm(
            cam = cam,
            modifier = Modifier.padding(innerPadding),
            onSaved = { updatedRequest ->
                // call editCam and finish
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        CamRepository().editCam(cam.traffic_cam_id, updatedRequest)
                    } finally {
                        onBackPressed()
                    }
                }
            }
        )
    }
}

@Composable
fun WifiForm(
    cam: TrafficCam,
    modifier: Modifier = Modifier,
    onSaved: (CamRequest) -> Unit
) {
    // initialize state with cam values
    var alias by remember { mutableStateOf(cam.alias) }
    var latitud by remember { mutableStateOf(cam.location_lat.toString()) }
    var longitud by remember { mutableStateOf(cam.location_lng.toString()) }

    var inicioAx by remember { mutableStateOf(cam.start_ref_line.ax.toInt().toString()) }
    var inicioAy by remember { mutableStateOf(cam.start_ref_line.ay.toInt().toString()) }
    var inicioBx by remember { mutableStateOf(cam.start_ref_line.bx.toInt().toString()) }
    var inicioBy by remember { mutableStateOf(cam.start_ref_line.by.toInt().toString()) }

    var finalAx by remember { mutableStateOf(cam.finish_ref_line.ax.toInt().toString()) }
    var finalAy by remember { mutableStateOf(cam.finish_ref_line.ay.toInt().toString()) }
    var finalBx by remember { mutableStateOf(cam.finish_ref_line.bx.toInt().toString()) }
    var finalBy by remember { mutableStateOf(cam.finish_ref_line.by.toInt().toString()) }

    var distancia by remember { mutableStateOf(cam.ref_distance.toInt().toString()) }
    var selectedOption by remember { mutableStateOf(cam.track_orientation.lowercase(Locale.ROOT)) }
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Horizontal", "Vertical")

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = alias,
            onValueChange = { alias = it },
            label = { Text("Alias") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = latitud,
                onValueChange = { if (it.toDoubleOrNull() != null) latitud = it },
                label = { Text("Latitud") },
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = longitud,
                onValueChange = { if (it.toDoubleOrNull() != null) longitud = it },
                label = { Text("Longitud") },
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Box(modifier = Modifier.padding(vertical = 8.dp)) {
            Button(onClick = { expanded = true }) { Text(selectedOption) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = {
                        selectedOption = option
                        expanded = false
                    })
                }
            }
        }

        // --- Start/Finish lines input (abbreviated for brevity) ---
        // Punto A Inicio
        OutlinedTextField(
            value = inicioAx,
            onValueChange = { if (it.all(Char::isDigit)) inicioAx = it },
            label = { Text("Inicio Ax") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = inicioAy,
            onValueChange = { if (it.all(Char::isDigit)) inicioAy = it },
            label = { Text("Inicio Ay") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        // Punto B Inicio
        OutlinedTextField(
            value = inicioBx,
            onValueChange = { if (it.all(Char::isDigit)) inicioBx = it },
            label = { Text("Inicio Bx") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = inicioBy,
            onValueChange = { if (it.all(Char::isDigit)) inicioBy = it },
            label = { Text("Inicio By") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        // Punto A Final
        OutlinedTextField(
            value = finalAx,
            onValueChange = { if (it.all(Char::isDigit)) finalAx = it },
            label = { Text("Final Ax") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = finalAy,
            onValueChange = { if (it.all(Char::isDigit)) finalAy = it },
            label = { Text("Final Ay") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        // Punto B Final
        OutlinedTextField(
            value = finalBx,
            onValueChange = { if (it.all(Char::isDigit)) finalBx = it },
            label = { Text("Final Bx") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = finalBy,
            onValueChange = { if (it.all(Char::isDigit)) finalBy = it },
            label = { Text("Final By") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = distancia,
            onValueChange = { if (it.all(Char::isDigit)) distancia = it },
            label = { Text("Distancia (m)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // build CamRequest and invoke onSaved
                val updated = CamRequest(
                    traffic_cam_id = cam.traffic_cam_id,
                    alias = alias,
                    location_lat = latitud.toDouble(),
                    location_lng = longitud.toDouble(),
                    start_ref_line = RefLine(
                        ax = inicioAx.toFloat(),
                        ay = inicioAy.toFloat(),
                        bx = inicioBx.toFloat(),
                        by = inicioBy.toFloat()
                    ),
                    finish_ref_line = RefLine(
                        ax = finalAx.toFloat(),
                        ay = finalAy.toFloat(),
                        bx = finalBx.toFloat(),
                        by = finalBy.toFloat()
                    ),
                    ref_distance = distancia.toFloat(),
                    track_orientation = selectedOption.lowercase()
                )
                onSaved(updated)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}
