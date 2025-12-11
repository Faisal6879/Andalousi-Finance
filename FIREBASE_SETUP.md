# Firebase Realtime Database Setup

## 🔥 Firebase Console Konfiguration

### 1. Authentication aktivieren
1. Gehe zu [Firebase Console](https://console.firebase.google.com/project/finance-app-7d29e)
2. Klicke auf **Authentication** im linken Menü
3. Klicke auf **Get Started**
4. Wähle **Email/Password** als Sign-in method
5. Aktiviere **Email/Password** und speichere

### 2. Realtime Database aktivieren
1. Klicke auf **Realtime Database** im linken Menü
2. Klicke auf **Create Database**
3. Wähle eine Region (z.B. `europe-west1`)
4. Wähle **Start in test mode** (später ändern wir die Regeln)
5. Klicke auf **Enable**

### 3. Database Rules konfigurieren
Nachdem die Database erstellt wurde:
1. Gehe zu **Realtime Database** → **Rules**
2. Ersetze die Regeln mit folgendem Code:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

3. Klicke auf **Publish**

## 📱 Datenstruktur

Die Daten werden wie folgt organisiert:

```
users/
  └── {userId}/
      ├── entries/
      │   ├── {entryId}/
      │   │   ├── id: Long
      │   │   ├── name: String
      │   │   ├── amount: Double
      │   │   ├── type: String (INCOME/EXPENSE/DEBT)
      │   │   ├── category: String
      │   │   └── isAutoCalculated: Boolean
      │   └── ...
      ├── shopItems/
      │   ├── {itemId}/
      │   │   ├── id: Long
      │   │   ├── name: String
      │   │   ├── count: Int
      │   │   ├── pricePerUnit: Double
      │   │   └── purchasePrice: Double
      │   └── ...
      ├── soldItems/
      │   ├── {soldId}/
      │   │   ├── id: Long
      │   │   ├── name: String
      │   │   ├── profit: Double
      │   │   ├── dateTimestamp: Long
      │   │   ├── month: Int
      │   │   └── year: Int
      │   └── ...
      └── history/
          └── {entryId}/
              ├── {historyId}/
              │   ├── id: Long
              │   ├── entryId: Long
              │   ├── oldAmount: Double
              │   ├── newAmount: Double
              │   └── timestamp: Long
              └── ...
```

## ✅ Vorteile dieser Struktur

1. **Multi-User Support**: Jeder Benutzer hat seine eigenen Daten
2. **Sicherheit**: Benutzer können nur ihre eigenen Daten lesen/schreiben
3. **Cloud Sync**: Daten sind überall verfügbar
4. **Echtzeit**: Änderungen werden sofort synchronisiert
5. **Offline Support**: Firebase cached Daten automatisch

## 🔐 Sicherheit

- **Authentication**: Nur angemeldete Benutzer haben Zugriff
- **Database Rules**: Jeder Benutzer kann nur seine eigenen Daten sehen
- **Biometric Login**: Nach erster Anmeldung mit Fingerabdruck möglich

## 🚀 App Features

- ✅ Email/Password Login
- ✅ Fingerabdruck-Authentifizierung
- ✅ Cloud-Datenspeicherung
- ✅ Echtzeit-Synchronisation
- ✅ Multi-Device Support
- ✅ Offline-Modus
- ✅ Automatische Datensicherung
