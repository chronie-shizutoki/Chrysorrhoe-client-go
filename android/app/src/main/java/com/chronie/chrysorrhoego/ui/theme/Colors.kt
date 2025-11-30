package com.chronie.chrysorrhoego.ui.theme

import androidx.compose.ui.graphics.Color

// 主色调 - 蓝色系列
val PrimaryBlue = Color(0xFF1976D2)       // 主蓝色
val OnPrimaryWhite = Color(0xFFFFFFFF)    // 主色调上的文字 - 白色
val PrimaryContainer = Color(0xFFE3F2FD)  // 主色调容器背景
val OnPrimaryContainer = Color(0xFF001E3C) // 主色调容器上的文字
val PrimaryContainerDark = Color(0xFF001E3C) // 深色模式下主色调容器

// 次要色调 - 浅蓝色系列
val SecondaryBlue = Color(0xFF2196F3)     // 次蓝色
val OnSecondaryWhite = Color(0xFFFFFFFF)  // 次要色调上的文字 - 白色
val SecondaryContainer = Color(0xFFE3F2FD) // 次要色调容器背景
val OnSecondaryContainer = Color(0xFF001E3C) // 次要色调容器上的文字
val SecondaryContainerDark = Color(0xFF001E3C) // 深色模式下次要色调容器

// 强调色 - 紫色系列
val AccentPurple = Color(0xFF9C27B0)      // 强调紫色
val OnTertiaryWhite = Color(0xFFFFFFFF)   // 强调色上的文字 - 白色

// 背景色系列
val BackgroundWhite = Color(0xFFFFFFFF)   // 浅色背景
val OnBackgroundDark = Color(0xFF121212)  // 浅色背景上的文字
val BackgroundDark = Color(0xFF121212)    // 深色背景
val OnBackgroundLight = Color(0xFFF5F5F5) // 深色背景上的文字

// 表面色系列
val SurfaceWhite = Color(0xFFFFFFFF)      // 浅色表面
val OnSurfaceDark = Color(0xFF121212)     // 浅色表面上的文字
val SurfaceDark = Color(0xFF1E1E1E)       // 深色表面
val OnSurfaceLight = Color(0xFFF5F5F5)    // 深色表面上的文字
val SurfaceVariantGray = Color(0xFFF5F5F5) // 变体表面 - 浅灰色
val OnSurfaceVariantGray = Color(0xFF757575) // 变体表面上的文字 - 灰色
val SurfaceVariantDark = Color(0xFF2C2C2C) // 深色模式下变体表面
val OnSurfaceVariantDark = Color(0xFFBDBDBD) // 深色模式下变体表面上的文字

// 错误色系列
val ErrorRed = Color(0xFFD32F2F)          // 错误红色
val OnErrorWhite = Color(0xFFFFFFFF)      // 错误色上的文字 - 白色
val ErrorContainer = Color(0xFFFFEBEE)    // 错误容器背景
val OnErrorContainer = Color(0xFFB71C1C)  // 错误容器上的文字
val ErrorContainerDark = Color(0xFFB71C1C) // 深色模式下错误容器

// 边框和分割线颜色
val OutlineGray = Color(0xFFBDBDBD)       // 边框灰色
val OutlineDark = Color(0xFF424242)       // 深色模式下边框颜色

// 特殊用途颜色
val SuccessGreen = Color(0xFF4CAF50)      // 成功绿色
val WarningYellow = Color(0xFFFFC107)     // 警告黄色
val InfoBlue = Color(0xFF2196F3)          // 信息蓝色

// 交易类型颜色
val IncomeColor = Color(0xFF4CAF50)       // 收入颜色
val ExpenseColor = Color(0xFFF44336)      // 支出颜色

// 透明度变体
val PrimaryBlueTransparent = PrimaryBlue.copy(alpha = 0.7f)
val SecondaryBlueTransparent = SecondaryBlue.copy(alpha = 0.7f)
val BlackTransparent = Color.Black.copy(alpha = 0.5f)
val WhiteTransparent = Color.White.copy(alpha = 0.5f)