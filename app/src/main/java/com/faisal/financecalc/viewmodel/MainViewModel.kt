package com.faisal.financecalc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.faisal.financecalc.data.CreditCard
import com.faisal.financecalc.data.EntryType
import com.faisal.financecalc.data.FinanceEntry
import com.faisal.financecalc.data.FirestoreRepository
import com.faisal.financecalc.data.HistoryEntry
import com.faisal.financecalc.data.ShopItem
import com.faisal.financecalc.data.SoldItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application, private val repository: FirestoreRepository) : AndroidViewModel(application) {

    // Call this after user logs in
    fun initializeUserData() {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("finance_prefs", android.content.Context.MODE_PRIVATE)
            val isSeeded = prefs.getBoolean("data_seeded_v3", false)
            
            if (!isSeeded) {
                // Check if Cloud DB is actually empty before wiping/seeding
                val entries = repository.getAllEntries().first()
                val shopItems = repository.getAllShopItems().first()
                
                if (entries.isEmpty() && shopItems.isEmpty()) {
                    repopulateData()
                }
                
                // Mark as seeded so we don't check/write again
                prefs.edit().putBoolean("data_seeded_v3", true).apply()
            }
        }
    }


    val allEntries = repository.getAllEntries().map { list -> 
        list.sortedBy { it.orderIndex } 
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allShopItems = repository.getAllShopItems().map { list -> 
        list.sortedBy { it.orderIndex } 
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val shopTotalVal = allShopItems.map { items ->
        items.sumOf { it.count * it.purchasePrice }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val monthlyProfits = repository.getMonthlyProfits().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val allSoldItems = repository.getAllSoldItems().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Derived States
    val incomeEntries = combine(allEntries, shopTotalVal) { entries, shopValue ->
        // Create a list where "Shop T Total" entry (if exists) is updated with actual Inventory Value
        entries.filter { it.type == EntryType.INCOME }.map { entry ->
            if (entry.isAutoCalculated && entry.category == "Shop") {
                // Use Inventory Value here
                entry.copy(amount = shopValue)
            } else {
                entry
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseEntries = allEntries.map { list -> 
        list.filter { it.type == EntryType.EXPENSE }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debtEntries = allEntries.map { list -> 
        list.filter { it.type == EntryType.DEBT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome = incomeEntries.map { list ->
        list.filter { !it.excludedFromTotal && !it.isAutoCalculated }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense = expenseEntries.map { list ->
        list.filter { !it.excludedFromTotal }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalDebt = debtEntries.map { list ->
        list.filter { !it.excludedFromTotal }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val balance = combine(totalIncome, totalExpense, totalDebt, allSoldItems, shopTotalVal) { income, expense, debt, soldItems, shopVal ->
        val calendar = java.util.Calendar.getInstance()
        val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1
        val currentYear = calendar.get(java.util.Calendar.YEAR)
        
        val currentMonthProfit = soldItems
            .filter { it.month == currentMonth && it.year == currentYear }
            .sumOf { it.profit }

        // Balance = (Income + Debt - Expense - MonthlyProfit) + ShopInventoryValue
        income + debt - expense - currentMonthProfit + shopVal
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Actions
    fun manualAddProfit(profit: Double, month: Int, year: Int, name: String = "Manual Entry") = viewModelScope.launch {
         repository.insertSoldItem(SoldItem(
            name = name,
            profit = profit,
            dateTimestamp = System.currentTimeMillis(),
            month = month,
            year = year
        ))
    }
    
    fun deleteSoldItem(item: SoldItem) = viewModelScope.launch {
        repository.deleteSoldItem(item)
    }

    fun updateSoldItem(item: SoldItem) = viewModelScope.launch {
        repository.updateSoldItem(item)
    }

    fun resetSoldHistory() = viewModelScope.launch {
        val items = repository.getAllSoldItems().first()
        for (item in items) {
            repository.deleteSoldItem(item)
        }
    }

    fun sellItem(item: ShopItem, sellPrice: Double) = viewModelScope.launch {
        val profit = sellPrice - item.purchasePrice
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1 // 0-indexed
        val year = calendar.get(Calendar.YEAR)
        
        repository.insertSoldItem(SoldItem(
            name = item.name,
            profit = profit,
            dateTimestamp = System.currentTimeMillis(),
            month = month,
            year = year
        ))
        
        // If count > 1, decrease count. If 1, delete.
        if (item.count > 1) {
            repository.updateShopItem(item.copy(count = item.count - 1))
        } else {
            repository.deleteShopItem(item)
        }
    }

    fun addEntry(entry: FinanceEntry) = viewModelScope.launch {
        repository.insertEntry(entry)
    }

    fun updateEntry(entry: FinanceEntry, oldEntry: FinanceEntry? = null) = viewModelScope.launch {
        if (oldEntry != null && oldEntry.amount != entry.amount) {
            val history = HistoryEntry(
                entryId = entry.id,
                oldAmount = oldEntry.amount,
                newAmount = entry.amount,
                dateTimestamp = System.currentTimeMillis()
            )
            repository.insertHistoryEntry(history)
        }
        repository.updateEntry(entry)
    }

    fun deleteEntry(entry: FinanceEntry) = viewModelScope.launch {
        repository.deleteEntry(entry)
    }
    
    fun getEntryHistory(entryId: Long): Flow<List<HistoryEntry>> {
        return repository.getHistoryForEntry(entryId)
    }

    fun addShopItem(item: ShopItem) = viewModelScope.launch {
        repository.insertShopItem(item)
    }

    fun updateShopItem(item: ShopItem) = viewModelScope.launch {
        repository.updateShopItem(item)
    }

    fun deleteShopItem(item: ShopItem) = viewModelScope.launch {
        repository.deleteShopItem(item)
    }
    
    // Credit Cards
    val allCreditCards = repository.getAllCreditCards().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun addCreditCard(card: CreditCard) = viewModelScope.launch {
        repository.insertCreditCard(card)
    }
    
    fun deleteCreditCard(card: CreditCard) = viewModelScope.launch {
        repository.deleteCreditCard(card)
    }

    // Theme State
    private val prefs by lazy { getApplication<Application>().getSharedPreferences("finance_prefs", android.content.Context.MODE_PRIVATE) }
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", false)) 
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        prefs.edit().putBoolean("is_dark_mode", newValue).apply()
    }

    // Settings
    private val _currencySymbol = MutableStateFlow("€")
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    private val _appLanguage = MutableStateFlow(prefs.getString("app_language", "Deutsch") ?: "Deutsch")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    fun setCurrency(symbol: String) {
        _currencySymbol.value = symbol
    }

    fun setLanguage(language: String) {
        _appLanguage.value = language
        prefs.edit().putString("app_language", language).apply()
    }

    private suspend fun repopulateData() {
        // Clear existing data (Force refresh as per user request "delete all data")
        val currentEntries = repository.getAllEntries().first()
        for (entry in currentEntries) {
            repository.deleteEntry(entry)
        }
        val currentShopItems = repository.getAllShopItems().first()
        for (item in currentShopItems) {
            repository.deleteShopItem(item)
        }
        
        var i = 0
        // Income
        repository.insertEntry(FinanceEntry(name = "Sparkasse", amount = 1634.0, type = EntryType.INCOME, category = "Account", orderIndex = i++))
        repository.insertEntry(FinanceEntry(name = "DKB", amount = 106.0, type = EntryType.INCOME, category = "Account", orderIndex = i++))
        repository.insertEntry(FinanceEntry(name = "Revolut", amount = 0.0, type = EntryType.INCOME, category = "Account", orderIndex = i++))
        repository.insertEntry(FinanceEntry(name = "Bar 1", amount = 520.0, type = EntryType.INCOME, category = "Cash", orderIndex = i++))
        repository.insertEntry(FinanceEntry(name = "Bar 2", amount = 1080.0, type = EntryType.INCOME, category = "Cash", orderIndex = i++))
        repository.insertEntry(FinanceEntry(name = "Shop T (Cash)", amount = 2045.0, type = EntryType.INCOME, category = "Shop", orderIndex = i++))
        repository.insertEntry(FinanceEntry(name = "PayPal", amount = 310.0, type = EntryType.INCOME, category = "Account", orderIndex = i++))
        repository.insertEntry(FinanceEntry(name = "In Safe", amount = 1020.0, type = EntryType.INCOME, category = "Safe", orderIndex = i++))
        
        repository.insertEntry(FinanceEntry(name = "GIB", amount = 2952.0, type = EntryType.INCOME, category = "Bank", orderIndex = i++))
        repository.insertEntry(FinanceEntry(name = "PS", amount = 10.0, type = EntryType.INCOME, category = "Account", orderIndex = i++))
        
        // Auto-calc placeholder - Excluded from Total Balance as per user request (Profit/Inventory Value logic)
        repository.insertEntry(FinanceEntry(name = "Shop T Calculated", amount = 0.0, type = EntryType.INCOME, category = "Shop", isAutoCalculated = true, excludedFromTotal = true, orderIndex = i++))

        var d = 0
        // Debts
        repository.insertEntry(FinanceEntry(name = "Temu", amount = 196.0, type = EntryType.DEBT, category = "Shopping", orderIndex = d++))
        repository.insertEntry(FinanceEntry(name = "Debt", amount = 95.0, type = EntryType.DEBT, category = "General", orderIndex = d++))
        repository.insertEntry(FinanceEntry(name = "Amazon", amount = 700.0, type = EntryType.DEBT, category = "Shopping", orderIndex = d++))
        repository.insertEntry(FinanceEntry(name = "Mama", amount = 710.0, type = EntryType.DEBT, category = "Family", orderIndex = d++))
        repository.insertEntry(FinanceEntry(name = "Mama (p)", amount = 350.0, type = EntryType.DEBT, category = "Family", orderIndex = d++))
        repository.insertEntry(FinanceEntry(name = "Mona", amount = 56.0, type = EntryType.DEBT, category = "Family", orderIndex = d++))
        repository.insertEntry(FinanceEntry(name = "Mo", amount = 100.0, type = EntryType.DEBT, category = "Family", orderIndex = d++))

        var e = 0
        // Expenses
        repository.insertEntry(FinanceEntry(name = "Car", amount = 190.0, type = EntryType.EXPENSE, category = "Transport", orderIndex = e++))
        repository.insertEntry(FinanceEntry(name = "Save", amount = 305.0, type = EntryType.EXPENSE, category = "Savings", orderIndex = e++))
        repository.insertEntry(FinanceEntry(name = "Baba", amount = 4166.0, type = EntryType.EXPENSE, category = "Family", orderIndex = e++))
        repository.insertEntry(FinanceEntry(name = "Ola", amount = 1500.0, type = EntryType.EXPENSE, category = "Other", orderIndex = e++))
        
        // Excluded Expenses
        repository.insertEntry(FinanceEntry(name = "gm", amount = 160.0, type = EntryType.EXPENSE, category = "Excluded", excludedFromTotal = true, orderIndex = e++))
        repository.insertEntry(FinanceEntry(name = "gold", amount = 245.0, type = EntryType.EXPENSE, category = "Excluded", excludedFromTotal = true, orderIndex = e++))

        var s = 0
        // Shop Items with Categories
        repository.insertShopItem(ShopItem(name = "xss", count = 1, purchasePrice = 105.0, pricePerUnit = 0.0, orderIndex = s++, category = "Xbox"))
        repository.insertShopItem(ShopItem(name = "xsx", count = 1, purchasePrice = 210.0, pricePerUnit = 0.0, orderIndex = s++, category = "Xbox"))
        repository.insertShopItem(ShopItem(name = "xsx", count = 1, purchasePrice = 275.0, pricePerUnit = 0.0, orderIndex = s++, category = "Xbox"))
        repository.insertShopItem(ShopItem(name = "xc", count = 3, purchasePrice = 60.0, pricePerUnit = 0.0, orderIndex = s++, category = "Xbox"))
        
        repository.insertShopItem(ShopItem(name = "ps5", count = 1, purchasePrice = 290.0, pricePerUnit = 0.0, orderIndex = s++, category = "Playstation"))
        repository.insertShopItem(ShopItem(name = "ps5", count = 1, purchasePrice = 290.0, pricePerUnit = 0.0, orderIndex = s++, category = "Playstation"))
        repository.insertShopItem(ShopItem(name = "ps5", count = 1, purchasePrice = 295.0, pricePerUnit = 0.0, orderIndex = s++, category = "Playstation"))
        repository.insertShopItem(ShopItem(name = "ps5", count = 1, purchasePrice = 300.0, pricePerUnit = 0.0, orderIndex = s++, category = "Playstation"))
        
        repository.insertShopItem(ShopItem(name = "nin", count = 1, purchasePrice = 100.0, pricePerUnit = 0.0, orderIndex = s++, category = "Nintendo"))
        repository.insertShopItem(ShopItem(name = "nin", count = 1, purchasePrice = 120.0, pricePerUnit = 0.0, orderIndex = s++, category = "Nintendo"))
    }
}

class MainViewModelFactory(private val application: android.app.Application, private val repository: FirestoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
