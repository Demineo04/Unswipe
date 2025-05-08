package com.unswipe.android.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.unswipe.android.`data`.local.dao.UsageDao
import com.unswipe.android.`data`.local.dao.UsageDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _usageDao: Lazy<UsageDao> = lazy {
    UsageDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "5b8521b3f5069efdcae092550fabad91", "9f538dd0c73ded6fb04ca8c532c65503") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `usage_events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `packageName` TEXT NOT NULL, `eventType` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `daily_summaries` (`dateMillis` INTEGER NOT NULL, `totalScreenTimeMillis` INTEGER NOT NULL, `swipeCount` INTEGER NOT NULL, `unlockCount` INTEGER NOT NULL, PRIMARY KEY(`dateMillis`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '5b8521b3f5069efdcae092550fabad91')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `usage_events`")
        connection.execSQL("DROP TABLE IF EXISTS `daily_summaries`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsUsageEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsageEvents.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsageEvents.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsageEvents.put("packageName", TableInfo.Column("packageName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsageEvents.put("eventType", TableInfo.Column("eventType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsageEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsageEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUsageEvents: TableInfo = TableInfo("usage_events", _columnsUsageEvents,
            _foreignKeysUsageEvents, _indicesUsageEvents)
        val _existingUsageEvents: TableInfo = read(connection, "usage_events")
        if (!_infoUsageEvents.equals(_existingUsageEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |usage_events(com.unswipe.android.data.model.UsageEvent).
              | Expected:
              |""".trimMargin() + _infoUsageEvents + """
              |
              | Found:
              |""".trimMargin() + _existingUsageEvents)
        }
        val _columnsDailySummaries: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDailySummaries.put("dateMillis", TableInfo.Column("dateMillis", "INTEGER", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailySummaries.put("totalScreenTimeMillis",
            TableInfo.Column("totalScreenTimeMillis", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailySummaries.put("swipeCount", TableInfo.Column("swipeCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailySummaries.put("unlockCount", TableInfo.Column("unlockCount", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDailySummaries: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDailySummaries: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDailySummaries: TableInfo = TableInfo("daily_summaries", _columnsDailySummaries,
            _foreignKeysDailySummaries, _indicesDailySummaries)
        val _existingDailySummaries: TableInfo = read(connection, "daily_summaries")
        if (!_infoDailySummaries.equals(_existingDailySummaries)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |daily_summaries(com.unswipe.android.data.model.DailyUsageSummary).
              | Expected:
              |""".trimMargin() + _infoDailySummaries + """
              |
              | Found:
              |""".trimMargin() + _existingDailySummaries)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "usage_events",
        "daily_summaries")
  }

  public override fun clearAllTables() {
    super.performClear(false, "usage_events", "daily_summaries")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(UsageDao::class, UsageDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun usageDao(): UsageDao = _usageDao.value
}
