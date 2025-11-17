package com.example.quizapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@Composable
fun WrongAnswerScreen(
    viewModel: WrongAnswerViewModel = viewModel() // 1. ViewModel 주입
) {
    // 2. 오답 리스트 구독
    val wrongAnswerList by viewModel.wrongAnswerList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = "오답 노트",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 3. 오답 목록 표시
        if (wrongAnswerList.isEmpty()) {
            Text("틀린 문제가 없습니다. 완벽해요!")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(wrongAnswerList) { item ->
                    WrongAnswerItemCard(
                        item = item,
                        // 3. [추가] 삭제 버튼 클릭 시 ViewModel 함수 호출
                        onDeleteClick = { viewModel.deleteWrongAnswer(wrongAnswer = item) }
                    )
                }
            }
        }
    }
}

// 오답 아이템을 표시할 카드 Composable
@Composable
fun WrongAnswerItemCard(item: WrongAnswer, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 문제 (weight(1f)로 공간 차지)
                Text(
                    text = "Q. ${item.questionText}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f) // 텍스트가 공간을 차지
                )

                // 삭제 아이콘 버튼
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.padding(start = 8.dp) // 문제와의 간격
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "오답 삭제",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // 4지선다 (선택한 답과 정답을 강조)
            item.options.forEachIndexed { index, option ->
                val isCorrectAnswer = (index == item.correctAnswerIndex)
                val isUserSelection = (index == item.selectedAnswerIndex)

                val color = when {
                    isCorrectAnswer -> Color(0xFF008000) // 정답 (초록색)
                    isUserSelection -> Color.Red // 사용자의 오답 (빨간색)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                val fontWeight = if (isCorrectAnswer || isUserSelection) FontWeight.Bold else FontWeight.Normal

                Text(
                    text = "  ${index + 1}. $option",
                    color = color,
                    fontWeight = fontWeight,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 요약 (선택적)
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