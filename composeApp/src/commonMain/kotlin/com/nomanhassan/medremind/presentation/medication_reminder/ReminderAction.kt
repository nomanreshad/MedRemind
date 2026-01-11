package com.nomanhassan.medremind.presentation.medication_reminder

sealed interface ReminderAction {
    /**
     * Triggered when the user clicks the "Back to home" button.
     */
    data object OnAcknowledgeReminder : ReminderAction
}