package com.nomanhassan.medremind.di

import com.nomanhassan.medremind.core.notifications.ReminderScheduler
import com.nomanhassan.medremind.core.notifications.PlatformReminderScheduler
import com.nomanhassan.medremind.data.local.database.DatabaseFactory
import com.nomanhassan.medremind.data.local.settings.createDataStore
import com.nomanhassan.medremind.data.local.storage.FileHelper
import com.nomanhassan.medremind.data.local.storage.InternalImageStorage
import com.nomanhassan.medremind.data.local.storage.PlatformInternalImageStorage
import com.nomanhassan.medremind.domain.settings.Localization
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::DatabaseFactory)
    
    factoryOf(::PlatformInternalImageStorage) bind InternalImageStorage::class
    factoryOf(::FileHelper)
    
    factoryOf(::PlatformReminderScheduler) bind ReminderScheduler::class
    
    singleOf(::Localization)
    
    single {
        createDataStore(androidApplication())
    }
}