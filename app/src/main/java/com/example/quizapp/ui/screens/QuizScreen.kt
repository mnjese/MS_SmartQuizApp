package com.example.quizapp.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun QuizScreen(
    onNavigateToResult: () -> Unit // 결과 화면으로 이동 요청
) {
    Text(text = "퀴즈 화면 (QuizScreen)")

}