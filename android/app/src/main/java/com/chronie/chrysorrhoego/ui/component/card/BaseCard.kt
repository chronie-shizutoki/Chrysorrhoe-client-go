package com.chronie.chrysorrhoego.ui.component.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chronie.chrysorrhoego.ui.theme.Shapes
import com.chronie.chrysorrhoego.ui.theme.SurfaceVariantGray

/**
 * 基础卡片组件，提供灵活的布局和样式选项
 * @param modifier 修饰符
 * @param content 卡片内容
 * @param onClick 点击事件回调
 * @param enabled 是否启用点击事件
 * @param padding 卡片内部边距
 * @param cardType 卡片类型
 * @param elevation 卡片阴影高度
 * @param border 卡片边框
 */
@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    padding: PaddingValues = CardDefaults.ContentPadding,
    cardType: CardType = CardType.DEFAULT,
    elevation: Dp = CardDefaults.cardElevation().defaultElevation,
    border: BorderStroke? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // 根据是否可点击和卡片类型选择合适的容器
    val cardContainer: @Composable (() -> Unit) -> Unit = when {
        onClick != null && enabled -> {
            { content ->
                val clickableModifier = modifier.clickable(
                    interactionSource = interactionSource,
                    indication = CardDefaults.cardIndication(
                        interactionSource = interactionSource,
                        shape = Shapes.cardShape()
                    ),
                    onClick = onClick
                )
                when (cardType) {
                    CardType.DEFAULT -> Card(
                        modifier = clickableModifier,
                        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                        shape = Shapes.cardShape(),
                        border = border
                    ) { content() }
                    CardType.OUTLINED -> OutlinedCard(
                        modifier = clickableModifier,
                        shape = Shapes.cardShape(),
                        border = border ?: CardDefaults.outlinedCardBorder()
                    ) { content() }
                    CardType.SURFACE -> Surface(
                        modifier = clickableModifier,
                        elevation = elevation,
                        shape = Shapes.cardShape()
                    ) { content() }
                }
            }
        }
        else -> {
            { content ->
                when (cardType) {
                    CardType.DEFAULT -> Card(
                        modifier = modifier,
                        enabled = enabled,
                        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                        shape = Shapes.cardShape(),
                        border = border
                    ) { content() }
                    CardType.OUTLINED -> OutlinedCard(
                        modifier = modifier,
                        enabled = enabled,
                        shape = Shapes.cardShape(),
                        border = border ?: CardDefaults.outlinedCardBorder()
                    ) { content() }
                    CardType.SURFACE -> Surface(
                        modifier = modifier,
                        enabled = enabled,
                        elevation = elevation,
                        shape = Shapes.cardShape()
                    ) { content() }
                }
            }
        }
    }
    
    // 渲染卡片内容，应用边距和文字样式
    cardContainer {
        ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
            Box(modifier = Modifier.padding(padding)) {
                content()
            }
        }
    }
}

/**
 * 水平卡片，内容按水平方向排列
 */
@Composable
fun HorizontalCard(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    padding: PaddingValues = CardDefaults.ContentPadding,
    cardType: CardType = CardType.DEFAULT,
    elevation: Dp = CardDefaults.cardElevation().defaultElevation,
    border: BorderStroke? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
) {
    BaseCard(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        padding = padding,
        cardType = cardType,
        elevation = elevation,
        border = border,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = horizontalArrangement,
            content = content
        )
    }
}

/**
 * 垂直卡片，内容按垂直方向排列
 */
@Composable
fun VerticalCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    padding: PaddingValues = CardDefaults.ContentPadding,
    cardType: CardType = CardType.DEFAULT,
    elevation: Dp = CardDefaults.cardElevation().defaultElevation,
    border: BorderStroke? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
) {
    BaseCard(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        padding = padding,
        cardType = cardType,
        elevation = elevation,
        border = border,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = verticalArrangement,
            content = content
        )
    }
}

/**
 * 卡片类型枚举
 */
enum class CardType {
    DEFAULT,    // 默认卡片样式
    OUTLINED,   // 带边框的卡片样式
    SURFACE     // 表面样式卡片
}

/**
 * 创建默认的卡片颜色方案
 */
@Composable
fun defaultCardColors(): CardColors {
    return CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = SurfaceVariantGray,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/**
 * 创建默认的卡片边框
 */
@Composable
fun defaultCardBorder(): BorderStroke {
    return BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outline
    )
}