package com.faisal.financecalc.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    // Finance Entries
    @Query("SELECT * FROM entries")
    fun getAllEntries(): Flow<List<FinanceEntry>>

    @Query("SELECT COUNT(*) FROM entries")
    suspend fun getEntryCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FinanceEntry)

    @Update
    suspend fun updateEntry(entry: FinanceEntry)

    @Delete
    suspend fun deleteEntry(entry: FinanceEntry)

    @Query("SELECT * FROM entries WHERE name = :name LIMIT 1")
    suspend fun getEntryByName(name: String): FinanceEntry?

    // Shop Items
    @Query("SELECT * FROM shop_items")
    fun getAllShopItems(): Flow<List<ShopItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopItem(item: ShopItem)

    @Update
    suspend fun updateShopItem(item: ShopItem)

    @Delete
    suspend fun deleteShopItem(item: ShopItem)
    
    @Query("SELECT SUM(count * purchasePrice) FROM shop_items")
    fun getShopTotal(): Flow<Double?>
    
    // Sold History
    @Insert
    suspend fun insertSoldItem(item: SoldItem)

    @Update
    suspend fun updateSoldItem(item: SoldItem)

    @Delete
    suspend fun deleteSoldItem(item: SoldItem)
    
    @Query("SELECT * FROM sold_items ORDER BY dateTimestamp DESC") // Get all for editing if needed, or stick to monthly aggregate for view
    fun getAllSoldItems(): Flow<List<SoldItem>>

    @Query("SELECT year, month, SUM(profit) as profit FROM sold_items GROUP BY year, month ORDER BY year DESC, month DESC")
    fun getMonthlyProfits(): Flow<List<MonthlyProfit>>

    // Entry History
    @Insert
    suspend fun insertHistoryEntry(history: HistoryEntry)

    @Query("SELECT * FROM entry_history WHERE entryId = :entryId ORDER BY dateTimestamp DESC")
    fun getHistoryForEntry(entryId: Long): Flow<List<HistoryEntry>>

    // Credit Cards
    @Query("SELECT * FROM credit_cards")
    fun getAllCreditCards(): Flow<List<CreditCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditCard(card: CreditCard)

    @Delete
    suspend fun deleteCreditCard(card: CreditCard)
}
