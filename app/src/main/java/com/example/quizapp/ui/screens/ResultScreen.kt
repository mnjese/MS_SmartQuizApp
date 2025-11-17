package com.example.quizapp.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ResultScreen(
    onNavigateToMain: () -> Unit // 메인 화면으로 돌아가기 요청
) {
    Text(text = "결과 화면 (ResultScreen)")

}