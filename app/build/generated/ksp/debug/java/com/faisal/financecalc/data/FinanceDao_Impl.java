package com.faisal.financecalc.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FinanceDao_Impl implements FinanceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FinanceEntry> __insertionAdapterOfFinanceEntry;

  private final EntityInsertionAdapter<ShopItem> __insertionAdapterOfShopItem;

  private final EntityInsertionAdapter<SoldItem> __insertionAdapterOfSoldItem;

  private final EntityInsertionAdapter<HistoryEntry> __insertionAdapterOfHistoryEntry;

  private final EntityInsertionAdapter<CreditCard> __insertionAdapterOfCreditCard;

  private final EntityDeletionOrUpdateAdapter<FinanceEntry> __deletionAdapterOfFinanceEntry;

  private final EntityDeletionOrUpdateAdapter<ShopItem> __deletionAdapterOfShopItem;

  private final EntityDeletionOrUpdateAdapter<SoldItem> __deletionAdapterOfSoldItem;

  private final EntityDeletionOrUpdateAdapter<CreditCard> __deletionAdapterOfCreditCard;

  private final EntityDeletionOrUpdateAdapter<FinanceEntry> __updateAdapterOfFinanceEntry;

  private final EntityDeletionOrUpdateAdapter<ShopItem> __updateAdapterOfShopItem;

  private final EntityDeletionOrUpdateAdapter<SoldItem> __updateAdapterOfSoldItem;

  public FinanceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFinanceEntry = new EntityInsertionAdapter<FinanceEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `entries` (`id`,`name`,`amount`,`type`,`category`,`isAutoCalculated`,`excludedFromTotal`,`orderIndex`,`dateTimestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FinanceEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getAmount());
        statement.bindString(4, __EntryType_enumToString(entity.getType()));
        statement.bindString(5, entity.getCategory());
        final int _tmp = entity.isAutoCalculated() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.getExcludedFromTotal() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getOrderIndex());
        statement.bindLong(9, entity.getDateTimestamp());
      }
    };
    this.__insertionAdapterOfShopItem = new EntityInsertionAdapter<ShopItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `shop_items` (`id`,`name`,`count`,`pricePerUnit`,`purchasePrice`,`orderIndex`,`category`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ShopItem entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getCount());
        statement.bindDouble(4, entity.getPricePerUnit());
        statement.bindDouble(5, entity.getPurchasePrice());
        statement.bindLong(6, entity.getOrderIndex());
        statement.bindString(7, entity.getCategory());
      }
    };
    this.__insertionAdapterOfSoldItem = new EntityInsertionAdapter<SoldItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sold_items` (`id`,`name`,`profit`,`dateTimestamp`,`month`,`year`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SoldItem entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getProfit());
        statement.bindLong(4, entity.getDateTimestamp());
        statement.bindLong(5, entity.getMonth());
        statement.bindLong(6, entity.getYear());
      }
    };
    this.__insertionAdapterOfHistoryEntry = new EntityInsertionAdapter<HistoryEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `entry_history` (`id`,`entryId`,`oldAmount`,`newAmount`,`dateTimestamp`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HistoryEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getEntryId());
        statement.bindDouble(3, entity.getOldAmount());
        statement.bindDouble(4, entity.getNewAmount());
        statement.bindLong(5, entity.getDateTimestamp());
      }
    };
    this.__insertionAdapterOfCreditCard = new EntityInsertionAdapter<CreditCard>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `credit_cards` (`id`,`holderName`,`cardNumber`,`expiryDate`,`balance`,`cardType`,`colorTheme`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CreditCard entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getHolderName());
        statement.bindString(3, entity.getCardNumber());
        statement.bindString(4, entity.getExpiryDate());
        statement.bindDouble(5, entity.getBalance());
        statement.bindString(6, entity.getCardType());
        statement.bindLong(7, entity.getColorTheme());
      }
    };
    this.__deletionAdapterOfFinanceEntry = new EntityDeletionOrUpdateAdapter<FinanceEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FinanceEntry entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfShopItem = new EntityDeletionOrUpdateAdapter<ShopItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `shop_items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ShopItem entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfSoldItem = new EntityDeletionOrUpdateAdapter<SoldItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `sold_items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SoldItem entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfCreditCard = new EntityDeletionOrUpdateAdapter<CreditCard>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `credit_cards` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CreditCard entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFinanceEntry = new EntityDeletionOrUpdateAdapter<FinanceEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `entries` SET `id` = ?,`name` = ?,`amount` = ?,`type` = ?,`category` = ?,`isAutoCalculated` = ?,`excludedFromTotal` = ?,`orderIndex` = ?,`dateTimestamp` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FinanceEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getAmount());
        statement.bindString(4, __EntryType_enumToString(entity.getType()));
        statement.bindString(5, entity.getCategory());
        final int _tmp = entity.isAutoCalculated() ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.getExcludedFromTotal() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getOrderIndex());
        statement.bindLong(9, entity.getDateTimestamp());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__updateAdapterOfShopItem = new EntityDeletionOrUpdateAdapter<ShopItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `shop_items` SET `id` = ?,`name` = ?,`count` = ?,`pricePerUnit` = ?,`purchasePrice` = ?,`orderIndex` = ?,`category` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ShopItem entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getCount());
        statement.bindDouble(4, entity.getPricePerUnit());
        statement.bindDouble(5, entity.getPurchasePrice());
        statement.bindLong(6, entity.getOrderIndex());
        statement.bindString(7, entity.getCategory());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__updateAdapterOfSoldItem = new EntityDeletionOrUpdateAdapter<SoldItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sold_items` SET `id` = ?,`name` = ?,`profit` = ?,`dateTimestamp` = ?,`month` = ?,`year` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SoldItem entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getProfit());
        statement.bindLong(4, entity.getDateTimestamp());
        statement.bindLong(5, entity.getMonth());
        statement.bindLong(6, entity.getYear());
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insertEntry(final FinanceEntry entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFinanceEntry.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertShopItem(final ShopItem item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfShopItem.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSoldItem(final SoldItem item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSoldItem.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertHistoryEntry(final HistoryEntry history,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfHistoryEntry.insert(history);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCreditCard(final CreditCard card,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCreditCard.insert(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteEntry(final FinanceEntry entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFinanceEntry.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteShopItem(final ShopItem item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfShopItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSoldItem(final SoldItem item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSoldItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCreditCard(final CreditCard card,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCreditCard.handle(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateEntry(final FinanceEntry entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFinanceEntry.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateShopItem(final ShopItem item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfShopItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSoldItem(final SoldItem item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSoldItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FinanceEntry>> getAllEntries() {
    final String _sql = "SELECT * FROM entries";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"entries"}, new Callable<List<FinanceEntry>>() {
      @Override
      @NonNull
      public List<FinanceEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsAutoCalculated = CursorUtil.getColumnIndexOrThrow(_cursor, "isAutoCalculated");
          final int _cursorIndexOfExcludedFromTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "excludedFromTotal");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfDateTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimestamp");
          final List<FinanceEntry> _result = new ArrayList<FinanceEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FinanceEntry _item;
            _item = new FinanceEntry();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            _item.setName(_tmpName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _item.setAmount(_tmpAmount);
            final EntryType _tmpType;
            _tmpType = __EntryType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            _item.setType(_tmpType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            _item.setCategory(_tmpCategory);
            final boolean _tmpIsAutoCalculated;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAutoCalculated);
            _tmpIsAutoCalculated = _tmp != 0;
            _item.setAutoCalculated(_tmpIsAutoCalculated);
            final boolean _tmpExcludedFromTotal;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfExcludedFromTotal);
            _tmpExcludedFromTotal = _tmp_1 != 0;
            _item.setExcludedFromTotal(_tmpExcludedFromTotal);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _item.setOrderIndex(_tmpOrderIndex);
            final long _tmpDateTimestamp;
            _tmpDateTimestamp = _cursor.getLong(_cursorIndexOfDateTimestamp);
            _item.setDateTimestamp(_tmpDateTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getEntryCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM entries";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getEntryByName(final String name,
      final Continuation<? super FinanceEntry> $completion) {
    final String _sql = "SELECT * FROM entries WHERE name = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FinanceEntry>() {
      @Override
      @Nullable
      public FinanceEntry call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsAutoCalculated = CursorUtil.getColumnIndexOrThrow(_cursor, "isAutoCalculated");
          final int _cursorIndexOfExcludedFromTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "excludedFromTotal");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfDateTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimestamp");
          final FinanceEntry _result;
          if (_cursor.moveToFirst()) {
            _result = new FinanceEntry();
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            _result.setId(_tmpId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            _result.setName(_tmpName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            _result.setAmount(_tmpAmount);
            final EntryType _tmpType;
            _tmpType = __EntryType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            _result.setType(_tmpType);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            _result.setCategory(_tmpCategory);
            final boolean _tmpIsAutoCalculated;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAutoCalculated);
            _tmpIsAutoCalculated = _tmp != 0;
            _result.setAutoCalculated(_tmpIsAutoCalculated);
            final boolean _tmpExcludedFromTotal;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfExcludedFromTotal);
            _tmpExcludedFromTotal = _tmp_1 != 0;
            _result.setExcludedFromTotal(_tmpExcludedFromTotal);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _result.setOrderIndex(_tmpOrderIndex);
            final long _tmpDateTimestamp;
            _tmpDateTimestamp = _cursor.getLong(_cursorIndexOfDateTimestamp);
            _result.setDateTimestamp(_tmpDateTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ShopItem>> getAllShopItems() {
    final String _sql = "SELECT * FROM shop_items";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"shop_items"}, new Callable<List<ShopItem>>() {
      @Override
      @NonNull
      public List<ShopItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCount = CursorUtil.getColumnIndexOrThrow(_cursor, "count");
          final int _cursorIndexOfPricePerUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerUnit");
          final int _cursorIndexOfPurchasePrice = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasePrice");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final List<ShopItem> _result = new ArrayList<ShopItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ShopItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            final double _tmpPricePerUnit;
            _tmpPricePerUnit = _cursor.getDouble(_cursorIndexOfPricePerUnit);
            final double _tmpPurchasePrice;
            _tmpPurchasePrice = _cursor.getDouble(_cursorIndexOfPurchasePrice);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            _item = new ShopItem(_tmpId,_tmpName,_tmpCount,_tmpPricePerUnit,_tmpPurchasePrice,_tmpOrderIndex,_tmpCategory);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Double> getShopTotal() {
    final String _sql = "SELECT SUM(count * purchasePrice) FROM shop_items";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"shop_items"}, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<SoldItem>> getAllSoldItems() {
    final String _sql = "SELECT * FROM sold_items ORDER BY dateTimestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sold_items"}, new Callable<List<SoldItem>>() {
      @Override
      @NonNull
      public List<SoldItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "profit");
          final int _cursorIndexOfDateTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimestamp");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final List<SoldItem> _result = new ArrayList<SoldItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SoldItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpProfit;
            _tmpProfit = _cursor.getDouble(_cursorIndexOfProfit);
            final long _tmpDateTimestamp;
            _tmpDateTimestamp = _cursor.getLong(_cursorIndexOfDateTimestamp);
            final int _tmpMonth;
            _tmpMonth = _cursor.getInt(_cursorIndexOfMonth);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            _item = new SoldItem(_tmpId,_tmpName,_tmpProfit,_tmpDateTimestamp,_tmpMonth,_tmpYear);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MonthlyProfit>> getMonthlyProfits() {
    final String _sql = "SELECT year, month, SUM(profit) as profit FROM sold_items GROUP BY year, month ORDER BY year DESC, month DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sold_items"}, new Callable<List<MonthlyProfit>>() {
      @Override
      @NonNull
      public List<MonthlyProfit> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYear = 0;
          final int _cursorIndexOfMonth = 1;
          final int _cursorIndexOfProfit = 2;
          final List<MonthlyProfit> _result = new ArrayList<MonthlyProfit>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MonthlyProfit _item;
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            final int _tmpMonth;
            _tmpMonth = _cursor.getInt(_cursorIndexOfMonth);
            final double _tmpProfit;
            _tmpProfit = _cursor.getDouble(_cursorIndexOfProfit);
            _item = new MonthlyProfit(_tmpMonth,_tmpYear,_tmpProfit);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<HistoryEntry>> getHistoryForEntry(final long entryId) {
    final String _sql = "SELECT * FROM entry_history WHERE entryId = ? ORDER BY dateTimestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, entryId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"entry_history"}, new Callable<List<HistoryEntry>>() {
      @Override
      @NonNull
      public List<HistoryEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "entryId");
          final int _cursorIndexOfOldAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "oldAmount");
          final int _cursorIndexOfNewAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "newAmount");
          final int _cursorIndexOfDateTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimestamp");
          final List<HistoryEntry> _result = new ArrayList<HistoryEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HistoryEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpEntryId;
            _tmpEntryId = _cursor.getLong(_cursorIndexOfEntryId);
            final double _tmpOldAmount;
            _tmpOldAmount = _cursor.getDouble(_cursorIndexOfOldAmount);
            final double _tmpNewAmount;
            _tmpNewAmount = _cursor.getDouble(_cursorIndexOfNewAmount);
            final long _tmpDateTimestamp;
            _tmpDateTimestamp = _cursor.getLong(_cursorIndexOfDateTimestamp);
            _item = new HistoryEntry(_tmpId,_tmpEntryId,_tmpOldAmount,_tmpNewAmount,_tmpDateTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CreditCard>> getAllCreditCards() {
    final String _sql = "SELECT * FROM credit_cards";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"credit_cards"}, new Callable<List<CreditCard>>() {
      @Override
      @NonNull
      public List<CreditCard> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHolderName = CursorUtil.getColumnIndexOrThrow(_cursor, "holderName");
          final int _cursorIndexOfCardNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cardNumber");
          final int _cursorIndexOfExpiryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryDate");
          final int _cursorIndexOfBalance = CursorUtil.getColumnIndexOrThrow(_cursor, "balance");
          final int _cursorIndexOfCardType = CursorUtil.getColumnIndexOrThrow(_cursor, "cardType");
          final int _cursorIndexOfColorTheme = CursorUtil.getColumnIndexOrThrow(_cursor, "colorTheme");
          final List<CreditCard> _result = new ArrayList<CreditCard>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CreditCard _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpHolderName;
            _tmpHolderName = _cursor.getString(_cursorIndexOfHolderName);
            final String _tmpCardNumber;
            _tmpCardNumber = _cursor.getString(_cursorIndexOfCardNumber);
            final String _tmpExpiryDate;
            _tmpExpiryDate = _cursor.getString(_cursorIndexOfExpiryDate);
            final double _tmpBalance;
            _tmpBalance = _cursor.getDouble(_cursorIndexOfBalance);
            final String _tmpCardType;
            _tmpCardType = _cursor.getString(_cursorIndexOfCardType);
            final int _tmpColorTheme;
            _tmpColorTheme = _cursor.getInt(_cursorIndexOfColorTheme);
            _item = new CreditCard(_tmpId,_tmpHolderName,_tmpCardNumber,_tmpExpiryDate,_tmpBalance,_tmpCardType,_tmpColorTheme);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __EntryType_enumToString(@NonNull final EntryType _value) {
    switch (_value) {
      case INCOME: return "INCOME";
      case EXPENSE: return "EXPENSE";
      case DEBT: return "DEBT";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private EntryType __EntryType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "INCOME": return EntryType.INCOME;
      case "EXPENSE": return EntryType.EXPENSE;
      case "DEBT": return EntryType.DEBT;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
