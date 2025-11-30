package com.chronie.chrysorrhoego.ui.component.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.chronie.chrysorrhoe-client-lite.ui.component.text.BodyText
import com.chronie.chrysorrhoe-client-lite.ui.component.text.TitleText

/**
 * 统一的对话框组件，提供自定义标题、内容和操作按钮
 */
@Composable
fun AppDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    text: String? = null,
    confirmButtonText: String = "OK",
    dismissButtonText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
    dismissButtonColor: Color = MaterialTheme.colorScheme.secondary,
    icon: @Composable (() -> Unit)? = null,
    show: Boolean,
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = if (title != null) { { TitleText(text = title) } } else null,
            text = if (text != null) { { BodyText(text = text) } } else null,
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = confirmButtonColor
                    )
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = dismissButtonText?.let {
                { TextButton(onClick = onDismiss) { Text(it) } }
            },
            icon = icon,
            modifier = modifier
        )
    }
}

/**
 * 确认对话框组件
 */
@Composable
fun ConfirmDialog(
    modifier: Modifier = Modifier,
    title: String = "Confirm",
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    confirmColor: Color = MaterialTheme.colorScheme.error,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    show: Boolean,
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onCancel,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = confirmColor
                    )
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                TextButton(onClick = onCancel) {
                    Text(cancelText)
                }
            },
            modifier = modifier
        )
    }
}

/**
 * 自定义下拉菜单组件
 */
@Composable
fun AppDropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<String>,
    onItemClick: (String) -> Unit,
    disabledItems: Set<String> = emptySet(),
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    disabledContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .background(backgroundColor, shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = shape
            ),
        properties = PopupProperties(focusable = true)
    ) {
        items.forEach { item ->
            val isDisabled = disabledItems.contains(item)
            DropdownMenuItem(
                text = { Text(item, color = if (isDisabled) disabledContentColor else contentColor) },
                onClick = { if (!isDisabled) onItemClick(item) },
                enabled = !isDisabled
            )
        }
    }
}

/**
 * 简单的弹出菜单按钮
 */
@Composable
fun DropdownMenuButton(
    modifier: Modifier = Modifier,
    label: String,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    selectedItem: String? = null,
    disabled: Boolean = false,
    disabledItems: Set<String> = emptySet(),
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = selectedItem ?: label
    
    Box(modifier = modifier) {
        Button(
            onClick = { expanded = !expanded },
            enabled = !disabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(displayText)
            Spacer(modifier = Modifier.width(4.dp))
            Text("▼")
        }
        
        AppDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            items = items,
            onItemClick = {
                expanded = false
                onItemSelected(it)
            },
            disabledItems = disabledItems,
            shape = shape,
            backgroundColor = backgroundColor
        )
    }
}

/**
 * 自定义弹出框组件
 */
@Composable
fun CustomPopup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    alignment: Alignment = Alignment.Center,
    properties: PopupProperties = PopupProperties(focusable = true),
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f),
    contentBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentPadding: Dp = 16.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 8.dp,
) {
    Popup(
        onDismissRequest = onDismissRequest,
        alignment = alignment,
        properties = properties
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(backgroundColor)
                .clickable(onClick = onDismissRequest),
            contentAlignment = alignment
        ) {
            Card(
                modifier = Modifier
                    .padding(contentPadding)
                    .clickable { /* Prevent click from closing the popup */ },
                colors = CardDefaults.cardColors(
                    containerColor = contentBackgroundColor
                ),
                shape = shape,
                elevation = CardDefaults.cardElevation(defaultElevation = elevation)
            ) {
                content()
            }
        }
    }
}

/**
 * 底部弹出菜单组件
 */
@Composable
fun BottomSheetMenu(
    modifier: Modifier = Modifier,
    show: Boolean,
    onDismiss: () -> Unit,
    title: String? = null,
    items: List<String>,
    onItemClick: (String) -> Unit,
    cancelText: String = "Cancel",
    onCancel: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 8.dp,
    cornerRadius: Dp = 16.dp,
) {
    if (show) {
        CustomPopup(
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (title != null) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    
                    items.forEachIndexed { index, item ->
                        Button(
                            onClick = { 
                                onItemClick(item) 
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { 
                            onDismiss() 
                            onCancel?.invoke()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            text = cancelText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            onDismissRequest = onDismiss,
            alignment = Alignment.BottomCenter,
            contentBackgroundColor = backgroundColor,
            contentPadding = 0.dp,
            shape = RoundedCornerShape(
                topStart = cornerRadius,
                topEnd = cornerRadius,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            elevation = elevation
        )
    }
}

/**
 * 过滤菜单组件
 */
@Composable
fun FilterMenu(
    modifier: Modifier = Modifier,
    show: Boolean,
    onDismiss: () -> Unit,
    filters: List<Pair<String, List<String>>>,
    selectedFilters: Map<String, String>,
    onFilterChange: (String, String) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
) {
    if (show) {
        CustomPopup(
            content = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Filters",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    filters.forEach { (filterName, filterOptions) ->
                        Text(
                            text = filterName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                        
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            filterOptions.forEach { option ->
                                val isSelected = selectedFilters[filterName] == option
                                OutlinedButton(
                                    onClick = { onFilterChange(filterName, option) },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder,
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(option)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = onReset,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reset")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                onApply()
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Apply")
                        }
                    }
                }
            },
            onDismissRequest = onDismiss,
            alignment = Alignment.Center,
            contentPadding = 0.dp,
            elevation = 16.dp
        )
    }
}

/**
 * 选项对话框组件
 */
@Composable
fun OptionsDialog(
    modifier: Modifier = Modifier,
    title: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    onCancel: () -> Unit,
    show: Boolean,
    cancelable: Boolean = true,
) {
    if (show) {
        AlertDialog(
            onDismissRequest = { if (cancelable) onCancel() },
            title = { Text(title) },
            text = {
                Column {
                    options.forEach { option ->
                        Button(
                            onClick = { onOptionSelected(option) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(option)
                        }
                    }
                }
            },
            confirmButton = {
                if (cancelable) {
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                }
            },
            modifier = modifier
        )
    }
}

/**
 * 提示消息组件
 */
@Composable
fun ToastMessage(
    modifier: Modifier = Modifier,
    message: String,
    show: Boolean,
    duration: Long = 3000,
    backgroundColor: Color = Color.Black.copy(alpha = 0.7f),
    textColor: Color = Color.White,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    if (show) {
        CustomPopup(
            content = {
                Box(
                    modifier = Modifier
                        .background(backgroundColor, RoundedCornerShape(24.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(message, color = textColor, style = textStyle)
                }
            },
            onDismissRequest = {},
            alignment = Alignment.BottomCenter,
            properties = PopupProperties(focusable = false),
            backgroundColor = Color.Transparent,
            contentPadding = 16.dp,
            shape = RectangleShape,
            elevation = 0.dp
        )
    }
}