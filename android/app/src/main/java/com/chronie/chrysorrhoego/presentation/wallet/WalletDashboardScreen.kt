package com.chronie.chrysorrhoego.presentation.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chronie.chrysorrhoego.R
import com.chronie.chrysorrhoego.ui.theme.*
import com.chronie.chrysorrhoego.ui.wallet.WalletViewModel
import kotlinx.coroutines.delay

@Composable
fun WalletDashboardScreen(viewModel: WalletViewModel) {
    // 从ViewModel收集状态
    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val transferSuccess by viewModel.transferSuccess.collectAsStateWithLifecycle()
    
    // 本地UI状态
    var isTransferFormVisible by remember { mutableStateOf(false) }
    var recipientUsername by remember { mutableStateOf(TextFieldValue("")) }
    var transferAmount by remember { mutableStateOf(TextFieldValue("")) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    
    // 处理转账成功消息的显示和隐藏
    LaunchedEffect(transferSuccess) {
        if (transferSuccess) {
            showSuccessMessage = true
            // 3秒后隐藏成功消息
            delay(3000L)
            showSuccessMessage = false
            // 重置表单状态
            recipientUsername = TextFieldValue("")
            transferAmount = TextFieldValue("")
            isTransferFormVisible = false
        }
    }
    
    Scaffold(
        topBar = { WalletDashboardTopBar() },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 钱包信息卡片
                WalletInfoCard(wallet = wallet)
                
                // 主要操作按钮
                MainActionsButtons(
                    onTransferClick = { isTransferFormVisible = true },
                    onCdkExchangeClick = {},
                    onTransactionHistoryClick = {}
                )
                
                // 辅助操作按钮
                SecondaryActionsButtons(
                    onRefreshClick = { viewModel.refreshData() },
                    onLogoutClick = {}
                )
                
                // 转账表单
                AnimatedVisibility(
                    visible = isTransferFormVisible,
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    TransferForm(
                        recipientUsername = recipientUsername,
                        transferAmount = transferAmount,
                        onRecipientChanged = { recipientUsername = it },
                        onAmountChanged = { transferAmount = it },
                        onSubmitClick = {
                            // 验证输入并提交转账
                            val recipient = recipientUsername.text.trim()
                            val amount = transferAmount.text.toDoubleOrNull()
                            
                            if (recipient.isEmpty()) {
                                viewModel.errorMessage = "Recipient cannot be empty"
                            } else if (amount == null || amount <= 0) {
                                viewModel.errorMessage = "Amount must be greater than 0"
                            } else {
                                viewModel.transfer(recipient, amount)
                            }
                        },
                        onCancelClick = {
                            isTransferFormVisible = false
                            recipientUsername = TextFieldValue("")
                            transferAmount = TextFieldValue("")
                            viewModel.clearError()
                        }
                    )
                }
                
                // 错误消息
                if (error != null) {
                    ErrorMessage(error = error!!)
                }
                
                // 成功消息
                AnimatedVisibility(visible = showSuccessMessage) {
                    SuccessMessage()
                }
                
                // 加载指示器
                if (isLoading) {
                    LoadingIndicator()
                }
            }
        }
    )
}

@Composable
private fun WalletDashboardTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Wallet Dashboard",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = primaryColor,
            titleContentColor = onPrimaryColor
        )
    )
}

@Composable
private fun WalletInfoCard(wallet: WalletViewModel.Wallet?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 钱包图标
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(primaryColor, CircleShape)
                    .padding(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_camera_black_24dp),
                    contentDescription = "Wallet Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 用户名
            Text(
                text = wallet?.username ?: "Guest",
                fontSize = 18.sp,
                color = onBackgroundColor,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 余额
            Text(
                text = "$${wallet?.balance ?: "0.00"}",
                fontSize = 32.sp,
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MainActionsButtons(
    onTransferClick: () -> Unit,
    onCdkExchangeClick: () -> Unit,
    onTransactionHistoryClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onTransferClick,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text(text = "Transfer")
            }
            
            OutlinedButton(
                onClick = onCdkExchangeClick,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                border = BorderStroke(1.dp, primaryColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
            ) {
                Text(text = "CDK Exchange")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = onTransactionHistoryClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Transaction History", color = primaryColor)
        }
    }
}

@Composable
private fun SecondaryActionsButtons(
    onRefreshClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = onRefreshClick,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        ) {
            Text(text = "Refresh", fontSize = 14.sp)
        }
        
        TextButton(
            onClick = onLogoutClick,
            modifier = Modifier.weight(1f).padding(start = 8.dp)
        ) {
            Text(text = "Logout", fontSize = 14.sp)
        }
    }
}

@Composable
private fun TransferForm(
    recipientUsername: TextFieldValue,
    transferAmount: TextFieldValue,
    onRecipientChanged: (TextFieldValue) -> Unit,
    onAmountChanged: (TextFieldValue) -> Unit,
    onSubmitClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Transfer Funds",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            TextField(
                value = recipientUsername,
                onValueChange = onRecipientChanged,
                label = { Text("Recipient Username") },
                placeholder = { Text("Enter recipient's username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )
            
            TextField(
                value = transferAmount,
                onValueChange = onAmountChanged,
                label = { Text("Amount") },
                placeholder = { Text("Enter amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier.padding(end = 8.dp),
                    border = BorderStroke(1.dp, primaryColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
                ) {
                    Text(text = "Cancel")
                }
                
                Button(
                    onClick = onSubmitClick,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text(text = "Confirm Transfer")
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage(error: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = errorColor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = error,
            color = errorColor,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun SuccessMessage() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = successColor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "Transfer successful!",
            color = successColor,
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = primaryColor,
            strokeWidth = 4.dp
        )
    }
}

