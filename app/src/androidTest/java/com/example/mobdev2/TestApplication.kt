package com.example.mobdev2

import android.app.Application
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.ksp.generated.module

class TestApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(AppModule().module, TestModule().module)
        }
    }

    override fun onTerminate() {
        stopKoin()
        super.onTerminate()
    }
}