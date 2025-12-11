package com.faisal.financecalc.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date

enum class EntryType {
    INCOME, // Sources like Sparkasse, DKB, Temu
    EXPENSE, // Destinations like Car, Save, Baba
    DEBT    // Schulden
}

@Entity(tableName = "entries")
data class FinanceEntry(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String = "",
    var amount: Double = 0.0,
    var type: EntryType = EntryType.INCOME,
    var category: String = "", // "Fixed", "Shop", "Extra", "Account"
    var isAutoCalculated: Boolean = false, // True for Shop T Total
    @Ignore var subEntries: List<SubEntry> = emptyList(), // New: Split bookings
    var excludedFromTotal: Boolean = false, // User request: Option not to count in calculation
    var orderIndex: Int = 0,
    var dateTimestamp: Long = System.currentTimeMillis() // New: Date support
) {
    // No-arg constructor for Firebase
    constructor() : this(0, "", 0.0, EntryType.INCOME, "", false, emptyList(), false, 0, System.currentTimeMillis())
}

data class SubEntry(
    var name: String = "",
    var amount: Double = 0.0
) {
    constructor() : this("", 0.0)
}

@Entity(tableName = "shop_items")
data class ShopItem(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String = "",
    var count: Int = 0,
    var pricePerUnit: Double = 0.0,      // Verkaufspreis (Selling Price)
    var purchasePrice: Double = 0.0, // Einkaufspreis (Buying Price)
    var orderIndex: Int = 0,
    var category: String = "General" // New: Category support
) {
    // No-arg constructor for Firebase
    constructor() : this(0, "", 0, 0.0, 0.0, 0, "General")
    
    val total: Double get() = count * pricePerUnit
    val totalProfit: Double get() = count * (pricePerUnit - purchasePrice)
}

@Entity(tableName = "sold_items")
data class SoldItem(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String = "",
    var profit: Double = 0.0,
    var dateTimestamp: Long = 0L, // For processing
    var month: Int = 0, // 1-12
    var year: Int = 0   // 2024, 2025
) {
    // No-arg constructor for Firebase
    constructor() : this(0, "", 0.0, 0L, 0, 0)
}

@Entity(tableName = "entry_history")
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var entryId: Long = 0,
    var oldAmount: Double = 0.0,
    var newAmount: Double = 0.0,
    var dateTimestamp: Long = 0L
) {
    // No-arg constructor for Firebase
    constructor() : this(0, 0, 0.0, 0.0, 0L)
    
    val timestamp: Long get() = dateTimestamp
}

// For monthly profit aggregation
data class MonthlyProfit(
    val month: Int,
    val year: Int,
    val profit: Double
)

@Entity(tableName = "credit_cards")
data class CreditCard(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var holderName: String = "",
    var cardNumber: String = "", // Storing last 4 digits mainly for display
    var expiryDate: String = "",
    var balance: Double = 0.0,
    var cardType: String = "VISA", // VISA, MASTERCARD
    var colorTheme: Int = 0 // 0=DarkBlue, 1=Black, 2=Gold, 3=Purple
) {
    // No-arg constructor for Firebase
    constructor() : this(0, "", "", "", 0.0, "VISA", 0)
}


