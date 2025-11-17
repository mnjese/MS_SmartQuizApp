package com.example.quizapp.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainScreen(
    onNavigateToQuiz: () -> Unit, // 퀴즈 화면으로 이동 요청
    onNavigateToRanking: () -> Unit, // 랭킹 화면으로 이동 요청
    onNavigateToWrongAnswer: () -> Unit // 오답 노트로 이동 요청
) {
    // 지금은 UI를 만들지 않고, 내용이 비어있음을 알리는 텍스트만 둡니다.
    Text(text = "메인 화면 (MainScreen)")

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen({}, {}, {})
}