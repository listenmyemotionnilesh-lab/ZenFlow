package com.zenflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenflow.app.data.model.FocusSession
import com.zenflow.app.data.model.SessionType
import com.zenflow.app.data.repository.ZenFlowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val repository: ZenFlowRepository
) : ViewModel() {

    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState = _timerState.asStateFlow()

    private val _timeRemaining = MutableStateFlow(25 * 60)
    val timeRemaining = _timeRemaining.asStateFlow()

    private val _totalTime = MutableStateFlow(25 * 60)
    val totalTime = _totalTime.asStateFlow()

    private val _sessionType = MutableStateFlow(SessionType.POMODORO)
    val sessionType = _sessionType.asStateFlow()

    private var timerJob: Job? = null
    private var currentSessionId: Long = 0

    val todaySessions = repository.getTodaySessions().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun setSessionType(type: SessionType, minutes: Int) {
        _sessionType.value = type
        _totalTime.value = minutes * 60
        _timeRemaining.value = minutes * 60
        _timerState.value = TimerState.IDLE
    }

    fun startTimer() {
        if (_timerState.value == TimerState.RUNNING) return

        _timerState.value = TimerState.RUNNING

        viewModelScope.launch {
            val session = FocusSession(
                type = _sessionType.value,
                durationMinutes = _totalTime.value / 60
            )
            currentSessionId = repository.addSession(session)
        }

        timerJob = viewModelScope.launch {
            while (_timeRemaining.value > 0 && _timerState.value == TimerState.RUNNING) {
                delay(1000)
                _timeRemaining.value = _timeRemaining.value - 1
            }
            if (_timeRemaining.value <= 0) {
                completeSession()
            }
        }
    }

    fun pauseTimer() {
        _timerState.value = TimerState.PAUSED
        timerJob?.cancel()
    }

    fun resumeTimer() {
        startTimer()
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState.IDLE
        _timeRemaining.value = _totalTime.value
    }

    fun completeSession() {
        timerJob?.cancel()
        _timerState.value = TimerState.COMPLETED

        viewModelScope.launch {
            val session = repository.getAllSessions().first().find { it.id == currentSessionId }
            session?.let {
                repository.updateSession(it.copy(endTime = System.currentTimeMillis(), isCompleted = true))
            }
        }
    }

    fun resetTimer() {
        stopTimer()
        _timeRemaining.value = _totalTime.value
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

enum class TimerState {
    IDLE, RUNNING, PAUSED, COMPLETED
}