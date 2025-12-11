package com.faisal.financecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.faisal.financecalc.data.EntryType
import com.faisal.financecalc.ui.screens.FinanceListScreen

import com.faisal.financecalc.ui.screens.LoginScreen
import com.faisal.financecalc.ui.screens.PremiumDashboardScreen
import com.faisal.financecalc.ui.screens.ShopScreen
import com.faisal.financecalc.ui.theme.FinanceCalcTheme
import com.faisal.financecalc.viewmodel.MainViewModel
import com.faisal.financecalc.viewmodel.MainViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = (application as FinanceApplication).repository
        val viewModel = ViewModelProvider(this, MainViewModelFactory(application, repository))[MainViewModel::class.java]

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val language by viewModel.appLanguage.collectAsState()
            var isLoggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }

            val appStrings = when(language) {
                "English" -> com.faisal.financecalc.ui.theme.EnStrings
                "Deutsch" -> com.faisal.financecalc.ui.theme.DeStrings
                "العربية" -> com.faisal.financecalc.ui.theme.ArStrings
                "Français" -> com.faisal.financecalc.ui.theme.FrStrings
                else -> com.faisal.financecalc.ui.theme.DeStrings
            }
            
            val layoutDirection = if(language == "العربية") androidx.compose.ui.unit.LayoutDirection.Rtl else androidx.compose.ui.unit.LayoutDirection.Ltr
            
            com.faisal.financecalc.ui.theme.FinanceCalcTheme(darkTheme = isDarkMode) {
                androidx.compose.runtime.CompositionLocalProvider(
                    com.faisal.financecalc.ui.theme.LocalAppStrings provides appStrings,
                    androidx.compose.ui.platform.LocalLayoutDirection provides layoutDirection
                ) {
                    if (isLoggedIn) {
                        // Initialize data after login
                        LaunchedEffect(Unit) {
                            viewModel.initializeUserData()
                        }
                        
                        MainApp(
                            viewModel = viewModel, 
                            isDarkMode = isDarkMode,
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                isLoggedIn = false
                            }
                        )
                    } else {
                        LoginScreen(
                            onLoginSuccess = {
                                isLoggedIn = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: MainViewModel, isDarkMode: Boolean, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FinanceCalc") },
                actions = {
                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                    IconButton(onClick = { 
                        shareCsv(context, viewModel)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Export CSV")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                PremiumDashboardScreen(
                    viewModel = viewModel,
                    onNavigateToIncome = { navController.navigate("income") },
                    onNavigateToExpenses = { navController.navigate("expenses") },
                    onNavigateToCards = { navController.navigate("shop") },
                    onNavigateToAnalytics = { navController.navigate("profit") }
                )
            }
            composable("income") {
                FinanceListScreen(viewModel = viewModel, type = EntryType.INCOME, title = "Income")
            }
            composable("expenses") {
                FinanceListScreen(viewModel = viewModel, type = EntryType.EXPENSE, title = "Expenses")
            }
            composable("shop") {
                ShopScreen(viewModel = viewModel)
            }
            composable("debts") {
                FinanceListScreen(viewModel = viewModel, type = EntryType.DEBT, title = "Debts")
            }
            composable("profit") {
                com.faisal.financecalc.ui.screens.ProfitScreen(viewModel = viewModel)
            }
            composable("settings") {
                com.faisal.financecalc.ui.screens.SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

fun shareCsv(context: android.content.Context, viewModel: MainViewModel) {
    val entries = viewModel.allEntries.value
    val maxShopItems = viewModel.allShopItems.value
    
    val sb = StringBuilder()
    sb.append("Type,Name,Amount,Category\n")
    for (entry in entries) {
        sb.append("${entry.type},${entry.name},${entry.amount},${entry.category}\n")
    }
    sb.append("\nSHOP ITEMS\n")
    for (item in maxShopItems) {
        sb.append("Item,${item.name},${item.count},${item.pricePerUnit},${item.total}\n")
    }

    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_SUBJECT, "Finance Export")
        putExtra(android.content.Intent.EXTRA_TEXT, sb.toString())
    }
    context.startActivity(android.content.Intent.createChooser(intent, "Export Data"))
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        "home" to Icons.Default.Home, 
        "income" to Icons.Default.AccountBalanceWallet, 
        "debts" to Icons.Default.Warning, // Warning/Alert for Debts
        "expenses" to Icons.Default.MoneyOff, 
        "shop" to Icons.Default.ShoppingCart,
        "profit" to Icons.Default.Star 
    )
    
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        for ((route, icon) in items) {
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = route) },
                label = { Text(route.replaceFirstChar { it.uppercase() }) },
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
