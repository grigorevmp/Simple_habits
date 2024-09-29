package com.grigorevmp.habits.domain.usecase.synchronizer

import com.grigorevmp.habits.data.date.DateEntity
import com.grigorevmp.habits.data.habit.HabitRefEntity
import com.grigorevmp.habits.data.habit.HabitType
import com.grigorevmp.habits.data.repository.DateRepository
import com.grigorevmp.habits.data.repository.HabitRefRepository
import com.grigorevmp.habits.data.repository.HabitRepository
import com.grigorevmp.habits.data.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class UpdateSyncPointUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val habitRefRepository: HabitRefRepository,
    private val habitRepository: HabitRepository,
    private val dateRepository: DateRepository,
) {

    suspend operator fun invoke() {
        var lastDate = preferencesRepository.getLastSyncDate()
        val currentDate = LocalDate.now()

        val currentDateId = dateRepository.getDateId(currentDate)

        if (currentDateId == null) {
            dateRepository.insert(
                DateEntity(date = currentDate)
            )
        }

        var sync = true

        habitRepository.fetchHabits().first { habits ->
            while (sync) {
                var date = dateRepository.getDateId(lastDate)

                if (date == null) {
                    dateRepository.insert(
                        DateEntity(date = lastDate)
                    )

                    date = dateRepository.getDateId(lastDate)
                }

                val targetDayOfTheWeek = date!!.date.dayOfWeek

                for (habit in habits) {
                    val supportedDaysOfWeek = habit.habit.days

                    if (targetDayOfTheWeek in supportedDaysOfWeek) {

                        val possibleHabit =
                            habitRepository.getHabitForDate(date.id!!, habit.habit.id)

                        if (possibleHabit == null) {
                            habit.dateIdList.add(date.id!!)

                            habitRefRepository.insert(
                                HabitRefEntity(
                                    dateId = date.id!!,
                                    habitId = habit.habit.id,
                                    habitType = if (lastDate == currentDate) HabitType.Unknown else HabitType.Missed
                                )
                            )
                        }
                    }
                }

                if (lastDate >= currentDate) {
                    sync = false
                } else {
                    lastDate = lastDate.plusDays(1)
                }
            }

            true
        }

        preferencesRepository.updateLastSyncDate()
    }
}