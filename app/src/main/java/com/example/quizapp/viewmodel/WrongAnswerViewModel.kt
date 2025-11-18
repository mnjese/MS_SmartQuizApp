package com.example.quizapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.local.AppDatabase
import com.example.quizapp.data.model.WrongAnswer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 오답 노트 화면에서 사용할 데이터를 제공하는 ViewModel.
 *
 * - DB에서 오답 목록을 가져오고
 * - 항목 삭제 요청을 처리합니다.
 *
 * @param application Application 컨텍스트 (DB 접근용)
 */
class WrongAnswerViewModel(application: Application) : AndroidViewModel(application) {

    /** 오답 정보를 조회/삭제하는 DAO */
    private val quizDao = AppDatabase.getDatabase(application).quizDao()

    /**
     * DB에 저장된 전체 오답 목록을 StateFlow로 제공합니다.
     * - 오답이 추가/삭제되면 자동으로 갱신됩니다.
     */
    val wrongAnswerList: StateFlow<List<WrongAnswer>> = quizDao.getAllWrongAnswers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * 특정 오답 항목을 삭제합니다.
     *
     * @param wrongAnswer 삭제할 오답 엔티티
     */
    fun deleteWrongAnswer(wrongAnswer: WrongAnswer) {
        viewModelScope.launch {
            quizDao.deleteWrongAnswer(wrongAnswer)
        }
    }
}
