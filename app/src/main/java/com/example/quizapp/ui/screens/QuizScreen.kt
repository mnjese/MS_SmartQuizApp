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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.viewmodel.QuizViewModel

/**
 * 퀴즈 문제를 풀 수 있는 화면.
 * 현재 문제, 보기 선택, 다음 버튼을 제공하며 퀴즈 종료 시 결과 화면으로 이동합니다.
 *
 * @param onNavigateToResult 최종 점수와 총 문제 수를 전달하며 결과 화면으로 이동하는 콜백
 * @param viewModel 퀴즈 로직과 상태를 관리하는 ViewModel
 */
@Composable
fun QuizScreen(
    onNavigateToResult: (Int, Int) -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    // ViewModel의 uiState를 구독하여 상태 변화를 실시간으로 반영합니다.
    val uiState by viewModel.uiState.collectAsState()
    // 현재 화면에 표시할 질문 객체
    val currentQuestion = uiState.currentQuestion

    // uiState.isQuizFinished 상태가 true로 변경될 때를 감지하는 부수 효과
    LaunchedEffect(uiState.isQuizFinished) {
        if (uiState.isQuizFinished) {
            // 퀴즈가 종료되면, 점수와 총 문제 수를 가지고 결과 화면으로 이동합니다.
            onNavigateToResult(uiState.score, uiState.totalQuestions)
        }
    }

    // 화면 전체 레이아웃 (세로 배치)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        // 컴포넌트들을 화면 상단, 중단, 하단에 분산 배치합니다.
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 현재 문제가 존재하고 퀴즈가 끝나지 않았을 때만 UI를 표시합니다.
        if (currentQuestion != null && !uiState.isQuizFinished) {

            // 상단: 퀴즈 진행도 및 문제 텍스트
            Column(modifier = Modifier.fillMaxWidth()) {
                // 퀴즈 진행도 (예: "1 / 10")
                Text(
                    // currentQuestionIndex는 0부터 시작하므로 +1
                    text = "${uiState.currentQuestionIndex + 1} / ${uiState.totalQuestions}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.End) // 오른쪽 정렬
                        .padding(bottom = 8.dp)
                )
                // 현재 문제 텍스트
                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // 중단: 4지선다 선택지 버튼 목록
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 현재 문제의 선택지 목록을 순회하며 버튼을 생성합니다.
                currentQuestion.options.forEachIndexed { index, optionText ->
                    // 현재 선택된 답안인지 여부
                    val isSelected = (uiState.selectedAnswerIndex == index)

                    Button(
                        onClick = {
                            // 버튼 클릭 시 ViewModel에 선택한 인덱스(index)를 알립니다.
                            viewModel.onAnswerSelected(index)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        // 선택 상태(isSelected)에 따라 버튼의 색상을 동적으로 변경합니다.
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary // 선택됨
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant // 기본
                            },
                            contentColor = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary // 선택됨
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant // 기본
                            }
                        )
                    ) {
                        Text(text = optionText)
                    }
                }
            }

            // 하단: '다음' 또는 '결과 보기' 버튼
            Button(
                onClick = {
                    // 버튼 클릭 시 ViewModel에 다음 문제로 넘어가도록 요청합니다.
                    viewModel.moveToNextQuestion()
                },
                // 사용자가 답을 선택했을 때(selectedAnswerIndex != null)만 버튼을 활성화합니다.
                enabled = (uiState.selectedAnswerIndex != null),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                // 마지막 문제인지 여부에 따라 버튼 텍스트를 "결과 보기" 또는 "다음"으로 변경합니다.
                val buttonText = if (uiState.currentQuestionIndex == uiState.totalQuestions - 1) {
                    "결과 보기"
                } else {
                    "다음"
                }
                Text(buttonText)
            }
            // 문제가 로딩 중일 때 (currentQuestion == null)
        } else if (currentQuestion == null) {
            Text(text = "문제 로딩 중...")
        }
        // 퀴즈가 끝났을 때는 (isQuizFinished == true) LaunchedEffect가
        // 화면 전환을 처리하므로 여기서는 아무것도 그리지 않습니다.
    }
}