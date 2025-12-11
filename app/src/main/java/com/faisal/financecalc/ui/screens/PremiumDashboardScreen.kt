package com.faisal.financecalc.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faisal.financecalc.data.CreditCard
import com.faisal.financecalc.data.EntryType
import com.faisal.financecalc.viewmodel.MainViewModel
import com.faisal.financecalc.ui.theme.LocalAppStrings

@Composable
fun PremiumDashboardScreen(
    viewModel: MainViewModel,
    onNavigateToIncome: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToCards: () -> Unit, // Reusing this for generic Card nav if needed, but managing cards here now.
    onNavigateToAnalytics: () -> Unit
) {
    val balance by viewModel.balance.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val allEntries by viewModel.allEntries.collectAsState()
    val allCards by viewModel.allCreditCards.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    
    val strings = LocalAppStrings.current
    
    var showAddCardDialog by remember { mutableStateOf(false) }

    // Recent transactions (last 5)
    val recentTransactions = remember(allEntries) {
        allEntries.take(5)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        // FAB removed as per user request
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Welcome back,",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Faisal",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Main Balance Card
            item {
                MainBalanceCard(
                    balance = balance,
                    currencySymbol = currencySymbol,
                    income = totalIncome,
                    expense = totalExpense,
                    onAdd = onNavigateToIncome,
                    onSend = onNavigateToExpenses
                )
            }

            // MY CARDS Section (Dynamic)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Cards",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = { showAddCardDialog = true }) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = "Add Card", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allCards) { card ->
                        DynamicCreditCardItem(
                            card = card,
                            onDelete = { viewModel.deleteCreditCard(card) }
                        )
                    }
                    
                    // Add Card Button in List
                    item {
                        AddCardButton {
                             showAddCardDialog = true
                        }
                    }
                }
            }

            // Recent Transactions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onNavigateToIncome) {
                        Text("See all")
                    }
                }
            }

            items(recentTransactions) { entry ->
                TransactionRow(
                    title = entry.name,
                    subtitle = entry.category,
                    amount = entry.amount,
                    isIncome = entry.type == EntryType.INCOME,
                    currencySymbol = currencySymbol
                )
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
    
    if (showAddCardDialog) {
        AddCardDialog(
            onDismiss = { showAddCardDialog = false },
            onConfirm = { holder, number, expiry, colorIdx ->
                viewModel.addCreditCard(
                    CreditCard(
                        holderName = holder,
                        cardNumber = number,
                        expiryDate = expiry,
                        balance = 0.0,
                        cardType = "VISA",
                        colorTheme = colorIdx
                    )
                )
                showAddCardDialog = false
            }
        )
    }
}

