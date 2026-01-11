package com.nomanhassan.medremind.app.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationNavigator {
    
    private val _targetReminderTimeId = MutableStateFlow<Long?>(null)
    val targetReminderTimeId = _targetReminderTimeId.asStateFlow()
    
    fun onNotificationClicked(triggerTimeId: Long) {
        _targetReminderTimeId.value = triggerTimeId
    }
    
    fun clearTarget() {
        _targetReminderTimeId.value = null
    }
}