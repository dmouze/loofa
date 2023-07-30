package com.example.loofa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.loofa.ui.composable.navigation.SetupNavGraph
import com.example.loofa.ui.theme.LoofaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoofaTheme{
                val navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }
}
