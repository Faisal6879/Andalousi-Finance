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
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Andalousi Finance", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background, // Clean modern look (was Primary)
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme" // Keep generic or add string if needed, not critical textual
                        )
                    }
                    IconButton(onClick = { 
                        shareCsv(context, viewModel)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Export CSV")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = strings.settings)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = strings.logout)
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
    val strings = com.faisal.financecalc.ui.theme.LocalAppStrings.current
    val items = listOf(
        Triple("home", strings.dashboard, Icons.Default.Home), 
        Triple("income", strings.income, Icons.Default.AccountBalanceWallet), 
        Triple("debts", strings.debts, Icons.Default.AddCircle), 
        Triple("expenses", strings.expenses, Icons.Default.RemoveCircle), 
        Triple("shop", strings.shop, Icons.Default.ShoppingCart),
        Triple("profit", strings.profitLabel, Icons.Default.Star) 
    )
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface, 
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { 
                    Text(
                        label, 
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), // Smaller text to fit
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    ) 
                },
                selected = currentRoute == route,
                alwaysShowLabel = true, // Force show all labels as requested
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
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
