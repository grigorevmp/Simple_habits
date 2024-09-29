package com.grigorevmp.habits.presentation.screen.settings

import android.content.Context
import android.os.PowerManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grigorevmp.habits.data.repository.PreferencesRepository
import com.grigorevmp.habits.domain.usecase.date.GetAllDatesUseCase
import com.grigorevmp.habits.domain.usecase.habit_ref.GetHabitRefUseCase
import com.grigorevmp.habits.domain.usecase.habits.GetOnlyHabitsUseCase
import com.grigorevmp.habits.presentation.theme.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val getHabitsUseCase: GetOnlyHabitsUseCase,
    private val getHabitRefUseCase: GetHabitRefUseCase,
    private val getAllDatesUseCase: GetAllDatesUseCase,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private var scope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    override fun onCleared() {
        super.onCleared()

        scope.cancel()
    }


    fun isIgnoringBattery(context: Context): Boolean {
        val pm: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName: String = context.packageName

        return pm.isIgnoringBatteryOptimizations(packageName)
    }

    fun getPackageName(context: Context): String = context.packageName

    fun getCongratsEmoji(): Set<String> = preferencesRepository.getCongratsEmoji()

    fun setCongratsEmoji(emoji: Set<String>) = viewModelScope.launch {
        preferencesRepository.setCongratsEmoji(emoji)
    }

    fun getLongerDateFlag(): Boolean = preferencesRepository.getLongerDateFlag()

    fun setLongerDateFlag(makeDayLonger: Boolean) = viewModelScope.launch {
        preferencesRepository.setLongerDateFlag(makeDayLonger)
    }

    fun setTheme(theme: ThemePreference) = viewModelScope.launch {
        preferencesRepository.setAppTheme(theme)
    }

    fun getTheme() = preferencesRepository.getAppTheme()

    fun getPreparedHabitsList(payload: (String) -> Unit) = scope.launch {
        getHabitsUseCase().collectLatest { habitsData ->
            getAllDatesUseCase().collect { dates ->
                val habitMap: MutableMap<Long, String> = mutableMapOf()

                var result = "Habit tracker\n\n"

                for (habit in habitsData) {
                    result += habit.representForBackup()
                    result += habit.countableEntity?.representForBackup() ?: "\n"
                    habitMap[habit.id] = habit.title
                }
                result += "======\n"

                for (date in dates) {
                    result += "======\n"
                    result += date.date.toString() + "\n"
                    if (date.id == null) continue

                    for (habit in habitsData) {
                        val habitRef = getHabitRefUseCase(date.id, habit.id)

                        if (habitRef != null) {
                            result += habitMap[habit.id] + habitRef.representForBackup()
                        }
                    }
                }

                payload(result)
            }
        }
    }
}