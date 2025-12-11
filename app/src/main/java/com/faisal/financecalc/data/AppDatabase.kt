package com.faisal.financecalc.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [FinanceEntry::class, ShopItem::class, SoldItem::class, HistoryEntry::class, CreditCard::class], version = 8, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun financeDao(): FinanceDao
    // ... rest of file


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_database"
                )
                .fallbackToDestructiveMigration() // Wipe DB on schema change for simplicity during dev
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.financeDao())
                }
            }
        }

        suspend fun populateDatabase(dao: FinanceDao) {
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
            
            // Shop T Placeholder (Auto-calculated from items below)
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

            // 4. Shop Items (Profit test: Purchase Price = 0 for now)
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
        }
    }
}
