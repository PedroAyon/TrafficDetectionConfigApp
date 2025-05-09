package com.example.tsin_androidproyect.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tsin_androidproyect.models.WifiBluetoothToggleState

@Composable
fun WifiBluetoothToggle(
    onToggle: (state: WifiBluetoothToggleState) -> Unit
) {
    var state by remember { mutableStateOf(WifiBluetoothToggleState.WIFI) }
    Surface(
        modifier = Modifier
            .size(48.dp),
        shape = CircleShape,
        color = if (state == WifiBluetoothToggleState.WIFI) Color(0xFF42A5F5)
        else Color(0xFF26A69A),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    state = state.switchState()
                    onToggle(state)
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = state,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                }
            ) { state ->
                Icon(
                    imageVector = if (state == WifiBluetoothToggleState.WIFI) Icons.Filled.Wifi
                    else Icons.Filled.Bluetooth,
                    contentDescription = if (state == WifiBluetoothToggleState.WIFI) "Wi-Fi Enabled"
                    else "Bluetooth Enabled",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}