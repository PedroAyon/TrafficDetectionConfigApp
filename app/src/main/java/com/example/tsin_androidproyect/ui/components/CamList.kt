package com.example.tsin_androidproyect.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.tsin_androidproyect.ArduinoConfigWifi
import com.example.tsin_androidproyect.models.BluetoothTrafficCam
import com.example.tsin_androidproyect.models.RemoteTrafficCam
import com.example.tsin_androidproyect.models.WifiBluetoothToggleState
import com.example.tsin_androidproyect.repository.RemoteCamRepository
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CamList(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val remoteCamRepository = remember { RemoteCamRepository() }
    var remoteCams by remember { mutableStateOf<List<RemoteTrafficCam>?>(null) }
    var bluetoothCams by remember { mutableStateOf<List<BluetoothTrafficCam>?>(null) }
    var searchText by remember { mutableStateOf("") }
    var isRefreshing by remember { mutableStateOf(false) }
    var wifiBluetoothToggleState by remember { mutableStateOf(WifiBluetoothToggleState.WIFI) }
    val scope = rememberCoroutineScope()

    fun fetchWifiCams() {
        scope.launch {
            isRefreshing = true
            remoteCams = try {
                remoteCamRepository.fetchAllCams()
            } catch (e: Exception) {
                emptyList()
            }
            isRefreshing = false
        }
    }

    fun fetchBluetoothCams() {
        scope.launch {
            isRefreshing = true
            delay(1000)
            // TODO: detect available cams via bluetooth adapter
            bluetoothCams = listOf( // Placeholder for Bluetooth fetching logic
                 BluetoothTrafficCam("MyWifiAP", "password123", "ESP32-CAM-BT", "AA:BB:CC:DD:EE:FF"),
                 BluetoothTrafficCam(null, null, "BT_Device_1", "11:22:33:44:55:66")
            )
            isRefreshing = false
        }
    }

    LaunchedEffect(wifiBluetoothToggleState) {
        searchText = ""
        if (wifiBluetoothToggleState == WifiBluetoothToggleState.WIFI) {
            if (remoteCams == null) fetchWifiCams()
        } else {
            if (bluetoothCams == null) fetchBluetoothCams()
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            if (wifiBluetoothToggleState == WifiBluetoothToggleState.WIFI) {
                fetchWifiCams()
            } else {
                fetchBluetoothCams()
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            CamSearchBar(
                searchText = searchText,
                onSearchTextChanged = { newText -> searchText = newText },
                modifier = Modifier
                    .weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            WifiBluetoothToggle { state ->
                wifiBluetoothToggleState = state
            }
        }

        val isWifiMode = wifiBluetoothToggleState == WifiBluetoothToggleState.WIFI
        val initialLoading = (isWifiMode && remoteCams == null) || (!isWifiMode && bluetoothCams == null)

        if (initialLoading && !isRefreshing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                if (isWifiMode) {
                    val filteredRemoteCams = remoteCams
                        ?.filter { it.alias.contains(searchText, ignoreCase = true) || (searchText.isDigitsOnly() && it.traffic_cam_id == searchText.toInt()) }
                        ?: emptyList()

                    if (filteredRemoteCams.isEmpty() && !isRefreshing) {
                        Text(
                            "No Wi-Fi cameras found.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredRemoteCams, key = { cam -> cam.traffic_cam_id }) { cam ->
                                CamCard(remoteCam = cam) {
                                    val camJson = Gson().toJson(cam)
                                    Intent(context, ArduinoConfigWifi::class.java).apply {
                                        putExtra("cam_json", camJson)
                                        putExtra("cam_type", "remote")
                                    }.also(context::startActivity)
                                }
                            }
                        }
                    }
                } else { // Bluetooth Mode
                    val filteredBluetoothCams = bluetoothCams
                        ?.filter { it.deviceName.contains(searchText, ignoreCase = true) || it.macAddress.contains(searchText, ignoreCase = true) }
                        ?: emptyList()

                    if (filteredBluetoothCams.isEmpty() && !isRefreshing) {
                        Text(
                            "No Bluetooth cameras found.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredBluetoothCams, key = { cam -> cam.macAddress }) { cam ->
                                CamCard(bluetoothCam = cam) {

                                }
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}