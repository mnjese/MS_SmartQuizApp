package com.example.quizapp.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun QuizScreen(
    topicId: Int, // topicId 인자 추가
    onNavigateToResult: () -> Unit
) {
    // 전달받은 ID를 표시 (테스트용)
    Text(text = "퀴즈 화면 (Topic ID: $topicId)")
}