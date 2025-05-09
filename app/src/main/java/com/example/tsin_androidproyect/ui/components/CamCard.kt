package com.example.tsin_androidproyect.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tsin_androidproyect.models.BluetoothTrafficCam
import com.example.tsin_androidproyect.models.RemoteTrafficCam

@Composable
fun CamCard(
    remoteCam: RemoteTrafficCam? = null,
    bluetoothCam: BluetoothTrafficCam? = null,
    onClick: () -> Unit
) {
    require(remoteCam != null || bluetoothCam != null) {
        "Either remoteCam or bluetoothCam must be provided to CamCard"
    }
    require(remoteCam == null || bluetoothCam == null) {
        "Only one type of cam (remoteCam or bluetoothCam) can be provided to CamCard"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (remoteCam != null) {
                Column {
                    Text(text = remoteCam.alias)
                    Text(text = "ID: ${remoteCam.traffic_cam_id}")
                }
            } else if (bluetoothCam != null) {
                Column {
                    Text(text = bluetoothCam.deviceName)
                    Text(text = bluetoothCam.macAddress)
                }
            }
        }
    }
}