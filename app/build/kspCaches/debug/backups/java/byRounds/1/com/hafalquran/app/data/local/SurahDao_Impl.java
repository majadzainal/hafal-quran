package com.hafalquran.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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
public final class SurahDao_Impl implements SurahDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SurahEntity> __insertionAdapterOfSurahEntity;

  public SurahDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSurahEntity = new EntityInsertionAdapter<SurahEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `surahs` (`number`,`nameArabic`,`nameLatin`,`nameTranslation`,`numberOfAyahs`,`revelationType`,`juzNumber`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SurahEntity entity) {
        statement.bindLong(1, entity.getNumber());
        statement.bindString(2, entity.getNameArabic());
        statement.bindString(3, entity.getNameLatin());
        statement.bindString(4, entity.getNameTranslation());
        statement.bindLong(5, entity.getNumberOfAyahs());
        statement.bindString(6, entity.getRevelationType());
        statement.bindLong(7, entity.getJuzNumber());
      }
    };
  }

  @Override
  public Object insertSurahs(final List<SurahEntity> surahs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSurahEntity.insert(surahs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SurahEntity>> getAllSurahs() {
    final String _sql = "SELECT * FROM surahs ORDER BY number ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"surahs"}, new Callable<List<SurahEntity>>() {
      @Override
      @NonNull
      public List<SurahEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "number");
          final int _cursorIndexOfNameArabic = CursorUtil.getColumnIndexOrThrow(_cursor, "nameArabic");
          final int _cursorIndexOfNameLatin = CursorUtil.getColumnIndexOrThrow(_cursor, "nameLatin");
          final int _cursorIndexOfNameTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "nameTranslation");
          final int _cursorIndexOfNumberOfAyahs = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfAyahs");
          final int _cursorIndexOfRevelationType = CursorUtil.getColumnIndexOrThrow(_cursor, "revelationType");
          final int _cursorIndexOfJuzNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "juzNumber");
          final List<SurahEntity> _result = new ArrayList<SurahEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SurahEntity _item;
            final int _tmpNumber;
            _tmpNumber = _cursor.getInt(_cursorIndexOfNumber);
            final String _tmpNameArabic;
            _tmpNameArabic = _cursor.getString(_cursorIndexOfNameArabic);
            final String _tmpNameLatin;
            _tmpNameLatin = _cursor.getString(_cursorIndexOfNameLatin);
            final String _tmpNameTranslation;
            _tmpNameTranslation = _cursor.getString(_cursorIndexOfNameTranslation);
            final int _tmpNumberOfAyahs;
            _tmpNumberOfAyahs = _cursor.getInt(_cursorIndexOfNumberOfAyahs);
            final String _tmpRevelationType;
            _tmpRevelationType = _cursor.getString(_cursorIndexOfRevelationType);
            final int _tmpJuzNumber;
            _tmpJuzNumber = _cursor.getInt(_cursorIndexOfJuzNumber);
            _item = new SurahEntity(_tmpNumber,_tmpNameArabic,_tmpNameLatin,_tmpNameTranslation,_tmpNumberOfAyahs,_tmpRevelationType,_tmpJuzNumber);
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
  public Object getSurahByNumber(final int surahNumber,
      final Continuation<? super SurahEntity> $completion) {
    final String _sql = "SELECT * FROM surahs WHERE number = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, surahNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SurahEntity>() {
      @Override
      @Nullable
      public SurahEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "number");
          final int _cursorIndexOfNameArabic = CursorUtil.getColumnIndexOrThrow(_cursor, "nameArabic");
          final int _cursorIndexOfNameLatin = CursorUtil.getColumnIndexOrThrow(_cursor, "nameLatin");
          final int _cursorIndexOfNameTranslation = CursorUtil.getColumnIndexOrThrow(_cursor, "nameTranslation");
          final int _cursorIndexOfNumberOfAyahs = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfAyahs");
          final int _cursorIndexOfRevelationType = CursorUtil.getColumnIndexOrThrow(_cursor, "revelationType");
          final int _cursorIndexOfJuzNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "juzNumber");
          final SurahEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpNumber;
            _tmpNumber = _cursor.getInt(_cursorIndexOfNumber);
            final String _tmpNameArabic;
            _tmpNameArabic = _cursor.getString(_cursorIndexOfNameArabic);
            final String _tmpNameLatin;
            _tmpNameLatin = _cursor.getString(_cursorIndexOfNameLatin);
            final String _tmpNameTranslation;
            _tmpNameTranslation = _cursor.getString(_cursorIndexOfNameTranslation);
            final int _tmpNumberOfAyahs;
            _tmpNumberOfAyahs = _cursor.getInt(_cursorIndexOfNumberOfAyahs);
            final String _tmpRevelationType;
            _tmpRevelationType = _cursor.getString(_cursorIndexOfRevelationType);
            final int _tmpJuzNumber;
            _tmpJuzNumber = _cursor.getInt(_cursorIndexOfJuzNumber);
            _result = new SurahEntity(_tmpNumber,_tmpNameArabic,_tmpNameLatin,_tmpNameTranslation,_tmpNumberOfAyahs,_tmpRevelationType,_tmpJuzNumber);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
