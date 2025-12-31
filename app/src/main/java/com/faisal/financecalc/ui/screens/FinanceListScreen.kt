package com.faisal.financecalc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.faisal.financecalc.data.EntryType
import com.faisal.financecalc.data.FinanceEntry
import com.faisal.financecalc.ui.components.EntryRow
import com.faisal.financecalc.ui.components.FinanceInputField
import com.faisal.financecalc.ui.components.FinanceInputLabel
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

    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current
    
    // Derive localized title from type
    val screenTitle = when(type) {
        EntryType.INCOME -> strings.income
        EntryType.EXPENSE -> strings.expenses
        EntryType.DEBT -> strings.debts
    }

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
                Icon(Icons.Default.Add, contentDescription = strings.newEntry)
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
                title = "${strings.totalValue} $screenTitle",
                amount = totalAmount,
                backgroundColor = color.copy(alpha = 0.9f),
                icon = if(isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(strings.transactions, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            
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
    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current
    var name by remember { mutableStateOf(entry?.name ?: "") }
    // Category Dropdown Logic
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = if(type == EntryType.INCOME) listOf("Account", "Salary", "Bonus", "Other") else listOf("Fixed", "Variable", "One-time", "Housing", "Transport", "Food")
    var category by remember { mutableStateOf(entry?.category ?: categories.first()) }
    
    // Sub-entries state
    val subEntriesList = remember { mutableStateListOf<com.faisal.financecalc.data.SubEntry>().apply {
        if (entry != null) addAll(entry.subEntries)
    }}
    
    var manualAmount by remember { mutableStateOf(entry?.let { if (it.amount == 0.0) "" else if (it.amount % 1.0 == 0.0) it.amount.toInt().toString() else it.amount.toString() } ?: "") }
    val calculatedTotal = subEntriesList.sumOf { it.amount }
    val isSplitBooking = subEntriesList.isNotEmpty()
    val finalAmount = if (isSplitBooking) calculatedTotal else manualAmount.toDoubleOrNull() ?: 0.0

    var excludedFromTotal by remember { mutableStateOf(entry?.excludedFromTotal ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), // Premium Slate
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (entry == null) strings.newEntry else strings.editEntry,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    if (onHistoryClick != null) {
                         TextButton(onClick = onHistoryClick) {
                             Text(strings.history, color = MaterialTheme.colorScheme.primary)
                         }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Name Input
                FinanceInputLabel(strings.name)
                FinanceInputField(
                    value = name, 
                    onValueChange = { name = it }, 
                    placeholder = strings.placeholderExample
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Amount Input
                FinanceInputLabel(if (isSplitBooking) strings.totalAmountAuto else "${strings.amountLabel} €")
                FinanceInputField(
                    value = if (isSplitBooking) finalAmount.toString() else manualAmount,
                    onValueChange = { if (!isSplitBooking) manualAmount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = "0.00",
                    enabled = !isSplitBooking
                )
                
                // Quick Adjust (Only for existing entries and non-split)
                if (entry != null && !isSplitBooking) {
                    var adjustmentValue by remember { mutableStateOf("") }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FinanceInputField(
                            value = adjustmentValue,
                            onValueChange = { adjustmentValue = it },
                            placeholder = "+/-",
                            modifier = Modifier.weight(1f).height(40.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val current = manualAmount.toDoubleOrNull() ?: 0.0
                                val adj = adjustmentValue.toDoubleOrNull()
                                if (adj != null) {
                                    val newVal = current + adj
                                    manualAmount = if(newVal % 1.0 == 0.0) newVal.toInt().toString() else newVal.toString()
                                    adjustmentValue = "" // Reset
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(strings.quickAdjust, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Category Dropdown
                FinanceInputLabel(strings.category)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFF0B1220), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                        .clickable { categoryExpanded = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                         Text(category, color = Color.White)
                         Icon(Icons.Default.ArrowDropDown, null, tint = Color(0xFF94A3B8))
                    }
                    DropdownMenu(
                        expanded = categoryExpanded, 
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(Color(0xFF1E293B))
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = Color.White) },
                                onClick = { category = cat; categoryExpanded = false }
                            )
                        }
                    }
                }

                // Exclude Toggle (iOS Style)
                 if (type == EntryType.EXPENSE) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A).copy(alpha=0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.excludeFromTotal, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text(strings.excludeDescription, color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                        Switch(
                            checked = excludedFromTotal,
                            onCheckedChange = { excludedFromTotal = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = com.faisal.financecalc.ui.theme.ProfitGold,
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF334155)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Sub-items
                Text(strings.splitBookingDetails, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                
                subEntriesList.forEachIndexed { index, subEntry ->
                     Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tiny inputs for sub items
                         BasicTextField(
                            value = subEntry.name,
                            onValueChange = { subEntriesList[index] = subEntry.copy(name = it) },
                            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(Color(0xFF0B1220), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                         BasicTextField(
                            value = if (subEntry.amount == 0.0) "" else subEntry.amount.toString(),
                            onValueChange = { subEntriesList[index] = subEntry.copy(amount = it.toDoubleOrNull() ?: 0.0) },
                            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .width(80.dp)
                                .height(40.dp)
                                .background(Color(0xFF0B1220), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                         IconButton(onClick = { subEntriesList.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                OutlinedButton(
                    onClick = { subEntriesList.add(com.faisal.financecalc.data.SubEntry()) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.addSubItem, color = MaterialTheme.colorScheme.primary)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Actions
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(strings.cancel, color = Color(0xFFCBD5E1))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(name, finalAmount, category, subEntriesList.toList(), excludedFromTotal)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(14.dp),
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
    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.history) },
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
                Text(strings.close)
            }
        }
    )
}
