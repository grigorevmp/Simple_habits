package com.grigorevmp.habits.domain.usecase.synchronizer

import android.util.Log
import com.grigorevmp.habits.data.date.DateEntity
import com.grigorevmp.habits.data.habit.HabitRefEntity
import com.grigorevmp.habits.data.habit.HabitType
import com.grigorevmp.habits.data.repository.DateRepository
import com.grigorevmp.habits.data.repository.HabitRefRepository
import com.grigorevmp.habits.data.repository.HabitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class AddSyncPointForNewHabitUseCase @Inject constructor(
    private val habitRefRepository: HabitRefRepository,
    private val habitRepository: HabitRepository,
    private val dateRepository: DateRepository,
) {

    suspend operator fun invoke(habitId: Long) {
        val currentDate = LocalDate.now()

        var date = dateRepository.getDateId(currentDate)

        if (date == null) {
            date = DateEntity(date = currentDate)
            dateRepository.insert(date)
        }

        val habit = habitRepository.getHabitById(habitId)

        if (habit == null) {
            Log.d(
                AddSyncPointForNewHabitUseCase::class.simpleName,
                "Habit not found for id: $habitId"
            )
            return
        }

        val supportedDaysOfWeek = habit.days

        if (currentDate.dayOfWeek in supportedDaysOfWeek) {

            val possibleHabit = habitRepository.getHabitForDate(date.id!!, habit.id)

            if (possibleHabit == null) {
                habitRefRepository.insert(
                    HabitRefEntity(
                        dateId = date.id!!,
                        habitId = habit.id,
                        habitType = HabitType.Unknown
                    )
                )
            }
        }
    }
}