package com.grigorevmp.habits.domain.usecase.habit_ref

import com.grigorevmp.habits.data.repository.HabitRefRepository
import javax.inject.Inject

class GetHabitRefUseCase @Inject constructor(
    private var repository: HabitRefRepository,
) {

    operator fun invoke(targetDateId: Long, habitId: Long) = repository.getHabitRefForDate(targetDateId, habitId)
}