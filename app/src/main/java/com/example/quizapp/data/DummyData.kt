package com.example.quizapp.data

import com.example.quizapp.data.Question
import com.example.quizapp.data.QuizTopic

object DummyData {

    val topics = listOf(
        QuizTopic(1, "일반 상식", "다양한 분야의 상식 퀴즈"),
        QuizTopic(2, "과학", "우주와 생명에 대한 신비"),
        QuizTopic(3, "영화", "명대사와 영화 속 이야기")
    )

    val questions = listOf(
        // --- 일반 상식 (topicId = 1) ---
        Question(
            id = 101,
            questionText = "대한민국의 수도는?",
            options = listOf("부산", "서울", "인천", "대구"),
            correctAnswerIndex = 1,
            topicId = 1
        ),
        Question(
            id = 102,
            questionText = "세계에서 가장 높은 산은?",
            options = listOf("K2", "한라산", "에베레스트", "백두산"),
            correctAnswerIndex = 2,
            topicId = 1
        ),

        // --- 과학 (topicId = 2) ---
        Question(
            id = 201,
            questionText = "물 분자를 이루는 원소 기호는?",
            options = listOf("CO2", "O2", "H2O2", "H2O"),
            correctAnswerIndex = 3,
            topicId = 2
        ),
        Question(
            id = 202,
            questionText = "태양계의 중심 행성은?",
            options = listOf("지구", "태양", "화성", "목성"),
            correctAnswerIndex = 1,
            topicId = 2
        ),

        // --- 영화 (topicId = 3) ---
        Question(
            id = 301,
            questionText = "영화 '기생충'의 감독은?",
            options = listOf("박찬욱", "봉준호", "김지운", "최동훈"),
            correctAnswerIndex = 1,
            topicId = 3
        ),
        Question(
            id = 302,
            questionText = "영화 '인터스텔라'에서 주인공 쿠퍼가 탐사하는 행성이 아닌 것은?",
            options = listOf("밀러 행성", "만 행성", "에드먼즈 행성", "판도라 행성"),
            correctAnswerIndex = 3, // '판도라'는 아바타의 행성
            topicId = 3
        )
        // ... (문제를 더 추가합니다) ...
    )

    // 특정 주제의 문제만 가져오는 함수 (나중에 ViewModel에서 사용)
    fun getQuestionsForTopic(topicId: Int): List<Question> {
        return questions.filter { it.topicId == topicId }
    }
}