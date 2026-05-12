package com.example.planetlife.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.planetlife.MainActivity
import com.example.planetlife.R

class SedentaryReminderNotifier(
    private val context: Context,
) {
    fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            ChannelId,
            "久坐提醒",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "当星球感知到你久坐过久时发送提醒。"
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    fun showSedentaryReminder() {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, ChannelId)
            .setSmallIcon(R.drawable.ic_notification_planet)
            .setContentTitle("星球沙漠正在扩张")
            .setContentText("你已经很久没有活动了，起来走一走，可以让森林重新生长。")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("你已经很久没有活动了，星球上的沙漠正在扩张。起来走一走，可以让森林重新生长。")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(NotificationId, notification)
    }

    private fun hasNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val ChannelId = "sedentary_reminder"
        private const val NotificationId = 1001
    }
}
