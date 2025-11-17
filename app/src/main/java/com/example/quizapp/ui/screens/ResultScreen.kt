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

@Composable
fun ResultScreen(
    score: Int,
    totalQuestions: Int,
    onNavigateToMain: () -> Unit,
    viewModel: ResultViewModel = viewModel()
) {
    // Holder에서 문제 목록과 답안지 가져오기
    val questions = QuizResultHolder.questions
    val userAnswers = QuizResultHolder.userAnswers

    // DB에서 전체 랭킹 가져오기
    val rankings by viewModel.allRankings.collectAsState()

    // 예상 랭킹 계산
    val newRank = (rankings.count { it.score > score } + 1)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 결과 요약
        item {
            Text(
                text = "퀴즈 결과",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "총 $totalQuestions 문제 중 $score 문제를 맞혔습니다.",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 예상 랭킹 표시
            Text(
                text = "예상 랭킹: $newRank 위",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))

            // 문제 목록 헤더
            Text(
                text = "전체 문제 다시보기",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
        }

        // 전체 문제 목록
        items(questions) { question ->
            val selectedIndex = userAnswers[question.id]
            val isCorrect = (selectedIndex == question.correctAnswerIndex)
            ResultQuestionItem(
                question = question,
                selectedIndex = selectedIndex,
                isCorrect = isCorrect
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Button(
                onClick = {
                    // Holder 데이터 정리
                    QuizResultHolder.questions = emptyList()
                    QuizResultHolder.userAnswers = emptyMap()
                    onNavigateToMain()
                },
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text("메인으로 돌아가기")
            }
        }
    }
}

// 결과 화면에서 사용할 문제 아이템 Composable
@Composable
fun ResultQuestionItem(
    question: Question,
    selectedIndex: Int?,
    isCorrect: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            // 맞았으면 연한 초록색, 틀렸으면 연한 빨간색
            containerColor = if (isCorrect) {
                Color(0xFFE8F5E9) // Green 50
            } else {
                Color(0xFFFFEBEE) // Red 50
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Q. ${question.questionText}",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            question.options.forEachIndexed { index, option ->
                val isCorrectAnswer = (index == question.correctAnswerIndex)
                val isUserSelection = (index == selectedIndex)

                val color = when {
                    isCorrectAnswer -> Color(0xFF008000) // 정답 (초록색)
                    isUserSelection -> Color.Red // 사용자의 오답 (빨간색)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
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