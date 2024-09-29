package com.grigorevmp.habits.domain.usecase.date

import com.grigorevmp.habits.data.repository.DateRepository
import java.time.LocalDate
import javax.inject.Inject

class GetDateUseCase @Inject constructor(
    private var repository: DateRepository,
) {

    operator fun invoke(date: LocalDate) = repository.getDateId(date)
}