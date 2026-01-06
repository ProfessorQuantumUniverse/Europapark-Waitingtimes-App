package com.quantum_prof.eurpaparkwaittimes.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.quantum_prof.eurpaparkwaittimes.data.WaitTimeRepository
import com.quantum_prof.eurpaparkwaittimes.data.notification.AlertRepository
import com.quantum_prof.eurpaparkwaittimes.notification.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker zum Überprüfen von Wartezeit-Alerts
 */
@HiltWorker
class WaitTimeCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val waitTimeRepository: WaitTimeRepository,
    private val alertRepository: AlertRepository,
    private val notificationService: NotificationService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Lade aktive Alerts
            val activeAlerts = alertRepository.getAlerts()
            if (activeAlerts.isEmpty()) {
                return Result.success()
            }

            // 2. Hole aktuelle Wartezeiten
            val waitTimesResult = waitTimeRepository.getEuropaparkWaitTimes()

            // Retry if network call failed
            if (waitTimesResult.isFailure) {
                return Result.retry()
            }

            waitTimesResult.onSuccess { result ->
                val (waitTimes, _) = result
                // 3. Prüfe jeden Alert
                activeAlerts.forEach { alert ->
                    val currentAttraction = waitTimes.find { it.code == alert.attractionCode }

                    if (currentAttraction != null &&
                        currentAttraction.status.equals("opened", ignoreCase = true) &&
                        currentAttraction.waitTimeMinutes <= alert.targetTime) {

                        Log.d("WaitTimeCheckWorker", "Triggering notification for ${alert.attractionName}")

                        // 4. Sende Benachrichtigung
                        notificationService.showNotification(alert, currentAttraction.waitTimeMinutes)

                        // 5. Entferne Alert nach Benachrichtigung (einmalig)
                        alertRepository.removeAlert(alert.attractionCode)
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
