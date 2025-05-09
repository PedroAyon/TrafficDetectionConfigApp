package com.example.tsin_androidproyect.models

enum class WifiBluetoothToggleState {
    WIFI,
    BLUETOOTH;

    fun switchState(): WifiBluetoothToggleState {
        return when (this) {
            WIFI -> BLUETOOTH
            BLUETOOTH -> WIFI
        }
    }
}