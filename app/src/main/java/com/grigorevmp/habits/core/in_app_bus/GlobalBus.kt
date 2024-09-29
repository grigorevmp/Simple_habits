package com.grigorevmp.habits.core.in_app_bus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

object GlobalBus {

    private val data = MutableSharedFlow<Event>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun post(event: Event) = scope.launch {
        data.emit(event)
    }

    fun postWithDelay(event: Event, delayValue: Long)  = scope.launch {
        delay(delayValue)
        data.emit(event)
    }

    fun events() = flow {
        data.collect {
            emit(it)
        }
    }

    fun onDestroy() {
        scope.cancel()
    }
}