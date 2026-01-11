package com.nomanhassan.medremind.core.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.nomanhassan.medremind.MainActivity
import com.nomanhassan.medremind.R
import com.nomanhassan.medremind.core.util.NextOccurrenceCalculator
import com.nomanhassan.medremind.data.mapper.toMedicationEntity
import com.nomanhassan.medremind.domain.model.Medication
import com.nomanhassan.medremind.domain.repository.MedicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.notification_body_take_fallback
import medremind.composeapp.generated.resources.notification_title_reminder_fallback
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class ReminderReceiver: BroadcastReceiver(), KoinComponent {

    private val medicationRepository: MedicationRepository by inject()
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ReminderReceiver", "Alarm Received! App is likely cold-starting.")
        
        if (context == null || intent == null) {
            Log.d("ReminderReceiver", "Context or Intent was null")
            return
        }
        
        val triggerTimeId = intent.getLongExtra(EXTRA_REMINDER_NOTIFICATION_ID, -1L)
        val title = intent.getStringExtra(EXTRA_REMINDER_NOTIFICATION_TITLE)
        val body = intent.getStringExtra(EXTRA_REMINDER_NOTIFICATION_BODY)

        val pendingResult = goAsync()

        if (triggerTimeId == -1L) {
            pendingResult.finish()
            return
        }

        scope.launch {
            try {
                var activeMeds: List<Medication> = emptyList()

                // RETRY LOGIC: Try 5 times (total 2.5 seconds) to wait for DB initialization
                for (i in 1..5) {
                    activeMeds = medicationRepository.getActiveMedications().first()
                    if (activeMeds.isNotEmpty()) break // Success!

                    Log.d("ReminderReceiver", "DB not ready yet (Attempt $i), waiting 500ms...")
                    delay(500)
                }

                if (activeMeds.isEmpty()) {
                    Log.e("ReminderReceiver", "Failed to load any medications after 2.5s. Aborting.")
                    return@launch
                }

                // Use the targetTimeId to verify
                val isDue = activeMeds.any { med ->
                    NextOccurrenceCalculator.isDueAt(med.toMedicationEntity(), triggerTimeId)
                }

                if (isDue) {
                    showReminderNotification(
                        context = context,
                        id = triggerTimeId,
                        title = title ?: getString(Res.string.notification_title_reminder_fallback),
                        body = body ?: getString(Res.string.notification_body_take_fallback)
                    )
                    medicationRepository.scheduleGlobalAlarms()
                } else {
                    Log.d("ReminderReceiver", "Ignored: Alarm ID $triggerTimeId failed timing check.")
                }
            } catch (e: Exception) {
                Log.e("ReminderReceiver", "Fatal Error in background process", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
    
    @SuppressLint("FullScreenIntentPolicy")
    private fun showReminderNotification(
        context: Context,
        id: Long,
        title: String,
        body: String
    ) {
        val requestCodeId = id.toSafeRequestCode()
        
        val notificationManger = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                REMINDER_NOTIFICATION_CHANNEL_ID,
                "Medication Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminder for medications"
                enableLights(true)
                enableVibration(true)

                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                
                setSound(alarmSound, audioAttributes)
            }
            
            notificationManger.createNotificationChannel(channel)
        }

        val clickToOpenAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(EXTRA_NAVIGATE_TO_REMINDER_TIME, id)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCodeId,
            clickToOpenAppIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, REMINDER_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(alarmSound)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (notificationManger.canUseFullScreenIntent()) {
                notificationBuilder.setFullScreenIntent(pendingIntent, true)
            } else {
                Log.d("ReminderReceiver", "Full screen intent permission not granted")
            }
        } else {
            notificationBuilder.setFullScreenIntent(pendingIntent, true)
        }

        val notification = notificationBuilder.build()
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        notificationManger.notify(requestCodeId, notification)
    }
}