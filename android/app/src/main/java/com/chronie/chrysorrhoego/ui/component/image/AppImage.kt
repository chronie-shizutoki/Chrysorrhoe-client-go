package com.chronie.chrysorrhoego.ui.component.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chronie.chrysorrhoego.ui.component.text.BodyText
import java.io.File

/**
 * 统一的图像组件，提供多种样式和配置选项
 */
@Composable
fun AppImage(
    modifier: Modifier = Modifier,
    imageResId: Int,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    shape: Shape? = null,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    backgroundColor: Color = Color.Transparent,
    onClick: (() -> Unit)? = null,
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
) {
    val imageModifier = Modifier
        .let { if (width != Dp.Unspecified) it.width(width) else it }
        .let { if (height != Dp.Unspecified) it.height(height) else it }
        .let { if (shape != null) it.clip(shape) else it }
        .let { if (borderWidth > 0.dp) it.border(borderWidth, borderColor, shape ?: RoundedCornerShape(0.dp)) else it }
        .let { if (backgroundColor != Color.Transparent) it.background(backgroundColor) else it }
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .then(modifier)

    Image(
        painter = painterResource(id = imageResId),
        contentDescription = contentDescription,
        modifier = imageModifier,
        contentScale = contentScale
    )
}

/**
 * 圆形图像组件
 */
@Composable
fun CircleImage(
    modifier: Modifier = Modifier,
    imageResId: Int,
    contentDescription: String? = null,
    size: Dp = 48.dp,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    onClick: (() -> Unit)? = null,
) {
    AppImage(
        modifier = modifier,
        imageResId = imageResId,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        shape = CircleShape,
        borderWidth = borderWidth,
        borderColor = borderColor,
        width = size,
        height = size,
        onClick = onClick
    )
}

/**
 * 方形图像组件
 */
@Composable
fun SquareImage(
    modifier: Modifier = Modifier,
    imageResId: Int,
    contentDescription: String? = null,
    size: Dp = 80.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    onClick: (() -> Unit)? = null,
) {
    AppImage(
        modifier = modifier,
        imageResId = imageResId,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        shape = shape,
        borderWidth = borderWidth,
        borderColor = borderColor,
        width = size,
        height = size,
        onClick = onClick
    )
}

/**
 * 带文本的圆形头像组件
 */
