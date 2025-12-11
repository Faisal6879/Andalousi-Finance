package com.faisal.financecalc

import android.app.Application
import com.faisal.financecalc.data.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FinanceApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val repository by lazy { FirestoreRepository() }
}
