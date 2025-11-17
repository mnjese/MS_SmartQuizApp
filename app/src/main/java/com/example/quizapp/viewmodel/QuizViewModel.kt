package com.example.quizapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.DummyData
import com.example.quizapp.data.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val currentQuestion: Question? = null, // 현재 문제
    val currentQuestionIndex: Int = 0,     // 현재 문제 번호 (0부터)
    val totalQuestions: Int = 0,         // 전체 문제 수
    val selectedAnswerIndex: Int? = null,  // 사용자가 선택한 답 (null = 아직 선택 안 함)
    val score: Int = 0,                    // 현재 점수
    val isQuizFinished: Boolean = false,   // 퀴즈가 끝났는지 여부
    val wrongAnswers: List<Question> = emptyList() // 오답 목록
)

class QuizViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val topicId: Int = checkNotNull(savedStateHandle["topicId"])

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // 질문 목록을 저장할 변수 추가
    private var questionList: List<Question> = emptyList()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            // 로드한 질문을 questionList에 저장
            questionList = DummyData.getQuestionsForTopic(topicId)
            _uiState.update { currentState ->
                currentState.copy(
                    currentQuestion = questionList.firstOrNull(),
                    totalQuestions = questionList.size,
                    currentQuestionIndex = 0
                )
            }
        }
    }

    // 사용자가 답을 선택했을 때 호출할 함수
    fun onAnswerSelected(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedAnswerIndex = index
            )
        }
    }

    fun moveToNextQuestion() {
        val currentState = _uiState.value
        val currentQuestion = currentState.currentQuestion ?: return
        val selectedIndex = currentState.selectedAnswerIndex ?: return

        // 정답 채점
        val isCorrect = (currentQuestion.correctAnswerIndex == selectedIndex)
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        val newWrongAnswers = if (isCorrect) {
            currentState.wrongAnswers
        } else {
            currentState.wrongAnswers + currentQuestion // 틀린 문제 추가
        }

        // 다음 문제 인덱스 계산
        val nextQuestionIndex = currentState.currentQuestionIndex + 1

        // 퀴즈가 끝났는지 확인
        if (nextQuestionIndex >= questionList.size) {
            // 퀴즈 종료
            _uiState.update {
                it.copy(
                    score = newScore,
                    isQuizFinished = true,
                    wrongAnswers = newWrongAnswers,
                    selectedAnswerIndex = null // 선택 상태 초기화
                )
            }
        } else {
            // 다음 문제로 이동
            _uiState.update {
                it.copy(
                    currentQuestion = questionList[nextQuestionIndex],
                    currentQuestionIndex = nextQuestionIndex,
                    score = newScore,
                    wrongAnswers = newWrongAnswers,
                    selectedAnswerIndex = null // 선택 상태 초기화
                )
            }
        }
    }
}