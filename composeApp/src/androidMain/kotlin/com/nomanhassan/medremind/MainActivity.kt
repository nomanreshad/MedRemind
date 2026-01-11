package com.nomanhassan.medremind

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nomanhassan.medremind.app.App
import com.nomanhassan.medremind.app.navigation.NotificationNavigator
import com.nomanhassan.medremind.core.notifications.EXTRA_NAVIGATE_TO_REMINDER_TIME

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        handleNotificationIntent(intent)

        setContent {
            App()
            BackHandler() { }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }
    
    private fun handleNotificationIntent(intent: Intent?) {
        intent?.let {
            val reminderTimeId = it.getLongExtra(EXTRA_NAVIGATE_TO_REMINDER_TIME, -1L)
            if (reminderTimeId != -1L) {
                NotificationNavigator.onNotificationClicked(reminderTimeId)
            }
        }
    }
}

// Cold Start: App is closed, user clicks notification -> onCreate
// Warm Start: App is open/background, user clicks notification -> onNewIntent