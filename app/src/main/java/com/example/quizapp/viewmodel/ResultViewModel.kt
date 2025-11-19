package com.example.quizapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.local.AppDatabase
import com.example.quizapp.data.model.RankingItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * 결과 화면에서 예상 랭킹을 계산하기 위해
 * 전체 랭킹 목록을 제공하는 ViewModel.
 *
 * @param application Application 컨텍스트 (DB 접근용)
 */
class ResultViewModel(application: Application) : AndroidViewModel(application) {

    /** 랭킹 정보를 조회하는 DAO */
    private val quizDao = AppDatabase.getDatabase(application).quizDao()

    /**
     * 전체 랭킹 리스트를 담은 StateFlow.
     * ResultScreen에서 이 값을 기준으로 현재 점수의 순위를 계산합니다.
     */
    val allRankings: StateFlow<List<RankingItem>> = quizDao.getAllRankings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
