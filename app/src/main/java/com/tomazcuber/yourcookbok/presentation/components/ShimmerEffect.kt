package com.tomazcuber.yourcookbok.presentation.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun shimmerBrush(targetValue: Float = 1000f): Brush {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
        ),
        label = "ShimmerTranslateAnimation"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
}

fun Modifier.shimmerBackground(): Modifier = composed {
    var size = IntSize.Zero
    val shimmer = shimmerBrush(targetValue = 1000f)

    this
        .onGloballyPositioned {
            size = it.size
        }
        .background(shimmer)
}

/**
 * A simple Box with a shimmer background, useful for placeholders.
 */
@Composable
fun ShimmerPlaceholder(modifier: Modifier) {
    Box(modifier = modifier.shimmerBackground())
}

@Preview(showBackground = true)
@Composable
private fun ShimmerPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        // A large box, like an image
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(Modifier.height(16.dp))
        // A medium box, like a title
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
        Spacer(Modifier.height(8.dp))
        // A smaller box, like a line of text
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.7f) // 70% width
                .height(50.dp)
        )
    }
}
