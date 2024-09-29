package com.grigorevmp.habits.domain.usecase.habits

import android.util.Log
import com.grigorevmp.habits.data.HabitEntity
import com.grigorevmp.habits.data.repository.HabitRepository
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private var repository: HabitRepository,
) {

    suspend operator fun invoke(
        habit: HabitEntity
    ) {
        Log.d("Habit updated", habit.toString())

        repository.update(habit)
    }
}