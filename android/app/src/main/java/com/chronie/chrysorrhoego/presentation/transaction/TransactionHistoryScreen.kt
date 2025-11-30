package com.chronie.chrysorrhoego.presentation.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import com.chronie.chrysorrhoego.ui.theme.*
import com.chronie.chrysorrhoego.ui.transaction.TransactionViewModel

// 响应式设计的辅助函数
@Composable
fun getScreenWidth(): Int {
    return LocalConfiguration.current.screenWidthDp
}

@Composable
fun isExpanded(): Boolean {
    return getScreenWidth() >= 600 // 简单的断点检测
}

@Composable
fun getPaddingForScreenSize(): Dp {
    return if (isExpanded()) 24.dp else 16.dp
}

@Composable
fun getCardPaddingForScreenSize(): Dp {
    return if (isExpanded()) 16.dp else 12.dp
}

@Composable
fun TransactionHistoryScreen(viewModel: TransactionViewModel) {
    // 从ViewModel收集状态
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    // 本地UI状态
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    
    // 响应式设计参数
    val screenPadding = getPaddingForScreenSize()
    val cardPadding = getCardPaddingForScreenSize()
    val windowSizeClass = getWindowSizeClass()
    
    Scaffold(
        topBar = { TransactionHistoryTopBar(onBackClick = { /* 处理返回点击 */ }, onRefreshClick = { viewModel.refreshTransactions() }) },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(it)
            ) {
                // 搜索栏
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.searchTransactions(it)
                    },
                    modifier = Modifier.padding(screenPadding)
                )
                
                // 筛选按钮组 - 响应式排列
                val isCompact = !isExpanded()
                if (isCompact) {
                    // 在小屏幕上垂直排列按钮
                    Column(
                        modifier = Modifier.padding(horizontal = screenPadding),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterButton(
                                    label = "全部",
                                    isSelected = selectedFilter == "ALL",
                                    onClick = { selectedFilter = "ALL"; viewModel.filterByType("ALL") }
                                )
                                FilterButton(
                                    label = "发送",
                                    isSelected = selectedFilter == "SEND",
                                    onClick = { selectedFilter = "SEND"; viewModel.filterByType("SEND") }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterButton(
                                    label = "接收",
                                    isSelected = selectedFilter == "RECEIVE",
                                    onClick = { selectedFilter = "RECEIVE"; viewModel.filterByType("RECEIVE") }
                                )
                                Button(
                                    onClick = {
                                        selectedFilter = "ALL"
                                        searchQuery = ""
                                        viewModel.resetFilters()
                                    },
                                    variant = ButtonDefaults.buttonVariant(),
                                    size = ButtonDefaults.buttonSize(36.dp)
                                ) {
                                    Text(text = "重置", fontSize = MaterialTheme.typography.bodySmall.fontSize)
                                }
                            }
                    }
                } else {
                    // 在中等和大屏幕上水平排列按钮
                    FilterButtons(
                        selectedFilter = selectedFilter,
                        onFilterSelected = {
                            selectedFilter = it
                            viewModel.filterByType(it)
                        },
                        onResetClick = {
                            selectedFilter = "ALL"
                            searchQuery = ""
                            viewModel.resetFilters()
                        }
                    )
                }
                
                // 错误消息
                AnimatedVisibility(visible = error != null) {
                    error?.let { errorMessage ->
                        ErrorMessage(message = errorMessage)
                    }
                }
                
                // 交易列表或空状态
                Box(modifier = Modifier.fillMaxSize()) {
                    if (isLoading) {
                        // 加载指示器
                        LoadingIndicator()
                    } else if (filteredTransactions.isEmpty()) {
                        // 空状态
                        EmptyState()
                    } else {
                        // 交易列表
                        TransactionList(
                            transactions = filteredTransactions,
                            isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded,
                            cardPadding = cardPadding
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun TransactionHistoryTopBar(onBackClick: () -> Unit, onRefreshClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "交易记录",
                color = onPrimaryColor,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = onPrimaryColor)
            }
        },
        actions = {
            IconButton(onClick = onRefreshClick) {
                Icon(Icons.Default.Refresh, contentDescription = "刷新", tint = onPrimaryColor)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = primaryColor,
            titleContentColor = onPrimaryColor
        )
    )
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("搜索交易记录") },
            placeholder = { Text("输入用户名、金额或描述") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
    }
}

@Composable
private fun FilterButtons(selectedFilter: String, onFilterSelected: (String) -> Unit, onResetClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterButton(
            label = "全部",
            isSelected = selectedFilter == "ALL",
            onClick = { onFilterSelected("ALL") }
        )
        FilterButton(
            label = "发送",
            isSelected = selectedFilter == "SEND",
            onClick = { onFilterSelected("SEND") }
        )
        FilterButton(
            label = "接收",
            isSelected = selectedFilter == "RECEIVE",
            onClick = { onFilterSelected("RECEIVE") }
        )
        Button(
            onClick = onResetClick,
            variant = ButtonDefaults.buttonVariant(),
            size = ButtonDefaults.buttonSize(36.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "重置", fontSize = MaterialTheme.typography.bodySmall.fontSize)
        }
    }
}

@Composable
private fun FilterButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) primaryColor else backgroundLightColor,
            contentColor = if (isSelected) onPrimaryColor else onBackgroundColor
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) onPrimaryColor else onBackgroundColor
        )
    }
}

@Composable
private fun TransactionList(transactions: List<TransactionViewModel.Transaction>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(transactions) {\ transaction ->
            TransactionItem(transaction = transaction)
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun TransactionItem(transaction: TransactionViewModel.Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 交易类型和金额
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (transaction.type == "SEND") "发送" else "接收",
                    style = MaterialTheme.typography.titleSmall,
                    color = onBackgroundColor,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = if (transaction.type == "SEND") "-${transaction.amount}" else "+${transaction.amount}",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (transaction.type == "SEND") errorColor else successColor,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            
            // 交易对手
            Text(
                text = if (transaction.type == "SEND") "至: ${transaction.recipient}" else "来自: ${transaction.sender}",
                style = MaterialTheme.typography.bodySmall,
                color = textSecondaryColor,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            // 交易描述
            transaction.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondaryColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            // 日期和状态
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondaryColor
                )
                TransactionStatusBadge(status = transaction.status)
            }
        }
    }
}

@Composable
private fun TransactionStatusBadge(status: String) {
    val backgroundColor = when (status) {
        "COMPLETED" -> successColor.copy(alpha = 0.1f)
        "PENDING" -> warningColor.copy(alpha = 0.1f)
        "FAILED" -> errorColor.copy(alpha = 0.1f)
        else -> backgroundLightColor
    }
    
    val textColor = when (status) {
        "COMPLETED" -> successColor
        "PENDING" -> warningColor
        "FAILED" -> errorColor
        else -> onBackgroundColor
    }
    
    val displayText = when (status) {
        "COMPLETED" -> "已完成"
        "PENDING" -> "处理中"
        "FAILED" -> "失败"
        else -> status
    }
    
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = errorColor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = errorColor,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator(
            color = primaryColor,
            strokeWidth = 4.dp
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "没有找到交易记录",
            style = MaterialTheme.typography.headlineSmall,
            color = textSecondaryColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "尝试调整筛选条件或搜索查询",
            style = MaterialTheme.typography.bodyMedium,
            color = textSecondaryColor
        )
    }
}
