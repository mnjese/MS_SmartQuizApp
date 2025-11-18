package com.example.quizapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapp.data.model.WrongAnswer
import com.example.quizapp.viewmodel.WrongAnswerViewModel

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
                Text("틀린 문제가 없습니다. 완벽해요!")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(wrongAnswerList) { item ->
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "오답 삭제",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // 중단: 보기 목록 표시
            item.options.forEachIndexed { index, option ->
                val isCorrectAnswer = (index == item.correctAnswerIndex)
                val isUserSelection = (index == item.selectedAnswerIndex)

                val color = when {
                    isCorrectAnswer -> Color(0xFF008000) // 정답
                    isUserSelection -> Color.Red         // 사용자의 오답
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                val fontWeight = if (isCorrectAnswer || isUserSelection) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }

                Text(
                    text = "  ${index + 1}. $option",
                    color = color,
                    fontWeight = fontWeight,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 하단: 정답 / 나의 답 요약
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "정답: ${item.correctAnswerIndex + 1}",
                    color = Color(0xFF008000),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = " / 나의 답: ${item.selectedAnswerIndex + 1}",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
