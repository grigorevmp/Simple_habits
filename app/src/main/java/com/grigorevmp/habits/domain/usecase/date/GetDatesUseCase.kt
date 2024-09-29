package com.grigorevmp.habits.domain.usecase.date

import com.grigorevmp.habits.data.repository.DateRepository
import javax.inject.Inject

class GetAllDatesUseCase @Inject constructor(
    private var repository: DateRepository,
) {

    operator fun invoke() = repository.getAllDates()
}