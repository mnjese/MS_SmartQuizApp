package com.example.quizapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quizapp.data.DummyData // 더미 데이터 import
import com.example.quizapp.data.model.QuizTopic


@OptIn(ExperimentalMaterial3Api::class) // Card 클릭을 위해 추가
@Composable
fun MainScreen(
    onNavigateToQuiz: () -> Unit,
    onNavigateToRanking: () -> Unit,
    onNavigateToWrongAnswer: () -> Unit
) {
    val topics = DummyData.topics // 1. 더미 데이터 가져오기

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

        // 2. 퀴즈 주제 목록 (LazyColumn을 사용해도 좋습니다)
        topics.forEach { topic ->
            TopicCard(
                topic = topic,
                onClick = {
                    onNavigateToQuiz() // 3. 클릭 시 네비게이션 함수 호출
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp)) // 버튼 그룹과 간격

        // 4. 랭킹 및 오답 노트 버튼
        Button(
            onClick = onNavigateToRanking, // 3. 클릭 시 네비게이션 함수 호출
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("랭킹 보기")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onNavigateToWrongAnswer, // 3. 클릭 시 네비게이션 함수 호출
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("오답 노트")
        }
    }
}

// 주제 카드를 별도 Composable로 분리
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicCard(topic: QuizTopic, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick, // 카드 자체를 클릭 가능하게
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = topic.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen({}, {}, {})
}