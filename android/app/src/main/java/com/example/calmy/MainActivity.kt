package com.example.calmy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.calmy.di.AppModule
import com.example.calmy.presentation.navigation.AppNavGraph
import com.example.calmy.ui.theme.CalmyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalmyTheme {
                AppNavGraph(
                    registerViewModelFactory = AppModule.provideRegisterViewModelFactory()
                )
            }
        }
    }
}
