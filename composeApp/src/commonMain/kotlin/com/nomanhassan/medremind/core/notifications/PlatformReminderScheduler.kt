package com.nomanhassan.medremind.core.notifications

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class PlatformReminderScheduler: ReminderScheduler {

    override fun scheduleReminder(
        id: Long,
        title: String,
        body: String,
        triggerAtMillis: Long
    )
    
    override fun cancelReminder(id: Long)
}