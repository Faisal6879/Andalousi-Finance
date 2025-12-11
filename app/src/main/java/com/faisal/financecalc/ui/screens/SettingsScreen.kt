package com.faisal.financecalc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.faisal.financecalc.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val currency by viewModel.currencySymbol.collectAsState()
    val language by viewModel.appLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(strings.settings, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Appearance Section
        Text(strings.appearance, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(strings.darkMode, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isDarkMode, onCheckedChange = { viewModel.toggleDarkMode() })
        }
        
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        // General Section
        Text(strings.general, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))

        // Currency Setting
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showCurrencyDialog = true }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Icon(Icons.Default.AttachMoney, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
             Spacer(modifier = Modifier.width(16.dp))
             Column(modifier = Modifier.weight(1f)) {
                 Text(strings.currencySymbol, style = MaterialTheme.typography.bodyLarge)
                 Text(currency, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
             }
             Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.5f))

        // Language Setting
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLanguageDialog = true }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
             Spacer(modifier = Modifier.width(16.dp))
             Column(modifier = Modifier.weight(1f)) {
                 Text(strings.language, style = MaterialTheme.typography.bodyLarge)
                 Text(language, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
             }
             Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
    }

    // Currency Dialog
    if (showCurrencyDialog) {
        val currencies = listOf("€", "$", "£", "AED", "CHF")
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text(strings.chooseCurrency) },
            text = {
                Column {
                    currencies.forEach { symbol ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setCurrency(symbol)
                                    showCurrencyDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (symbol == currency), onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(symbol)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) { Text(strings.cancel) }
            }
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        val languages = listOf("English", "Deutsch", "Français", "العربية")
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(strings.chooseLanguage) },
            text = {
                Column {
                    languages.forEach { lang ->
                         Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (lang == language), onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang)
                        }
                    }
                }
            },
            confirmButton = {
                 TextButton(onClick = { showLanguageDialog = false }) { Text(strings.cancel) }
            }
        )
    }
}
