package com.example.quizapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val currentQuestion: com.example.quizapp.data.model.Question? = null, // 현재 문제
    val currentQuestionIndex: Int = 0,     // 현재 문제 번호 (0부터)
    val totalQuestions: Int = 0,         // 전체 문제 수
    val selectedAnswerIndex: Int? = null,  // 사용자가 선택한 답 (null = 아직 선택 안 함)
    val score: Int = 0,                    // 현재 점수
    val isQuizFinished: Boolean = false    // 퀴즈가 끝났는지 여부
)

class QuizViewModel(
    // Navigation Argument를 받기 위해 SavedStateHandle 사용
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 1. topicId 추출
    private val topicId: Int = checkNotNull(savedStateHandle["topicId"])

    // 2. UI 상태를 위한 StateFlow 선언
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // 3. ViewModel이 생성될 때 문제 목록을 로드
    init {
        loadQuestions()
    }

    // 4. 문제 로드 로직
    private fun loadQuestions() {
        viewModelScope.launch {
            val questions = Question.getQuestionsForTopic(topicId)
            _uiState.update { currentState ->
                currentState.copy(
                    currentQuestion = questions.firstOrNull(), // 첫 번째 문제
                    totalQuestions = questions.size,
                    currentQuestionIndex = 0
                )
            }
        }
    }

}