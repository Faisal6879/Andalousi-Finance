package com.faisal.financecalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.draw.rotate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faisal.financecalc.data.FinanceEntry
import com.faisal.financecalc.data.ShopItem

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    icon: ImageVector? = null,
    currencySymbol: String = "€"
) {
    // Premium Gradient Logic
    val brush = if (backgroundColor == MaterialTheme.colorScheme.primaryContainer) {
        androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(
                Color(0xFF2563EB), // Primary Blue
                Color(0xFF1E40AF)  // Darker Blue
            )
        )
    } else {
        androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(
                backgroundColor,
                backgroundColor.copy(alpha = 0.9f)
            )
        )
    }
    
    val contentColor = if (backgroundColor == MaterialTheme.colorScheme.primaryContainer) 
        MaterialTheme.colorScheme.onPrimary 
    else 
        MaterialTheme.colorScheme.onSurface

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp), // Increased height
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        ) {
            // Watermark Icon
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = 0.1f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(90.dp)
                        .offset(x = 20.dp, y = 20.dp)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(contentColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.9f),
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = "${String.format("%.2f", amount)} $currencySymbol",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha=0.1f),
                            offset = androidx.compose.ui.geometry.Offset(0f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    fontWeight = FontWeight.ExtraBold,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun EntryRow(
    entry: FinanceEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    currencySymbol: String = "€"
) {
    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 2.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Placeholder based on entry type
            val isPositive = entry.type == com.faisal.financecalc.data.EntryType.INCOME || entry.type == com.faisal.financecalc.data.EntryType.DEBT
            val iconColor = if(isPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(alpha=0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                 Icon(
                     if(isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                     contentDescription = null,
                     tint = iconColor,
                     modifier = Modifier.size(24.dp)
                 )
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.name, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.category + if(entry.isAutoCalculated) " ${strings.auto}" else "", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (entry.subEntries.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    entry.subEntries.forEach { sub ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "• ${sub.name}: ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${if (sub.amount % 1.0 == 0.0) sub.amount.toInt().toString() else sub.amount.toString()} $currencySymbol",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = "${if (entry.amount % 1.0 == 0.0) entry.amount.toInt().toString() else entry.amount.toString()} $currencySymbol",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (entry.excludedFromTotal) Color(0xFFFFC107) else iconColor 
                )
                if (entry.excludedFromTotal) {
                    Text(
                        "(Excluded)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFC107)
                    )
                }
            }
            
            if (!entry.isAutoCalculated) {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = strings.editEntry, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = MaterialTheme.colorScheme.error.copy(alpha=0.8f))
                    }
                }
            }
        }
    }
}


@Composable
fun ShopItemRow(
    item: ShopItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    currencySymbol: String = "€"
) {
    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 2.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Use surface for clean look
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Placeholder (Optional, strictly visual)
             Box(
                 modifier = Modifier
                     .size(48.dp)
                     .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha=0.4f), RoundedCornerShape(12.dp)),
                 contentAlignment = Alignment.Center
             ) {
                 Text(
                     text = item.name.take(1).uppercase(),
                     style = MaterialTheme.typography.titleMedium,
                     fontWeight = FontWeight.Bold,
                     color = MaterialTheme.colorScheme.onSecondaryContainer
                 )
             }
             
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.count} ${strings.itemsCount} • ${strings.buyPrice}: ${String.format("%.2f", item.purchasePrice)} $currencySymbol",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Value Column
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = "${String.format("%.2f", item.count * item.purchasePrice)} $currencySymbol",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Actions
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = strings.editEntry, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = MaterialTheme.colorScheme.error.copy(alpha=0.8f))
                }
            }
        }
    }
}

@Composable
fun PremiumCreditCard(
    holderName: String,
    cardNumber: String = "**** **** **** 1234",
    balance: Double,
    modifier: Modifier = Modifier,
    startColor: Color = MaterialTheme.colorScheme.primary,
    endColor: Color = MaterialTheme.colorScheme.secondary,
    currencySymbol: String = "€"
) {
    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current
    // Metallic/Gradient Look (Gold/Bronze)
    val brush = androidx.compose.ui.graphics.Brush.linearGradient(
        colors = listOf(
            startColor,
            endColor
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1000f, 1000f) // Diagonal
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp) // Maintain standard credit card aspect ratio height
            .padding(8.dp), // Reduced padding for better list fit
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
                .padding(24.dp)
        ) {
            // Background Pattern/Texture (Optional subtle noise can be added here)
            
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Chip and Bank Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chip
                    Box(
                        modifier = Modifier
                            .size(50.dp, 36.dp)
                            .background(
                                color = Color(0xFFFFD700).copy(alpha=0.8f), 
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(1.dp, Color.Black.copy(alpha=0.1f), RoundedCornerShape(6.dp))
                    )
                    
                    // Contactless Icon or Bank Name
                    Icon(
                        androidx.compose.material.icons.Icons.Default.Wifi,
                        contentDescription = null,
                        tint = Color.White.copy(alpha=0.7f),
                        modifier = Modifier.rotate(90f).size(28.dp)
                    )
                }

                // Middle: Card Number (The "Other Number")
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cardNumber,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                        maxLines = 1
                    )
                }

                // Bottom Row: Name and Valid Thru
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CARD HOLDER",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 8.sp // Micro text
                        )
                        Text(
                            text = holderName.uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "VALID THRU",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 8.sp
                        )
                        Text(
                             text = "12/28", // Placeholder or pass as param if available
                             style = MaterialTheme.typography.bodyMedium,
                             color = Color.White,
                             fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Helper to disable conflicting previews or unused code


@Composable
fun FinanceInputLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Color(0xFF94A3B8), // Slate 400
        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
    )
}

@Composable
fun FinanceInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    enabled: Boolean = true,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled, // When false, it will be handled by custom styling or alpha below if needed
        readOnly = readOnly,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = if(enabled) Color.White else Color.White.copy(alpha=0.5f)),
        keyboardOptions = keyboardOptions,
        singleLine = true,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color(0xFF0B1220), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(placeholder, color = Color(0xFF475569)) // Slate 600
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                         innerTextField()
                    }
                    if (trailingIcon != null) {
                        trailingIcon()
                    }
                }
            }
        }
    )
}
