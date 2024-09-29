package com.grigorevmp.habits.domain.usecase.habits

import android.util.Log
import com.grigorevmp.habits.data.CountableEntity
import com.grigorevmp.habits.data.HabitCategory
import com.grigorevmp.habits.data.HabitEntity
import com.grigorevmp.habits.data.SerializableTimePickerState
import com.grigorevmp.habits.data.repository.HabitRepository
import java.time.DayOfWeek
import javax.inject.Inject

class AddHabitUseCase @Inject constructor(
    private var repository: HabitRepository,
) {

    suspend operator fun invoke(
        title: String,
        description: String,
        selectedDays: Array<DayOfWeek>,
        useAlert: Boolean,
        timePickerState: SerializableTimePickerState,
        countable: Boolean,
        countableEntity: CountableEntity?,
        habitCategory: HabitCategory,
    ): HabitEntity {
        val habit = HabitEntity(
            title = title,
            description = description,
            days = selectedDays,
            time = timePickerState,
            alertEnabled = useAlert,
            completed = false,
            countable = countable,
            countableEntity = countableEntity,
            habitCategory = habitCategory,
        )

        Log.d("Habit added", habit.toString())

        habit.id = repository.insert(habit)

        return habit
    }
}