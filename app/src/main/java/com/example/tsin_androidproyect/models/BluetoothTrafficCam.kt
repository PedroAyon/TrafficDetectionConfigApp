package com.example.tsin_androidproyect.models

data class BluetoothTrafficCam(
    val SSID: String?, // Wifi network public name
    val password: String?, // Wifi password
    val deviceName: String,
    val macAddress: String
)
