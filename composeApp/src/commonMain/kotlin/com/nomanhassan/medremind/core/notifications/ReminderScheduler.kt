package com.nomanhassan.medremind.core.notifications

interface ReminderScheduler {
    
    fun scheduleReminder(
        id: Long,
        title: String,
        body: String,
        triggerAtMillis: Long
    )
    
    fun cancelReminder(id: Long)
}