package com.example.luma.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class SleepSession(
    val id: Long = 0,
    val date: String,
    val bedtime: String,
    val sleepOnset: String,
    val wakeUp: String,
    val durationMinutes: Int,
    val quality: Float,
    val notes: String
)

data class AlarmEntity(
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: String,
    val sound: String,
    val vibration: Boolean
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "luma_sleep.db"
        private const val DATABASE_VERSION = 1

        // Sleep Sessions Table
        const val TABLE_SLEEP = "sleep_sessions"
        const val COLUMN_SLEEP_ID = "id"
        const val COLUMN_SLEEP_DATE = "date"
        const val COLUMN_SLEEP_BEDTIME = "bedtime"
        const val COLUMN_SLEEP_ONSET = "sleep_onset"
        const val COLUMN_SLEEP_WAKE = "wake_up"
        const val COLUMN_SLEEP_DURATION = "duration_minutes"
        const val COLUMN_SLEEP_QUALITY = "quality"
        const val COLUMN_SLEEP_NOTES = "notes"

        // Alarms Table
        const val TABLE_ALARMS = "alarms"
        const val COLUMN_ALARM_ID = "id"
        const val COLUMN_ALARM_HOUR = "hour"
        const val COLUMN_ALARM_MINUTE = "minute"
        const val COLUMN_ALARM_LABEL = "label"
        const val COLUMN_ALARM_ENABLED = "is_enabled"
        const val COLUMN_ALARM_DAYS = "repeat_days"
        const val COLUMN_ALARM_SOUND = "sound"
        const val COLUMN_ALARM_VIBRATION = "vibration"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createSleepTable = """
            CREATE TABLE $TABLE_SLEEP (
                $COLUMN_SLEEP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SLEEP_DATE TEXT NOT NULL,
                $COLUMN_SLEEP_BEDTIME TEXT NOT NULL,
                $COLUMN_SLEEP_ONSET TEXT NOT NULL,
                $COLUMN_SLEEP_WAKE TEXT NOT NULL,
                $COLUMN_SLEEP_DURATION INTEGER NOT NULL,
                $COLUMN_SLEEP_QUALITY REAL NOT NULL,
                $COLUMN_SLEEP_NOTES TEXT
            )
        """

        val createAlarmsTable = """
            CREATE TABLE $TABLE_ALARMS (
                $COLUMN_ALARM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ALARM_HOUR INTEGER NOT NULL,
                $COLUMN_ALARM_MINUTE INTEGER NOT NULL,
                $COLUMN_ALARM_LABEL TEXT,
                $COLUMN_ALARM_ENABLED INTEGER NOT NULL,
                $COLUMN_ALARM_DAYS TEXT,
                $COLUMN_ALARM_SOUND TEXT,
                $COLUMN_ALARM_VIBRATION INTEGER NOT NULL
            )
        """

        db.execSQL(createSleepTable)
        db.execSQL(createAlarmsTable)

        // Insert sample initial data for testing
        val initialAlarm = ContentValues().apply {
            put(COLUMN_ALARM_HOUR, 7)
            put(COLUMN_ALARM_MINUTE, 0)
            put(COLUMN_ALARM_LABEL, "Morning Routine")
            put(COLUMN_ALARM_ENABLED, 1)
            put(COLUMN_ALARM_DAYS, "Mon, Tue, Wed, Thu, Fri")
            put(COLUMN_ALARM_SOUND, "default")
            put(COLUMN_ALARM_VIBRATION, 1)
        }
        db.insert(TABLE_ALARMS, null, initialAlarm)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SLEEP")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALARMS")
        onCreate(db)
    }

    // --- Sleep Session CRUD ---

    fun insertSleepSession(session: SleepSession): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SLEEP_DATE, session.date)
            put(COLUMN_SLEEP_BEDTIME, session.bedtime)
            put(COLUMN_SLEEP_ONSET, session.sleepOnset)
            put(COLUMN_SLEEP_WAKE, session.wakeUp)
            put(COLUMN_SLEEP_DURATION, session.durationMinutes)
            put(COLUMN_SLEEP_QUALITY, session.quality)
            put(COLUMN_SLEEP_NOTES, session.notes)
        }
        return db.insert(TABLE_SLEEP, null, values)
    }

    fun getAllSleepSessions(): List<SleepSession> {
        val sessions = mutableListOf<SleepSession>()
        val db = readableDatabase
        val cursor = db.query(TABLE_SLEEP, null, null, null, null, null, "$COLUMN_SLEEP_ID DESC")

        cursor.use {
            while (it.moveToNext()) {
                val session = SleepSession(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_SLEEP_ID)),
                    date = it.getString(it.getColumnIndexOrThrow(COLUMN_SLEEP_DATE)),
                    bedtime = it.getString(it.getColumnIndexOrThrow(COLUMN_SLEEP_BEDTIME)),
                    sleepOnset = it.getString(it.getColumnIndexOrThrow(COLUMN_SLEEP_ONSET)),
                    wakeUp = it.getString(it.getColumnIndexOrThrow(COLUMN_SLEEP_WAKE)),
                    durationMinutes = it.getInt(it.getColumnIndexOrThrow(COLUMN_SLEEP_DURATION)),
                    quality = it.getFloat(it.getColumnIndexOrThrow(COLUMN_SLEEP_QUALITY)),
                    notes = it.getString(it.getColumnIndexOrThrow(COLUMN_SLEEP_NOTES)) ?: ""
                )
                sessions.add(session)
            }
        }
        return sessions
    }

    // --- Alarm CRUD ---

    fun insertAlarm(alarm: AlarmEntity): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ALARM_HOUR, alarm.hour)
            put(COLUMN_ALARM_MINUTE, alarm.minute)
            put(COLUMN_ALARM_LABEL, alarm.label)
            put(COLUMN_ALARM_ENABLED, if (alarm.isEnabled) 1 else 0)
            put(COLUMN_ALARM_DAYS, alarm.repeatDays)
            put(COLUMN_ALARM_SOUND, alarm.sound)
            put(COLUMN_ALARM_VIBRATION, if (alarm.vibration) 1 else 0)
        }
        return db.insert(TABLE_ALARMS, null, values)
    }

    fun getAllAlarms(): List<AlarmEntity> {
        val alarms = mutableListOf<AlarmEntity>()
        val db = readableDatabase
        val cursor = db.query(TABLE_ALARMS, null, null, null, null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                val alarm = AlarmEntity(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ALARM_ID)),
                    hour = it.getInt(it.getColumnIndexOrThrow(COLUMN_ALARM_HOUR)),
                    minute = it.getInt(it.getColumnIndexOrThrow(COLUMN_ALARM_MINUTE)),
                    label = it.getString(it.getColumnIndexOrThrow(COLUMN_ALARM_LABEL)) ?: "",
                    isEnabled = it.getInt(it.getColumnIndexOrThrow(COLUMN_ALARM_ENABLED)) == 1,
                    repeatDays = it.getString(it.getColumnIndexOrThrow(COLUMN_ALARM_DAYS)) ?: "",
                    sound = it.getString(it.getColumnIndexOrThrow(COLUMN_ALARM_SOUND)) ?: "default",
                    vibration = it.getInt(it.getColumnIndexOrThrow(COLUMN_ALARM_VIBRATION)) == 1
                )
                alarms.add(alarm)
            }
        }
        return alarms
    }

    fun updateAlarmEnabled(id: Long, isEnabled: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ALARM_ENABLED, if (isEnabled) 1 else 0)
        }
        return db.update(TABLE_ALARMS, values, "$COLUMN_ALARM_ID = ?", arrayOf(id.toString()))
    }

    fun deleteAlarm(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_ALARMS, "$COLUMN_ALARM_ID = ?", arrayOf(id.toString()))
    }
}
