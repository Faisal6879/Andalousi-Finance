package com.faisal.financecalc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.faisal.financecalc.ui.components.SummaryCard
import com.faisal.financecalc.viewmodel.MainViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import com.faisal.financecalc.ui.components.FinanceInputField
import com.faisal.financecalc.ui.components.FinanceInputLabel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

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
    
    // Sorting (New)
    var sortOption by remember { mutableStateOf(SortOption.DATE_DESC) }
    var showSortMenu by remember { mutableStateOf(false) }

    // ... (rest of the content)



    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showManualProfitDialog = true },
                containerColor = com.faisal.financecalc.ui.theme.SuccessGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = strings.addProfitManually)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card: Current Month Profit
            item {
                SummaryCard(
                    title = strings.profitLabel,
                    amount = currentMonthProfit,
                    backgroundColor = com.faisal.financecalc.ui.theme.SuccessGreen.copy(alpha = 0.9f),
                    icon = Icons.Default.TrendingUp
                )
            }

            // Current Month Breakdown
            item {
                if (currentMonthItems.isNotEmpty() || true) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(strings.sales, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            
                            Row {
                                 // Sort Button
                                Box {
                                    IconButton(onClick = { showSortMenu = true }) {
                                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                                    }
                                    DropdownMenu(
                                        expanded = showSortMenu,
                                        onDismissRequest = { showSortMenu = false },
                                        modifier = Modifier
                                            .background(Color(0xFF1E293B))
                                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Newest First", color = Color.White, fontWeight = FontWeight.Medium) },
                                            leadingIcon = { Icon(Icons.Default.AccessTime, null, tint = Color(0xFF94A3B8)) },
                                            onClick = { sortOption = SortOption.DATE_DESC; showSortMenu = false },
                                            modifier = Modifier.background(if(sortOption == SortOption.DATE_DESC) Color(0xFF334155) else Color.Transparent)
                                        )
                                        Divider(color = Color(0xFF334155), modifier = Modifier.padding(horizontal = 8.dp))
                                        DropdownMenuItem(
                                            text = { Text("Profit: High to Low", color = Color.White, fontWeight = FontWeight.Medium) },
                                            leadingIcon = { Icon(Icons.Default.TrendingUp, null, tint = com.faisal.financecalc.ui.theme.SuccessGreen) },
                                            onClick = { sortOption = SortOption.AMOUNT_DESC; showSortMenu = false },
                                            modifier = Modifier.background(if(sortOption == SortOption.AMOUNT_DESC) Color(0xFF334155) else Color.Transparent)
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Profit: Low to High", color = Color.White, fontWeight = FontWeight.Medium) },
                                            leadingIcon = { Icon(Icons.Default.TrendingDown, null, tint = MaterialTheme.colorScheme.error) },
                                            onClick = { sortOption = SortOption.AMOUNT_ASC; showSortMenu = false },
                                            modifier = Modifier.background(if(sortOption == SortOption.AMOUNT_ASC) Color(0xFF334155) else Color.Transparent)
                                        )
                                    }
                                }
                                
                                IconButton(onClick = { showManualProfitDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = strings.addProfitManually, tint = com.faisal.financecalc.ui.theme.SuccessGreen)
                                }
                            }
                        }
                        
                        // Sort Logic (Logic unmodified, purely visual grouping change)
                        val sortedItems = remember(currentMonthItems, sortOption) {
                            when(sortOption) {
                                SortOption.DATE_DESC -> currentMonthItems.sortedByDescending { it.dateTimestamp }
                                SortOption.AMOUNT_DESC -> currentMonthItems.sortedByDescending { it.profit }
                                SortOption.AMOUNT_ASC -> currentMonthItems.sortedBy { it.profit }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Sold Items List Box - Now using Column instead of LazyColumn
                        Card(
                             colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                             elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                             shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                             border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f)),
                             modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                sortedItems.forEach { item ->
                                    // Individual Sold Item Row (Premium)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { editingItem = item }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            // Visual Icon / Bullet
                                            Box(
                                                modifier = Modifier.size(8.dp).background(com.faisal.financecalc.ui.theme.SuccessGreen, androidx.compose.foundation.shape.CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                                Text(
                                                    text = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(item.dateTimestamp)),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                "+${String.format("%.2f", item.profit)} €", 
                                                style = MaterialTheme.typography.bodyLarge, 
                                                fontWeight = FontWeight.Bold, 
                                                color = com.faisal.financecalc.ui.theme.SuccessGreen
                                            )
                                            
                                            // Actions (Subtle)
                                            Row(modifier = Modifier.padding(top = 2.dp)) {
                                                 Icon(
                                                    Icons.Default.Edit, 
                                                    contentDescription = "Edit", 
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.4f),
                                                    modifier = Modifier.size(16.dp).clickable { editingItem = item }
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Icon(
                                                    Icons.Default.Delete, 
                                                    contentDescription = "Delete", 
                                                    tint = MaterialTheme.colorScheme.error.copy(alpha=0.4f),
                                                    modifier = Modifier.size(16.dp).clickable { 
                                                        itemToDelete = item
                                                        showDeleteItemDialog = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    if (item != sortedItems.last()) {
                                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.2f))
                                    }
                                }
                            }
                        }
                    }
                } else {
                     Text("No sales this month.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            // Monthly Profits Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(strings.monthlyProfits, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(strings.monthLabel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(strings.sales, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text(strings.profitLabel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                        }

                        monthlyProfits.forEach { profit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        selectedMonthYear = profit.month to profit.year
                                        showHistoryDetailDialog = true 
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${profit.month}/${profit.year}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                Text("Tap for details", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                Text(
                                    "${String.format("%.2f", profit.profit)} €", 
                                    style = MaterialTheme.typography.bodyMedium, 
                                    fontWeight = FontWeight.Bold, 
                                    color = com.faisal.financecalc.ui.theme.SuccessGreen, 
                                    modifier = Modifier.weight(1f), 
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                                )
                            }
                            if (profit != monthlyProfits.last()) {
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                            }
                        }
                        
                        if (monthlyProfits.isNotEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 24.dp), contentAlignment = Alignment.Center) {
                                 OutlinedButton(
                                     onClick = { showResetConfirmDialog = true },
                                     border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha=0.5f))
                                 ) {
                                     Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                     Spacer(modifier = Modifier.width(8.dp))
                                     Text("Reset History", color = MaterialTheme.colorScheme.error)
                                 }
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
    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current
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
                Text(strings.addProfitManually, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(24.dp))
                
                FinanceInputLabel(strings.descriptionName)
                FinanceInputField(value = name, onValueChange = { name = it })
                
                Spacer(modifier = Modifier.height(16.dp))
                
                FinanceInputLabel("${strings.profitAmount} (€)")
                FinanceInputField(
                    value = profitAmount, 
                    onValueChange = { profitAmount = it },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        FinanceInputLabel(strings.monthRange)
                        FinanceInputField(
                            value = month, 
                            onValueChange = { month = it },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        FinanceInputLabel(strings.yearLabel)
                        FinanceInputField(
                            value = year, 
                            onValueChange = { year = it },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
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
                            val p = profitAmount.toDoubleOrNull()
                            val m = month.toIntOrNull()
                            val y = year.toIntOrNull()
                            if (p != null && m != null && y != null) {
                                onConfirm(p, m, y, name.ifBlank { "Manual Entry" })
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
fun EditProfitDialog(
    item: com.faisal.financecalc.data.SoldItem,
    onDismiss: () -> Unit,
    onConfirm: (Double, Int, Int, String) -> Unit
) {
    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current
    var name by remember { mutableStateOf(item.name) }
    var profitAmount by remember { mutableStateOf(if (item.profit == 0.0) "" else if (item.profit % 1.0 == 0.0) item.profit.toInt().toString() else item.profit.toString()) }
    var month by remember { mutableStateOf(item.month.toString()) }
    var year by remember { mutableStateOf(item.year.toString()) }

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
                Text(strings.editEntry, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(24.dp))
                
                FinanceInputLabel(strings.descriptionName)
                FinanceInputField(value = name, onValueChange = { name = it })
                
                Spacer(modifier = Modifier.height(16.dp))
                
                FinanceInputLabel("${strings.profitAmount} (€)")
                FinanceInputField(
                    value = profitAmount, 
                    onValueChange = { profitAmount = it },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        FinanceInputLabel(strings.monthRange)
                        FinanceInputField(
                            value = month, 
                            onValueChange = { month = it },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        FinanceInputLabel(strings.yearLabel)
                        FinanceInputField(
                            value = year, 
                            onValueChange = { year = it },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
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
                            val p = profitAmount.toDoubleOrNull()
                            val m = month.toIntOrNull()
                            val y = year.toIntOrNull()
                            if (p != null && m != null && y != null) {
                                onConfirm(p, m, y, name.ifBlank { "Manual Entry" })
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

enum class SortOption {
    DATE_DESC,
    AMOUNT_DESC,
    AMOUNT_ASC
}
