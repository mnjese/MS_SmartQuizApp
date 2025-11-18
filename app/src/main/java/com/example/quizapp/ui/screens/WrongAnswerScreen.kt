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

/**
 * DB에 저장된 틀린 문제 목록(오답 노트)을 표시하는 화면 Composable.
 * 각 항목에는 삭제 기능이 포함되어 있습니다.
 *
 * @param viewModel 오답 데이터 로드 및 삭제 로직을 관리하는 [WrongAnswerViewModel].
 */
@Composable
fun WrongAnswerScreen(
    viewModel: WrongAnswerViewModel = viewModel()
) {
    // ViewModel로부터 오답 리스트를 DB 변경에 따라 실시간으로 구독합니다.
    val wrongAnswerList by viewModel.wrongAnswerList.collectAsState()

    // 화면 전체 레이아웃
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 화면 타이틀
        Text(
            text = "오답 노트",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 오답 목록 표시
        if (wrongAnswerList.isEmpty()) {
            // 목록이 비었을 때 메시지
            Text("틀린 문제가 없습니다. 완벽해요!")
        } else {
            // 오답 리스트를 LazyColumn으로 효율적으로 표시합니다.
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(wrongAnswerList) { item ->
                    // 각 오답 아이템 카드
                    WrongAnswerItemCard(
                        item = item,
                        // 삭제 버튼 클릭 시 ViewModel의 deleteWrongAnswer 함수 호출
                        onDeleteClick = { viewModel.deleteWrongAnswer(wrongAnswer = item) }
                    )
                }
            }
        }
    }
}

/**
 * 오답 목록의 각 문제를 카드 형태로 표시하는 Composable.
 *
 * @param item 표시할 오답 데이터 [WrongAnswer].
 * @param onDeleteClick 삭제 버튼 클릭 시 호출될 람다.
 */
@Composable
fun WrongAnswerItemCard(item: WrongAnswer, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 상단: 문제 텍스트와 삭제 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // 양쪽 끝으로 정렬
                verticalAlignment = Alignment.Top
            ) {
                // 문제 텍스트 (삭제 버튼을 위해 가로 공간을 최대한 차지)
                Text(
                    text = "Q. ${item.questionText}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // 삭제 아이콘 버튼
                IconButton(
                    onClick = onDeleteClick, // 클릭 시 삭제 요청
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "오답 삭제",
                        tint = MaterialTheme.colorScheme.error // 삭제는 에러 색상(빨간색)으로 표시
                    )
                }
            }

            // 중단: 4지선다 옵션 목록
            item.options.forEachIndexed { index, option ->
                val isCorrectAnswer = (index == item.correctAnswerIndex) // 실제 정답
                val isUserSelection = (index == item.selectedAnswerIndex) // 사용자 오답

                // 색상 결정 로직 (정답은 초록, 사용자 오답은 빨강)
                val color = when {
                    isCorrectAnswer -> Color(0xFF008000) // 정답 (초록색)
                    isUserSelection -> Color.Red         // 사용자의 오답 (빨간색)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                // 정답 또는 사용자가 선택한 오답은 굵게 표시
                val fontWeight = if (isCorrectAnswer || isUserSelection) FontWeight.Bold else FontWeight.Normal

                Text(
                    text = "  ${index + 1}. $option",
                    color = color,
                    fontWeight = fontWeight,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 하단: 정답 및 오답 요약
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