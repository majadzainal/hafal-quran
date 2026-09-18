package com.hafalquran.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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
public final class AyahDao_Impl implements AyahDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AyahEntity> __insertionAdapterOfAyahEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMemorizationStatus;

  private final SharedSQLiteStatement __preparedStmtOfIncrementReviewCount;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLocalAudioPath;

  public AyahDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAyahEntity = new EntityInsertionAdapter<AyahEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ayahs` (`surahNumber`,`ayahNumber`,`textArabic`,`textLatin`,`translationId`,`audioUrl`,`localAudioPath`,`isMemorized`,`memorizationLevel`,`reviewCount`,`lastReviewedTimestamp`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AyahEntity entity) {
        statement.bindLong(1, entity.getSurahNumber());
        statement.bindLong(2, entity.getAyahNumber());
        statement.bindString(3, entity.getTextArabic());
        statement.bindString(4, entity.getTextLatin());
        statement.bindString(5, entity.getTranslationId());
        statement.bindString(6, entity.getAudioUrl());
        if (entity.getLocalAudioPath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getLocalAudioPath());
        }
        final int _tmp = entity.isMemorized() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindString(9, entity.getMemorizationLevel());
        statement.bindLong(10, entity.getReviewCount());
        statement.bindLong(11, entity.getLastReviewedTimestamp());
      }
    };
    this.__preparedStmtOfUpdateMemorizationStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE ayahs SET memorizationLevel = ?, isMemorized = ?, lastReviewedTimestamp = ? WHERE surahNumber = ? AND ayahNumber = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementReviewCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE ayahs SET reviewCount = reviewCount + 1, lastReviewedTimestamp = ? WHERE surahNumber = ? AND ayahNumber = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLocalAudioPath = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE ayahs SET localAudioPath = ? WHERE surahNumber = ? AND ayahNumber = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAyahs(final List<AyahEntity> ayahs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAyahEntity.insert(ayahs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMemorizationStatus(final int surahNumber, final int ayahNumber,
      final String level, final boolean isMemorized, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMemorizationStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, level);
        _argIndex = 2;
        final int _tmp = isMemorized ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, surahNumber);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, ayahNumber);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateMemorizationStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementReviewCount(final int surahNumber, final int ayahNumber,
      final long timestamp, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementReviewCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, surahNumber);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, ayahNumber);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementReviewCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLocalAudioPath(final int surahNumber, final int ayahNumber, final String path,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLocalAudioPath.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, path);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, surahNumber);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, ayahNumber);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateLocalAudioPath.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AyahEntity>> getAyahsBySurah(final int surahNumber) {
    final String _sql = "SELECT * FROM ayahs WHERE surahNumber = ? ORDER BY ayahNumber ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, surahNumber);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ayahs"}, new Callable<List<AyahEntity>>() {
      @Override
      @NonNull
      public List<AyahEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSurahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "surahNumber");
          final int _cursorIndexOfAyahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahNumber");
          final int _cursorIndexOfTextArabic = CursorUtil.getColumnIndexOrThrow(_cursor, "textArabic");
          final int _cursorIndexOfTextLatin = CursorUtil.getColumnIndexOrThrow(_cursor, "textLatin");
          final int _cursorIndexOfTranslationId = CursorUtil.getColumnIndexOrThrow(_cursor, "translationId");
          final int _cursorIndexOfAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "audioUrl");
          final int _cursorIndexOfLocalAudioPath = CursorUtil.getColumnIndexOrThrow(_cursor, "localAudioPath");
          final int _cursorIndexOfIsMemorized = CursorUtil.getColumnIndexOrThrow(_cursor, "isMemorized");
          final int _cursorIndexOfMemorizationLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "memorizationLevel");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedTimestamp");
          final List<AyahEntity> _result = new ArrayList<AyahEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AyahEntity _item;
            final int _tmpSurahNumber;
            _tmpSurahNumber = _cursor.getInt(_cursorIndexOfSurahNumber);
            final int _tmpAyahNumber;
            _tmpAyahNumber = _cursor.getInt(_cursorIndexOfAyahNumber);
            final String _tmpTextArabic;
            _tmpTextArabic = _cursor.getString(_cursorIndexOfTextArabic);
            final String _tmpTextLatin;
            _tmpTextLatin = _cursor.getString(_cursorIndexOfTextLatin);
            final String _tmpTranslationId;
            _tmpTranslationId = _cursor.getString(_cursorIndexOfTranslationId);
            final String _tmpAudioUrl;
            _tmpAudioUrl = _cursor.getString(_cursorIndexOfAudioUrl);
            final String _tmpLocalAudioPath;
            if (_cursor.isNull(_cursorIndexOfLocalAudioPath)) {
              _tmpLocalAudioPath = null;
            } else {
              _tmpLocalAudioPath = _cursor.getString(_cursorIndexOfLocalAudioPath);
            }
            final boolean _tmpIsMemorized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsMemorized);
            _tmpIsMemorized = _tmp != 0;
            final String _tmpMemorizationLevel;
            _tmpMemorizationLevel = _cursor.getString(_cursorIndexOfMemorizationLevel);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final long _tmpLastReviewedTimestamp;
            _tmpLastReviewedTimestamp = _cursor.getLong(_cursorIndexOfLastReviewedTimestamp);
            _item = new AyahEntity(_tmpSurahNumber,_tmpAyahNumber,_tmpTextArabic,_tmpTextLatin,_tmpTranslationId,_tmpAudioUrl,_tmpLocalAudioPath,_tmpIsMemorized,_tmpMemorizationLevel,_tmpReviewCount,_tmpLastReviewedTimestamp);
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
  public Object getAyahsRange(final int surahNumber, final int start, final int end,
      final Continuation<? super List<AyahEntity>> $completion) {
    final String _sql = "SELECT * FROM ayahs WHERE surahNumber = ? AND ayahNumber BETWEEN ? AND ? ORDER BY ayahNumber ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, surahNumber);
    _argIndex = 2;
    _statement.bindLong(_argIndex, start);
    _argIndex = 3;
    _statement.bindLong(_argIndex, end);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AyahEntity>>() {
      @Override
      @NonNull
      public List<AyahEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSurahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "surahNumber");
          final int _cursorIndexOfAyahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahNumber");
          final int _cursorIndexOfTextArabic = CursorUtil.getColumnIndexOrThrow(_cursor, "textArabic");
          final int _cursorIndexOfTextLatin = CursorUtil.getColumnIndexOrThrow(_cursor, "textLatin");
          final int _cursorIndexOfTranslationId = CursorUtil.getColumnIndexOrThrow(_cursor, "translationId");
          final int _cursorIndexOfAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "audioUrl");
          final int _cursorIndexOfLocalAudioPath = CursorUtil.getColumnIndexOrThrow(_cursor, "localAudioPath");
          final int _cursorIndexOfIsMemorized = CursorUtil.getColumnIndexOrThrow(_cursor, "isMemorized");
          final int _cursorIndexOfMemorizationLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "memorizationLevel");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedTimestamp");
          final List<AyahEntity> _result = new ArrayList<AyahEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AyahEntity _item;
            final int _tmpSurahNumber;
            _tmpSurahNumber = _cursor.getInt(_cursorIndexOfSurahNumber);
            final int _tmpAyahNumber;
            _tmpAyahNumber = _cursor.getInt(_cursorIndexOfAyahNumber);
            final String _tmpTextArabic;
            _tmpTextArabic = _cursor.getString(_cursorIndexOfTextArabic);
            final String _tmpTextLatin;
            _tmpTextLatin = _cursor.getString(_cursorIndexOfTextLatin);
            final String _tmpTranslationId;
            _tmpTranslationId = _cursor.getString(_cursorIndexOfTranslationId);
            final String _tmpAudioUrl;
            _tmpAudioUrl = _cursor.getString(_cursorIndexOfAudioUrl);
            final String _tmpLocalAudioPath;
            if (_cursor.isNull(_cursorIndexOfLocalAudioPath)) {
              _tmpLocalAudioPath = null;
            } else {
              _tmpLocalAudioPath = _cursor.getString(_cursorIndexOfLocalAudioPath);
            }
            final boolean _tmpIsMemorized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsMemorized);
            _tmpIsMemorized = _tmp != 0;
            final String _tmpMemorizationLevel;
            _tmpMemorizationLevel = _cursor.getString(_cursorIndexOfMemorizationLevel);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final long _tmpLastReviewedTimestamp;
            _tmpLastReviewedTimestamp = _cursor.getLong(_cursorIndexOfLastReviewedTimestamp);
            _item = new AyahEntity(_tmpSurahNumber,_tmpAyahNumber,_tmpTextArabic,_tmpTextLatin,_tmpTranslationId,_tmpAudioUrl,_tmpLocalAudioPath,_tmpIsMemorized,_tmpMemorizationLevel,_tmpReviewCount,_tmpLastReviewedTimestamp);
            _result.add(_item);
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
  public Object getAyah(final int surahNumber, final int ayahNumber,
      final Continuation<? super AyahEntity> $completion) {
    final String _sql = "SELECT * FROM ayahs WHERE surahNumber = ? AND ayahNumber = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, surahNumber);
    _argIndex = 2;
    _statement.bindLong(_argIndex, ayahNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AyahEntity>() {
      @Override
      @Nullable
      public AyahEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSurahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "surahNumber");
          final int _cursorIndexOfAyahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahNumber");
          final int _cursorIndexOfTextArabic = CursorUtil.getColumnIndexOrThrow(_cursor, "textArabic");
          final int _cursorIndexOfTextLatin = CursorUtil.getColumnIndexOrThrow(_cursor, "textLatin");
          final int _cursorIndexOfTranslationId = CursorUtil.getColumnIndexOrThrow(_cursor, "translationId");
          final int _cursorIndexOfAudioUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "audioUrl");
          final int _cursorIndexOfLocalAudioPath = CursorUtil.getColumnIndexOrThrow(_cursor, "localAudioPath");
          final int _cursorIndexOfIsMemorized = CursorUtil.getColumnIndexOrThrow(_cursor, "isMemorized");
          final int _cursorIndexOfMemorizationLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "memorizationLevel");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedTimestamp");
          final AyahEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpSurahNumber;
            _tmpSurahNumber = _cursor.getInt(_cursorIndexOfSurahNumber);
            final int _tmpAyahNumber;
            _tmpAyahNumber = _cursor.getInt(_cursorIndexOfAyahNumber);
            final String _tmpTextArabic;
            _tmpTextArabic = _cursor.getString(_cursorIndexOfTextArabic);
            final String _tmpTextLatin;
            _tmpTextLatin = _cursor.getString(_cursorIndexOfTextLatin);
            final String _tmpTranslationId;
            _tmpTranslationId = _cursor.getString(_cursorIndexOfTranslationId);
            final String _tmpAudioUrl;
            _tmpAudioUrl = _cursor.getString(_cursorIndexOfAudioUrl);
            final String _tmpLocalAudioPath;
            if (_cursor.isNull(_cursorIndexOfLocalAudioPath)) {
              _tmpLocalAudioPath = null;
            } else {
              _tmpLocalAudioPath = _cursor.getString(_cursorIndexOfLocalAudioPath);
            }
            final boolean _tmpIsMemorized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsMemorized);
            _tmpIsMemorized = _tmp != 0;
            final String _tmpMemorizationLevel;
            _tmpMemorizationLevel = _cursor.getString(_cursorIndexOfMemorizationLevel);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final long _tmpLastReviewedTimestamp;
            _tmpLastReviewedTimestamp = _cursor.getLong(_cursorIndexOfLastReviewedTimestamp);
            _result = new AyahEntity(_tmpSurahNumber,_tmpAyahNumber,_tmpTextArabic,_tmpTextLatin,_tmpTranslationId,_tmpAudioUrl,_tmpLocalAudioPath,_tmpIsMemorized,_tmpMemorizationLevel,_tmpReviewCount,_tmpLastReviewedTimestamp);
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
  public Object getAyahsCountForSurah(final int surahNumber,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM ayahs WHERE surahNumber = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, surahNumber);
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
  public Flow<Integer> getMemorizedAyahsCount() {
    final String _sql = "SELECT COUNT(*) FROM ayahs WHERE isMemorized = 1 OR memorizationLevel = 'MUTQIN'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ayahs"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getCountByLevel(final String level) {
    final String _sql = "SELECT COUNT(*) FROM ayahs WHERE memorizationLevel = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, level);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ayahs"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getJuz30MutqinCount() {
    final String _sql = "SELECT COUNT(*) FROM ayahs WHERE surahNumber >= 78 AND surahNumber <= 114 AND (isMemorized = 1 OR memorizationLevel = 'MUTQIN')";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ayahs"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getTotalAyahsInDb() {
    final String _sql = "SELECT COUNT(*) FROM ayahs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ayahs"}, new Callable<Integer>() {
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
}
