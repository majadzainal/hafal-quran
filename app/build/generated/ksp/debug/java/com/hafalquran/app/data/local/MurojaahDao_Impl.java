package com.hafalquran.app.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
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
public final class MurojaahDao_Impl implements MurojaahDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MurojaahHistoryEntity> __insertionAdapterOfMurojaahHistoryEntity;

  public MurojaahDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMurojaahHistoryEntity = new EntityInsertionAdapter<MurojaahHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `murojaah_history` (`id`,`surahNumber`,`startAyah`,`endAyah`,`timestamp`,`totalRepetitions`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MurojaahHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSurahNumber());
        statement.bindLong(3, entity.getStartAyah());
        statement.bindLong(4, entity.getEndAyah());
        statement.bindLong(5, entity.getTimestamp());
        statement.bindLong(6, entity.getTotalRepetitions());
      }
    };
  }

  @Override
  public Object insertHistory(final MurojaahHistoryEntity history,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMurojaahHistoryEntity.insert(history);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MurojaahHistoryEntity>> getRecentHistory() {
    final String _sql = "SELECT * FROM murojaah_history ORDER BY timestamp DESC LIMIT 20";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"murojaah_history"}, new Callable<List<MurojaahHistoryEntity>>() {
      @Override
      @NonNull
      public List<MurojaahHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSurahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "surahNumber");
          final int _cursorIndexOfStartAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "startAyah");
          final int _cursorIndexOfEndAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "endAyah");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfTotalRepetitions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalRepetitions");
          final List<MurojaahHistoryEntity> _result = new ArrayList<MurojaahHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MurojaahHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpSurahNumber;
            _tmpSurahNumber = _cursor.getInt(_cursorIndexOfSurahNumber);
            final int _tmpStartAyah;
            _tmpStartAyah = _cursor.getInt(_cursorIndexOfStartAyah);
            final int _tmpEndAyah;
            _tmpEndAyah = _cursor.getInt(_cursorIndexOfEndAyah);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final int _tmpTotalRepetitions;
            _tmpTotalRepetitions = _cursor.getInt(_cursorIndexOfTotalRepetitions);
            _item = new MurojaahHistoryEntity(_tmpId,_tmpSurahNumber,_tmpStartAyah,_tmpEndAyah,_tmpTimestamp,_tmpTotalRepetitions);
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
}
