package com.grigorevmp.habits.domain.usecase.synchronizer

import android.content.Context
import android.util.Log
import com.grigorevmp.habits.core.alarm.AlarmScheduler
import com.grigorevmp.habits.core.utils.Utils
import com.grigorevmp.habits.data.repository.HabitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateNotificationUseCase @Inject constructor(
    private val habitRepository: HabitRepository
){
    private val alarmScheduler = AlarmScheduler()

    suspend operator fun invoke(context: Context) {
        habitRepository.fetchHabitsWithAlerts().collect { habits ->
            for (habit in habits) {

                val requestCode = (habit.id * 100).toInt() + habit.days[0].ordinal
                val isAlertRegistered = Utils.checkIfPendingIntentIsRegistered(context, requestCode)
                Log.d("Alarm manager", "Habit ${habit.title}, Is alert created: $isAlertRegistered")

                if (!isAlertRegistered) {
                    alarmScheduler.schedule(context, habit)
                }
            }
        }
    }
}