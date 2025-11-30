package com.chronie.chrysorrhoego.ui.component.text

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow

/**
 * 应用的统一文本组件，提供灵活的样式选项
 * @param text 文本内容
 * @param modifier 修饰符
 * @param style 文本样式
 * @param color 文本颜色
 * @param fontSize 字体大小
 * @param fontWeight 字重
 * @param fontStyle 字体样式（正常、斜体等）
 * @param letterSpacing 字间距
 * @param lineHeight 行高
 * @param textAlign 文本对齐方式
 * @param textDecoration 文本装饰（下划线、删除线等）
 * @param overflow 文本溢出处理
 * @param maxLines 最大行数
 */
@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = LocalContentColor.current,
    fontSize: androidx.compose.ui.unit.TextUnit = style.fontSize,
    fontWeight: FontWeight? = style.fontWeight,
    fontStyle: FontStyle? = style.fontStyle,
    letterSpacing: androidx.compose.ui.unit.TextUnit = style.letterSpacing,
    lineHeight: androidx.compose.ui.unit.TextUnit = style.lineHeight,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight,
            textDecoration = textDecoration
        ),
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

/**
 * 标题文本组件，使用应用的标题样式
 */
@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    color: Color = LocalContentColor.current,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    AppText(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

/**
 * 副标题文本组件，使用应用的副标题样式
 */
@Composable
fun SubtitleText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = LocalContentColor.current,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    AppText(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

/**
 * 正文文本组件，使用应用的正文样式
 */
@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = LocalContentColor.current,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    AppText(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

/**
 * 标签文本组件，使用应用的标签样式
 */
@Composable
fun LabelText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    color: Color = LocalContentColor.current,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    AppText(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

/**
 * 金额文本组件，使用应用的金额样式
 */
@Composable
fun AmountText(
    amount: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    color: Color = LocalContentColor.current,
    currency: String = "",
    prefixCurrency: Boolean = true,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    val formattedText = if (currency.isNotEmpty()) {
        if (prefixCurrency) "$currency $amount" else "$amount $currency"
    } else {
        amount
    }
    
    AppText(
        text = formattedText,
        modifier = modifier,
        style = style.copy(
            fontWeight = FontWeight.Bold
        ),
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

/**
 * 错误文本组件，显示错误信息
 */
@Composable
fun ErrorText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    color: Color = MaterialTheme.colorScheme.error,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    AppText(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}

/**
 * 提示文本组件，显示辅助信息
 */
@Composable
fun HintText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    AppText(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines
    )
}