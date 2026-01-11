package com.nomanhassan.medremind.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.nomanhassan.medremind.BuildKonfig
import com.nomanhassan.medremind.data.local.database.DatabaseFactory
import com.nomanhassan.medremind.data.local.database.MedRemindDatabase
import com.nomanhassan.medremind.data.local.settings.AppearancePreferences
import com.nomanhassan.medremind.data.network.AIPrescriptionDataExtractor
import com.nomanhassan.medremind.data.network.ImageDataExtractor
import com.nomanhassan.medremind.data.repository.AiPrescriptionRepositoryImpl
import com.nomanhassan.medremind.data.repository.MedicationRepositoryImpl
import com.nomanhassan.medremind.domain.repository.AiPrescriptionRepository
import com.nomanhassan.medremind.domain.repository.MedicationRepository
import com.nomanhassan.medremind.presentation.add_edit_medication.AddEditMedicationViewModel
import com.nomanhassan.medremind.presentation.medication_detail.MedicationDetailViewModel
import com.nomanhassan.medremind.presentation.medication_list.MedicationListViewModel
import com.nomanhassan.medremind.presentation.medication_reminder.ReminderViewModel
import com.nomanhassan.medremind.presentation.medication_settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val AI_API_KEY_QUALIFIER = named("aiApiKey")
val APPLICATION_SCOPE_QUALIFIER = named("applicationScope")

val sharedModule = module {
    single { 
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { 
        get<MedRemindDatabase>().medicationDao
    }
    
    single(AI_API_KEY_QUALIFIER) {
        val aiApiKey = BuildKonfig.AI_API_KEY
        println("DEBUG: AI API Key Length: ${aiApiKey.length}")
        if (aiApiKey.isEmpty()) {
            error("AI_API_KEY is empty! Check local.properties and BuildKonfig setup.")
        }
        aiApiKey
    }
    single<ImageDataExtractor> {
        AIPrescriptionDataExtractor(
            modelName = "gemini-2.5-flash",
            apiKey = get(AI_API_KEY_QUALIFIER)
        )
    }
    
    single(APPLICATION_SCOPE_QUALIFIER) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    single {
        AppearancePreferences(
            dataStore = get(),
            applicationScope = get(APPLICATION_SCOPE_QUALIFIER)
        )
    }
    
    singleOf(::MedicationRepositoryImpl) bind MedicationRepository::class
    singleOf(::AiPrescriptionRepositoryImpl) bind AiPrescriptionRepository::class
    
    viewModelOf(::MedicationListViewModel)
    viewModelOf(::AddEditMedicationViewModel)
    viewModelOf(::MedicationDetailViewModel)
    viewModelOf(::ReminderViewModel)
    viewModelOf(::SettingsViewModel)
}