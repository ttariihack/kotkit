package com.kotkit.basic.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotkit.basic.ui.theme.AeonikFontFamily
import com.kotkit.basic.ui.theme.GradientCyan
import com.kotkit.basic.ui.theme.GradientPink
import com.kotkit.basic.ui.theme.TikTokBlack
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Animation states
    var showTitle by remember { mutableStateOf(false) }
    var glitchActive by remember { mutableStateOf(false) }
    var fadeOut by remember { mutableStateOf(false) }

    // Glitch intensity
    var glitchStep by remember { mutableIntStateOf(0) }

    // Glitch slices
    var glitchSlices by remember {
        mutableStateOf(listOf<GlitchSlice>())
    }

    // Title animation
    val titleAlpha by animateFloatAsState(
        targetValue = if (showTitle && !glitchActive && !fadeOut) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (glitchActive || fadeOut) 100 else 800,
            easing = if (glitchActive || fadeOut) LinearEasing
            else LinearOutSlowInEasing
        ),
        label = "titleAlpha"
    )

    val titleScale by animateFloatAsState(
        targetValue = if (showTitle && !glitchActive) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "titleScale"
    )

    val titleOffsetY by animateFloatAsState(
        targetValue = if (showTitle && !glitchActive) 0f else 10f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "titleOffsetY"
    )

    // Glow pulsation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Screen fade out
    val screenAlpha by animateFloatAsState(
        targetValue = if (fadeOut) 0f else 1f,
        animationSpec = tween(250, easing = LinearEasing),
        finishedListener = {
            if (fadeOut) onSplashFinished()
        },
        label = "screenAlpha"
    )

    // Splash timeline: title first, then glitch
    LaunchedEffect(Unit) {
        delay(1800)
        showTitle = true

        delay(2500)
        glitchActive = true
    }

    // Glitch animation loop
    LaunchedEffect(glitchActive) {
        if (glitchActive) {
            for (step in 8 downTo 0) {
                glitchStep = step
                val intensity = step / 8f

                repeat(2) {
                    val sliceCount = (
                        Random.nextInt(4, 10) * intensity
                    ).toInt().coerceAtLeast(2)

                    glitchSlices = if (Random.nextFloat() < 0.8f) {
                        List(sliceCount) {
                            GlitchSlice(
                                yOffset = Random.nextFloat(),
                                height = Random.nextFloat() * 0.15f + 0.01f,
                                xShift = (Random.nextFloat() - 0.5f) * 80f,
                                color = when (Random.nextInt(3)) {
                                    0 -> GradientCyan
                                    1 -> GradientPink
                                    else -> Color.White
                                },
                                alpha = Random.nextFloat() * 0.6f + 0.2f
                            )
                        }
                    } else {
                        emptyList()
                    }

                    delay(30L)
                }
            }

            glitchSlices = emptyList()
            fadeOut = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TikTokBlack)
            .alpha(screenAlpha),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect
        Box(
            modifier = Modifier
                .size(400.dp)
                .blur(100.dp)
                .alpha(glowAlpha * 0.4f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GradientCyan.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Glitch slices overlay
        if (glitchActive && glitchSlices.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                glitchSlices.forEach { slice ->
                    drawRect(
                        color = slice.color.copy(alpha = slice.alpha),
                        topLeft = Offset(
                            slice.xShift.dp.toPx(),
                            size.height * slice.yOffset
                        ),
                        size = Size(
                            size.width + 100f,
                            size.height * slice.height
                        )
                    )

                    if (Random.nextFloat() > 0.5f) {
                        drawRect(
                            color = slice.color.copy(alpha = slice.alpha * 0.5f),
                            topLeft = Offset(
                                -slice.xShift.dp.toPx() * 0.5f,
                                size.height * slice.yOffset + 5.dp.toPx()
                            ),
                            size = Size(
                                size.width,
                                size.height * slice.height * 0.5f
                            )
                        )
                    }
                }
            }
        }

        // Scanlines overlay
        if (glitchActive && glitchStep > 3) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.08f)
            ) {
                val lineHeight = 1.dp.toPx()
                val gap = 4.dp.toPx()
                var y = 0f

                while (y < size.height) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(0f, y),
                        size = Size(size.width, lineHeight)
                    )
                    y += gap + lineHeight
                }
            }
        }

        // App title
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "KotKit",
                fontSize = 42.sp,
                fontFamily = AeonikFontFamily,
                fontWeight = FontWeight.Bold,
                style = androidx.compose.ui.text.TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(GradientCyan, GradientPink)
                    )
                ),
                modifier = Modifier
                    .alpha(titleAlpha)
                    .scale(titleScale)
                    .offset(y = titleOffsetY.dp)
            )

            Text(
                text = "Smart TikTok Autoposter",
                fontSize = 14.sp,
                fontFamily = AeonikFontFamily,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .alpha(titleAlpha * 0.8f)
                    .offset(y = titleOffsetY.dp)
                    .padding(top = 8.dp)
            )
        }
    }
}

private data class GlitchSlice(
    val yOffset: Float,
    val height: Float,
    val xShift: Float,
    val color: Color,
    val alpha: Float = 0.4f
)
