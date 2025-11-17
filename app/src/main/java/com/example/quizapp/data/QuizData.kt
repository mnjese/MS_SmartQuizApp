package com.example.quizapp.data

// 퀴즈 주제 (예: 과학, 영화)
data class QuizTopic(
    val id: Int,
    val name: String,
    val description: String
    // val icon: Int // (선택적) 아이콘 리소스 ID
)

// 퀴즈 문제
data class Question(
    val id: Int,
    val questionText: String,
    val options: List<String>, // 항상 4개의 선택지
    val correctAnswerIndex: Int, // 정답 (0, 1, 2, 3)
    val topicId: Int // 어떤 주제에 속하는지
)