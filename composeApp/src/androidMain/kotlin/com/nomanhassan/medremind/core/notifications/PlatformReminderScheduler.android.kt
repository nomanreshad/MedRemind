package com.nomanhassan.medremind.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformReminderScheduler(
    private val context: Context
) : ReminderScheduler {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    actual override fun scheduleReminder(
        id: Long,
        title: String,
        body: String,
        triggerAtMillis: Long
    ) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            flags = Intent.FLAG_RECEIVER_FOREGROUND
            putExtra(EXTRA_REMINDER_NOTIFICATION_ID, id)
            putExtra(EXTRA_REMINDER_NOTIFICATION_TITLE, title)
            putExtra(EXTRA_REMINDER_NOTIFICATION_BODY, body)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.toSafeRequestCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ReminderScheduler", "Failed to schedule alarm: Permission denied", e)
            // Fall back to a less precise alarm.
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
            Log.w("ReminderScheduler", "Scheduled inexact alarm as general fallback for ID: $id at $triggerAtMillis")
        }
    }

    actual override fun cancelReminder(id: Long) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_NOTIFICATION_ID, id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.toSafeRequestCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.i("ReminderScheduler", "Reminder cancelled for ID: $id")
    }
}