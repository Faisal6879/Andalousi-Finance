package com.faisal.financecalc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.faisal.financecalc.data.ShopItem
import com.faisal.financecalc.ui.components.ShopItemRow
import com.faisal.financecalc.viewmodel.MainViewModel
import com.faisal.financecalc.ui.theme.LocalAppStrings
import com.faisal.financecalc.ui.theme.SuccessGreen

@Composable
fun ShopScreen(viewModel: MainViewModel) {
    val shopItems by viewModel.allShopItems.collectAsState()
    val shopTotal by viewModel.shopTotalVal.collectAsState()
    val monthlyProfits by viewModel.monthlyProfits.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val strings = LocalAppStrings.current
    
    var showAddDialog by remember { mutableStateOf(false) }
    var currentItem by remember { mutableStateOf<ShopItem?>(null) } // null = new, non-null = edit

    // Sell Dialog States
    var showSellDialog by remember { mutableStateOf(false) }
    var itemToSell by remember { mutableStateOf<ShopItem?>(null) }
    
    // History Details Dialog
    var showHistoryDetailDialog by remember { mutableStateOf(false) }
    var selectedMonthYear by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    // Group items by category
    val groupedItems = remember(shopItems) { shopItems.groupBy { it.category } }
    val existingCategories = remember(shopItems) { shopItems.map { it.category }.distinct().sorted() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                currentItem = null
                showAddDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(strings.shopOverview, style = MaterialTheme.typography.headlineMedium)
            
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    // Revenue Card -> Inventory Value
                    com.faisal.financecalc.ui.components.SummaryCard(
                        title = strings.inventoryValue,
                        amount = shopTotal ?: 0.0,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        icon = Icons.Default.TrendingUp,
                        currencySymbol = currencySymbol 
                    )
        
                    Spacer(modifier = Modifier.height(12.dp))
                }
        
                item {
                    Text(strings.inventoryItems, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                }
                
                // Display items grouped by category
                groupedItems.forEach { (category, items) ->
                    item {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                        Divider()
                    }
                    items(items) { item ->
                        com.faisal.financecalc.ui.components.ShopItemRow(
                            item = item,
                            onEdit = { 
                                currentItem = item
                                showAddDialog = true 
                            },
                            onDelete = { 
                                itemToSell = item
                                showSellDialog = true 
                            },
                            currencySymbol = currencySymbol
                        )
                    }
                }
            }
        }
    }

    // Dialogs...
    if (showAddDialog) {
        ShopItemDialog(
            item = currentItem,
            existingCategories = existingCategories,
            onDismiss = { showAddDialog = false },
            currencySymbol = currencySymbol,
            onConfirm = { name, count, price, purchasePrice, category ->
                // Price (Selling) is set to 0.0 here as it is not used anymore
                if (currentItem == null) {
                    viewModel.addShopItem(ShopItem(name = name, count = count, pricePerUnit = 0.0, purchasePrice = purchasePrice, category = category))
                } else {
                    viewModel.updateShopItem(currentItem!!.copy(name = name, count = count, pricePerUnit = 0.0, purchasePrice = purchasePrice, category = category))
                }
                showAddDialog = false
            }
        )
    }
    
    if (showSellDialog && itemToSell != null) {
        SellDialog(
            item = itemToSell!!,
            currencySymbol = currencySymbol,
            onDismiss = { showSellDialog = false },
            onDelete = {
                viewModel.deleteShopItem(itemToSell!!)
                showSellDialog = false
            },
            onConfirm = { price ->
                viewModel.sellItem(itemToSell!!, price)
                showSellDialog = false
            }
        )
    }
}

@Composable
fun SellDialog(
    item: ShopItem,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var sellPrice by remember { mutableStateOf("") } // Start empty to force input
    val currentPrice = sellPrice.toDoubleOrNull() ?: 0.0
    val estimatedProfit = currentPrice - item.purchasePrice
    val strings = LocalAppStrings.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.sellItem) },
        text = {
            Column {
                Text("${strings.sellQuestion} '${item.name}'?")
                Text(
                    "${strings.buyPrice}: ${String.format("%.2f", item.purchasePrice)} $currencySymbol", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = sellPrice,
                    onValueChange = { sellPrice = it },
                    label = { Text("${strings.sellPrice} ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                     Text("${strings.profitLabel}: ", style = MaterialTheme.typography.bodyMedium)
                     Text(
                         "${String.format("%.2f", estimatedProfit)} $currencySymbol", 
                         style = MaterialTheme.typography.titleMedium, 
                         fontWeight = FontWeight.Bold,
                         color = SuccessGreen
                     )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val p = sellPrice.toDoubleOrNull()
                if (p != null) {
                    onConfirm(p)
                }
            }) {
                Text(strings.sell)
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) {
                    Text(strings.justDelete, color = MaterialTheme.colorScheme.error)
                }
                TextButton(onClick = onDismiss) {
                    Text(strings.cancel)
                }
            }
        }
    )
}

@Composable
fun ShopItemDialog(
    item: ShopItem?,
    existingCategories: List<String>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Double, Double, String) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var count by remember { mutableStateOf(item?.count?.toString() ?: "1") }
    // Purchase Price (Einkaufspreis)
    var purchasePrice by remember { mutableStateOf(item?.let { if (it.purchasePrice == 0.0) "" else if (it.purchasePrice % 1.0 == 0.0) it.purchasePrice.toInt().toString() else it.purchasePrice.toString() } ?: "") }
    var category by remember { mutableStateOf(item?.category ?: "General") }
    
    val strings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) strings.newItem else strings.editItem) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.name) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    OutlinedTextField(
                        value = count,
                        onValueChange = { count = it },
                        label = { Text(strings.quantity) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Einkaufspreis (Purchase Price)
                OutlinedTextField(
                    value = purchasePrice,
                    onValueChange = { purchasePrice = it },
                    label = { Text("${strings.buyPrice} ($currencySymbol)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Category with Suggestions
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Suggestions
                if (existingCategories.isNotEmpty()) {
                    Text("Suggestions:", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(existingCategories) { cat ->
                            SuggestionChip(
                                onClick = { category = cat },
                                label = { Text(cat) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val c = count.toIntOrNull() ?: 1
                val buyP = purchasePrice.toDoubleOrNull() ?: 0.0
                
                if (name.isNotBlank()) {
                    onConfirm(name, c, 0.0, buyP, category) // Selling Price assumed 0.0
                }
            }) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
