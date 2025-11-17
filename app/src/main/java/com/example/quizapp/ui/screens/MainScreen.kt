package com.example.quizapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quizapp.data.DummyData
import com.example.quizapp.data.model.QuizTopic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToQuiz: (Int) -> Unit,
    onNavigateToRanking: () -> Unit,
    onNavigateToWrongAnswer: () -> Unit
) {
    val topics = DummyData.topics
    // 1. 현재 선택된 주제 ID를 기억하는 상태 (null = 선택 안 함)
    var selectedTopicId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "퀴즈 앱",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 2. 주제 목록
        topics.forEach { topic ->
            val isSelected = (topic.id == selectedTopicId)
            TopicCard(
                topic = topic,
                isSelected = isSelected, // 3. 선택 상태 전달
                onClick = {
                    selectedTopicId = if (selectedTopicId == topic.id) {
                        null // 선택 해제
                    } else {
                        topic.id // 새 주제 선택
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 5. 퀴즈 시작 버튼
        AnimatedVisibility(visible = (selectedTopicId != null)) {
            // AnimatedVisibility는 Column 스코프를 제공하므로 Column으로 감싸줍니다.
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        selectedTopicId?.let { id ->
                            onNavigateToQuiz(id) // 선택된 ID로 네비게이션
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("퀴즈 시작하기")
                }
                // '퀴즈 시작' 버튼과 '랭킹 보기' 버튼 사이의 간격
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // 3. '랭킹' 및 '오답 노트' 버튼
        // (이 버튼들 사이의 Spacer(16.dp)는 AnimatedVisibility 안으로 이동했습니다)
        Button(
            onClick = onNavigateToRanking,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("랭킹 보기")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onNavigateToWrongAnswer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("오답 노트")
        }
    }
}

// TopicCard가 isSelected 파라미터를 받도록 수정
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicCard(
    topic: QuizTopic,
    isSelected: Boolean, // 선택 상태
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then( // 8. 선택되면 테두리 표시 (시각적 피드백)
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CardDefaults.shape
                    )
                } else {
                    Modifier
                }
            ),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        // 선택되면 배경색도 변경
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant // 기본 카드색
            }
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = topic.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen({}, {}, {})
}