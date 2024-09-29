package com.grigorevmp.habits.core.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.grigorevmp.habits.R
import com.grigorevmp.habits.presentation.screen.common.MainActivity
import com.grigorevmp.habits.receiver.habit_notification.MarkAsDoneBroadcastReceiver
import com.grigorevmp.habits.receiver.habit_notification.MarkAsMissedBroadcastReceiver

const val NOTIFICATION_ID = 33
const val CHANNEL_ID = "ReminderChannel"

//TODO Refactor Util class

fun createChannel(context: Context) {
    val notificationChannel =
        NotificationChannel(CHANNEL_ID, "Habit reminder", NotificationManager.IMPORTANCE_HIGH)
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(notificationChannel)
}

fun NotificationManager.sendReminderNotification(
    id: Long,
    context: Context,
    title: String,
    message: String
) {

    Log.d("NotificationManager", "Saved $message with id=$id")

    val notificationId = (NOTIFICATION_ID * 1000 + id).toInt()

    val contentIntent = Intent(context, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val doneIntent = Intent(context, MarkAsDoneBroadcastReceiver::class.java).apply {
        putExtra("EXTRA_DONE_NOTIF_ID", id)
    }
    val time = System.currentTimeMillis().toInt()

    val donePendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId + time - 1,
        doneIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val missedIntent = Intent(context, MarkAsMissedBroadcastReceiver::class.java).apply {
        putExtra("EXTRA_MISSED_NOTIF_ID", id)
    }
    val missedPendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId + time + 1,
        missedIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(message)
        )
        .setSmallIcon(R.drawable.ic_task)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .addAction(R.drawable.ic_done, "Done", donePendingIntent)
        .addAction(R.drawable.ic_clear, "Missed", missedPendingIntent)
        .setContentIntent(contentPendingIntent)
        .setOngoing(false)
        .setAutoCancel(true)
        .build()

    builder.flags = builder.flags or Notification.FLAG_AUTO_CANCEL

    Log.d("NotificationManager", "Saved $message with notificationId=$notificationId")

    this.notify(notificationId, builder)
}