package com.example.aitanskillsdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.aitanskillsdemo.data.FieldRepository
import com.example.aitanskillsdemo.ui.FieldScene
import com.example.aitanskillsdemo.ui.theme.AITANSkillsDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AITANSkillsDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FieldScene(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}