package com.example.quizapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.DummyData
import com.example.quizapp.data.QuizResultHolder
import com.example.quizapp.data.local.AppDatabase
import com.example.quizapp.data.local.QuizDao
import com.example.quizapp.data.model.Question
import com.example.quizapp.data.model.RankingItem
import com.example.quizapp.data.model.WrongAnswer
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
    val wrongAnswers: Map<Question, Int> = emptyMap() // 오답 목록
)

class QuizViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val topicId: Int = checkNotNull(savedStateHandle["topicId"])
    private val quizDao: QuizDao = AppDatabase.getDatabase(application).quizDao()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // 질문 목록을 저장할 변수
    private var questionList: List<Question> = emptyList()

    private val userAnswerMap = mutableMapOf<Int, Int>()

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
            currentState.wrongAnswers + (currentQuestion to selectedIndex) // 틀린 문제 추가
        }

        // 다음 문제 인덱스 계산
        val nextQuestionIndex = currentState.currentQuestionIndex + 1

        // 퀴즈가 끝났는지 확인
        if (nextQuestionIndex >= questionList.size) {
            _uiState.value.currentQuestion?.let {
                userAnswerMap[it.id] = selectedIndex
            }
            saveResults(newScore, userAnswerMap)
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

    // DB 저장 함수
    private fun saveResults(
        finalScore: Int,
        userAnswers: Map<Int, Int>
    ) {
        viewModelScope.launch {
            val topicName: String = DummyData.topics.find { it.id == topicId }?.name ?: "알 수 없는 주제"
            // 랭킹 저장
            val rankingItem = RankingItem(
                score = finalScore,
                totalQuestions = questionList.size,
                timestamp = System.currentTimeMillis(),
                topicName = topicName
            )
            quizDao.insertRanking(rankingItem)

            // 오답 노트 저장
            val finalWrongAnswers = mutableMapOf<Question, Int>()
            questionList.forEach { question ->
                val userAnswerIndex = userAnswers[question.id]
                if (userAnswerIndex != null && userAnswerIndex != question.correctAnswerIndex) {
                    // 틀린 경우
                    finalWrongAnswers[question] = userAnswerIndex

                    // DB에 저장
                    val wrongAnswerEntry = WrongAnswer(
                        questionText = question.questionText,
                        options = question.options,
                        correctAnswerIndex = question.correctAnswerIndex,
                        selectedAnswerIndex = userAnswerIndex
                    )
                    quizDao.insertWrongAnswer(wrongAnswerEntry)
                }
            }

            QuizResultHolder.questions = questionList
            QuizResultHolder.userAnswers = userAnswers.toMap()

            // 저장이 완료된 후, 퀴즈 종료 신호 전송
            _uiState.update {
                it.copy(
                    score = finalScore,
                    isQuizFinished = true,
                    //wrongAnswers = finalWrongAnswers,
                    selectedAnswerIndex = null
                )
            }
        }
    }
}