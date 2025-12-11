package com.faisal.financecalc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.faisal.financecalc.data.EntryType
import com.faisal.financecalc.data.FinanceEntry
import com.faisal.financecalc.ui.components.EntryRow
import com.faisal.financecalc.viewmodel.MainViewModel

@Composable
fun FinanceListScreen(
    title: String,
    entries: List<FinanceEntry>,
    type: EntryType,
    onAdd: (FinanceEntry) -> Unit,
    onEdit: (FinanceEntry, FinanceEntry?) -> Unit,
    onDelete: (FinanceEntry) -> Unit,
    onViewHistory: (FinanceEntry) -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }
    var currentEntry by remember { mutableStateOf<FinanceEntry?>(null) }

    val totalAmount = entries.filter { !it.excludedFromTotal }.sumOf { it.amount }
    // Using Green for INCOME and DEBT (as requested "Plus")
    val isPositive = type == EntryType.INCOME || type == EntryType.DEBT
    val color = if (isPositive) com.faisal.financecalc.ui.theme.SuccessGreen else androidx.compose.material3.MaterialTheme.colorScheme.error
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var entryToDelete by remember { mutableStateOf<FinanceEntry?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    currentEntry = null
                    showDialog = true 
                },
                containerColor = color,
                contentColor = androidx.compose.ui.graphics.Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            com.faisal.financecalc.ui.components.SummaryCard(
                title = "Total $title",
                amount = totalAmount,
                backgroundColor = color.copy(alpha = 0.9f),
                icon = if(isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Transactions", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(entries) { entry ->
                    EntryRow(
                        entry = entry,
                        onEdit = { 
                            currentEntry = entry
                            showDialog = true 
                        },
                        onDelete = { 
                            entryToDelete = entry
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog && entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete '${entryToDelete!!.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(entryToDelete!!)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDialog) {
        EntryDialog(
            entry = currentEntry,
            type = type,
            onDismiss = { showDialog = false },
            onConfirm = { name, amount, category, subEntries, excluded ->
                if (currentEntry == null) {
                    onAdd(FinanceEntry(name = name, amount = amount, type = type, category = category, subEntries = subEntries, excludedFromTotal = excluded))
                } else {
                    onEdit(currentEntry!!.copy(name = name, amount = amount, category = category, subEntries = subEntries, excludedFromTotal = excluded), currentEntry)
                }
                showDialog = false
            },
            onHistoryClick = if (currentEntry != null) { { onViewHistory(currentEntry!!) } } else null
        )
    }
}

@Composable
fun EntryDialog(
    entry: FinanceEntry?,
    type: EntryType,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, List<com.faisal.financecalc.data.SubEntry>, Boolean) -> Unit,
    onHistoryClick: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(entry?.name ?: "") }
    var category by remember { mutableStateOf(entry?.category ?: if(type == EntryType.INCOME) "Account" else "Fixed") }
    
    // Sub-entries state
    val subEntriesList = remember { mutableStateListOf<com.faisal.financecalc.data.SubEntry>().apply {
        if (entry != null) addAll(entry.subEntries)
    }}
    
    // Calculate total from sub-entries if any exist, otherwise use manual input
    var manualAmount by remember { mutableStateOf(entry?.let { if (it.amount == 0.0) "" else if (it.amount % 1.0 == 0.0) it.amount.toInt().toString() else it.amount.toString() } ?: "") }
    
    val calculatedTotal = subEntriesList.sumOf { it.amount }
    val isSplitBooking = subEntriesList.isNotEmpty()
    
    val finalAmount = if (isSplitBooking) calculatedTotal else manualAmount.toDoubleOrNull() ?: 0.0

    var excludedFromTotal by remember { mutableStateOf(entry?.excludedFromTotal ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (entry == null) "New Entry" else "Edit Entry") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Main Amount (Disabled if split booking)
                OutlinedTextField(
                    value = if (isSplitBooking) finalAmount.toString() else manualAmount,
                    onValueChange = { if (!isSplitBooking) manualAmount = it },
                    label = { Text(if (isSplitBooking) "Total Amount (Auto)" else "Amount €") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSplitBooking,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Exclude Option (Only for Expenses)
                if (type == EntryType.EXPENSE) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { excludedFromTotal = !excludedFromTotal }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = excludedFromTotal,
                            onCheckedChange = { excludedFromTotal = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Exclude from Total", fontWeight = FontWeight.SemiBold)
                            Text("If checked, this will be highlighted gold and not counted.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Sub-entries Section
                Text("Split Booking / Details", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                subEntriesList.forEachIndexed { index, subEntry ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = subEntry.name,
                            onValueChange = { subEntriesList[index] = subEntry.copy(name = it) },
                            label = { Text("Detail") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = if (subEntry.amount == 0.0) "" else if (subEntry.amount % 1.0 == 0.0) subEntry.amount.toInt().toString() else subEntry.amount.toString(),
                            onValueChange = { subEntriesList[index] = subEntry.copy(amount = it.toDoubleOrNull() ?: 0.0) },
                            label = { Text("€") },
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        IconButton(onClick = { subEntriesList.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                
                TextButton(onClick = { subEntriesList.add(com.faisal.financecalc.data.SubEntry()) }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Sub-Item")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    onConfirm(name, finalAmount, category, subEntriesList.toList(), excludedFromTotal)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (onHistoryClick != null) {
                    TextButton(onClick = onHistoryClick) {
                        Text("History")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}


@Composable
fun FinanceListScreen(
    viewModel: MainViewModel,
    type: EntryType,
    title: String
) {
    val entries by when (type) {
        EntryType.INCOME -> viewModel.incomeEntries
        EntryType.EXPENSE -> viewModel.expenseEntries
        EntryType.DEBT -> viewModel.debtEntries
    }.collectAsState(initial = emptyList())

    var showingHistoryEntry by remember { mutableStateOf<FinanceEntry?>(null) }

    FinanceListScreen(
        title = title,
        entries = entries,
        type = type,
        onAdd = { viewModel.addEntry(it) },
        onEdit = { entry, old -> viewModel.updateEntry(entry, old) },
        onDelete = { viewModel.deleteEntry(it) },
        onViewHistory = { showingHistoryEntry = it }
    )

    if (showingHistoryEntry != null) {
        val history by viewModel.getEntryHistory(showingHistoryEntry!!.id).collectAsState(initial = emptyList())
        HistoryDialog(
            history = history,
            onDismiss = { showingHistoryEntry = null }
        )
    }
}

@Composable
fun HistoryDialog(
    history: List<com.faisal.financecalc.data.HistoryEntry>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit History") },
        text = {
            if (history.isEmpty()) {
                Text("No changes recorded.")
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(history) { h ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(h.dateTimestamp)),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Row {
                                Text("${h.oldAmount} €", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                                Text(" -> ", style = MaterialTheme.typography.bodyMedium)
                                Text("${h.newAmount} €", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
