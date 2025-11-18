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

/**
 * 랭킹 화면에서 사용할 데이터를 제공하는 ViewModel.
 *
 * - DB에서 랭킹 목록을 가져오고
 * - 선택된 주제에 따라 필터링된 랭킹 리스트를 제공합니다.
 *
 * @param application Application 컨텍스트 (DB 접근용)
 */
class RankingViewModel(application: Application) : AndroidViewModel(application) {

    /** 랭킹 정보를 조회하는 DAO */
    private val quizDao = AppDatabase.getDatabase(application).quizDao()

    /**
     * DB에서 가져온 전체 랭킹 리스트.
     * StateFlow로 변환하여 UI에서 구독할 수 있게 합니다.
     */
    private val allRankings: StateFlow<List<RankingItem>> = quizDao.getAllRankings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * 현재 선택된 주제 이름.
     * 기본값은 "전체" (모든 주제 랭킹 표시).
     */
    private val _selectedTopic = MutableStateFlow("전체")
    val selectedTopic: StateFlow<String> = _selectedTopic.asStateFlow()

    /**
     * 주제 필터가 적용된 랭킹 리스트.
     *
     * - "전체" 인 경우: 모든 랭킹 반환
     * - 특정 주제인 경우: 해당 topicName만 필터링
     */
    val filteredRankingList: StateFlow<List<RankingItem>> =
        combine(allRankings, selectedTopic) { rankings, topic ->
            if (topic == "전체") {
                rankings
            } else {
                rankings.filter { it.topicName == topic }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** UI에서 사용 가능한 토픽 이름 리스트 ("전체" + DummyData의 각 주제 이름) */
    val topicList: List<String> = listOf("전체") + DummyData.topics.map { it.name }

    /**
     * 주제 칩을 눌렀을 때 호출되는 함수.
     *
     * @param topicName 선택된 주제 이름
     */
    fun selectTopic(topicName: String) {
        _selectedTopic.value = topicName
    }
}
