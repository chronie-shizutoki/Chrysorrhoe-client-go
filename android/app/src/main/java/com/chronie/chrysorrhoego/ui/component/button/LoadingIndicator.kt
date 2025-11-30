package com.chronie.chrysorrhoego.ui.component.button

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chronie.chrysorrhoego.ui.theme.OnPrimaryWhite

/**
 * 加载指示器组件，用于按钮等组件的加载状态显示
 * @param size 指示器大小
 * @param color 指示器颜色
 * @param strokeWidth 指示器描边宽度
 * @param animationDuration 动画周期时长（毫秒）
 */
@Composable
fun LoadingIndicator(
    size: Dp = 24.dp,
    color: Color = OnPrimaryWhite,
    strokeWidth: Dp = 2.dp,
    animationDuration: Int = 1000
) {
    // 创建无限循环的旋转动画
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = LinearEasing
            )
        )
    )

    Canvas(
        modifier = Modifier.size(size)
    ) {
        val center = Offset(size.toPx() / 2, size.toPx() / 2)
        val radius = (size.toPx() - strokeWidth.toPx()) / 2
        
        // 绘制加载指示器背景圆环
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth.toPx())
        )
        
        // 绘制加载指示器圆弧
        drawArc(
            color = color,
            startAngle = rotationAngle.value - 90,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(
                x = center.x - radius,
                y = center.y - radius
            ),
            size = Size(radius * 2, radius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth.toPx(),
                cap = androidx.compose.ui.graphics.drawscope.StrokeCap.Round
            )
        )
    }
}

/**
 * 按钮加载指示器，根据按钮大小自动调整尺寸
 */
@Composable
fun LoadingIndicator(
    size: ButtonSize
) {
    val indicatorSize = when (size) {
        ButtonSize.SMALL -> 16.dp
        ButtonSize.MEDIUM -> 20.dp
        ButtonSize.LARGE -> 24.dp
    }
    
    val strokeWidth = when (size) {
        ButtonSize.SMALL -> 1.5.dp
        ButtonSize.MEDIUM -> 2.dp
        ButtonSize.LARGE -> 2.5.dp
    }
    
    LoadingIndicator(
        size = indicatorSize,
        color = OnPrimaryWhite,
        strokeWidth = strokeWidth
    )
}