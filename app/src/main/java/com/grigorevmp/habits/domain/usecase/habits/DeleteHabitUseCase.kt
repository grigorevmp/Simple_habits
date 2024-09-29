package com.grigorevmp.habits.domain.usecase.habits

import com.grigorevmp.habits.data.HabitEntity
import com.grigorevmp.habits.data.repository.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private var repository: HabitRepository,
) {

    suspend operator fun invoke(
        habit: HabitEntity
    ) {
        repository.delete(habit)
    }
}