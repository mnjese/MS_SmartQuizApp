package com.example.quizapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.data.model.WrongAnswer
import com.example.quizapp.viewmodel.WrongAnswerViewModel
import kotlinx.coroutines.delay

/**
 * 오답 노트 화면.
 *
 * @param onBack 뒤로가기 버튼 클릭 시 호출되는 콜백
 * @param viewModel 오답 목록을 관리하는 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrongAnswerScreen(
    onBack: () -> Unit,
    viewModel: WrongAnswerViewModel = viewModel()
) {
    val wrongAnswerList by viewModel.wrongAnswerList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("오답 노트") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (wrongAnswerList.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "틀린 문제가 없습니다",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "완벽해요! 🎉",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(wrongAnswerList) { index, item ->
                        // 아이템 등장 애니메이션
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(item) {
                            delay(index * 50L)
                            visible = true
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(300)) +
                                    slideInHorizontally(
                                        animationSpec = tween(300),
                                        initialOffsetX = { it / 2 }
                                    )
                        ) {
                            WrongAnswerItemCard(
                                item = item,
                                onDeleteClick = { viewModel.deleteWrongAnswer(wrongAnswer = item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 오답 문제 하나를 카드 형태로 표시합니다.
 *
 * @param item 표시할 오답 데이터
 * @param onDeleteClick 삭제 버튼 클릭 시 호출되는 콜백
 */
@Composable
fun WrongAnswerItemCard(
    item: WrongAnswer,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 상단: 문제 텍스트 + 삭제 아이콘
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Q. ${item.questionText}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "오답 삭제",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 중단: 보기 목록 표시
            item.options.forEachIndexed { index, option ->
                val isCorrectAnswer = (index == item.correctAnswerIndex)
                val isUserSelection = (index == item.selectedAnswerIndex)

                val color = when {
                    isCorrectAnswer -> Color(0xFF4CAF50) // 정답 - 밝은 초록
                    isUserSelection -> Color(0xFFE53935) // 사용자의 오답 - 밝은 빨강
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                val fontWeight = if (isCorrectAnswer || isUserSelection) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                }

                Text(
                    text = "${index + 1}. $option",
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    fontWeight = fontWeight,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 하단: 정답 / 나의 답 요약
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "✓ 정답: ${item.correctAnswerIndex + 1}번",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "✗ 나의 답: ${item.selectedAnswerIndex + 1}번",
                    color = Color(0xFFE53935),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
