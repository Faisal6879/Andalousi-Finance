# Project Implementation Plan: FinanceCalc Android App

## Goal
Build a complete, local Android application for managing finances with specific requirements (Shop T, pre-defined incomes/expenses, charts, clean UI).

## Technology Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material3)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Persistence**: Room Database (SQLite)
- **Dependency Injection**: Hilt (Manual DI if simpler for single file generation, but Hilt is standard. I will use manual DI or simple Singleton/Provider pattern to avoid complex Gradle setup issues for the user if they copy-paste, but sticking to standard Android practices is better. I'll stick to manual dependency injection for simplicity in a "text-based" generation environment to reduce file count, or standard Hilt if I can generate all files. Let's go with Manual DI/Service Locator for simplicity and robustness in this format).
- **Charts**: Vico or MPAndroidChart (Since I cannot easily import external heavy libs without internet/build issues, I might simple custom Canvas drawings for charts or use a lightweight library dependency in gradle. I will add the dependency for a chart lib like Vico or YCharts in the gradle file, assuming the user will sync).

## Phase 1: Project Setup (Gradle & Structure)
- [ ] `settings.gradle.kts`
- [ ] `build.gradle.kts` (Project level)
- [ ] `app/build.gradle.kts` (Module level)
- [ ] `app/src/main/AndroidManifest.xml`

## Phase 2: Data Layer (Room)
- [ ] `Entity`: `Transaction`, `Account`, `ShopItem`
- [ ] `DAO`: `FinanceDao`
- [ ] `Database`: `AppDatabase`
- [ ] `Repository`: `FinanceRepository`

## Phase 3: Domain & Logic
- [ ] Data classes for specific account types (Sparkasse, DKB, etc.)
- [ ] Calculation Logic (Income - Expense, Shop Summing)

## Phase 4: UI Components (Compose)
- [ ] `Theme`: Light/Dark mode support
- [ ] `Components`: TransactionItem, SummaryCard, ChartComponent
- [ ] `Screens`:
    - `HomeScreen` (Dashboard)
    - `IncomeScreen` (List & Edit)
    - `ExpenseScreen` (List & Edit)
    - `ShopScreen` (Special handling for Shop T items)
    - `ReportsScreen` (Charts)
    - `SettingsScreen`

## Phase 5: ViewModels & Navigation
- [ ] `MainViewModel`: Holds state, calculations, interacts with Repository
- [ ] `NavHost`: Setup tabs and routing

## Phase 6: Implementation of Specific User Data
- [ ] Pre-populate DB with requested values (Sparkasse: 1634, Shop items, etc.) on first run.

## Phase 7: Final Review
- [ ] Check requirements: CSV Export, PDF (basic generation), Editable values.
