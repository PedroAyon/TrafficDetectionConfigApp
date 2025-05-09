package com.example.tsin_androidproyect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.tsin_androidproyect.ui.components.CamList
import com.example.tsin_androidproyect.ui.components.TopBar
import com.example.tsin_androidproyect.ui.theme.TSIN_ProyectAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TSIN_ProyectAndroidTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBar() }
                ) { innerPadding ->
                    CamList(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
