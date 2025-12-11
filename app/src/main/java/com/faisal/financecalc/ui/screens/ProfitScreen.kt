package com.faisal.financecalc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.faisal.financecalc.ui.components.SummaryCard
import com.faisal.financecalc.viewmodel.MainViewModel

@Composable
fun ProfitScreen(viewModel: MainViewModel) {
    val monthlyProfits by viewModel.monthlyProfits.collectAsState(initial = emptyList())
    val soldItems by viewModel.allSoldItems.collectAsState(initial = emptyList())
    
    // Current Month Logic
    val calendar = java.util.Calendar.getInstance()
    val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1
    val currentYear = calendar.get(java.util.Calendar.YEAR)

    // Filter for current month
    val currentMonthItems = soldItems.filter { it.month == currentMonth && it.year == currentYear }
    val currentMonthProfit = currentMonthItems.sumOf { it.profit }

    var showManualProfitDialog by remember { mutableStateOf(false) }
    var showHistoryDetailDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteItemDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<com.faisal.financecalc.data.SoldItem?>(null) }
    var editingItem by remember { mutableStateOf<com.faisal.financecalc.data.SoldItem?>(null) }
    var selectedMonthYear by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    // ... (rest of the content)



    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showManualProfitDialog = true },
                containerColor = com.faisal.financecalc.ui.theme.SuccessGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Profit")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Card: Current Month Profit
            SummaryCard(
                title = "Profit (Current Month)",
                amount = currentMonthProfit,
                backgroundColor = com.faisal.financecalc.ui.theme.SuccessGreen.copy(alpha = 0.9f),
                icon = Icons.Default.TrendingUp
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // Current Month Breakdown
            if (currentMonthItems.isNotEmpty() || true) { // Always show header to allow adding even if empty
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sold this Month:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { showManualProfitDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Manual Sale", tint = com.faisal.financecalc.ui.theme.SuccessGreen)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 250.dp) // Slightly taller
                ) {
                    LazyColumn(modifier = Modifier.padding(8.dp)) {
                        items(currentMonthItems) { item ->
                             Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { editingItem = item } // Click to edit
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(item.dateTimestamp)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "+${String.format("%.2f", item.profit)} €", 
                                        style = MaterialTheme.typography.bodyMedium, 
                                        fontWeight = FontWeight.Bold, 
                                        color = com.faisal.financecalc.ui.theme.SuccessGreen
                                    )
                                    // IconButton size optimization for row
                                    IconButton(
                                        onClick = { editingItem = item },
                                        modifier = Modifier.size(32.dp).padding(start = 8.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { 
                                            itemToDelete = item
                                            showDeleteItemDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha=0.7f), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f))
                        }
                    }
                }
            } else {
                 Text("No sales this month yet.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("All History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (monthlyProfits.isEmpty()) {
                    item {
                        Text(
                            "No profit history yet. Sell items in 'Shop' or add manually.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(monthlyProfits) { history ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                selectedMonthYear = history.month to history.year
                                showHistoryDetailDialog = true
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "${history.month}/${history.year}", 
                                    style = MaterialTheme.typography.titleMedium, 
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Tap for details", 
                                    style = MaterialTheme.typography.labelSmall, 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "${String.format("%.2f", history.profit)} €", 
                                style = MaterialTheme.typography.headlineSmall, 
                                fontWeight = FontWeight.Bold, 
                                color = com.faisal.financecalc.ui.theme.SuccessGreen
                            )
                        }
                    }
                }
                
                if (monthlyProfits.isNotEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                             TextButton(onClick = { showResetConfirmDialog = true }) {
                                 Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                 Spacer(modifier = Modifier.width(4.dp))
                                 Text("Reset All History", color = MaterialTheme.colorScheme.error)
                             }
                        }
                    }
                }
            }
        }
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Confirm Reset") },
            text = { Text("Are you sure you want to delete ALL profit history? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSoldHistory()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialogs (Reused logic)
    if (showManualProfitDialog) {
        ManualProfitDialog(
            onDismiss = { showManualProfitDialog = false },
            onConfirm = { profit, m, y, name ->
                viewModel.manualAddProfit(profit, m, y, name)
                showManualProfitDialog = false
            }
        )
    }

    if (showHistoryDetailDialog && selectedMonthYear != null) {
        val (m, y) = selectedMonthYear!!
        val monthlyItems = soldItems.filter { it.month == m && it.year == y }
        val totalMonthProfit = monthlyItems.sumOf { it.profit }
        
        AlertDialog(
            onDismissRequest = { showHistoryDetailDialog = false },
            title = { 
                Column {
                    Text("Sales Breakdown $m/$y")
                    Text("Total: ${String.format("%.2f", totalMonthProfit)} €", style = MaterialTheme.typography.titleMedium, color = com.faisal.financecalc.ui.theme.SuccessGreen)
                }
            },
            text = {
                Column {
                    Text("Sold Items:", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(monthlyItems) { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("Profit: ${String.format("%.2f", item.profit)} €", style = MaterialTheme.typography.bodySmall, color = com.faisal.financecalc.ui.theme.SuccessGreen)
                                }
                                Row {
                                    IconButton(onClick = { editingItem = item }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Item", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { viewModel.deleteSoldItem(item) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Item", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                        if (monthlyItems.isEmpty()) {
                            item { Text("No items found.") }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistoryDetailDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (editingItem != null) {
        EditProfitDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onConfirm = { profit, m, y, name ->
                viewModel.updateSoldItem(editingItem!!.copy(profit = profit, month = m, year = y, name = name))
                editingItem = null
            }
        )
    }

    if (showDeleteItemDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteItemDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete '${itemToDelete!!.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSoldItem(itemToDelete!!)
                        showDeleteItemDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteItemDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ManualProfitDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, Int, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var profitAmount by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    
    // Default current date
    val calendar = java.util.Calendar.getInstance()
    LaunchedEffect(Unit) {
        month = (calendar.get(java.util.Calendar.MONTH) + 1).toString()
        year = calendar.get(java.util.Calendar.YEAR).toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Profit manually") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Description / Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = profitAmount,
                    onValueChange = { profitAmount = it },
                    label = { Text("Profit Amount (€)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = month,
                        onValueChange = { month = it },
                        label = { Text("Month (1-12)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Year") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val p = profitAmount.toDoubleOrNull()
                val m = month.toIntOrNull()
                val y = year.toIntOrNull()
                if (p != null && m != null && y != null) {
                    onConfirm(p, m, y, name.ifBlank { "Manual Entry" })
                }
            }) {
                Text("Add")
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
fun EditProfitDialog(
    item: com.faisal.financecalc.data.SoldItem,
    onDismiss: () -> Unit,
    onConfirm: (Double, Int, Int, String) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var profitAmount by remember { mutableStateOf(if (item.profit == 0.0) "" else if (item.profit % 1.0 == 0.0) item.profit.toInt().toString() else item.profit.toString()) }
    var month by remember { mutableStateOf(item.month.toString()) }
    var year by remember { mutableStateOf(item.year.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profit Entry") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Description / Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = profitAmount,
                    onValueChange = { profitAmount = it },
                    label = { Text("Profit Amount (€)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = month,
                        onValueChange = { month = it },
                        label = { Text("Month (1-12)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Year") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val p = profitAmount.toDoubleOrNull()
                val m = month.toIntOrNull()
                val y = year.toIntOrNull()
                if (p != null && m != null && y != null) {
                    onConfirm(p, m, y, name.ifBlank { "Manual Entry" })
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
