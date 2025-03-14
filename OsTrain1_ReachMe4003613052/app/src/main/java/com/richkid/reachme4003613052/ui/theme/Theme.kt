package com.richkid.reachme4003613052.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimaryContainer = Color(0x61000000),
    background = Color.Black,
    surface = Color(0xFF121212),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5F0ED2),
    onPrimaryContainer = Color(0x61000000),
    background = Color.Black,
    surface = Color(0xFFE0E0E0),
    onSurface = Color.White
)


val DarkGradient: Brush
    @Composable
    get() = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.90f),
            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
        )
    )

@Composable
fun ReachMe4003613052Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
