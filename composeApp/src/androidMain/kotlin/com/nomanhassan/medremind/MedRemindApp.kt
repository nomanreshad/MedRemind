package com.nomanhassan.medremind

import android.app.Application
import com.nomanhassan.medremind.di.initKoin
import org.koin.android.ext.koin.androidContext

class MedRemindApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin { 
            androidContext(this@MedRemindApp)
        }
    }
}