package com.example.luma.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.luma.database.AlarmEntity
import com.example.luma.database.DatabaseHelper
import com.example.luma.services.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val _alarms = MutableStateFlow<List<AlarmEntity>>(emptyList())
    val alarms: StateFlow<List<AlarmEntity>> = _alarms.asStateFlow()

    private val _nextAlarm = MutableStateFlow<AlarmEntity?>(null)
    val nextAlarm: StateFlow<AlarmEntity?> = _nextAlarm.asStateFlow()

    init {
        loadAlarms()
    }

    fun loadAlarms() {
        viewModelScope.launch {
            val list = dbHelper.getAllAlarms()
            _alarms.value = list
            updateNextAlarm(list)
        }
    }

    fun addAlarm(alarm: AlarmEntity, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = dbHelper.insertAlarm(alarm)
            val savedAlarm = alarm.copy(id = id)
            if (savedAlarm.isEnabled) {
                AlarmScheduler.scheduleAlarm(getApplication(), savedAlarm)
            }
            loadAlarms()
            onComplete(id)
        }
    }

    fun toggleAlarm(alarm: AlarmEntity, isEnabled: Boolean) {
        viewModelScope.launch {
            dbHelper.updateAlarmEnabled(alarm.id, isEnabled)
            val updated = alarm.copy(isEnabled = isEnabled)
            if (isEnabled) {
                AlarmScheduler.scheduleAlarm(getApplication(), updated)
            } else {
                AlarmScheduler.cancelAlarm(getApplication(), updated.id)
            }
            loadAlarms()
        }
    }

    fun removeAlarm(alarmId: Long) {
        viewModelScope.launch {
            AlarmScheduler.cancelAlarm(getApplication(), alarmId)
            dbHelper.deleteAlarm(alarmId)
            loadAlarms()
        }
    }

    private fun updateNextAlarm(list: List<AlarmEntity>) {
        val enabledAlarms = list.filter { it.isEnabled }
        if (enabledAlarms.isEmpty()) {
            _nextAlarm.value = null
            return
        }

        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)

        var earliest: AlarmEntity? = null
        var minDiffMinutes = Int.MAX_VALUE

        for (alarm in enabledAlarms) {
            var diff = (alarm.hour * 60 + alarm.minute) - (currentHour * 60 + currentMinute)
            if (diff <= 0) {
                diff += 24 * 60
            }
            if (diff < minDiffMinutes) {
                minDiffMinutes = diff
                earliest = alarm
            }
        }

        _nextAlarm.value = earliest ?: enabledAlarms.first()
    }
}
