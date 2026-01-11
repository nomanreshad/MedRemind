package com.nomanhassan.medremind.core.notifications

import com.nomanhassan.medremind.domain.repository.MedicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class IOSNotificationHandler: KoinComponent {

    private val medicationRepository: MedicationRepository = get()
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Called when the iOS app launches (equivalent to Android's BootReceiver)
     * or when a medication is saved/edited.
     */
    fun onAppLaunched() {
        scope.launch {
            try {
                medicationRepository.scheduleGlobalAlarms()
                println("IOSNotificationHandler: Global alarms successfully rescheduled on app launch.")
            } catch (e: Exception) {
                println("IOSNotificationHandler Error: ${e.message}")
            }
        }
    }

    /**
     * Called when an iOS notification is delivered to the foreground app.
     * This immediately schedules the next alarm cycle, ensuring continuity
     * even if the user ignores the notification.
     */
    fun onNotificationDelivered(triggerTimeId: Long) {
        scope.launch {
            try {
                // The main purpose is to reschedule the entire next cycle
                medicationRepository.scheduleGlobalAlarms()
                println("IOSNotificationHandler: Alarms rescheduled after delivery for $triggerTimeId")
            } catch (e: Exception) {
                println("IOSNotificationHandler Error: ${e.message}")
            }
        }
    }
}