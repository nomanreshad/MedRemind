package com.nomanhassan.medremind.core.notifications

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformReminderScheduler : ReminderScheduler {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    
    actual override fun scheduleReminder(
        id: Long,
        title: String,
        body: String,
        triggerAtMillis: Long
    ) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            setSound(UNNotificationSound.defaultSound)

            // Store ID in userInfo to handle navigation later
            setUserInfo(mapOf("navigate_to_reminder_time" to id))
        }
        
        val date = NSDate.dateWithTimeIntervalSince1970(triggerAtMillis / 1000.0)

        // Create components from date
        val targetDateComponents = NSCalendar.currentCalendar.components(
            unitFlags = NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or NSCalendarUnitHour or NSCalendarUnitMinute,
            fromDate = date
        )
        
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = targetDateComponents,
            repeats = false
        )
        
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = id.toString(),
            content = content,
            trigger = trigger
        )
        
        notificationCenter.addNotificationRequest(request) { error ->
            error?.let {
                println("Error scheduling iOS notification: ${error.localizedDescription}")
            }
        }
    }

    actual override fun cancelReminder(id: Long) {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(
            identifiers = listOf(id.toString())
        )
    }
}