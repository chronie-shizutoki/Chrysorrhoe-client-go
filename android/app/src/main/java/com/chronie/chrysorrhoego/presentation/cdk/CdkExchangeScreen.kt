package com.chronie.chrysorrhoego.presentation.cdk

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chronie.chrysorrhoego.ui.cdk.CdkViewModel

// 屏幕类型枚举
enum class ScreenSize {
    Compact,
    Medium,
    Expanded
}

// 获取屏幕尺寸类型
@Composable
fun getWindowSizeClass(): ScreenSize {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    return when {
        screenWidth < 600 -> ScreenSize.Compact
        screenWidth < 840 -> ScreenSize.Medium
        else -> ScreenSize.Expanded
    }
}

// 根据屏幕尺寸获取合适的内边距
@Composable
fun getPaddingForScreenSize(): Dp {
    return when (getWindowSizeClass()) {
        ScreenSize.Compact -> 16.dp
        ScreenSize.Medium -> 24.dp
        ScreenSize.Expanded -> 32.dp
    }
}

// 根据屏幕尺寸获取卡片内边距
@Composable
fun getCardPaddingForScreenSize(): Dp {
    return when (getWindowSizeClass()) {
        ScreenSize.Compact -> 12.dp
        ScreenSize.Medium -> 16.dp
        ScreenSize.Expanded -> 20.dp
    }
}

// 根据屏幕尺寸获取字体大小
@Composable
fun getFontSizeForScreenSize(): TextUnit {
    return when (getWindowSizeClass()) {
        ScreenSize.Compact -> 16.sp
        ScreenSize.Medium -> 18.sp
        ScreenSize.Expanded -> 20.sp
    }
}

@Composable
fun CdkExchangeScreen(viewModel: CdkViewModel) {
    // 从ViewModel收集状态
    val exchangeResult by viewModel.exchangeResult.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val exchangeHistory by viewModel.exchangeHistory.collectAsStateWithLifecycle()
    
    // 获取响应式参数
    val screenPadding = getPaddingForScreenSize()
    val cardPadding = getCardPaddingForScreenSize()
    val screenSize = getWindowSizeClass()
    
    // 本地UI状态
    var cdkCode by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }
    
    // 当兑换结果改变时显示结果
    LaunchedEffect(exchangeResult) {
        if (exchangeResult.isNotEmpty()) {
            showResult = true
        }
    }
    
    // 当错误改变时处理
    LaunchedEffect(error) {
        if (error != null) {
            showResult = false
        }
    }
    
    Scaffold(
        topBar = { CdkExchangeTopBar(onBackClick = { /* 处理返回点击 */ }) },
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(it)
            ) {
                // 主要内容区域
                item {
                    // 根据屏幕尺寸决定布局
                    when (screenSize) {
                        ScreenSize.Expanded -> {
                            // 大屏幕使用两列布局
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(screenPadding)
                                    .heightIn(min = 600.dp)
                            ) {
                                // 左侧兑换表单
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 16.dp)
                                ) {
                                    buildExchangeForm(
                                        cdkCode = cdkCode,
                                        onCdkCodeChange = { cdkCode = it },
                                        onExchangeClick = { 
                                            viewModel.exchangeCdk(cdkCode)
                                            showResult = true
                                        },
                                        isLoading = isLoading,
                                        showResult = showResult,
                                        exchangeResult = exchangeResult,
                                        error = error,
                                        screenPadding = screenPadding
                                    )
                                }
                                
                                // 右侧历史记录
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp)
                                        .background(Color.White)
                                        .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                                ) {
                                    Text(
                                        text = "兑换历史",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(screenPadding)
                                            .padding(top = 16.dp)
                                    )
                                    
                                    if (exchangeHistory.isNotEmpty()) {
                                        Column(modifier = Modifier.fillMaxHeight()) {
                                            exchangeHistory.forEachIndexed { index, record ->
                                                CdkHistoryItem(record = record, cardPadding = cardPadding)
                                                if (index < exchangeHistory.size - 1) {
                                                    Divider(modifier = Modifier.padding(horizontal = screenPadding))
                                                }
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = "暂无兑换记录",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                                .align(Alignment.CenterHorizontally)
                                        )
                                    }
                                }
                            }
                        }
                        
                        // 小屏幕和中等屏幕使用单列布局
                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(screenPadding)
                            ) {
                                buildExchangeForm(
                                    cdkCode = cdkCode,
                                    onCdkCodeChange = { cdkCode = it },
                                    onExchangeClick = { 
                                        viewModel.exchangeCdk(cdkCode)
                                        showResult = true
                                    },
                                    isLoading = isLoading,
                                    showResult = showResult,
                                    exchangeResult = exchangeResult,
                                    error = error,
                                    screenPadding = screenPadding
                                )
                                
                                // 历史记录部分
                                CdkHistorySection(history = exchangeHistory, cardPadding = cardPadding)
                            }
                        }
                    }
                }
                
                // 历史记录部分
                item {
                    CdkHistorySection(history = exchangeHistory)
                }
            }
        }
    )
}

@Composable
private fun CdkExchangeTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = Color.White)
            }
        },
        title = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF4CAF50) // 使用应用主色调
        )
    )
}

// 构建兑换表单组件
@Composable
private fun buildExchangeForm(
    cdkCode: String,
    onCdkCodeChange: (String) -> Unit,
    onExchangeClick: () -> Unit,
    isLoading: Boolean,
    showResult: Boolean,
    exchangeResult: String,
    error: String?,
    screenPadding: Dp
) {
    // 标题
    Text(
        text = "CDK兑换",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 20.dp)
    )
    
    // CDK输入框
    TextField(
        value = cdkCode,
        onValueChange = onCdkCodeChange,
        label = { Text("请输入CDK码") },
        placeholder = { Text("输入有效的CDK码") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        singleLine = true
    )
    
    // 兑换按钮和加载指示器
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onExchangeClick,
            enabled = !isLoading && cdkCode.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text(text = "兑换")
        }
        
        AnimatedVisibility(visible = isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)
            )
        }
    }
    
    // 结果展示区域
    AnimatedVisibility(visible = showResult) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            
            // 成功结果
            if (exchangeResult.isNotEmpty() && error == null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = exchangeResult,
                        fontSize = 16.sp,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(screenPadding)
                    )
                }
            }
            
            // 错误信息
            error?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        color = Color(0xFFF44336),
                        modifier = Modifier.padding(screenPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun CdkHistorySection(history: List<CdkViewModel.CdkExchangeRecord>, cardPadding: Dp) {
    if (history.isEmpty()) {
        // 无历史记录状态
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "兑换历史",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "暂无兑换记录",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    } else {
        // 历史记录列表
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "兑换历史",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(top = 32.dp)
            )
            
            history.forEachIndexed { index, record ->
                CdkHistoryItem(record = record, cardPadding = cardPadding)
                if (index < history.size - 1) {
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun CdkHistoryItem(record: CdkViewModel.CdkExchangeRecord, cardPadding: Dp) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(cardPadding)) {
            // CDK码和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.code,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(isSuccessful = record.isSuccessful, status = record.status)
            }
            
            // 奖励
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "获得奖励",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = record.reward,
                    fontSize = 14.sp,
                    color = if (record.isSuccessful) Color(0xFF4CAF50) else Color.Gray
                )
            }
            
            // 时间
            Text(
                text = record.dateTime,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun StatusBadge(isSuccessful: Boolean, status: String) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSuccessful) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFFF44336).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 12.sp,
            color = if (isSuccessful) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
    }
}
