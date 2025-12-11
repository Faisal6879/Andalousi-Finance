package com.faisal.financecalc.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile FinanceDao _financeDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(8) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `amount` REAL NOT NULL, `type` TEXT NOT NULL, `category` TEXT NOT NULL, `isAutoCalculated` INTEGER NOT NULL, `excludedFromTotal` INTEGER NOT NULL, `orderIndex` INTEGER NOT NULL, `dateTimestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `shop_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `count` INTEGER NOT NULL, `pricePerUnit` REAL NOT NULL, `purchasePrice` REAL NOT NULL, `orderIndex` INTEGER NOT NULL, `category` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sold_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `profit` REAL NOT NULL, `dateTimestamp` INTEGER NOT NULL, `month` INTEGER NOT NULL, `year` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `entry_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `entryId` INTEGER NOT NULL, `oldAmount` REAL NOT NULL, `newAmount` REAL NOT NULL, `dateTimestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `credit_cards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `holderName` TEXT NOT NULL, `cardNumber` TEXT NOT NULL, `expiryDate` TEXT NOT NULL, `balance` REAL NOT NULL, `cardType` TEXT NOT NULL, `colorTheme` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '1e71eaf20ec3e4de92e8fd0f4f572f16')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `entries`");
        db.execSQL("DROP TABLE IF EXISTS `shop_items`");
        db.execSQL("DROP TABLE IF EXISTS `sold_items`");
        db.execSQL("DROP TABLE IF EXISTS `entry_history`");
        db.execSQL("DROP TABLE IF EXISTS `credit_cards`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsEntries = new HashMap<String, TableInfo.Column>(9);
        _columnsEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntries.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntries.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntries.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntries.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntries.put("isAutoCalculated", new TableInfo.Column("isAutoCalculated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntries.put("excludedFromTotal", new TableInfo.Column("excludedFromTotal", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntries.put("orderIndex", new TableInfo.Column("orderIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntries.put("dateTimestamp", new TableInfo.Column("dateTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEntries = new TableInfo("entries", _columnsEntries, _foreignKeysEntries, _indicesEntries);
        final TableInfo _existingEntries = TableInfo.read(db, "entries");
        if (!_infoEntries.equals(_existingEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "entries(com.faisal.financecalc.data.FinanceEntry).\n"
                  + " Expected:\n" + _infoEntries + "\n"
                  + " Found:\n" + _existingEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsShopItems = new HashMap<String, TableInfo.Column>(7);
        _columnsShopItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsShopItems.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsShopItems.put("count", new TableInfo.Column("count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsShopItems.put("pricePerUnit", new TableInfo.Column("pricePerUnit", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsShopItems.put("purchasePrice", new TableInfo.Column("purchasePrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsShopItems.put("orderIndex", new TableInfo.Column("orderIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsShopItems.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysShopItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesShopItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoShopItems = new TableInfo("shop_items", _columnsShopItems, _foreignKeysShopItems, _indicesShopItems);
        final TableInfo _existingShopItems = TableInfo.read(db, "shop_items");
        if (!_infoShopItems.equals(_existingShopItems)) {
          return new RoomOpenHelper.ValidationResult(false, "shop_items(com.faisal.financecalc.data.ShopItem).\n"
                  + " Expected:\n" + _infoShopItems + "\n"
                  + " Found:\n" + _existingShopItems);
        }
        final HashMap<String, TableInfo.Column> _columnsSoldItems = new HashMap<String, TableInfo.Column>(6);
        _columnsSoldItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoldItems.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoldItems.put("profit", new TableInfo.Column("profit", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoldItems.put("dateTimestamp", new TableInfo.Column("dateTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoldItems.put("month", new TableInfo.Column("month", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoldItems.put("year", new TableInfo.Column("year", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSoldItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSoldItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSoldItems = new TableInfo("sold_items", _columnsSoldItems, _foreignKeysSoldItems, _indicesSoldItems);
        final TableInfo _existingSoldItems = TableInfo.read(db, "sold_items");
        if (!_infoSoldItems.equals(_existingSoldItems)) {
          return new RoomOpenHelper.ValidationResult(false, "sold_items(com.faisal.financecalc.data.SoldItem).\n"
                  + " Expected:\n" + _infoSoldItems + "\n"
                  + " Found:\n" + _existingSoldItems);
        }
        final HashMap<String, TableInfo.Column> _columnsEntryHistory = new HashMap<String, TableInfo.Column>(5);
        _columnsEntryHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntryHistory.put("entryId", new TableInfo.Column("entryId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntryHistory.put("oldAmount", new TableInfo.Column("oldAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntryHistory.put("newAmount", new TableInfo.Column("newAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEntryHistory.put("dateTimestamp", new TableInfo.Column("dateTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEntryHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEntryHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEntryHistory = new TableInfo("entry_history", _columnsEntryHistory, _foreignKeysEntryHistory, _indicesEntryHistory);
        final TableInfo _existingEntryHistory = TableInfo.read(db, "entry_history");
        if (!_infoEntryHistory.equals(_existingEntryHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "entry_history(com.faisal.financecalc.data.HistoryEntry).\n"
                  + " Expected:\n" + _infoEntryHistory + "\n"
                  + " Found:\n" + _existingEntryHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsCreditCards = new HashMap<String, TableInfo.Column>(7);
        _columnsCreditCards.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("holderName", new TableInfo.Column("holderName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("cardNumber", new TableInfo.Column("cardNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("expiryDate", new TableInfo.Column("expiryDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("balance", new TableInfo.Column("balance", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("cardType", new TableInfo.Column("cardType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCreditCards.put("colorTheme", new TableInfo.Column("colorTheme", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCreditCards = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCreditCards = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCreditCards = new TableInfo("credit_cards", _columnsCreditCards, _foreignKeysCreditCards, _indicesCreditCards);
        final TableInfo _existingCreditCards = TableInfo.read(db, "credit_cards");
        if (!_infoCreditCards.equals(_existingCreditCards)) {
          return new RoomOpenHelper.ValidationResult(false, "credit_cards(com.faisal.financecalc.data.CreditCard).\n"
                  + " Expected:\n" + _infoCreditCards + "\n"
                  + " Found:\n" + _existingCreditCards);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "1e71eaf20ec3e4de92e8fd0f4f572f16", "7ee0d39d44df4c76f0e5708bd2b260d4");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "entries","shop_items","sold_items","entry_history","credit_cards");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `entries`");
      _db.execSQL("DELETE FROM `shop_items`");
      _db.execSQL("DELETE FROM `sold_items`");
      _db.execSQL("DELETE FROM `entry_history`");
      _db.execSQL("DELETE FROM `credit_cards`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(FinanceDao.class, FinanceDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public FinanceDao financeDao() {
    if (_financeDao != null) {
      return _financeDao;
    } else {
      synchronized(this) {
        if(_financeDao == null) {
          _financeDao = new FinanceDao_Impl(this);
        }
        return _financeDao;
      }
    }
  }
}
