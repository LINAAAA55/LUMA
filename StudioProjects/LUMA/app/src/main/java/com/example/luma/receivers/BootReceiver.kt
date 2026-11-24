package com.example.luma.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.luma.database.DatabaseHelper
import com.example.luma.services.AlarmScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val dbHelper = DatabaseHelper(context)
            val alarms = dbHelper.getAllAlarms()
            for (alarm in alarms) {
                if (alarm.isEnabled) {
                    AlarmScheduler.scheduleAlarm(context, alarm)
                }
            }
        }
    }
}
