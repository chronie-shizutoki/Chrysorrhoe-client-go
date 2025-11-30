package com.chronie.chrysorrhoego.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// 应用的形状定义
val Shapes = Shapes(
    // 小部件，如Button、Chip
    small = RoundedCornerShape(8.dp),
    
    // 中等大小部件，如卡片、对话框
    medium = RoundedCornerShape(12.dp),
    
    // 大部件，如弹窗、底部表单
    large = RoundedCornerShape(16.dp),
    
    // 超大部件，如主屏幕面板
    extraLarge = RoundedCornerShape(24.dp)
)

// 自定义形状扩展
fun Shapes.cardShape() = medium
fun Shapes.buttonShape() = small
fun Shapes.chipShape() = RoundedCornerShape(16.dp)
fun Shapes.dialogShape() = large
fun Shapes.sheetShape() = large

// 特殊形状
val CircularShape = RoundedCornerShape(50)
val BottomSheetShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp
)
val CardShape = RoundedCornerShape(12.dp)
val ButtonShape = RoundedCornerShape(8.dp)
val TextFieldShape = RoundedCornerShape(12.dp)
val ProgressIndicatorShape = RoundedCornerShape(50)
val AvatarShape = RoundedCornerShape(50)
val BadgeShape = RoundedCornerShape(12.dp)
val FloatingActionButtonShape = RoundedCornerShape(12.dp)

// 形状扩展函数
fun CornerBasedShape.topOnly(size: CornerSize = CornerSize(16.dp)) = copy(
    bottomStart = CornerSize(0.dp),
    bottomEnd = CornerSize(0.dp),
    topStart = size,
    topEnd = size
)

fun CornerBasedShape.bottomOnly(size: CornerSize = CornerSize(16.dp)) = copy(
    topStart = CornerSize(0.dp),
    topEnd = CornerSize(0.dp),
    bottomStart = size,
    bottomEnd = size
)

fun CornerBasedShape.startOnly(size: CornerSize = CornerSize(16.dp)) = copy(
    topStart = size,
    bottomStart = size,
    topEnd = CornerSize(0.dp),
    bottomEnd = CornerSize(0.dp)
)

fun CornerBasedShape.endOnly(size: CornerSize = CornerSize(16.dp)) = copy(
    topEnd = size,
    bottomEnd = size,
    topStart = CornerSize(0.dp),
    bottomStart = CornerSize(0.dp)
)