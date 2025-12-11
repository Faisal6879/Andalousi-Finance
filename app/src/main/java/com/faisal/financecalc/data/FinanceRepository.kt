package com.faisal.financecalc.data

import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val dao: FinanceDao) {

    val allEntries: Flow<List<FinanceEntry>> = dao.getAllEntries()
    val allShopItems: Flow<List<ShopItem>> = dao.getAllShopItems()
    val shopTotal: Flow<Double?> = dao.getShopTotal()
    val monthlyProfits: Flow<List<MonthlyProfit>> = dao.getMonthlyProfits()
    val allSoldItems: Flow<List<SoldItem>> = dao.getAllSoldItems()

    suspend fun insertEntry(entry: FinanceEntry) {
        dao.insertEntry(entry)
    }

    suspend fun updateEntry(entry: FinanceEntry) {
        dao.updateEntry(entry)
    }

    suspend fun deleteEntry(entry: FinanceEntry) {
        dao.deleteEntry(entry)
    }

    suspend fun insertShopItem(item: ShopItem) {
        dao.insertShopItem(item)
    }

    suspend fun updateShopItem(item: ShopItem) {
        dao.updateShopItem(item)
    }

    suspend fun deleteShopItem(item: ShopItem) {
        dao.deleteShopItem(item)
    }
    
    suspend fun insertSoldItem(item: SoldItem) {
        dao.insertSoldItem(item)
    }

    suspend fun updateSoldItem(item: SoldItem) {
        dao.updateSoldItem(item)
    }

    suspend fun deleteSoldItem(item: SoldItem) {
        dao.deleteSoldItem(item)
    }
    
    // History
    fun getHistoryForEntry(entryId: Long): Flow<List<HistoryEntry>> = dao.getHistoryForEntry(entryId)
    
    suspend fun insertHistoryEntry(history: HistoryEntry) {
        dao.insertHistoryEntry(history)
    }

    // Credit Cards
    val allCreditCards: Flow<List<CreditCard>> = dao.getAllCreditCards()
    
    suspend fun insertCreditCard(card: CreditCard) {
        dao.insertCreditCard(card)
    }
    
    suspend fun deleteCreditCard(card: CreditCard) {
        dao.deleteCreditCard(card)
    }

    suspend fun getEntryByName(name: String): FinanceEntry? {
        return dao.getEntryByName(name)
    }

    suspend fun populateIfEmpty() {
        if (dao.getEntryCount() == 0) {
            // 1. Account / Main Income
            dao.insertEntry(FinanceEntry(name = "Sparkasse", amount = 1634.0, type = EntryType.INCOME, category = "Account"))
            dao.insertEntry(FinanceEntry(name = "DKB", amount = 106.0, type = EntryType.INCOME, category = "Account"))
            dao.insertEntry(FinanceEntry(name = "Revolut", amount = 0.0, type = EntryType.INCOME, category = "Account"))
            dao.insertEntry(FinanceEntry(name = "Bar 1", amount = 520.0, type = EntryType.INCOME, category = "Cash"))
            dao.insertEntry(FinanceEntry(name = "Bar 2", amount = 1080.0, type = EntryType.INCOME, category = "Cash"))
            dao.insertEntry(FinanceEntry(name = "Shop T (Cash/Alt)", amount = 2400.0, type = EntryType.INCOME, category = "Shop"))
            dao.insertEntry(FinanceEntry(name = "PayPal", amount = 310.0, type = EntryType.INCOME, category = "Account"))
            dao.insertEntry(FinanceEntry(name = "In Safe (Tresor)", amount = 1020.0, type = EntryType.INCOME, category = "Safe"))
            dao.insertEntry(FinanceEntry(name = "GIB", amount = 2952.0, type = EntryType.INCOME, category = "Account"))
            dao.insertEntry(FinanceEntry(name = "PS", amount = 10.0, type = EntryType.INCOME, category = "Account"))
            
            // Shop T Placeholder
            dao.insertEntry(FinanceEntry(name = "Shop T Calculated", amount = 0.0, type = EntryType.INCOME, category = "Shop", isAutoCalculated = true))

            // 2. Extra Income
            dao.insertEntry(FinanceEntry(name = "Temu", amount = 196.0, type = EntryType.INCOME, category = "Extra"))
            dao.insertEntry(FinanceEntry(name = "Temu 2", amount = 95.0, type = EntryType.INCOME, category = "Extra"))
            dao.insertEntry(FinanceEntry(name = "Amazon", amount = 700.0, type = EntryType.INCOME, category = "Extra"))
            dao.insertEntry(FinanceEntry(name = "Mama", amount = 710.0, type = EntryType.INCOME, category = "Extra"))
            dao.insertEntry(FinanceEntry(name = "Mama extra", amount = 350.0, type = EntryType.INCOME, category = "Extra"))
            dao.insertEntry(FinanceEntry(name = "Mona", amount = 56.0, type = EntryType.INCOME, category = "Extra"))
            dao.insertEntry(FinanceEntry(name = "Mo", amount = 100.0, type = EntryType.INCOME, category = "Extra"))

            // 3. Expenses
            dao.insertEntry(FinanceEntry(name = "Car", amount = 190.0, type = EntryType.EXPENSE, category = "Fixed"))
            dao.insertEntry(FinanceEntry(name = "Save", amount = 305.0, type = EntryType.EXPENSE, category = "Fixed"))
            dao.insertEntry(FinanceEntry(name = "Baba", amount = 4166.0, type = EntryType.EXPENSE, category = "Fixed"))
            dao.insertEntry(FinanceEntry(name = "Ola", amount = 1500.0, type = EntryType.EXPENSE, category = "Fixed"))
            dao.insertEntry(FinanceEntry(name = "GM", amount = 245.0, type = EntryType.EXPENSE, category = "Fixed"))
            dao.insertEntry(FinanceEntry(name = "GM2", amount = 160.0, type = EntryType.EXPENSE, category = "Fixed"))

            // 4. Shop Items
            val shopItems = listOf(
                ShopItem(name = "xss", count = 1, pricePerUnit = 105.0, purchasePrice = 0.0),
                ShopItem(name = "xsx", count = 1, pricePerUnit = 210.0, purchasePrice = 0.0),
                ShopItem(name = "xsx", count = 1, pricePerUnit = 275.0, purchasePrice = 0.0),
                ShopItem(name = "xc", count = 3, pricePerUnit = 60.0, purchasePrice = 0.0),
                ShopItem(name = "ps5", count = 1, pricePerUnit = 290.0, purchasePrice = 0.0),
                ShopItem(name = "ps5", count = 1, pricePerUnit = 290.0, purchasePrice = 0.0),
                ShopItem(name = "ps5", count = 1, pricePerUnit = 295.0, purchasePrice = 0.0),
                ShopItem(name = "ps5", count = 1, pricePerUnit = 300.0, purchasePrice = 0.0),
                ShopItem(name = "nin", count = 1, pricePerUnit = 100.0, purchasePrice = 0.0),
                ShopItem(name = "nin", count = 1, pricePerUnit = 120.0, purchasePrice = 0.0)
            )
            shopItems.forEach { dao.insertShopItem(it) }

            // 5. Pre-fill Sold History (Monthly Profits)
            // 12 : 330€, 1: 632€, 2: 350€ ...
            val historyData = listOf(
                Triple(12, 2024, 330.0),
                Triple(1, 2025, 632.0),
                Triple(2, 2025, 350.0),
                Triple(3, 2025, 135.0),
                Triple(4, 2025, 60.0),
                Triple(5, 2025, 765.0),
                Triple(6, 2025, 70.0),
                Triple(7, 2025, 510.0),
                Triple(8, 2025, 10.0),
                Triple(9, 2025, 840.0),
                Triple(10, 2025, 607.0),
                Triple(11, 2025, 850.0)
            )
            val now = System.currentTimeMillis()
            historyData.forEach { (m, y, profit) ->
                dao.insertSoldItem(SoldItem(
                    name = "Legacy Sale",
                    profit = profit,
                    dateTimestamp = now,
                    month = m,
                    year = y
                ))
            }
        }
    }
}
