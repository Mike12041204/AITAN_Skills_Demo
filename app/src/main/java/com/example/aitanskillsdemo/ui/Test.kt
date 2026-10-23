package com.example.aitanskillsdemo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aitanskillsdemo.data.TestBackend
import com.example.aitanskillsdemo.ui.theme.AITANSkillsDemoTheme

@Composable
fun Test(modifier: Modifier = Modifier) {
    val backend = remember { TestBackend() }
    var text by remember { mutableStateOf("") }
    val result by backend.output.subscribeAsState(initial = "")

    Column(modifier = modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                backend.send(it)
            },
            placeholder = { Text("Type Here...") }
        )
        Text("Backend says: $result")
    }
}

@Preview(showBackground = true)
@Composable
fun TestPreview() {
    AITANSkillsDemoTheme {
        Test()
    }
}
