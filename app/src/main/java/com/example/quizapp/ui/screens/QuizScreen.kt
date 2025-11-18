package com.example.quizapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.viewmodel.QuizViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment

/**
 * 퀴즈 문제를 풀 수 있는 화면.
 * 현재 문제, 보기 선택, 다음 버튼을 제공하며 퀴즈 종료 시 결과 화면으로 이동합니다.
 *
 * @param onNavigateToResult 최종 점수와 총 문제 수를 전달하며 결과 화면으로 이동하는 콜백
 * @param onNavigateToMain 퀴즈 종료(백 버튼) 시 메인 화면으로 이동하는 콜백
 * @param viewModel 퀴즈 로직과 상태를 관리하는 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateToResult: (Int, Int) -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentQuestion = uiState.currentQuestion

    // 종료 여부 확인을 위한 다이얼로그 상태
    var showExitDialog by remember { mutableStateOf(false) }

    // 시스템 뒤로가기 버튼 처리
    BackHandler {
        showExitDialog = true
    }

    // 종료 확인 다이얼로그
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("퀴즈 종료") },
            text = { Text("정말 퀴즈를 종료하고 메인 화면으로 돌아가시겠습니까?") },
            confirmButton = {
                TextButton(onClick = onNavigateToMain) {
                    Text("종료")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("퀴즈") },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "퀴즈 종료"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentQuestion != null && !uiState.isQuizFinished) {

                // 진행도 + 문제 텍스트
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${uiState.currentQuestionIndex + 1} / ${uiState.totalQuestions}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(bottom = 8.dp)
                    )
                    Text(
                        text = currentQuestion.questionText,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                // 보기 버튼들
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    currentQuestion.options.forEachIndexed { index, optionText ->
                        val isSelected = (uiState.selectedAnswerIndex == index)

                        Button(
                            onClick = { viewModel.onAnswerSelected(index) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
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

                // 다음 / 결과 보기 버튼
                Button(
                    onClick = { viewModel.moveToNextQuestion() },
                    enabled = (uiState.selectedAnswerIndex != null),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    val buttonText =
                        if (uiState.currentQuestionIndex == uiState.totalQuestions - 1) {
                            "결과 보기"
                        } else {
                            "다음"
                        }
                    Text(buttonText)
                }
            } else if (currentQuestion == null) {
                Text(text = "문제 로딩 중...")
            }
        }
    }

    // 퀴즈 종료 플래그에 따른 결과 화면 이동
    LaunchedEffect(uiState.isQuizFinished) {
        if (uiState.isQuizFinished) {
            onNavigateToResult(uiState.score, uiState.totalQuestions)
        }
    }
}
