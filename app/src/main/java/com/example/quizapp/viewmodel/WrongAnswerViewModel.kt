package com.example.quizapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.local.AppDatabase
import com.example.quizapp.data.model.WrongAnswer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class WrongAnswerViewModel(application: Application) : AndroidViewModel(application) {

    private val quizDao = AppDatabase.getDatabase(application).quizDao()

    val wrongAnswerList: StateFlow<List<WrongAnswer>> = quizDao.getAllWrongAnswers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}