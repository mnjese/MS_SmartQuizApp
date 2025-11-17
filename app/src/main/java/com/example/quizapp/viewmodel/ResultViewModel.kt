package com.example.quizapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.local.AppDatabase
import com.example.quizapp.data.model.RankingItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ResultViewModel(application: Application) : AndroidViewModel(application) {

    private val quizDao = AppDatabase.getDatabase(application).quizDao()

    // 예상 랭킹 표시를 위한 랭킹 가져오기
    val allRankings: StateFlow<List<RankingItem>> = quizDao.getAllRankings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}