# FinanceCalc Android App

This is a complete Native Android Application (Kotlin + Compose + Room) for managing finances.

## Setup
1. Open **Android Studio**.
2. Select **Open** and choose this directory: `c:\Users\Faisa\Desktop\Selling system`.
3. Allow Gradle to sync. (It may take a few minutes to download dependencies).
4. Connect an Android device or start an Emulator.
5. Click **Run**.

## Features
- **Dashboard**: View Total Balance, Income, Expenses, and Distribution Chart.
- **Income/Expenses**: Manage lists of entries. Edit amounts, categories, etc.
- **Shop T**: Special section for "Shop T" items. The total is calculated automatically based on items (e.g. 105 xss, 210 xsx).
- **Export**: Click the Share icon in the top right to export data as CSV text (via WhatsApp, Email, Drive, etc.).
- **Persistence**: All data is saved locally using Room Database.

## Architecture
- **MVVM**: Separation of concerns with ViewModels and Repository.
- **Jetpack Compose**: Modern UI toolkit.
- **Room**: SQLite database abstraction.
- **Coroutines/Flow**: Asynchronous data handling.

## specific Data
The app comes pre-populated with the requested data (Sparkasse: 1634, Shop Items, etc.) on the first run. You can edit or delete these entries in the app.