@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int) -> Unit
) {
    var holderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(0) } // 0=Blue, 1=Green, 2=Purple, 3=Gold

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Card") },
        text = {
            Column {
                OutlinedTextField(
                    value = holderName,
                    onValueChange = { holderName = it.uppercase() },
                    label = { Text("Card Holder Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 16) cardNumber = it },
                    label = { Text("Card Number (Last 4 Digits)") }, // Simplification
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry (MM/YY)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Select Style:")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val colors = listOf(Color(0xFF1565C0), Color(0xFF2E7D32), Color(0xFF6A1B9A), Color(0xFFD4AF37))
                    colors.forEachIndexed { index, color ->
                         Box(
                             modifier = Modifier
                                 .size(40.dp)
                                 .clip(CircleShape)
                                 .background(color)
                                 .clickable { selectedColor = index }
                                 .then(if (selectedColor == index) Modifier.background(Color.Transparent) else Modifier /*border?*/) 
                         ) {
                             if (selectedColor == index) {
                                 Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.align(Alignment.Center))
                             }
                         }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (holderName.isNotEmpty()) {
                    onConfirm(holderName, cardNumber, expiryDate, selectedColor)
                }
            }) {
                Text("Add Card")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddCardButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(60.dp)
            .height(170.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                 Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                 Text("Add", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun DynamicCreditCardItem(
    card: CreditCard,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Improved Gradients
    val brush = when(card.colorTheme) {
        0 -> Brush.linearGradient( // Blue
            colors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF42A5F5)),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
        1 -> Brush.linearGradient( // Emerald
            colors = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF4CAF50)),
             start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
        2 -> Brush.linearGradient( // Purple
            colors = listOf(Color(0xFF4A148C), Color(0xFF7B1FA2), Color(0xFFBA68C8)),
             start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
        3 -> Brush.linearGradient( // Gold
            colors = listOf(Color(0xFFFF6F00), Color(0xFFFF8F00), Color(0xFFFFCA28)),
             start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
        else -> Brush.linearGradient( // Black/Dark
            colors = listOf(Color(0xFF212121), Color(0xFF424242), Color(0xFF616161)),
             start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Card?") },
            text = { Text("Are you sure you want to remove this card?") },
            confirmButton = { TextButton(onClick = onDelete) { Text("Delete", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    Card(
        modifier = Modifier
            .width(300.dp) // Slightly wider
            .height(190.dp) // Real aspect ratio roughly
            .clickable { showDeleteDialog = true },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        ) {
            // Metallic Shine / Noise Overlay (Simulated with Gradient)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.1f), Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(150f, 50f),
                            radius = 400f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Row 1: Bank Name / Logo (Placeholder text for now) & Contactless Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "FinanceCalc Bank", // Or user's bank name if we had it
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    // Contactless Symbol (Simulated)
                    Icon(
                        Icons.Default.Wifi, 
                        contentDescription = "Contactless", 
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.rotate(90f).size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: Chip
                EmvChip()

                Spacer(modifier = Modifier.height(10.dp))

                // Row 3: Card Number
                // Add spacing for groups of 4: "1234 5678 1234 5678"
                val formattedNumber = card.cardNumber.chunked(4).joinToString("   ")
                Text(
                    text = if(card.cardNumber.length > 4) formattedNumber else "****  ****  ****  ${card.cardNumber}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // Row 4: Details & Master/Visa Logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            "CARD HOLDER", 
                            style = MaterialTheme.typography.labelSmall, 
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 8.sp
                        )
                        Text(
                            card.holderName, 
                            style = MaterialTheme.typography.bodyMedium, 
                            color = Color.White, 
                            fontWeight = FontWeight.Medium,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "EXPIRES", 
                            style = MaterialTheme.typography.labelSmall, 
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 8.sp
                        )
                        Text(
                            card.expiryDate, 
                            style = MaterialTheme.typography.bodyMedium, 
                            color = Color.White, 
                            fontWeight = FontWeight.Medium,
                             fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    // VISA Logo Placeholder (Text styled)
                    Text(
                        card.cardType.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White.copy(alpha = 0.9f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                         modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmvChip() {
    Box(
        modifier = Modifier
            .size(45.dp, 34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFD54F), Color(0xFFFFB300), Color(0xFFFFE082))
                )
            )
            .border(1.dp, Color(0xFF795548), RoundedCornerShape(6.dp))
    ) {
         // Chip Lines (Simple)
         Row(modifier = Modifier.fillMaxSize()) {
             Spacer(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.Black.copy(alpha=0.2f)))
             Spacer(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.Black.copy(alpha=0.2f)))
             Spacer(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.Black.copy(alpha=0.2f)))
         }
         Column(modifier = Modifier.fillMaxSize()) {
             Spacer(modifier = Modifier.weight(1f).fillMaxWidth().border(0.5.dp, Color.Black.copy(alpha=0.2f)))
             Spacer(modifier = Modifier.weight(1f).fillMaxWidth().border(0.5.dp, Color.Black.copy(alpha=0.2f)))
         }
    }
}

// ... MainBalanceCard and TransactionRow remain same as previous step
@Composable
fun MainBalanceCard(
    balance: Double,
    currencySymbol: String,
    income: Double,
    expense: Double,
    onAdd: () -> Unit,
    onSend: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary, // Navy
                            MaterialTheme.colorScheme.secondary // Gold/Accent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%.2f", balance)} $currencySymbol",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Stats Row
                    Column(modifier = Modifier.weight(1f)) {
                         Row(verticalAlignment = Alignment.CenterVertically) {
                             Icon(Icons.Default.ArrowCircleUp, null, tint = Color(0xFF69F0AE), modifier = Modifier.size(16.dp))
                             Spacer(modifier = Modifier.width(4.dp))
                             Text(
                                 text = "Income",
                                 style = MaterialTheme.typography.labelSmall,
                                 color = Color.White.copy(alpha=0.7f)
                             )
                         }
                         Text(
                             text = "+${String.format("%.0f", income)}",
                             style = MaterialTheme.typography.bodyLarge,
                             fontWeight = FontWeight.SemiBold,
                             color = Color.White
                         )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                         Row(verticalAlignment = Alignment.CenterVertically) {
                             Icon(Icons.Default.ArrowCircleDown, null, tint = Color(0xFFFF8A80), modifier = Modifier.size(16.dp))
                             Spacer(modifier = Modifier.width(4.dp))
                             Text(
                                 text = "Expense",
                                 style = MaterialTheme.typography.labelSmall,
                                 color = Color.White.copy(alpha=0.7f)
                             )
                         }
                         Text(
                             text = "-${String.format("%.0f", expense)}",
                             style = MaterialTheme.typography.bodyLarge,
                             fontWeight = FontWeight.SemiBold,
                             color = Color.White
                         )
                    }
                }
            }
        }
    }
}


@Composable
fun TransactionRow(
    title: String,
    subtitle: String,
    amount: Double,
    isIncome: Boolean,
    currencySymbol: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
        
        Text(
            text = "${if (isIncome) "+" else "-"}${String.format("%.2f", amount)} $currencySymbol",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isIncome) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
        )
    }
}
