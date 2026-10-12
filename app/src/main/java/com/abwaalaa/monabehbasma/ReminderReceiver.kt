package com.abwaalaa.monabehbasma
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "تنبيهات مواعيد البصمة", NotificationManager.IMPORTANCE_HIGH))
        }
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.Notification.Builder(context, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            android.app.Notification.Builder(context)
        }
        val notification = builder
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(intent.getStringExtra(EXTRA_TITLE) ?: "منبّه البصمة")
            .setContentText("افتح التطبيق لمراجعة الموعد وتحديث سجل البصمات.")
            .setAutoCancel(true)
            .build()
        manager.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }

    companion object {
        const val EXTRA_TITLE = "reminder_title"
        const val EXTRA_APPOINTMENT_ID = "appointment_id"
        const val EXTRA_REMINDER_INDEX = "reminder_index"
        private const val CHANNEL_ID = "fingerprint_appointment_reminders"
    }
}