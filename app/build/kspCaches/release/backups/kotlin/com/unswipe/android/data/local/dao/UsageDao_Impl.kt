package com.unswipe.android.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.unswipe.android.`data`.model.DailyUsageSummary
import com.unswipe.android.`data`.model.UsageEvent
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class UsageDao_Impl(
  __db: RoomDatabase,
) : UsageDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUsageEvent: EntityInsertAdapter<UsageEvent>

  private val __insertAdapterOfDailyUsageSummary: EntityInsertAdapter<DailyUsageSummary>
  init {
    this.__db = __db
    this.__insertAdapterOfUsageEvent = object : EntityInsertAdapter<UsageEvent>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `usage_events` (`id`,`timestamp`,`packageName`,`eventType`,`additionalData`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UsageEvent) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.timestamp)
        statement.bindText(3, entity.packageName)
        statement.bindText(4, entity.eventType)
        val _tmpAdditionalData: String? = entity.additionalData
        if (_tmpAdditionalData == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpAdditionalData)
        }
      }
    }
    this.__insertAdapterOfDailyUsageSummary = object : EntityInsertAdapter<DailyUsageSummary>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `daily_summaries` (`dateMillis`,`totalScreenTimeMillis`,`swipeCount`,`unlockCount`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DailyUsageSummary) {
        statement.bindLong(1, entity.dateMillis)
        statement.bindLong(2, entity.totalScreenTimeMillis)
        statement.bindLong(3, entity.swipeCount.toLong())
        statement.bindLong(4, entity.unlockCount.toLong())
      }
    }
  }

  public override suspend fun insertUsageEvent(event: UsageEvent): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfUsageEvent.insert(_connection, event)
  }

  public override suspend fun insertDailySummary(summary: DailyUsageSummary): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfDailyUsageSummary.insert(_connection, summary)
  }

  public override fun getUsageEventsForPeriodFlow(startTime: Long, endTime: Long):
      Flow<List<UsageEvent>> {
    val _sql: String =
        "SELECT * FROM usage_events WHERE timestamp >= ? AND timestamp < ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("usage_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startTime)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endTime)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfPackageName: Int = getColumnIndexOrThrow(_stmt, "packageName")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfAdditionalData: Int = getColumnIndexOrThrow(_stmt, "additionalData")
        val _result: MutableList<UsageEvent> = mutableListOf()
        while (_stmt.step()) {
          val _item: UsageEvent
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpPackageName: String
          _tmpPackageName = _stmt.getText(_columnIndexOfPackageName)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpAdditionalData: String?
          if (_stmt.isNull(_columnIndexOfAdditionalData)) {
            _tmpAdditionalData = null
          } else {
            _tmpAdditionalData = _stmt.getText(_columnIndexOfAdditionalData)
          }
          _item = UsageEvent(_tmpId,_tmpTimestamp,_tmpPackageName,_tmpEventType,_tmpAdditionalData)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDailySummariesFlow(startDateMillis: Long, limit: Int):
      Flow<List<DailyUsageSummary>> {
    val _sql: String =
        "SELECT * FROM daily_summaries WHERE dateMillis >= ? ORDER BY dateMillis DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("daily_summaries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startDateMillis)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfDateMillis: Int = getColumnIndexOrThrow(_stmt, "dateMillis")
        val _columnIndexOfTotalScreenTimeMillis: Int = getColumnIndexOrThrow(_stmt,
            "totalScreenTimeMillis")
        val _columnIndexOfSwipeCount: Int = getColumnIndexOrThrow(_stmt, "swipeCount")
        val _columnIndexOfUnlockCount: Int = getColumnIndexOrThrow(_stmt, "unlockCount")
        val _result: MutableList<DailyUsageSummary> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyUsageSummary
          val _tmpDateMillis: Long
          _tmpDateMillis = _stmt.getLong(_columnIndexOfDateMillis)
          val _tmpTotalScreenTimeMillis: Long
          _tmpTotalScreenTimeMillis = _stmt.getLong(_columnIndexOfTotalScreenTimeMillis)
          val _tmpSwipeCount: Int
          _tmpSwipeCount = _stmt.getLong(_columnIndexOfSwipeCount).toInt()
          val _tmpUnlockCount: Int
          _tmpUnlockCount = _stmt.getLong(_columnIndexOfUnlockCount).toInt()
          _item =
              DailyUsageSummary(_tmpDateMillis,_tmpTotalScreenTimeMillis,_tmpSwipeCount,_tmpUnlockCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDailySummary(dateMillis: Long): DailyUsageSummary? {
    val _sql: String = "SELECT * FROM daily_summaries WHERE dateMillis = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, dateMillis)
        val _columnIndexOfDateMillis: Int = getColumnIndexOrThrow(_stmt, "dateMillis")
        val _columnIndexOfTotalScreenTimeMillis: Int = getColumnIndexOrThrow(_stmt,
            "totalScreenTimeMillis")
        val _columnIndexOfSwipeCount: Int = getColumnIndexOrThrow(_stmt, "swipeCount")
        val _columnIndexOfUnlockCount: Int = getColumnIndexOrThrow(_stmt, "unlockCount")
        val _result: DailyUsageSummary?
        if (_stmt.step()) {
          val _tmpDateMillis: Long
          _tmpDateMillis = _stmt.getLong(_columnIndexOfDateMillis)
          val _tmpTotalScreenTimeMillis: Long
          _tmpTotalScreenTimeMillis = _stmt.getLong(_columnIndexOfTotalScreenTimeMillis)
          val _tmpSwipeCount: Int
          _tmpSwipeCount = _stmt.getLong(_columnIndexOfSwipeCount).toInt()
          val _tmpUnlockCount: Int
          _tmpUnlockCount = _stmt.getLong(_columnIndexOfUnlockCount).toInt()
          _result =
              DailyUsageSummary(_tmpDateMillis,_tmpTotalScreenTimeMillis,_tmpSwipeCount,_tmpUnlockCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestDailySummary(): DailyUsageSummary? {
    val _sql: String = "SELECT * FROM daily_summaries ORDER BY dateMillis DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDateMillis: Int = getColumnIndexOrThrow(_stmt, "dateMillis")
        val _columnIndexOfTotalScreenTimeMillis: Int = getColumnIndexOrThrow(_stmt,
            "totalScreenTimeMillis")
        val _columnIndexOfSwipeCount: Int = getColumnIndexOrThrow(_stmt, "swipeCount")
        val _columnIndexOfUnlockCount: Int = getColumnIndexOrThrow(_stmt, "unlockCount")
        val _result: DailyUsageSummary?
        if (_stmt.step()) {
          val _tmpDateMillis: Long
          _tmpDateMillis = _stmt.getLong(_columnIndexOfDateMillis)
          val _tmpTotalScreenTimeMillis: Long
          _tmpTotalScreenTimeMillis = _stmt.getLong(_columnIndexOfTotalScreenTimeMillis)
          val _tmpSwipeCount: Int
          _tmpSwipeCount = _stmt.getLong(_columnIndexOfSwipeCount).toInt()
          val _tmpUnlockCount: Int
          _tmpUnlockCount = _stmt.getLong(_columnIndexOfUnlockCount).toInt()
          _result =
              DailyUsageSummary(_tmpDateMillis,_tmpTotalScreenTimeMillis,_tmpSwipeCount,_tmpUnlockCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getSummariesSince(startDateMillis: Long): List<DailyUsageSummary> {
    val _sql: String = "SELECT * FROM daily_summaries WHERE dateMillis >= ? ORDER BY dateMillis ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startDateMillis)
        val _columnIndexOfDateMillis: Int = getColumnIndexOrThrow(_stmt, "dateMillis")
        val _columnIndexOfTotalScreenTimeMillis: Int = getColumnIndexOrThrow(_stmt,
            "totalScreenTimeMillis")
        val _columnIndexOfSwipeCount: Int = getColumnIndexOrThrow(_stmt, "swipeCount")
        val _columnIndexOfUnlockCount: Int = getColumnIndexOrThrow(_stmt, "unlockCount")
        val _result: MutableList<DailyUsageSummary> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyUsageSummary
          val _tmpDateMillis: Long
          _tmpDateMillis = _stmt.getLong(_columnIndexOfDateMillis)
          val _tmpTotalScreenTimeMillis: Long
          _tmpTotalScreenTimeMillis = _stmt.getLong(_columnIndexOfTotalScreenTimeMillis)
          val _tmpSwipeCount: Int
          _tmpSwipeCount = _stmt.getLong(_columnIndexOfSwipeCount).toInt()
          val _tmpUnlockCount: Int
          _tmpUnlockCount = _stmt.getLong(_columnIndexOfUnlockCount).toInt()
          _item =
              DailyUsageSummary(_tmpDateMillis,_tmpTotalScreenTimeMillis,_tmpSwipeCount,_tmpUnlockCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEventCountSince(startTimeMillis: Long, eventType: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM usage_events WHERE timestamp >= ? AND eventType = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startTimeMillis)
        _argIndex = 2
        _stmt.bindText(_argIndex, eventType)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEventCountSince(
    startTimeMillis: Long,
    eventType: String,
    packageName: String,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usage_events WHERE timestamp >= ? AND eventType = ? AND packageName = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startTimeMillis)
        _argIndex = 2
        _stmt.bindText(_argIndex, eventType)
        _argIndex = 3
        _stmt.bindText(_argIndex, packageName)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEventsInRange(startTime: Long, endTime: Long): List<UsageEvent> {
    val _sql: String =
        "SELECT * FROM usage_events WHERE timestamp >= ? AND timestamp <= ? ORDER BY timestamp ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startTime)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endTime)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfPackageName: Int = getColumnIndexOrThrow(_stmt, "packageName")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfAdditionalData: Int = getColumnIndexOrThrow(_stmt, "additionalData")
        val _result: MutableList<UsageEvent> = mutableListOf()
        while (_stmt.step()) {
          val _item: UsageEvent
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpPackageName: String
          _tmpPackageName = _stmt.getText(_columnIndexOfPackageName)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpAdditionalData: String?
          if (_stmt.isNull(_columnIndexOfAdditionalData)) {
            _tmpAdditionalData = null
          } else {
            _tmpAdditionalData = _stmt.getText(_columnIndexOfAdditionalData)
          }
          _item = UsageEvent(_tmpId,_tmpTimestamp,_tmpPackageName,_tmpEventType,_tmpAdditionalData)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEventCountInRange(
    startTime: Long,
    endTime: Long,
    eventType: String,
    packageName: String,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM usage_events WHERE timestamp >= ? AND timestamp <= ? AND eventType = ? AND packageName = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, startTime)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endTime)
        _argIndex = 3
        _stmt.bindText(_argIndex, eventType)
        _argIndex = 4
        _stmt.bindText(_argIndex, packageName)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEventsForApp(packageName: String, startTime: Long):
      List<UsageEvent> {
    val _sql: String =
        "SELECT * FROM usage_events WHERE packageName = ? AND timestamp >= ? ORDER BY timestamp DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, packageName)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startTime)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfPackageName: Int = getColumnIndexOrThrow(_stmt, "packageName")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfAdditionalData: Int = getColumnIndexOrThrow(_stmt, "additionalData")
        val _result: MutableList<UsageEvent> = mutableListOf()
        while (_stmt.step()) {
          val _item: UsageEvent
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpPackageName: String
          _tmpPackageName = _stmt.getText(_columnIndexOfPackageName)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpAdditionalData: String?
          if (_stmt.isNull(_columnIndexOfAdditionalData)) {
            _tmpAdditionalData = null
          } else {
            _tmpAdditionalData = _stmt.getText(_columnIndexOfAdditionalData)
          }
          _item = UsageEvent(_tmpId,_tmpTimestamp,_tmpPackageName,_tmpEventType,_tmpAdditionalData)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEventsByType(eventType: String, startTime: Long):
      List<UsageEvent> {
    val _sql: String =
        "SELECT * FROM usage_events WHERE eventType = ? AND timestamp >= ? ORDER BY timestamp DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, eventType)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startTime)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfPackageName: Int = getColumnIndexOrThrow(_stmt, "packageName")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfAdditionalData: Int = getColumnIndexOrThrow(_stmt, "additionalData")
        val _result: MutableList<UsageEvent> = mutableListOf()
        while (_stmt.step()) {
          val _item: UsageEvent
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpPackageName: String
          _tmpPackageName = _stmt.getText(_columnIndexOfPackageName)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpAdditionalData: String?
          if (_stmt.isNull(_columnIndexOfAdditionalData)) {
            _tmpAdditionalData = null
          } else {
            _tmpAdditionalData = _stmt.getText(_columnIndexOfAdditionalData)
          }
          _item = UsageEvent(_tmpId,_tmpTimestamp,_tmpPackageName,_tmpEventType,_tmpAdditionalData)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOldUsageEvents(olderThanTimestamp: Long) {
    val _sql: String = "DELETE FROM usage_events WHERE timestamp < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, olderThanTimestamp)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOldSummaries(olderThanTimestamp: Long) {
    val _sql: String = "DELETE FROM daily_summaries WHERE dateMillis < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, olderThanTimestamp)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