@Composable
fun AvatarWithText(
    modifier: Modifier = Modifier,
    text: String,
    size: Dp = 48.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = Color.White,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    onClick: (() -> Unit)? = null,
) {
    // 取文本的第一个字符作为头像显示内容
    val displayText = text.take(1).uppercase()
    
    val avatarModifier = Modifier
        .size(size)
        .clip(CircleShape)
        .background(backgroundColor)
        .let { if (borderWidth > 0.dp) it.border(borderWidth, borderColor, CircleShape) else it }
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .then(modifier)
    
    Box(
        modifier = avatarModifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            color = textColor,
            fontSize = (size.value / 2.5).dp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 图标按钮组件
 */
@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    imageResId: Int,
    contentDescription: String? = null,
    size: Dp = 40.dp,
    iconSize: Dp = size * 0.6f,
    backgroundColor: Color = Color.Transparent,
    tintColor: Color? = null,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(backgroundColor, shape = CircleShape)
            .let { if (borderWidth > 0.dp) it.border(borderWidth, borderColor, CircleShape) else it }
            .clickable(onClick = onClick)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * 带徽章的图像组件
 */
@Composable
fun BadgeImage(
    modifier: Modifier = Modifier,
    imageResId: Int,
    contentDescription: String? = null,
    badgeText: String? = null,
    badgeIcon: Int? = null,
    badgePosition: Alignment = Alignment.TopEnd,
    badgeSize: Dp = 20.dp,
    badgeBackgroundColor: Color = MaterialTheme.colorScheme.error,
    badgeTextColor: Color = Color.White,
    imageSize: Dp = 48.dp,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    onClick: (() -> Unit)? = null,
) {
    Box(modifier = modifier) {
        CircleImage(
            imageResId = imageResId,
            contentDescription = contentDescription,
            size = imageSize,
            borderWidth = borderWidth,
            borderColor = borderColor,
            onClick = onClick
        )
        
        if (badgeText != null || badgeIcon != null) {
            Box(
                modifier = Modifier
                    .align(badgePosition)
                    .size(badgeSize)
                    .background(badgeBackgroundColor, shape = CircleShape)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                if (badgeIcon != null) {
                    Image(
                        painter = painterResource(id = badgeIcon),
                        contentDescription = null,
                        modifier = Modifier.size(badgeSize * 0.6f)
                    )
                } else if (badgeText != null) {
                    Text(
                        text = badgeText,
                        color = badgeTextColor,
                        fontSize = (badgeSize.value / 2).dp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * 图像网格组件
 */
@Composable
fun ImageGrid(
    modifier: Modifier = Modifier,
    imageResIds: List<Int>,
    columns: Int = 3,
    spacing: Dp = 8.dp,
    imageSize: Dp = Dp.Unspecified,
    contentDescription: String? = null,
    onClick: ((Int) -> Unit)? = null,
) {
    val gridModifier = Modifier
        .let { if (imageSize != Dp.Unspecified) it.size(imageSize) else it }
        .then(modifier)
    
    Column(
        modifier = gridModifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        for (row in 0 until (imageResIds.size + columns - 1) / columns) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < imageResIds.size) {
                        Box(modifier = Modifier.weight(1f)) {
                            AppImage(
                                imageResId = imageResIds[index],
                                contentDescription = contentDescription,
                                contentScale = ContentScale.Crop,
                                width = if (imageSize != Dp.Unspecified) imageSize / columns else Dp.Unspecified,
                                height = if (imageSize != Dp.Unspecified) imageSize / columns else Dp.Unspecified,
                                onClick = if (onClick != null) { { onClick(index) } } else null
                            )
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * 占位图像组件，当图片加载失败或不存在时显示
 */
@Composable
fun PlaceholderImage(
    modifier: Modifier = Modifier,
    text: String = "Image",
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    width: Dp = 120.dp,
    height: Dp = 120.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    onClick: (() -> Unit)? = null,
) {
    val placeholderModifier = Modifier
        .width(width)
        .height(height)
        .background(backgroundColor, shape = shape)
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .then(modifier)
    
    Box(modifier = placeholderModifier, contentAlignment = Alignment.Center) {
        BodyText(
            text = text,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 可点击的图像与文本组合组件
 */
@Composable
fun ClickableImageWithText(
    modifier: Modifier = Modifier,
    imageResId: Int,
    text: String,
    contentDescription: String? = null,
    orientation: Orientation = Orientation.Vertical,
    spacing: Dp = 8.dp,
    imageSize: Dp = 48.dp,
    onClick: () -> Unit
) {
    val containerModifier = Modifier
        .clickable(onClick = onClick)
        .then(modifier)
    
    when (orientation) {
        Orientation.Vertical -> {
            Column(
                modifier = containerModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                AppImage(
                    imageResId = imageResId,
                    contentDescription = contentDescription,
                    size = imageSize
                )
                BodyText(
                    text = text,
                    textAlign = TextAlign.Center
                )
            }
        }
        Orientation.Horizontal -> {
            Row(
                modifier = containerModifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                AppImage(
                    imageResId = imageResId,
                    contentDescription = contentDescription,
                    size = imageSize
                )
                BodyText(text = text)
            }
        }
    }
}

/**
 * 图像方向枚举
 */
enum class Orientation {
    Horizontal,
    Vertical
}

/**
 * 加载中的图像占位组件
 */
@Composable
fun LoadingImage(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    width: Dp = 120.dp,
    height: Dp = 120.dp,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(backgroundColor, shape = shape)
    )
}