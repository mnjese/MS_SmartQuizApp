package com.example.quizapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.data.QuizResultHolder
import com.example.quizapp.data.model.Question
import com.example.quizapp.viewmodel.ResultViewModel

/**
 * 퀴즈 종료 후 점수 요약, 예상 랭킹, 문제별 정오답을 표시하는 화면.
 *
 * @param score 맞힌 문제 수
 * @param totalQuestions 전체 문제 수
 * @param onNavigateToMain 메인 화면으로 돌아가는 콜백
 * @param viewModel 랭킹 목록을 조회하는 ViewModel
 */
@Composable
fun ResultScreen(
    score: Int,
    totalQuestions: Int,
    onNavigateToMain: () -> Unit,
    viewModel: ResultViewModel = viewModel()
) {
    // 1. 임시 저장소(QuizResultHolder)에서 문제 목록과 사용자 답안지를 가져옵니다.
    // 이 데이터는 QuizViewModel이 퀴즈 종료 직전에 저장한 것입니다.
    val questions = QuizResultHolder.questions
    val userAnswers = QuizResultHolder.userAnswers

    // 2. DB에서 저장된 모든 랭킹 기록을 State로 구독합니다.
    val rankings by viewModel.allRankings.collectAsState()

    // 3. 예상 랭킹을 계산합니다.
    // (현재 랭킹 목록에서 나보다 점수가 높은 사람의 수 + 1)
    // QuizViewModel이 DB에 저장을 완료하면 'rankings' Flow가 갱신되어 순위가 계산됩니다.
    val newRank = (rankings.count { it.score > score } + 1)

    // 상세 결과는 스크롤이 필요하므로 LazyColumn을 사용합니다.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. 상단: 결과 요약 ---
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "퀴즈 결과",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(24.dp))

                // 맞힌 개수 요약
                Text(
                    text = "총 $totalQuestions 문제 중 $score 문제를 맞혔습니다.",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 예상 랭킹 표시
                Text(
                    text = "예상 랭킹: $newRank 위",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary // 랭킹 강조
                )
                Spacer(modifier = Modifier.height(32.dp))

                // 문제 목록 헤더
                Text(
                    text = "전체 문제 다시보기",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth() // 왼쪽 정렬을 위해
                )
                // 헤더 구분선
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
            }
        }

        // --- 2. 중단: 풀었던 문제 목록 ---
        // 'questions' 리스트를 순회하며 각 문제에 대한 'ResultQuestionItem'을 생성합니다.
        items(questions) { question ->
            // 이 문제에 대해 사용자가 선택했던 답안
            val selectedIndex = userAnswers[question.id]
            // 이 문제를 사용자가 맞혔는지 여부
            val isCorrect = (selectedIndex == question.correctAnswerIndex)

            ResultQuestionItem(
                question = question,
                selectedIndex = selectedIndex,
                isCorrect = isCorrect
            )
            // 아이템 사이 간격
            Spacer(modifier = Modifier.height(12.dp))
        }

        // --- 3. 하단: 메인으로 돌아가기 버튼 ---
        item {
            Button(
                onClick = {
                    // [중요] 메인 화면으로 돌아가기 전, 임시 저장소의 데이터를 비웁니다.
                    // 이렇게 하지 않으면 다음에 다른 퀴즈를 풀고 결과 화면에 왔을 때
                    // 이전 데이터가 남아있을 수 있습니다.
                    QuizResultHolder.questions = emptyList()
                    QuizResultHolder.userAnswers = emptyMap()

                    // 네비게이션 람다 호출
                    onNavigateToMain()
                },
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text("메인으로 돌아가기")
            }
        }
    }
}

/**
 * 문제 하나의 정답/오답 여부와 옵션을 표시하는 카드.
 *
 * @param question 문제 정보
 * @param selectedIndex 사용자가 선택한 보기 인덱스
 * @param isCorrect 정답 여부
 */
@Composable
fun ResultQuestionItem(
    question: Question,
    selectedIndex: Int?,
    isCorrect: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        // 사용자가 맞혔는지(isCorrect) 여부에 따라 카드 배경색을 다르게 설정
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) {
                Color(0xFFE8F5E9) // 맞음 (연한 초록)
            } else {
                Color(0xFFFFEBEE) // 틀림 (연한 빨강)
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 문제 텍스트
            Text(
                text = "Q. ${question.questionText}",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 4지선다 옵션 목록
            question.options.forEachIndexed { index, option ->
                val isCorrectAnswer = (index == question.correctAnswerIndex) // 이 옵션이 '정답'인가?
                val isUserSelection = (index == selectedIndex)           // 이 옵션이 '사용자 선택'인가?

                // when문을 사용해 옵션 텍스트의 색상을 결정
                val color = when {
                    // 1순위: '정답'은 항상 진한 초록색
                    isCorrectAnswer -> Color(0xFF008000)
                    // 2순위: '사용자가 선택한 오답'은 빨간색
                    isUserSelection -> Color.Red
                    // 3순위: 그 외 (선택 안 된 오답)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                // 정답 또는 사용자가 선택한 답은 굵게 표시
                val fontWeight = if (isCorrectAnswer || isUserSelection) FontWeight.Bold else FontWeight.Normal

                Text(
                    text = "${index + 1}. $option",
                    color = color,
                    fontWeight = fontWeight
                )
            }
        }
    }
}