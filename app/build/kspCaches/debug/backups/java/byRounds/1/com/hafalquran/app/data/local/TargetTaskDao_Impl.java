package com.hafalquran.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
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
import java.lang.Long;
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
public final class TargetTaskDao_Impl implements TargetTaskDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TargetTaskEntity> __insertionAdapterOfTargetTaskEntity;

  private final EntityDeletionOrUpdateAdapter<TargetTaskEntity> __updateAdapterOfTargetTaskEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTaskStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTask;

  public TargetTaskDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTargetTaskEntity = new EntityInsertionAdapter<TargetTaskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `target_tasks` (`id`,`title`,`surahNumber`,`surahName`,`startAyah`,`endAyah`,`repeatPerAyah`,`isLoopEntireSet`,`pauseSeconds`,`isCompleted`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TargetTaskEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindLong(3, entity.getSurahNumber());
        statement.bindString(4, entity.getSurahName());
        statement.bindLong(5, entity.getStartAyah());
        statement.bindLong(6, entity.getEndAyah());
        statement.bindLong(7, entity.getRepeatPerAyah());
        final int _tmp = entity.isLoopEntireSet() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindDouble(9, entity.getPauseSeconds());
        final int _tmp_1 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        statement.bindLong(11, entity.getCreatedAt());
      }
    };
    this.__updateAdapterOfTargetTaskEntity = new EntityDeletionOrUpdateAdapter<TargetTaskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `target_tasks` SET `id` = ?,`title` = ?,`surahNumber` = ?,`surahName` = ?,`startAyah` = ?,`endAyah` = ?,`repeatPerAyah` = ?,`isLoopEntireSet` = ?,`pauseSeconds` = ?,`isCompleted` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TargetTaskEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindLong(3, entity.getSurahNumber());
        statement.bindString(4, entity.getSurahName());
        statement.bindLong(5, entity.getStartAyah());
        statement.bindLong(6, entity.getEndAyah());
        statement.bindLong(7, entity.getRepeatPerAyah());
        final int _tmp = entity.isLoopEntireSet() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindDouble(9, entity.getPauseSeconds());
        final int _tmp_1 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        statement.bindLong(11, entity.getCreatedAt());
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateTaskStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE target_tasks SET isCompleted = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteTask = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM target_tasks WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTask(final TargetTaskEntity task,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTargetTaskEntity.insertAndReturnId(task);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTask(final TargetTaskEntity task,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTargetTaskEntity.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTaskStatus(final long id, final boolean completed,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTaskStatus.acquire();
        int _argIndex = 1;
        final int _tmp = completed ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfUpdateTaskStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTask(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTask.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeleteTask.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TargetTaskEntity>> getAllTasks() {
    final String _sql = "SELECT * FROM target_tasks ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"target_tasks"}, new Callable<List<TargetTaskEntity>>() {
      @Override
      @NonNull
      public List<TargetTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSurahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "surahNumber");
          final int _cursorIndexOfSurahName = CursorUtil.getColumnIndexOrThrow(_cursor, "surahName");
          final int _cursorIndexOfStartAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "startAyah");
          final int _cursorIndexOfEndAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "endAyah");
          final int _cursorIndexOfRepeatPerAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatPerAyah");
          final int _cursorIndexOfIsLoopEntireSet = CursorUtil.getColumnIndexOrThrow(_cursor, "isLoopEntireSet");
          final int _cursorIndexOfPauseSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "pauseSeconds");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TargetTaskEntity> _result = new ArrayList<TargetTaskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TargetTaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final int _tmpSurahNumber;
            _tmpSurahNumber = _cursor.getInt(_cursorIndexOfSurahNumber);
            final String _tmpSurahName;
            _tmpSurahName = _cursor.getString(_cursorIndexOfSurahName);
            final int _tmpStartAyah;
            _tmpStartAyah = _cursor.getInt(_cursorIndexOfStartAyah);
            final int _tmpEndAyah;
            _tmpEndAyah = _cursor.getInt(_cursorIndexOfEndAyah);
            final int _tmpRepeatPerAyah;
            _tmpRepeatPerAyah = _cursor.getInt(_cursorIndexOfRepeatPerAyah);
            final boolean _tmpIsLoopEntireSet;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLoopEntireSet);
            _tmpIsLoopEntireSet = _tmp != 0;
            final float _tmpPauseSeconds;
            _tmpPauseSeconds = _cursor.getFloat(_cursorIndexOfPauseSeconds);
            final boolean _tmpIsCompleted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TargetTaskEntity(_tmpId,_tmpTitle,_tmpSurahNumber,_tmpSurahName,_tmpStartAyah,_tmpEndAyah,_tmpRepeatPerAyah,_tmpIsLoopEntireSet,_tmpPauseSeconds,_tmpIsCompleted,_tmpCreatedAt);
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
  public Object getTaskById(final long id,
      final Continuation<? super TargetTaskEntity> $completion) {
    final String _sql = "SELECT * FROM target_tasks WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TargetTaskEntity>() {
      @Override
      @Nullable
      public TargetTaskEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSurahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "surahNumber");
          final int _cursorIndexOfSurahName = CursorUtil.getColumnIndexOrThrow(_cursor, "surahName");
          final int _cursorIndexOfStartAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "startAyah");
          final int _cursorIndexOfEndAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "endAyah");
          final int _cursorIndexOfRepeatPerAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatPerAyah");
          final int _cursorIndexOfIsLoopEntireSet = CursorUtil.getColumnIndexOrThrow(_cursor, "isLoopEntireSet");
          final int _cursorIndexOfPauseSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "pauseSeconds");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final TargetTaskEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final int _tmpSurahNumber;
            _tmpSurahNumber = _cursor.getInt(_cursorIndexOfSurahNumber);
            final String _tmpSurahName;
            _tmpSurahName = _cursor.getString(_cursorIndexOfSurahName);
            final int _tmpStartAyah;
            _tmpStartAyah = _cursor.getInt(_cursorIndexOfStartAyah);
            final int _tmpEndAyah;
            _tmpEndAyah = _cursor.getInt(_cursorIndexOfEndAyah);
            final int _tmpRepeatPerAyah;
            _tmpRepeatPerAyah = _cursor.getInt(_cursorIndexOfRepeatPerAyah);
            final boolean _tmpIsLoopEntireSet;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLoopEntireSet);
            _tmpIsLoopEntireSet = _tmp != 0;
            final float _tmpPauseSeconds;
            _tmpPauseSeconds = _cursor.getFloat(_cursorIndexOfPauseSeconds);
            final boolean _tmpIsCompleted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new TargetTaskEntity(_tmpId,_tmpTitle,_tmpSurahNumber,_tmpSurahName,_tmpStartAyah,_tmpEndAyah,_tmpRepeatPerAyah,_tmpIsLoopEntireSet,_tmpPauseSeconds,_tmpIsCompleted,_tmpCreatedAt);
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
  public Flow<Integer> getTotalTasksCount() {
    final String _sql = "SELECT COUNT(*) FROM target_tasks";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"target_tasks"}, new Callable<Integer>() {
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
  public Flow<Integer> getCompletedTasksCount() {
    final String _sql = "SELECT COUNT(*) FROM target_tasks WHERE isCompleted = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"target_tasks"}, new Callable<Integer>() {
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
