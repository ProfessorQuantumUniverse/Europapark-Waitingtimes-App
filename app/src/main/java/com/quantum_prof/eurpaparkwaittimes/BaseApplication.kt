package com.quantum_prof.eurpaparkwaittimes

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.quantum_prof.eurpaparkwaittimes.notification.NotificationService
import com.quantum_prof.eurpaparkwaittimes.worker.WaitTimeCheckService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var waitTimeCheckService: WaitTimeCheckService

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        initializeServices()
    }

    private fun initializeServices() {
        // Erstelle Notification Channel
        notificationService.createNotificationChannel()

        // Initialisiere Background Services
        waitTimeCheckService.startPeriodicChecks()
    }
}
