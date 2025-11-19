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
import com.example.quizapp.util.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 퀴즈 화면에서 사용할 UI 상태를 담는 데이터 클래스.
 *
 * @param currentQuestion 현재 표시 중인 문제
 * @param currentQuestionIndex 현재 문제 인덱스(0부터 시작)
 * @param totalQuestions 전체 문제 수
 * @param selectedAnswerIndex 사용자가 선택한 보기 인덱스 (null이면 아직 선택 안 함)
 * @param score 현재까지 맞힌 문제 수
 * @param isQuizFinished 퀴즈 종료 여부
 * @param wrongAnswers 틀린 문제와 사용자가 선택한 인덱스를 저장하는 맵
 */
data class QuizUiState(
    val currentQuestion: Question? = null,
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val score: Int = 0,
    val isQuizFinished: Boolean = false,
    val wrongAnswers: Map<Question, Int> = emptyMap()
)

/**
 * 퀴즈 진행 로직과 DB 저장을 담당하는 ViewModel.
 *
 * - 선택한 주제의 문제를 로드하고
 * - 정답/오답 처리, 점수 계산
 * - 랭킹/오답을 Room DB에 저장합니다.
 *
 * @param application Application 컨텍스트 (DB 접근용)
 * @param savedStateHandle Navigation으로부터 전달된 topicId 등을 포함
 */
class QuizViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    /** 선택된 퀴즈 주제 ID (Navigation argument에서 가져옴) */
    private val topicId: Int = checkNotNull(savedStateHandle["topicId"])

    /** 퀴즈 관련 DB 작업을 수행할 DAO */
    private val quizDao: QuizDao = AppDatabase.getDatabase(application).quizDao()

    /** 효과음 재생을 위한 SoundManager */
    private val soundManager: SoundManager = SoundManager.getInstance(application)

    /** 내부에서만 변경 가능한 UI 상태 Flow */
    private val _uiState = MutableStateFlow(QuizUiState())

    /** 화면에서 구독하는 공개용 UI 상태 Flow */
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    /** 현재 주제에 대한 문제 목록 */
    private var questionList: List<Question> = emptyList()

    /**
     * 사용자의 답안을 저장하는 맵.
     * key = Question ID, value = 선택한 보기 인덱스
     */
    private val userAnswerMap = mutableMapOf<Int, Int>()

    init {
        // ViewModel 생성 시, 선택된 주제의 문제를 로드
        loadQuestions()
    }

    /**
     * 선택된 주제(topicId)에 해당하는 문제 목록을 로드하고
     * 첫 번째 문제를 UI 상태에 설정합니다.
     */
    private fun loadQuestions() {
        viewModelScope.launch {
            // 선택된 주제의 문제 리스트 가져오기
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

    /**
     * 사용자가 보기 하나를 선택했을 때 호출됩니다.
     *
     * @param index 선택한 보기 인덱스
     */
    fun onAnswerSelected(index: Int) {
        // 현재 문제 ID 기준으로 사용자의 선택을 기록
        _uiState.value.currentQuestion?.let {
            userAnswerMap[it.id] = index
        }
        // UI 상태에 선택된 인덱스 반영
        _uiState.update { currentState ->
            currentState.copy(
                selectedAnswerIndex = index
            )
        }
    }

    /**
     * '다음' 또는 '결과 보기' 버튼을 눌렀을 때 호출됩니다.
     * - 정답 여부를 판단해 점수/오답 목록을 갱신하고
     * - 마지막 문제라면 결과를 DB에 저장하고 퀴즈를 종료합니다.
     */
    fun moveToNextQuestion() {
        val currentState = _uiState.value
        val currentQuestion = currentState.currentQuestion ?: return
        val selectedIndex = currentState.selectedAnswerIndex ?: return

        // 정답 여부 판단
        val isCorrect = (currentQuestion.correctAnswerIndex == selectedIndex)

        // 정답/오답 효과음 재생
        if (isCorrect) {
            soundManager.playCorrectSound()
        } else {
            soundManager.playWrongSound()
        }

        // 맞았으면 점수 +1
        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        // 틀렸을 경우만 오답 목록에 추가
        val newWrongAnswers = if (isCorrect) {
            currentState.wrongAnswers
        } else {
            currentState.wrongAnswers + (currentQuestion to selectedIndex)
        }

        // 다음 문제 인덱스
        val nextQuestionIndex = currentState.currentQuestionIndex + 1

        // 마지막 문제인지 확인
        if (nextQuestionIndex >= questionList.size) {
            // 마지막 문제의 답안도 userAnswerMap에 반영
            _uiState.value.currentQuestion?.let {
                userAnswerMap[it.id] = selectedIndex
            }
            // 최종 결과 DB 저장 및 퀴즈 종료 처리
            saveResults(newScore, userAnswerMap)
        } else {
            // 다음 문제로 이동하면서 상태 갱신
            _uiState.update {
                it.copy(
                    currentQuestion = questionList[nextQuestionIndex],
                    currentQuestionIndex = nextQuestionIndex,
                    score = newScore,
                    wrongAnswers = newWrongAnswers,
                    selectedAnswerIndex = null // 다음 문제를 위해 선택 상태 초기화
                )
            }
        }
    }

    /**
     * 최종 점수와 사용자의 전체 답안을 DB와 QuizResultHolder에 저장합니다.
     *
     * @param finalScore 최종 점수
     * @param userAnswers 문제 ID와 선택한 보기 인덱스를 담은 맵
     */
    private fun saveResults(
        finalScore: Int,
        userAnswers: Map<Int, Int>
    ) {
        viewModelScope.launch {
            // 1) 랭킹 저장용 주제 이름 찾기
            val topicName = DummyData.topics.find { it.id == topicId }?.name ?: "알 수 없는 주제"

            // 2) 랭킹 엔티티 생성 및 DB에 저장
            val rankingItem = RankingItem(
                score = finalScore,
                totalQuestions = questionList.size,
                timestamp = System.currentTimeMillis(),
                topicName = topicName
            )
            quizDao.insertRanking(rankingItem)

            // 3) 오답 노트 저장 (문제당 최대 1건, 다시 틀리면 덮어쓰기)
            questionList.forEach { question ->
                val userAnswerIndex = userAnswers[question.id]
                // 사용자가 답을 했고, 그 답이 정답이 아닐 경우만 저장
                if (userAnswerIndex != null && userAnswerIndex != question.correctAnswerIndex) {
                    val wrongAnswerEntry = WrongAnswer(
                        questionId = question.id,
                        questionText = question.questionText,
                        options = question.options,
                        correctAnswerIndex = question.correctAnswerIndex,
                        selectedAnswerIndex = userAnswerIndex
                    )
                    quizDao.insertWrongAnswer(wrongAnswerEntry)
                }
            }

            // 4) 결과 화면에서 사용할 데이터를 임시 저장소에 복사
            QuizResultHolder.questions = questionList
            QuizResultHolder.userAnswers = userAnswers.toMap()

            // 5) 퀴즈 종료 상태로 변경
            _uiState.update {
                it.copy(
                    score = finalScore,
                    isQuizFinished = true,
                    selectedAnswerIndex = null
                )
            }
        }
    }
}
