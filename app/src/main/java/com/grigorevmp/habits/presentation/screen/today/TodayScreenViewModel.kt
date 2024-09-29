package com.grigorevmp.habits.presentation.screen.today

import android.content.Context
import androidx.lifecycle.ViewModel
import com.grigorevmp.habits.core.in_app_bus.GlobalBus
import com.grigorevmp.habits.data.date.DateEntity
import com.grigorevmp.habits.data.habit.HabitType
import com.grigorevmp.habits.data.repository.PreferencesRepository
import com.grigorevmp.habits.domain.usecase.date.GetDateUseCase
import com.grigorevmp.habits.domain.usecase.habit_ref.GetHabitRefUseCase
import com.grigorevmp.habits.domain.usecase.habit_ref.UpdateCountableHabitRefUseCase
import com.grigorevmp.habits.domain.usecase.habit_ref.UpdateHabitRefUseCase
import com.grigorevmp.habits.domain.usecase.habits.GetOnlyHabitsUseCase
import com.grigorevmp.habits.presentation.screen.today.data.HabitEntityUI
import com.grigorevmp.habits.presentation.screen.today.data.HabitStatisticItemUi
import com.grigorevmp.habits.presentation.screen.today.data.HabitWithDatesUI
import com.grigorevmp.habits.presentation.screen.today.mapper.TodayScreenMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class TodayScreenViewModel @Inject constructor(
    private val getHabitsUseCase: GetOnlyHabitsUseCase,
    private val getDateUseCase: GetDateUseCase,
    private val getHabitRefUseCase: GetHabitRefUseCase,
    private val updateHabitRefUseCase: UpdateHabitRefUseCase,
    private val updateCountableHabitRefUseCase: UpdateCountableHabitRefUseCase,
    private val todayScreenMapper: TodayScreenMapper,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    var uiState = TodayScreenUiState()
        private set
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    override fun onCleared() {
        super.onCleared()

        scope.cancel()
    }


    fun init(context: Context, daysCount: Int) = scope.launch {
        delay(1000)

        GlobalBus.events().collect {
            getPreparedHabitsList(context, daysCount) { }
            updateWeekStatistic()
        }
    }

    fun getPreparedHabitsList(context: Context, daysCount: Int, payload: () -> Unit) = scope.launch {
        getHabitsUseCase().collectLatest { habitsData ->
            val minorAllHabitsWithDateData = mutableListOf<HabitWithDatesUI>()
            val datesData = getDatesCut(daysCount)

            for (date in datesData) {
                getDate(date).collect { targetDate ->
                    val habitWithDate = HabitWithDatesUI(
                        date = todayScreenMapper.getFancyTodayString(
                            context,
                            date.dayOfMonth,
                            date.month,
                            date.dayOfWeek
                        ),
                        habits = arrayListOf()
                    )

                    targetDate.id?.let { targetDateId ->
                        for (habit in habitsData) {
                            if (habit.deleted) continue

                            val habitRef =
                                getHabitRefUseCase(
                                    targetDateId,
                                    habit.id
                                )

                            if (habitRef != null) {

                                habitWithDate.habits.add(
                                    HabitEntityUI(
                                        id = habit.id,
                                        dateId = targetDateId,
                                        title = habit.title,
                                        category = habit.habitCategory.name,
                                        description = habit.description,
                                        alert = habit.alertEnabled,
                                        time = habit.time,
                                        type = habitRef.habitType,
                                        countable = habit.countable,
                                        maxValue = habit.countableEntity?.targetValue,
                                        value = habitRef.value ?: 0,
                                        valueName = habit.countableEntity?.valueName,
                                        valueAction = habit.countableEntity?.actionName,
                                    )
                                )
                            }
                        }
                    }

                    minorAllHabitsWithDateData.add(habitWithDate)
                }
            }

            uiState.todayHabitData.value = minorAllHabitsWithDateData
            payload()
        }
    }

    fun updateWeekStatistic() = scope.launch {
        getHabitsUseCase().collectLatest { habitsData ->
            val minorAllHabitStatisticItemUiData = mutableListOf<HabitStatisticItemUi>()
            val datesData = getWeekDays()

            val localDateTime = LocalDateTime.now()

            for ((index, date) in datesData.withIndex()) {
                val statisticItem = HabitStatisticItemUi(0, 0, index, date.dayOfMonth.toLong())

                getDateOrNull(date).collect { targetDate ->
                    if (targetDate == null) return@collect

                    if (date == localDateTime.toLocalDate() && isLongerDate() && localDateTime.hour < extendDayValue) return@collect

                    for (habit in habitsData) {

                        if (habit.deleted) continue

                        val habitRef = getHabitRefUseCase(
                            targetDate.id!!,
                            habit.id
                        )

                        if (habitRef != null) {
                            statisticItem.habitsNumber += 1

                            if (habitRef.habitType == HabitType.Done) {
                                statisticItem.checkedHabitsNumber += 1
                            }
                        }
                    }
                }

                minorAllHabitStatisticItemUiData.add(statisticItem)
            }

            uiState.statisticData.value = minorAllHabitStatisticItemUiData
        }
        cancel()
    }

    fun updateHabitRef(dateId: Long, habitId: Long, habitType: HabitType) = scope.launch {
        updateHabitRefUseCase(dateId, habitId, habitType)
        updateWeekStatistic()
    }

    fun updateHabitRefCountable(dateId: Long, habitId: Long, habitType: HabitType, value: Int) = scope.launch {
        updateCountableHabitRefUseCase(dateId, habitId, habitType, value)
        updateWeekStatistic()
    }

    fun getRandomEmoji(seed: Long): String {
        val emojiSet = preferencesRepository.getCongratsEmoji()

        return emojiSet.random(Random(seed))
    }

    fun getVersion() = preferencesRepository.getVersion()

    fun setVersion(version: Long) = preferencesRepository.setVersion(version)

    private fun isLongerDate(): Boolean = preferencesRepository.getLongerDateFlag()

    private fun getDate(date: LocalDate): Flow<DateEntity> = flow {
        getDateUseCase(date)?.also {
            emit(it)
        }
    }.flowOn(Dispatchers.IO)

    private fun getDateOrNull(date: LocalDate): Flow<DateEntity?> = flow {
        emit(getDateUseCase(date))
    }.flowOn(Dispatchers.IO)

    private fun getDatesCut(days: Int): List<LocalDate> {
        val dates = mutableListOf<LocalDateTime>()
        var date = LocalDateTime.now()

        if (isLongerDate()) {
            date = date.minusHours(extendDayValue)
        }

        for (i in 0 until days) {
            dates.add(date)
            date = date.minusDays(1)
        }
        return dates.map { it.toLocalDate() }
    }

    private fun getWeekDays(): List<LocalDate> {
        val startDate = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY)

        val weekDays = Array(7) { i -> startDate.plusDays(i.toLong()) }

        return weekDays.map { it.toLocalDate() }
    }


    companion object {
        private const val extendDayValue = 3L
    }
}