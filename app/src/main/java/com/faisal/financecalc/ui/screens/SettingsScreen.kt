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
        SettingsGroupCard(title = strings.appearance) {
             Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(strings.darkMode, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Switch(
                    checked = isDarkMode, 
                    onCheckedChange = { viewModel.toggleDarkMode() },
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // General Section
        SettingsGroupCard(title = strings.general) {
             // Currency
            SettingItemRow(
                icon = Icons.Default.AttachMoney,
                title = strings.currencySymbol,
                value = currency,
                onClick = { showCurrencyDialog = true }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f))

            // Language
            SettingItemRow(
                icon = Icons.Default.Language,
                title = strings.language,
                value = language,
                onClick = { showLanguageDialog = true }
            )
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
@Composable
fun SettingsGroupCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title, 
            style = MaterialTheme.typography.labelLarge, 
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
         Icon(
             imageVector = icon, 
             contentDescription = null, 
             tint = MaterialTheme.colorScheme.primary,
             modifier = Modifier.size(24.dp).padding(end = 4.dp)
         )
         Spacer(modifier = Modifier.width(16.dp))
         Column(modifier = Modifier.weight(1f)) {
             Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
             Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
         }
         Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
