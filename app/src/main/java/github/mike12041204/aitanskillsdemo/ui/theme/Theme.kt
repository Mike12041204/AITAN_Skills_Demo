package github.mike12041204.aitanskillsdemo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme()

// App theme, always light mode because the colors in Color.kt are made for a light background
@Composable
fun AITANSkillsDemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
