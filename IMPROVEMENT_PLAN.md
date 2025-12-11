# Premium App Optimization Plan

## 1. Design & UI (Visual Excellence)
**Goal:** Transform the app into a "Premium Finance Assistant".
- **Color Palette:** Move from standard Material colors to a curated palette.
    - *Primary:* Deep Emerald Green (#006C4C) or Midnight Blue (#1A237E).
    - *Secondary:* Gold/Amber accent (#FFD700) for "Premium" feel.
    - *Background:* Off-white (#F5F7FA) for Light Mode, Deep Charcoal (#121212) for Dark Mode.
    - *Surface:* Pure White with soft shadows (Elevation 2dp-4dp).
- **Typography:** specific font weights (ExtraBold for values, Medium for labels).
- **Components:**
    - Use **Gradient Cards** for key metrics (Total Balance, Shop Value).
    - **Rounded Corners:** Consistent 16dp or 24dp shapes.
    - **Glassmorphism:** Use semi-transparent backgrounds for Navigation Bar and Dialogs.

## 2. Structure & Architecture
**Goal:** Modularize and Clean up.
- **Navigation:** Extract `NavHost` from `MainActivity` into `navigation/AppNavigation.kt`.
- **ViewModel Separation:** Split `MainViewModel` into:
    - `FinanceViewModel` (Income, Expense, Debt)
    - `ShopViewModel` (Inventory, Sales)
    - `SettingsViewModel` (Theme, Language, Currency)
    - *Why?* Prevents the "God Object" anti-pattern and makes testing easier.
- **Repository Pattern:** Ensure all data access goes through `FinanceRepository`.

## 3. UX (User Experience)
**Goal:** Frictionless interactions.
- **Input Forms:** Replace standard `AlertDialog` with **ModalBottomSheets**. They are more reachable on mobile and feel more modern.
- **Gestures:** Implement Swipe-to-Delete or Swipe-to-Edit instead of cluttering rows with icon buttons.
- **Feedback:** Add "Undo" Snackbars when deleting items.

## 4. Performance
- **Lazy Loading:** Ensure `LazyColumn` is used everywhere (already mostly done).
- **State Management:** Use `collectAsStateWithLifecycle()` for better lifecycle handling.
- **Database:** Ensure heavy calculations happen in Coroutines (already done, but verify).

## 5. Security & Logic
- **Data Backup:** Ensure cloud sync (Firestore) is reliable.
- **Input Validation:** Prevent negative numbers where not allowed.
- **Zero States:** Beautiful "Empty State" illustrations when lists are empty.

---

# Priority Tasks (Step-by-Step)

## Phase 1: Visual Foundation (IMMEDIATE)
1.  **Update Colors & Theme:** Define the new Premium Palette.
2.  **Redesign Dashboard (HomeScreen):** Create the "Wow" factor immediately.
3.  **Modernize Navigation:** Create a custom Bottom Navigation Bar.

## Phase 2: UX Refinement
4.  **Refactor Dialogs:** Convert key dialogs (Add Item, Sell) to Bottom Sheets.
5.  **Empty States:** Add visuals for empty lists.

## Phase 3: Structural Cleanup
6.  **Split ViewModels:** Refactor code structure.
7.  **Optimize Navigation:** Move to separate file.
