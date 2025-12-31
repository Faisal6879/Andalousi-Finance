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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.faisal.financecalc.data.ShopItem
import com.faisal.financecalc.ui.components.ShopItemRow
import com.faisal.financecalc.viewmodel.MainViewModel
import androidx.compose.ui.window.Dialog
import com.faisal.financecalc.ui.components.FinanceInputField
import com.faisal.financecalc.ui.components.FinanceInputLabel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.faisal.financecalc.ui.theme.LocalAppStrings
import com.faisal.financecalc.ui.theme.SuccessGreen

@Composable
fun ShopScreen(viewModel: MainViewModel) {
    val shopItems by viewModel.allShopItems.collectAsState()
    val shopTotal by viewModel.shopTotalVal.collectAsState()
    val monthlyProfits by viewModel.monthlyProfits.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val incomeEntries by viewModel.incomeEntries.collectAsState()
    val strings = LocalAppStrings.current
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeductDialog by remember { mutableStateOf(false) }
    var currentItem by remember { mutableStateOf<ShopItem?>(null) } // null = new, non-null = edit
    var pendingItemToAdd by remember { mutableStateOf<ShopItem?>(null) }
    
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
                Icon(Icons.Default.Add, contentDescription = strings.newItem)
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
        
                    Spacer(modifier = Modifier.height(20.dp))
                }
        
                item {
                    Text(
                        strings.inventoryItems, 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold, 
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                // Display items grouped by category
                groupedItems.forEach { (category, items) ->
                    item {
                         Surface(
                             color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f),
                             shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                             modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp)
                         ) {
                             Text(
                                 text = category,
                                 style = MaterialTheme.typography.labelMedium,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant,
                                 fontWeight = FontWeight.Bold,
                                 modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                             )
                         }
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
                val newItem = ShopItem(
                    id = currentItem?.id ?: 0,
                    name = name, 
                    count = count, 
                    pricePerUnit = 0.0, 
                    purchasePrice = purchasePrice, 
                    category = category
                )
                
                if (currentItem == null) {
                    // New Item -> Ask for deduction
                    pendingItemToAdd = newItem
                    showAddDialog = false
                    showDeductDialog = true
                } else {
                    // Edit -> Just update
                    viewModel.updateShopItem(newItem)
                    showAddDialog = false
                }
            }
        )
    }

    if (showDeductDialog && pendingItemToAdd != null) {
        val totalCost = pendingItemToAdd!!.purchasePrice * pendingItemToAdd!!.count
        DeductFromIncomeDialog(
            totalCost = totalCost,
            currencySymbol = currencySymbol,
            incomeEntries = incomeEntries.filter { !it.isAutoCalculated && !it.excludedFromTotal },
            onDismiss = {
                // If dismissed/skipped, just add the item without deduction
                viewModel.addShopItem(pendingItemToAdd!!)
                pendingItemToAdd = null
                showDeductDialog = false
            },
            onConfirm = { sourceEntry ->
                // Deduct from source
                if (sourceEntry != null) {
                    val newAmount = sourceEntry.amount - totalCost
                    viewModel.updateEntry(sourceEntry.copy(amount = newAmount), sourceEntry)
                }
                // Add item
                viewModel.addShopItem(pendingItemToAdd!!)
                pendingItemToAdd = null
                showDeductDialog = false
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
    var sellPrice by remember { mutableStateOf("") }
    val currentPrice = sellPrice.toDoubleOrNull() ?: 0.0
    val estimatedProfit = currentPrice - item.purchasePrice
    val strings = LocalAppStrings.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(strings.sellItem, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("${strings.sellQuestion} '${item.name}'?", color = Color.White)
                Text(
                    "${strings.buyPrice}: ${String.format("%.2f", item.purchasePrice)} $currencySymbol", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(16.dp))

                FinanceInputLabel("${strings.sellPrice} ($currencySymbol)")
                FinanceInputField(
                    value = sellPrice,
                    onValueChange = { sellPrice = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = "0.00"
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                     Text("${strings.profitLabel}: ", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                     Text(
                         "${String.format("%.2f", estimatedProfit)} $currencySymbol", 
                         style = MaterialTheme.typography.titleMedium, 
                         fontWeight = FontWeight.Bold,
                         color = com.faisal.financecalc.ui.theme.SuccessGreen
                     )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                        Text(strings.justDelete, color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onDismiss, 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(strings.cancel, color = Color(0xFFCBD5E1))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            val p = sellPrice.toDoubleOrNull()
                            if (p != null) {
                                onConfirm(p)
                            }
                        }, 
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(strings.sell, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
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
    var purchasePrice by remember { mutableStateOf(item?.let { if (it.purchasePrice == 0.0) "" else if (it.purchasePrice % 1.0 == 0.0) it.purchasePrice.toInt().toString() else it.purchasePrice.toString() } ?: "") }
    var category by remember { mutableStateOf(item?.category ?: "General") }
    
    val strings = LocalAppStrings.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(if (item == null) strings.newItem else strings.editItem, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(24.dp))
                
                FinanceInputLabel(strings.name)
                FinanceInputField(value = name, onValueChange = { name = it })
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        FinanceInputLabel(strings.quantity)
                        FinanceInputField(
                            value = count, 
                            onValueChange = { count = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        FinanceInputLabel("${strings.buyPrice} ($currencySymbol)")
                        FinanceInputField(
                            value = purchasePrice, 
                            onValueChange = { purchasePrice = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                FinanceInputLabel(strings.category)
                FinanceInputField(value = category, onValueChange = { category = it })

                if (existingCategories.isNotEmpty()) {
                    Text(strings.suggestions, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp), color = Color(0xFF94A3B8))
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(existingCategories) { cat ->
                            SuggestionChip(
                                onClick = { category = cat },
                                label = { Text(cat, color = Color.White) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF334155), labelColor = Color.White)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onDismiss, 
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(strings.cancel, color = Color(0xFFCBD5E1))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            val c = count.toIntOrNull() ?: 1
                            val buyP = purchasePrice.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank()) {
                                onConfirm(name, c, 0.0, buyP, category)
                            }
                        }, 
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(strings.save, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DeductFromIncomeDialog(
    totalCost: Double,
    currencySymbol: String,
    incomeEntries: List<com.faisal.financecalc.data.FinanceEntry>,
    onDismiss: () -> Unit,
    onConfirm: (com.faisal.financecalc.data.FinanceEntry?) -> Unit
) {
    var selectedEntry by remember { mutableStateOf<com.faisal.financecalc.data.FinanceEntry?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Payment Source") },
        text = {
            Column {
                Text("Total Cost: ${String.format("%.2f", totalCost)} $currencySymbol")
                Text("Payed from:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    item {
                         Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedEntry = null }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = selectedEntry == null,
                                onClick = { selectedEntry = null }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Skip (No Deduction)", fontWeight = if (selectedEntry == null) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                    
                    items(incomeEntries) { entry ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedEntry = entry }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = selectedEntry?.id == entry.id,
                                onClick = { selectedEntry = entry }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(entry.name, fontWeight = if (selectedEntry?.id == entry.id) FontWeight.Bold else FontWeight.Normal)
                                Text("${entry.amount} $currencySymbol", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedEntry) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Skip")
            }
        }
    )
}
