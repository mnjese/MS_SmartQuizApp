package com.example.quizapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    onNavigateToResult: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentQuestion = uiState.currentQuestion

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween // 콘텐츠를 위아래로 분산
    ) {
        if (currentQuestion != null) {
            // 상단: 질문 텍스트
            Text(
                text = currentQuestion.questionText,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 중단: 4지선다
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                currentQuestion.options.forEachIndexed { index, optionText ->
                    val isSelected = (uiState.selectedAnswerIndex == index)

                    Button(
                        onClick = {
                            // 1. 버튼 클릭 시 ViewModel의 함수 호출
                            viewModel.onAnswerSelected(index)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        // 2. 선택 상태에 따라 버튼 색상 변경
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    ) {
                        Text(text = optionText)
                    }
                }
            }

            // 하단: '다음' 버튼
            Button(
                onClick = {
                    viewModel.moveToNextQuestion() // 다음 커밋에서 이 함수를 구현
                },
                // 3. 답이 선택되었을 때만 활성화
                enabled = (uiState.selectedAnswerIndex != null),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text("다음")
            }
        } else {
            // 로딩 중... (또는 에러)
            Text(text = "문제 로딩 중...")
        }
    }
}