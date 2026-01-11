package com.nomanhassan.medremind.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nomanhassan.medremind.domain.repository.MedicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootCompletedReceiver: BroadcastReceiver(), KoinComponent {
    private val medicationRepository: MedicationRepository by inject()
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("BootCompletedReceiver", "onReceive logic started!")

        if (context == null || intent == null) {
            Log.d("BootCompletedReceiver", "Context or Intent was null")
            return
        }
        
        // Log that the receiver woke up
        Log.d("BootCompletedReceiver", "Received action: ${intent.action}")
        
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            intent.action == "com.nomanhassan.medremind.TEST_BOOT") {
            val pendingResult = goAsync()

            scope.launch {
                try {
                    Log.d("BootCompletedReceiver", "Starting global alarm rescheduling...")

                    medicationRepository.scheduleGlobalAlarms()

                    Log.d("BootCompletedReceiver", "Successfully rescheduled all alarms.")
                } catch (e: Exception) {
                    Log.e("BootCompletedReceiver", "Failed to reschedule alarms", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}