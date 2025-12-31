package com.faisal.financecalc.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
    }
    
    // ========== Finance Entries ==========
    
    fun getAllEntries(): Flow<List<FinanceEntry>> = callbackFlow {
        val listener = db.collection("users").document(getUserId())
            .collection("entries")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val entries = snapshot?.documents?.mapNotNull { it.toObject(FinanceEntry::class.java) } ?: emptyList()
                trySend(entries)
            }
        awaitClose { listener.remove() }
    }
    
    suspend fun insertEntry(entry: FinanceEntry) {
        val docRef = db.collection("users").document(getUserId())
            .collection("entries").document()
        val entryWithId = entry.copy(id = docRef.id.hashCode().toLong())
        docRef.set(entryWithId).await()
    }
    
    suspend fun updateEntry(entry: FinanceEntry) {
        db.collection("users").document(getUserId())
            .collection("entries")
            .whereEqualTo("id", entry.id)
            .get().await()
            .documents.firstOrNull()?.reference?.set(entry)?.await()
    }
    
    suspend fun deleteEntry(entry: FinanceEntry) {
        db.collection("users").document(getUserId())
            .collection("entries")
            .whereEqualTo("id", entry.id)
            .get().await()
            .documents.firstOrNull()?.reference?.delete()?.await()
    }
    
    suspend fun getEntryByName(name: String): FinanceEntry? {
        return db.collection("users").document(getUserId())
            .collection("entries")
            .whereEqualTo("name", name)
            .get().await()
            .documents.firstOrNull()?.toObject(FinanceEntry::class.java)
    }
    
    // ========== Shop Items ==========
    
    fun getAllShopItems(): Flow<List<ShopItem>> = callbackFlow {
        val listener = db.collection("users").document(getUserId())
            .collection("shopItems")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { it.toObject(ShopItem::class.java) } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }
    
    suspend fun insertShopItem(item: ShopItem) {
        val docRef = db.collection("users").document(getUserId())
            .collection("shopItems").document()
        val itemWithId = item.copy(id = docRef.id.hashCode().toLong())
        docRef.set(itemWithId).await()
    }
    
    suspend fun updateShopItem(item: ShopItem) {
        db.collection("users").document(getUserId())
            .collection("shopItems")
            .whereEqualTo("id", item.id)
            .get().await()
            .documents.firstOrNull()?.reference?.set(item)?.await()
    }
    
    suspend fun deleteShopItem(item: ShopItem) {
        db.collection("users").document(getUserId())
            .collection("shopItems")
            .whereEqualTo("id", item.id)
            .get().await()
            .documents.firstOrNull()?.reference?.delete()?.await()
    }
    
    fun getShopTotal(): Flow<Double?> = callbackFlow {
        val listener = db.collection("users").document(getUserId())
            .collection("shopItems")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val total = snapshot?.documents?.mapNotNull { it.toObject(ShopItem::class.java) }
                    ?.sumOf { it.total } ?: 0.0
                trySend(total)
            }
        awaitClose { listener.remove() }
    }
    
    // ========== Sold Items ==========
    
    fun getAllSoldItems(): Flow<List<SoldItem>> = callbackFlow {
        val listener = db.collection("users").document(getUserId())
            .collection("soldItems")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { it.toObject(SoldItem::class.java) } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }
    
    suspend fun insertSoldItem(item: SoldItem) {
        val docRef = db.collection("users").document(getUserId())
            .collection("soldItems").document()
        val itemWithId = item.copy(id = docRef.id.hashCode().toLong())
        docRef.set(itemWithId).await()
    }
    
    suspend fun updateSoldItem(item: SoldItem) {
        db.collection("users").document(getUserId())
            .collection("soldItems")
            .whereEqualTo("id", item.id)
            .get().await()
            .documents.firstOrNull()?.reference?.set(item)?.await()
    }
    
    suspend fun deleteSoldItem(item: SoldItem) {
        db.collection("users").document(getUserId())
            .collection("soldItems")
            .whereEqualTo("id", item.id)
            .get().await()
            .documents.firstOrNull()?.reference?.delete()?.await()
    }
    
    fun getMonthlyProfits(): Flow<List<MonthlyProfit>> = callbackFlow {
        val listener = db.collection("users").document(getUserId())
            .collection("soldItems")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val profitMap = mutableMapOf<Pair<Int, Int>, Double>()
                snapshot?.documents?.mapNotNull { it.toObject(SoldItem::class.java) }?.forEach { item ->
                    val key = Pair(item.month, item.year)
                    profitMap[key] = (profitMap[key] ?: 0.0) + item.profit
                }
                val profits = profitMap.map { (key, profit) ->
                    MonthlyProfit(month = key.first, year = key.second, profit = profit)
                }.sortedWith(compareBy({ it.year }, { it.month }))
                trySend(profits)
            }
        awaitClose { listener.remove() }
    }
    
    // ========== History ==========
    
    fun getAllHistory(): Flow<List<HistoryEntry>> = callbackFlow {
        val listener = db.collection("users").document(getUserId())
            .collection("history")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val history = snapshot?.documents?.mapNotNull { it.toObject(HistoryEntry::class.java) }
                    ?.sortedByDescending { it.timestamp } ?: emptyList()
                trySend(history)
            }
        awaitClose { listener.remove() }
    }

    fun getHistoryForEntry(entryId: Long): Flow<List<HistoryEntry>> = callbackFlow {
        val listener = db.collection("users").document(getUserId())
            .collection("history")
            .whereEqualTo("entryId", entryId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val history = snapshot?.documents?.mapNotNull { it.toObject(HistoryEntry::class.java) }
                    ?.sortedByDescending { it.timestamp } ?: emptyList()
                trySend(history)
            }
        awaitClose { listener.remove() }
    }
    
    suspend fun insertHistoryEntry(history: HistoryEntry) {
        val docRef = db.collection("users").document(getUserId())
            .collection("history").document()
        val historyWithId = history.copy(id = docRef.id.hashCode().toLong())
        docRef.set(historyWithId).await()
    }
    
    // ========== Credit Cards ==========
    
    fun getAllCreditCards(): Flow<List<CreditCard>> = callbackFlow {
        val listener = db.collection("users").document(getUserId())
            .collection("creditCards")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { it.toObject(CreditCard::class.java) } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }
    
    suspend fun insertCreditCard(card: CreditCard) {
        val docRef = db.collection("users").document(getUserId())
            .collection("creditCards").document()
        val cardWithId = card.copy(id = docRef.id.hashCode().toLong())
        docRef.set(cardWithId).await()
    }
    
    suspend fun updateCreditCard(card: CreditCard) {
        db.collection("users").document(getUserId())
            .collection("creditCards")
            .whereEqualTo("id", card.id)
            .get().await()
            .documents.firstOrNull()?.reference?.set(card)?.await()
    }
    
    suspend fun deleteCreditCard(card: CreditCard) {
        db.collection("users").document(getUserId())
            .collection("creditCards")
            .whereEqualTo("id", card.id)
            .get().await()
            .documents.firstOrNull()?.reference?.delete()?.await()
    }

    // ========== Initial Data Population ==========
    
    suspend fun populateIfEmpty() {
        // Handled by MainViewModel to ensure specific user data is used.
    }
}
