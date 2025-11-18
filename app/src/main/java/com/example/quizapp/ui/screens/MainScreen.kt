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

/**
 * 앱의 메인 화면 Composable.
 * 퀴즈 주제 선택, 랭킹/오답노트 화면으로의 탐색을 담당합니다.
 *
 * @param onNavigateToQuiz 퀴즈 화면으로 이동을 요청하는 람다. 선택된 주제 ID(Int)를 전달합니다.
 * @param onNavigateToRanking 랭킹 화면으로 이동을 요청하는 람다.
 * @param onNavigateToWrongAnswer 오답 노트 화면으로 이동을 요청하는 람다.
 */
@OptIn(ExperimentalMaterial3Api::class) // Card의 onClick을 사용하기 위해 필요
@Composable
fun MainScreen(
    onNavigateToQuiz: (Int) -> Unit,
    onNavigateToRanking: () -> Unit,
    onNavigateToWrongAnswer: () -> Unit
) {
    // 퀴즈 주제 목록을 더미 데이터에서 가져옵니다.
    val topics = DummyData.topics

    // 현재 선택된 주제의 ID를 기억하는 상태 변수.
    // null은 아무것도 선택되지 않았음을 의미합니다.
    var selectedTopicId by remember { mutableStateOf<Int?>(null) }

    // 화면 전체를 채우는 세로 배치
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // 가로 중앙 정렬
        verticalArrangement = Arrangement.Center // 세로 중앙 정렬 (콘텐츠가 적을 경우)
    ) {
        // 앱 타이틀
        Text(
            text = "퀴즈 앱",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 퀴즈 주제 목록을 동적으로 생성합니다.
        topics.forEach { topic ->
            // 현재 주제가 선택된 상태인지 여부
            val isSelected = (topic.id == selectedTopicId)

            // 각 주제를 표시하는 카드 Composable
            TopicCard(
                topic = topic,
                isSelected = isSelected,
                onClick = {
                    // 카드를 클릭했을 때의 로직
                    // 만약 이미 선택된 카드를 다시 클릭하면 선택을 해제(null)하고,
                    // 다른 카드를 클릭하면 해당 ID로 상태를 업데이트합니다.
                    selectedTopicId = if (selectedTopicId == topic.id) {
                        null
                    } else {
                        topic.id
                    }
                }
            )
            // 카드 사이의 간격
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 주제 카드 목록과 하단 버튼 사이의 간격
        Spacer(modifier = Modifier.height(32.dp))

        // '퀴즈 시작하기' 버튼 (애니메이션 적용)
        // selectedTopicId가 null이 아닐 때 (즉, 주제가 선택되었을 때)만 보입니다.
        AnimatedVisibility(visible = (selectedTopicId != null)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        // 선택된 주제 ID가 null이 아님을 확인하고 네비게이션 함수 호출
                        selectedTopicId?.let { id ->
                            onNavigateToQuiz(id)
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

        // '랭킹 보기' 버튼
        Button(
            onClick = onNavigateToRanking, // 클릭 시 랭킹 화면으로 이동
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("랭킹 보기")
        }
        // 버튼 사이 간격
        Spacer(modifier = Modifier.height(8.dp))

        // '오답 노트' 버튼
        Button(
            onClick = onNavigateToWrongAnswer, // 클릭 시 오답 노트 화면으로 이동
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("오답 노트")
        }
    }
}

/**
 * 퀴즈 주제 하나를 표시하는 재사용 가능한 카드 Composable.
 *
 * @param topic 표시할 퀴즈 주제 데이터
 * @param isSelected 이 카드가 현재 선택되었는지 여부
 * @param onClick 카드가 클릭되었을 때 호출될 람다
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicCard(
    topic: QuizTopic,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // .then()을 사용해 isSelected 상태에 따라 동적으로 Modifier를 추가합니다.
            .then(
                if (isSelected) {
                    // 선택되었을 때: Primary 색상의 테두리 추가
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CardDefaults.shape
                    )
                } else {
                    // 선택되지 않았을 때: 아무 효과 없음
                    Modifier
                }
            ),
        onClick = onClick, // 카드 클릭 이벤트 연결
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        // 선택 상태에 따라 카드의 배경색과 내용물 색상을 변경합니다.
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer // 선택됨 (강조)
            } else {
                MaterialTheme.colorScheme.surfaceVariant // 기본
            },
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer // 선택됨
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant // 기본
            }
        )
    ) {
        // 카드 내용물 (주제 이름)
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = topic.name,
                style = MaterialTheme.typography.titleMedium,
                // Text의 색상도 상태에 따라 직접 변경 (colors에서 contentColor를 사용해도 됨)
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

/**
 * Android Studio 프리뷰에서 MainScreen을 확인하기 위한 Composable
 */
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // 프리뷰에서는 실제 탐색 로직이 필요 없으므로 빈 람다({})를 전달합니다.
    MainScreen({}, {}, {})
}