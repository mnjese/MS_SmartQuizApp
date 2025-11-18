package com.example.quizapp.data.model

/**
 * 퀴즈 주제 정보를 나타내는 데이터 클래스.
 *
 * @param id 주제 식별 ID
 * @param name 화면에 표시할 주제 이름
 */
data class QuizTopic(
    val id: Int,
    val name: String,
    // val icon: Int // 필요 시 아이콘 리소스 추가 가능
)

/**
 * 퀴즈 문제 정보를 나타내는 데이터 클래스.
 *
 * @param id 문제 ID
 * @param questionText 문제 내용
 * @param options 보기 리스트 (4개)
 * @param correctAnswerIndex 정답 인덱스 (0~3)
 * @param topicId 해당 문제의 주제 ID
 */
data class Question(
    val id: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val topicId: Int
)
