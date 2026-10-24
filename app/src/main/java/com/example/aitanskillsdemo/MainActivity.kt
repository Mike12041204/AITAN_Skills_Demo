package com.example.aitanskillsdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.example.aitanskillsdemo.ui.FieldScene
import com.example.aitanskillsdemo.ui.theme.AITANSkillsDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AITANSkillsDemoTheme {
                Surface {
                    FieldScene()
                }
            }
        }
    }
}
