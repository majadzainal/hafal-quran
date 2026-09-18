package com.hafalquran.app.data.local;

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
public final class QuranDatabase_Impl extends QuranDatabase {
  private volatile SurahDao _surahDao;

  private volatile AyahDao _ayahDao;

  private volatile TargetTaskDao _targetTaskDao;

  private volatile MurojaahDao _murojaahDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `surahs` (`number` INTEGER NOT NULL, `nameArabic` TEXT NOT NULL, `nameLatin` TEXT NOT NULL, `nameTranslation` TEXT NOT NULL, `numberOfAyahs` INTEGER NOT NULL, `revelationType` TEXT NOT NULL, `juzNumber` INTEGER NOT NULL, PRIMARY KEY(`number`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ayahs` (`surahNumber` INTEGER NOT NULL, `ayahNumber` INTEGER NOT NULL, `textArabic` TEXT NOT NULL, `textLatin` TEXT NOT NULL, `translationId` TEXT NOT NULL, `audioUrl` TEXT NOT NULL, `localAudioPath` TEXT, `isMemorized` INTEGER NOT NULL, `memorizationLevel` TEXT NOT NULL, `reviewCount` INTEGER NOT NULL, `lastReviewedTimestamp` INTEGER NOT NULL, PRIMARY KEY(`surahNumber`, `ayahNumber`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `target_tasks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `surahNumber` INTEGER NOT NULL, `surahName` TEXT NOT NULL, `startAyah` INTEGER NOT NULL, `endAyah` INTEGER NOT NULL, `repeatPerAyah` INTEGER NOT NULL, `isLoopEntireSet` INTEGER NOT NULL, `pauseSeconds` REAL NOT NULL, `isCompleted` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `murojaah_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `surahNumber` INTEGER NOT NULL, `startAyah` INTEGER NOT NULL, `endAyah` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `totalRepetitions` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4a6b82cb654f2a3e82b2cefbb93899da')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `surahs`");
        db.execSQL("DROP TABLE IF EXISTS `ayahs`");
        db.execSQL("DROP TABLE IF EXISTS `target_tasks`");
        db.execSQL("DROP TABLE IF EXISTS `murojaah_history`");
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
        final HashMap<String, TableInfo.Column> _columnsSurahs = new HashMap<String, TableInfo.Column>(7);
        _columnsSurahs.put("number", new TableInfo.Column("number", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurahs.put("nameArabic", new TableInfo.Column("nameArabic", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurahs.put("nameLatin", new TableInfo.Column("nameLatin", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurahs.put("nameTranslation", new TableInfo.Column("nameTranslation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurahs.put("numberOfAyahs", new TableInfo.Column("numberOfAyahs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurahs.put("revelationType", new TableInfo.Column("revelationType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurahs.put("juzNumber", new TableInfo.Column("juzNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSurahs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSurahs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSurahs = new TableInfo("surahs", _columnsSurahs, _foreignKeysSurahs, _indicesSurahs);
        final TableInfo _existingSurahs = TableInfo.read(db, "surahs");
        if (!_infoSurahs.equals(_existingSurahs)) {
          return new RoomOpenHelper.ValidationResult(false, "surahs(com.hafalquran.app.data.local.SurahEntity).\n"
                  + " Expected:\n" + _infoSurahs + "\n"
                  + " Found:\n" + _existingSurahs);
        }
        final HashMap<String, TableInfo.Column> _columnsAyahs = new HashMap<String, TableInfo.Column>(11);
        _columnsAyahs.put("surahNumber", new TableInfo.Column("surahNumber", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyahs.put("ayahNumber", new TableInfo.Column("ayahNumber", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyahs.put("textArabic", new TableInfo.Column("textArabic", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyahs.put("textLatin", new TableInfo.Column("textLatin", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyahs.put("translationId", new TableInfo.Column("translationId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyahs.put("audioUrl", new TableInfo.Column("audioUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyahs.put("localAudioPath", new TableInfo.Column("localAudioPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyahs.put("isMemorized", new TableInfo.Column("isMemorized", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyahs.put("memorizationLevel", new TableInfo.Column("memorizationLevel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyahs.put("reviewCount", new TableInfo.Column("reviewCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyahs.put("lastReviewedTimestamp", new TableInfo.Column("lastReviewedTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAyahs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAyahs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAyahs = new TableInfo("ayahs", _columnsAyahs, _foreignKeysAyahs, _indicesAyahs);
        final TableInfo _existingAyahs = TableInfo.read(db, "ayahs");
        if (!_infoAyahs.equals(_existingAyahs)) {
          return new RoomOpenHelper.ValidationResult(false, "ayahs(com.hafalquran.app.data.local.AyahEntity).\n"
                  + " Expected:\n" + _infoAyahs + "\n"
                  + " Found:\n" + _existingAyahs);
        }
        final HashMap<String, TableInfo.Column> _columnsTargetTasks = new HashMap<String, TableInfo.Column>(11);
        _columnsTargetTasks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargetTasks.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargetTasks.put("surahNumber", new TableInfo.Column("surahNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargetTasks.put("surahName", new TableInfo.Column("surahName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargetTasks.put("startAyah", new TableInfo.Column("startAyah", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargetTasks.put("endAyah", new TableInfo.Column("endAyah", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargetTasks.put("repeatPerAyah", new TableInfo.Column("repeatPerAyah", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargetTasks.put("isLoopEntireSet", new TableInfo.Column("isLoopEntireSet", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargetTasks.put("pauseSeconds", new TableInfo.Column("pauseSeconds", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargetTasks.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTargetTasks.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTargetTasks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTargetTasks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTargetTasks = new TableInfo("target_tasks", _columnsTargetTasks, _foreignKeysTargetTasks, _indicesTargetTasks);
        final TableInfo _existingTargetTasks = TableInfo.read(db, "target_tasks");
        if (!_infoTargetTasks.equals(_existingTargetTasks)) {
          return new RoomOpenHelper.ValidationResult(false, "target_tasks(com.hafalquran.app.data.local.TargetTaskEntity).\n"
                  + " Expected:\n" + _infoTargetTasks + "\n"
                  + " Found:\n" + _existingTargetTasks);
        }
        final HashMap<String, TableInfo.Column> _columnsMurojaahHistory = new HashMap<String, TableInfo.Column>(6);
        _columnsMurojaahHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMurojaahHistory.put("surahNumber", new TableInfo.Column("surahNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMurojaahHistory.put("startAyah", new TableInfo.Column("startAyah", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMurojaahHistory.put("endAyah", new TableInfo.Column("endAyah", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMurojaahHistory.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMurojaahHistory.put("totalRepetitions", new TableInfo.Column("totalRepetitions", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMurojaahHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMurojaahHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMurojaahHistory = new TableInfo("murojaah_history", _columnsMurojaahHistory, _foreignKeysMurojaahHistory, _indicesMurojaahHistory);
        final TableInfo _existingMurojaahHistory = TableInfo.read(db, "murojaah_history");
        if (!_infoMurojaahHistory.equals(_existingMurojaahHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "murojaah_history(com.hafalquran.app.data.local.MurojaahHistoryEntity).\n"
                  + " Expected:\n" + _infoMurojaahHistory + "\n"
                  + " Found:\n" + _existingMurojaahHistory);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4a6b82cb654f2a3e82b2cefbb93899da", "cc25210c04477072113aea71dbcaea30");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "surahs","ayahs","target_tasks","murojaah_history");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `surahs`");
      _db.execSQL("DELETE FROM `ayahs`");
      _db.execSQL("DELETE FROM `target_tasks`");
      _db.execSQL("DELETE FROM `murojaah_history`");
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
    _typeConvertersMap.put(SurahDao.class, SurahDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AyahDao.class, AyahDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TargetTaskDao.class, TargetTaskDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MurojaahDao.class, MurojaahDao_Impl.getRequiredConverters());
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
  public SurahDao surahDao() {
    if (_surahDao != null) {
      return _surahDao;
    } else {
      synchronized(this) {
        if(_surahDao == null) {
          _surahDao = new SurahDao_Impl(this);
        }
        return _surahDao;
      }
    }
  }

  @Override
  public AyahDao ayahDao() {
    if (_ayahDao != null) {
      return _ayahDao;
    } else {
      synchronized(this) {
        if(_ayahDao == null) {
          _ayahDao = new AyahDao_Impl(this);
        }
        return _ayahDao;
      }
    }
  }

  @Override
  public TargetTaskDao targetTaskDao() {
    if (_targetTaskDao != null) {
      return _targetTaskDao;
    } else {
      synchronized(this) {
        if(_targetTaskDao == null) {
          _targetTaskDao = new TargetTaskDao_Impl(this);
        }
        return _targetTaskDao;
      }
    }
  }

  @Override
  public MurojaahDao murojaahDao() {
    if (_murojaahDao != null) {
      return _murojaahDao;
    } else {
      synchronized(this) {
        if(_murojaahDao == null) {
          _murojaahDao = new MurojaahDao_Impl(this);
        }
        return _murojaahDao;
      }
    }
  }
}
