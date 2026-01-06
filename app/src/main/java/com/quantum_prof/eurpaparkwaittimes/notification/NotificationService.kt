package com.quantum_prof.eurpaparkwaittimes.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.quantum_prof.eurpaparkwaittimes.MainActivity
import com.quantum_prof.eurpaparkwaittimes.R
import com.quantum_prof.eurpaparkwaittimes.data.notification.WaitTimeAlert
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "wait_time_alerts"
    }

    fun createNotificationChannel() {
        val name = "Wait Time Alerts"
        val descriptionText = "Notifications for when attraction wait times drop"
        // Use IMPORTANCE_HIGH to ensuring heads-up notifications
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableVibration(true)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(alert: WaitTimeAlert, currentWaitTime: Int) {
        val notificationId = alert.attractionCode.hashCode()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_coaster) // Ersetze dies mit einem passenden Icon
            .setContentTitle("Wait time for ${alert.attractionName} is low!")
            .setContentText("Current wait time is $currentWaitTime minutes (target: < ${alert.targetTime} min).")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Current wait time is $currentWaitTime minutes. Go catch a ride!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Show on lock screen
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
