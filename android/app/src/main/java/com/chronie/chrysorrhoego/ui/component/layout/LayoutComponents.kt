package com.chronie.chrysorrhoego.ui.component.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 灵活的Row布局组件，提供额外的对齐和间距选项
 */
@Composable
fun FlexRow(
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    padding: PaddingValues = PaddingValues(0.dp),
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(padding),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = if (spacing == 0.dp) horizontalArrangement else Arrangement.spacedBy(spacing)
    ) {
        content()
    }
}

/**
 * 灵活的Column布局组件，提供额外的对齐和间距选项
 */
@Composable
fun FlexColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    padding: PaddingValues = PaddingValues(0.dp),
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(padding),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = if (spacing == 0.dp) verticalArrangement else Arrangement.spacedBy(spacing)
    ) {
        content()
    }
}

/**
 * 居中的Box布局组件
 */
@Composable
fun CenteredBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

/**
 * 屏幕安全区域填充的布局组件
 */
@Composable
fun SafeAreaLayout(
    modifier: Modifier = Modifier,
    top: Boolean = true,
    bottom: Boolean = true,
    left: Boolean = true,
    right: Boolean = true,
    content: @Composable () -> Unit
) {
    var insets = WindowInsets.safeDrawing
    
    if (!top) insets = insets.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
    if (!bottom) insets = insets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    if (!left) insets = insets.only(WindowInsetsSides.Top + WindowInsetsSides.Right + WindowInsetsSides.Bottom)
    if (!right) insets = insets.only(WindowInsetsSides.Top + WindowInsetsSides.Left + WindowInsetsSides.Bottom)
    
    Box(modifier = modifier.windowInsetsPadding(insets)) {
        content()
    }
}

/**
 * 可折叠的空间组件，用于自适应布局
 */
@Composable
fun CollapsibleSpacer(
    modifier: Modifier = Modifier,
    minHeight: Dp = 0.dp,
    maxHeight: Dp = 1000.dp,
    weight: Float = 1f
) {
    Spacer(modifier = modifier
        .heightIn(minHeight, maxHeight)
        .weight(weight))
}

/**
 * 水平分隔线组件
 */
@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.LightGray,
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp,
    endIndent: Dp = 0.dp
) {
    val indentModifiers = if (startIndent != 0.dp || endIndent != 0.dp) {
        Modifier.padding(start = startIndent, end = endIndent)
    } else {
        Modifier
    }
    
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .then(indentModifiers)
            .height(thickness)
            .background(color)
    )
}

/**
 * 垂直分隔线组件
 */
@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.LightGray,
    thickness: Dp = 1.dp,
    topIndent: Dp = 0.dp,
    bottomIndent: Dp = 0.dp
) {
    val indentModifiers = if (topIndent != 0.dp || bottomIndent != 0.dp) {
        Modifier.padding(top = topIndent, bottom = bottomIndent)
    } else {
        Modifier
    }
    
    Spacer(
        modifier = modifier
            .fillMaxHeight()
            .then(indentModifiers)
            .width(thickness)
            .background(color)
    )
}

/**
 * 自适应网格布局组件
 */
@Composable
fun AdaptiveGrid(
    modifier: Modifier = Modifier,
    itemCount: Int,
    columnCount: Int = 2,
    spacing: Dp = 16.dp,
    content: @Composable (Int) -> Unit
) {
    val density = LocalDensity.current
    val spacingPx = density.run { spacing.toPx() }
    
    Layout(
        content = { repeat(itemCount) { index -> content(index) } },
        modifier = modifier
    ) { measurables, constraints ->
        val itemWidth = (constraints.maxWidth - (columnCount - 1) * spacingPx) / columnCount
        val itemConstraints = constraints.copy(minWidth = itemWidth.toInt(), maxWidth = itemWidth.toInt())
        
        val placeables = measurables.map { it.measure(itemConstraints) }
        
        val rowCount = (placeables.size + columnCount - 1) / columnCount
        val itemHeight = placeables.maxOfOrNull { it.height } ?: 0
        val height = rowCount * itemHeight + (rowCount - 1) * spacingPx
        
        layout(constraints.maxWidth, height.toInt()) {
            var x = 0
            var y = 0
            
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(x = x, y = y)
                
                x += (itemWidth + spacingPx).toInt()
                if ((index + 1) % columnCount == 0) {
                    x = 0
                    y += (itemHeight + spacingPx).toInt()
                }
            }
        }
    }
}

/**
 * 等宽的Row布局，每个子项占据相同的宽度
 */
@Composable
fun EqualWidthRow(
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val spacingPx = density.run { spacing.toPx() }
    
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val itemCount = measurables.size
        if (itemCount == 0) {
            return@Layout layout(constraints.minWidth, constraints.minHeight) {}
        }
        
        val itemWidth = (constraints.maxWidth - (itemCount - 1) * spacingPx) / itemCount
        val itemConstraints = constraints.copy(minWidth = itemWidth.toInt(), maxWidth = itemWidth.toInt())
        
        val placeables = measurables.map { it.measure(itemConstraints) }
        val height = placeables.maxOfOrNull { it.height } ?: constraints.minHeight
        
        layout(constraints.maxWidth, height) {
            var x = 0
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(x = x, y = 0)
                x += (itemWidth + spacingPx).toInt()
            }
        }
    }
}

/**
 * 等宽的Column布局，每个子项占据相同的高度
 */
@Composable
fun EqualHeightColumn(
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val spacingPx = density.run { spacing.toPx() }
    
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val itemCount = measurables.size
        if (itemCount == 0) {
            return@Layout layout(constraints.minWidth, constraints.minHeight) {}
        }
        
        val itemHeight = (constraints.maxHeight - (itemCount - 1) * spacingPx) / itemCount
        val itemConstraints = constraints.copy(minHeight = itemHeight.toInt(), maxHeight = itemHeight.toInt())
        
        val placeables = measurables.map { it.measure(itemConstraints) }
        val width = placeables.maxOfOrNull { it.width } ?: constraints.minWidth
        
        layout(width, constraints.maxHeight) {
            var y = 0
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(x = 0, y = y)
                y += (itemHeight + spacingPx).toInt()
            }
        }
    }
}

/**
 * 固定比例的布局组件，用于创建宽高比固定的容器
 */
@Composable
fun AspectRatioBox(
    modifier: Modifier = Modifier,
    ratio: Float = 1f,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val itemWidth = constraints.maxWidth
        val itemHeight = (itemWidth / ratio).toInt()
        
        val itemConstraints = constraints.copy(minWidth = itemWidth, maxWidth = itemWidth, minHeight = itemHeight, maxHeight = itemHeight)
        val placeable = measurables.firstOrNull()?.measure(itemConstraints)
        
        layout(itemWidth, itemHeight) {
            placeable?.placeRelative(0, 0)
        }
    }
}

/**
 * 空间填充组件，用于在布局中创建指定大小的空间
 */
@Composable
fun Space(
    modifier: Modifier = Modifier,
    width: Dp = 0.dp,
    height: Dp = 0.dp
) {
    Spacer(modifier = modifier.width(width).height(height))
}

/**
 * 卡片网格布局组件，用于展示卡片列表
 */
@Composable
fun CardGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    spacing: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    AdaptiveGrid(
        modifier = modifier,
        itemCount = columns,
        columnCount = columns,
        spacing = spacing
    ) {
        content()
    }
}