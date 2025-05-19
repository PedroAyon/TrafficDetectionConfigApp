package com.example.tsin_androidproyect.ui.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.tsin_androidproyect.ArduinoConfigBluetooth
import com.example.tsin_androidproyect.ArduinoConfigWifi
import com.example.tsin_androidproyect.models.RemoteTrafficCam
import com.example.tsin_androidproyect.models.WifiBluetoothToggleState
import com.example.tsin_androidproyect.repository.RemoteCamRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CamList(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val remoteCamRepository = remember { RemoteCamRepository() }
    var remoteCams by remember { mutableStateOf<List<RemoteTrafficCam>?>(null) }
    var searchText by remember { mutableStateOf("") }
    var isRefreshing by remember { mutableStateOf(false) }
    var wifiBluetoothToggleState by remember { mutableStateOf(WifiBluetoothToggleState.WIFI) }
    val scope = rememberCoroutineScope()
    val bluetoothDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

// Receiver to collect found devices
    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        device?.let { newDevice ->
                            // Add only if not already in the list
                            if (bluetoothDevices.none { it.address == newDevice.address }) {
                                bluetoothDevices.add(newDevice)
                            }
                        }
                    }

                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        isRefreshing = false
                    }
                }
            }
        }
    }


    // Function to fetch Wi-Fi cams
    fun fetchWifiCams() {
        scope.launch {
            isRefreshing = true
            remoteCams = try {
                remoteCamRepository.fetchAllCams()
            } catch (_: Exception) {
                emptyList()
            }
            isRefreshing = false
        }
    }

    // Function to fetch Bluetooth devices using discovery
    fun fetchBluetoothDevices() {
        if (bluetoothAdapter == null) return
        // Clear existing
        bluetoothDevices.clear()
        isRefreshing = true
        // Register receiver
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(receiver, filter)
        // Start discovery
        bluetoothAdapter.startDiscovery()
    }

// Ensure receiver is unregistered when no longer needed
    DisposableEffect(Unit) {
        onDispose {
            context.unregisterReceiver(receiver)
            bluetoothAdapter?.cancelDiscovery()
        }
    }

    LaunchedEffect(wifiBluetoothToggleState) {
        searchText = ""
        if (wifiBluetoothToggleState == WifiBluetoothToggleState.WIFI) {
            if (remoteCams == null) fetchWifiCams()
        } else {
            fetchBluetoothDevices()
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            if (wifiBluetoothToggleState == WifiBluetoothToggleState.WIFI) {
                fetchWifiCams()
            } else {
                fetchBluetoothDevices()
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
        val initialLoading =
            (isWifiMode && remoteCams == null) || (!isWifiMode && bluetoothDevices.isEmpty())

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
                        ?.filter {
                            it.alias.contains(
                                searchText,
                                ignoreCase = true
                            ) || (searchText.isDigitsOnly() && it.traffic_cam_id == searchText.toInt())
                        }
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
                    val filteredBluetoothDevices = bluetoothDevices.filter {
                        (it.name != null && it.name.contains(
                            searchText,
                            ignoreCase = true
                        )) || it.address.contains(searchText, ignoreCase = true)
                    }

                    if (filteredBluetoothDevices.isEmpty() && !isRefreshing) {
                        Text(
                            "No Bluetooth cameras found.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredBluetoothDevices, key = { cam -> cam.address }) { device ->
                                CamCard(bluetoothCam = device) {
                                    Intent(context, ArduinoConfigBluetooth::class.java).also { intent ->
                                        intent.putExtra("bluetooth_device", device)
                                        context.startActivity(intent)
                                    }                                }
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
