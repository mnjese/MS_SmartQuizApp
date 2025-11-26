package com.example.quizapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
                    // 진행 표시 바
                    LinearProgressIndicator(
                        progress = { (uiState.currentQuestionIndex + 1).toFloat() / uiState.totalQuestions },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "문제 ${uiState.currentQuestionIndex + 1} / ${uiState.totalQuestions}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.End)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentQuestion.questionText,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                // 보기 버튼들 (애니메이션 적용)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    currentQuestion.options.forEachIndexed { index, optionText ->
                        val isSelected = (uiState.selectedAnswerIndex == index)

                        // 선택 시 스케일 애니메이션
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.02f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "scale"
                        )

                        // 배경색 애니메이션
                        val backgroundColor by animateColorAsState(
                            targetValue = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            animationSpec = tween(durationMillis = 200),
                            label = "backgroundColor"
                        )

                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            animationSpec = tween(durationMillis = 200),
                            label = "contentColor"
                        )

                        Button(
                            onClick = { viewModel.onAnswerSelected(index) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .scale(scale),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = backgroundColor,
                                contentColor = contentColor
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = if (isSelected) 8.dp else 2.dp
                            )
                        ) {
                            Text(
                                text = optionText,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                // 다음 / 결과 보기 버튼
                val buttonEnabled = uiState.selectedAnswerIndex != null
                val buttonScale by animateFloatAsState(
                    targetValue = if (buttonEnabled) 1f else 0.95f,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "buttonScale"
                )

                Button(
                    onClick = { viewModel.moveToNextQuestion() },
                    enabled = buttonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .scale(buttonScale),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    val buttonText =
                        if (uiState.currentQuestionIndex == uiState.totalQuestions - 1) {
                            "결과 보기"
                        } else {
                            "다음"
                        }
                    Text(
                        text = buttonText,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
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
