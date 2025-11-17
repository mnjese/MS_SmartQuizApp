package com.example.quizapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.DummyData
import com.example.quizapp.data.local.AppDatabase
import com.example.quizapp.data.model.RankingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RankingViewModel(application: Application) : AndroidViewModel(application) {

    private val quizDao = AppDatabase.getDatabase(application).quizDao()

    // DB에서 가져온 모든 랭킹
    private val allRankings: StateFlow<List<RankingItem>> = quizDao.getAllRankings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 현재 선택된 주제를 저장하는 StateFlow
    private val _selectedTopic = MutableStateFlow("전체")
    val selectedTopic: StateFlow<String> = _selectedTopic.asStateFlow()

    // 4. UI가 구독할 필터링된 랭킹 리스트
    val filteredRankingList: StateFlow<List<RankingItem>> =
        combine(allRankings, selectedTopic) { rankings, topic ->
            if (topic == "전체") {
                rankings // "전체"면 모든 랭킹 반환
            } else {
                rankings.filter { it.topicName == topic } // 아니면 topicName으로 필터링
            }
        }.stateIn( // combine 결과를 다시 StateFlow로 변환
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 주제 버튼을 클릭할 때 호출할 함수
    fun selectTopic(topicName: String) {
        _selectedTopic.value = topicName
    }

    // UI에서 사용할 토픽 리스트 (하드코딩)
    val topicList: List<String> = listOf("전체") + DummyData.topics.map { it.name }
}