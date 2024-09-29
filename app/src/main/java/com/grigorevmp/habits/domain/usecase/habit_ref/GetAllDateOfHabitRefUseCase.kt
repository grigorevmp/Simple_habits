package com.grigorevmp.habits.domain.usecase.habit_ref

import com.grigorevmp.habits.data.repository.HabitRefRepository
import javax.inject.Inject

class GetAllDateOfHabitRefUseCase @Inject constructor(
    private var repository: HabitRefRepository,
) {

    operator fun invoke(habitId: Long) = repository.getAllHabitRefs(habitId)
}