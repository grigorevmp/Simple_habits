package com.grigorevmp.habits.domain.usecase.date

import com.grigorevmp.habits.data.repository.DateRepository
import javax.inject.Inject

class GetDateIdUseCase @Inject constructor(
    private var repository: DateRepository,
) {

    operator fun invoke(dateId: Long) = repository.getDate(dateId)
}