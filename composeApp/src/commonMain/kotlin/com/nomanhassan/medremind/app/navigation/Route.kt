package com.nomanhassan.medremind.app.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    
    @Serializable
    data object MedicationGraph: Route
    
    @Serializable
    data object MedicationList: Route
    
    @Serializable
    data class MedicationDetail(val id: Int): Route
    
    @Serializable
    data class AddEditMedication(val id: Int?): Route
    
    @Serializable
    data object MedicationSettings: Route

    @Serializable
    data class MedicationReminder(val reminderTimeId: Long): Route
}