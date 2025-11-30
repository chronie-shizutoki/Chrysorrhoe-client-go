package com.chronie.chrysorrhoego.ui.component.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chronie.chrysorrhoego.ui.theme.PrimaryBlue
import com.chronie.chrysorrhoego.ui.theme.Shapes
import com.chronie.chrysorrhoego.ui.theme.OnPrimaryWhite
import com.chronie.chrysorrhoego.ui.theme.PrimaryBlueTransparent

/**
 * 主要按钮组件，使用应用的主色调
 * @param text 按钮文字
 * @param onClick 点击事件回调
 * @param modifier 修饰符
 * @param enabled 是否启用
 * @param loading 是否显示加载状态
 * @param fullWidth 是否占满父容器宽度
 * @param variant 按钮变体（填充、描边等）
 * @param size 按钮大小
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    fullWidth: Boolean = true,
    variant: ButtonVariant = ButtonVariant.FILLED,
    size: ButtonSize = ButtonSize.MEDIUM,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val buttonColors = when (variant) {
        ButtonVariant.FILLED -> ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            contentColor = OnPrimaryWhite,
            disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f),
            disabledContentColor = OnPrimaryWhite.copy(alpha = 0.7f)
        )
        ButtonVariant.OUTLINED -> ButtonDefaults.outlinedButtonColors(
            contentColor = PrimaryBlue,
            disabledContentColor = PrimaryBlue.copy(alpha = 0.5f)
        )
        ButtonVariant.TEXT -> ButtonDefaults.textButtonColors(
            contentColor = PrimaryBlue,
            disabledContentColor = PrimaryBlue.copy(alpha = 0.5f)
        )
    }
    
    val buttonShape = when (variant) {
        ButtonVariant.FILLED -> RoundedCornerShape(Shapes.buttonShape().topStart)
        ButtonVariant.OUTLINED -> RoundedCornerShape(Shapes.buttonShape().topStart)
        ButtonVariant.TEXT -> RoundedCornerShape(0.dp)
    }
    
    val buttonPadding = when (size) {
        ButtonSize.SMALL -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ButtonSize.MEDIUM -> PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ButtonSize.LARGE -> PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    }
    
    val textStyle = when (size) {
        ButtonSize.SMALL -> MaterialTheme.typography.labelMedium
        ButtonSize.MEDIUM -> MaterialTheme.typography.labelLarge
        ButtonSize.LARGE -> MaterialTheme.typography.titleMedium
    }
    
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !loading,
        shape = buttonShape,
        colors = buttonColors,
        contentPadding = buttonPadding,
        interactionSource = interactionSource,
        elevation = if (variant == ButtonVariant.FILLED) ButtonDefaults.buttonElevation() else null,
        border = if (variant == ButtonVariant.OUTLINED) {
            ButtonDefaults.outlinedButtonBorder(enabled = enabled)
        } else null
    ) {
        if (loading) {
            LoadingIndicator(size = size)
        } else {
            Text(
                text = text,
                style = textStyle.copy(
                    fontWeight = if (variant == ButtonVariant.FILLED) FontWeight.Medium else FontWeight.Normal
                )
            )
        }
    }
}

/**
 * 按钮变体枚举
 */
enum class ButtonVariant {
    FILLED,   // 填充样式
    OUTLINED, // 描边样式
    TEXT      // 纯文本样式
}

/**
 * 按钮大小枚举
 */
enum class ButtonSize {
    SMALL,    // 小按钮
    MEDIUM,   // 中等按钮
    LARGE     // 大按钮
}

/**
 * 次要按钮组件，使用应用的次要色调
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    fullWidth: Boolean = true,
    variant: ButtonVariant = ButtonVariant.OUTLINED,
    size: ButtonSize = ButtonSize.MEDIUM,
) {
    // 这里可以使用SecondaryBlue来实现次要按钮
    PrimaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        fullWidth = fullWidth,
        variant = variant,
        size = size
    )
}