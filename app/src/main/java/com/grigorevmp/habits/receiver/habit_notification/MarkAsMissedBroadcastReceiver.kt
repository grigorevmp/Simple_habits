package com.grigorevmp.habits.receiver.habit_notification

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.grigorevmp.habits.R
import com.grigorevmp.habits.core.alarm.NOTIFICATION_ID
import com.grigorevmp.habits.core.in_app_bus.Event
import com.grigorevmp.habits.core.in_app_bus.EventType
import com.grigorevmp.habits.core.in_app_bus.GlobalBus
import com.grigorevmp.habits.data.habit.HabitType
import com.grigorevmp.habits.di.HiltBroadcastReceiver
import com.grigorevmp.habits.domain.usecase.date.GetDateUseCase
import com.grigorevmp.habits.domain.usecase.habit_ref.UpdateHabitRefUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class MarkAsMissedBroadcastReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var updateHabitRefUseCase: UpdateHabitRefUseCase

    @Inject
    lateinit var getDateUseCase: GetDateUseCase

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val id = intent.getLongExtra("EXTRA_MISSED_NOTIF_ID", -1L)
        if (id == -1L) {
            Log.e("MarkAsDoneBroadcastReceiver", "Invalid habit ID")
            return
        }

        Log.d("MarkAsMissedBroadcastReceiver", "Received notification action for habit ID: $id")

        CoroutineScope(Dispatchers.IO).launch {
            getDateUseCase(LocalDate.now())?.also {
                it.id?.let { dateId ->
                    Log.d("MarkAsMissedBroadcastReceiver", "Habit $id on date $dateId changed")

                    updateHabitRefUseCase(
                        dateId,
                        id,
                        HabitType.Missed
                    )
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    context.getString(R.string.marked_as_missed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val notificationId = (NOTIFICATION_ID * 1000 + id).toInt()
        Log.d("MarkAsMissedBroadcastReceiver", "Cancel $notificationId")
        context.getSystemService(NotificationManager::class.java).cancel(notificationId)

        GlobalBus.post(
            Event(
                EventType.NotificationChangedEvent,
                "Missed"
            )
        )
    }
}