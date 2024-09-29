package com.grigorevmp.habits.domain.usecase.habit_ref

import com.grigorevmp.habits.data.habit.HabitType
import com.grigorevmp.habits.data.repository.HabitRefRepository
import javax.inject.Inject

class UpdateCountableHabitRefUseCase @Inject constructor(
    private var repository: HabitRefRepository,
) {

    suspend operator fun invoke(dateId: Long, habitId: Long, habitType: HabitType, value: Int) {
        repository.update(dateId, habitId, habitType, value)
    }
}