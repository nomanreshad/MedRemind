package com.nomanhassan.medremind.core.notifications

const val REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel_id"

const val EXTRA_REMINDER_NOTIFICATION_ID = "extra_reminder_notification_id"
const val EXTRA_REMINDER_NOTIFICATION_TITLE = "extra_reminder_notification_title"
const val EXTRA_REMINDER_NOTIFICATION_BODY = "extra_reminder_notification_body"

const val EXTRA_NAVIGATE_TO_REMINDER_TIME = "navigate_to_reminder_time"

fun Long.toSafeRequestCode(): Int {
    val seconds = this / 1000
    // This gives us a unique range of ~68 years before a collision.
    return (seconds % Int.MAX_VALUE).toInt()
}