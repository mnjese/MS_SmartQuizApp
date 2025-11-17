package com.example.quizapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel // import 확인
import com.example.quizapp.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    // topicId: Int, // (이제 ViewModel이 알아서 처리하므로 삭제)
    onNavigateToResult: () -> Unit,
    viewModel: QuizViewModel = viewModel() // 1. ViewModel 주입
) {
    // 2. ViewModel의 uiState를 구독
    val uiState by viewModel.uiState.collectAsState()

    // 3. uiState에서 현재 질문을 가져옴
    val currentQuestion = uiState.currentQuestion

    Column {
        if (currentQuestion != null) {
            // 4. 더미 텍스트 대신 실제 문제 텍스트 표시
            Text(text = currentQuestion.questionText)

        } else {
            // 로딩 중이거나 문제가 없을 때
            Text(text = "문제 로딩 중...")
        }
    }
}