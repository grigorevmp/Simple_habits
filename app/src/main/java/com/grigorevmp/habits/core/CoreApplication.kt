package com.grigorevmp.habits.core

import android.app.Application
import com.grigorevmp.habits.core.in_app_bus.GlobalBus
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication : Application() {
    override fun onTerminate() {
        super.onTerminate()

        GlobalBus.onDestroy()
    }
}