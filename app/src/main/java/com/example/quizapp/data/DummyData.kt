package com.example.quizapp.data

import com.example.quizapp.data.model.Question
import com.example.quizapp.data.model.QuizTopic

/**
 * 앱에서 사용할 샘플 퀴즈 데이터 모음.
 *
 * - 실제 DB나 API를 사용하기 전 테스트용으로 활용됩니다.
 * - 주제 목록과 문제 목록을 제공합니다.
 */
object DummyData {

    /** 제공되는 퀴즈 주제 목록 */
    val topics = listOf(
        QuizTopic(1, "일반 상식"),
        QuizTopic(2, "과학"),
        QuizTopic(3, "영화")
    )

    /** 전체 퀴즈 문제 목록 */
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
        Question(
            id = 103,
            questionText = "프랑스의 수도는?",
            options = listOf("파리", "런던", "베를린", "로마"),
            correctAnswerIndex = 0,
            topicId = 1
        ),
        Question(
            id = 104,
            questionText = "셰익스피어의 4대 비극이 아닌 것은?",
            options = listOf("햄릿", "리어왕", "오셀로", "로미오와 줄리엣"),
            correctAnswerIndex = 3,
            topicId = 1
        ),
        Question(
            id = 105,
            questionText = "1년은 약 몇 주로 이루어져 있나요?",
            options = listOf("50주", "52주", "54주", "48주"),
            correctAnswerIndex = 1,
            topicId = 1
        ),
        Question(
            id = 106,
            questionText = "미국의 상징 동물이 아닌 것은?",
            options = listOf("흰머리수리", "들소", "곰", "캥거루"),
            correctAnswerIndex = 3,
            topicId = 1
        ),
        Question(
            id = 107,
            questionText = "그림 '모나리자'를 그린 화가는?",
            options = listOf("피카소", "반 고흐", "레오나르도 다 빈치", "미켈란젤로"),
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
            questionText = "태양계의 중심 천체는?",
            options = listOf("지구", "태양", "화성", "목성"),
            correctAnswerIndex = 1,
            topicId = 2
        ),
        Question(
            id = 203,
            questionText = "지구에서 가장 가까운 행성은?",
            options = listOf("화성", "금성", "수성", "목성"),
            correctAnswerIndex = 1,
            topicId = 2
        ),
        Question(
            id = 204,
            questionText = "인체에서 가장 큰 장기는?",
            options = listOf("간", "뇌", "피부", "심장"),
            correctAnswerIndex = 2,
            topicId = 2
        ),
        Question(
            id = 205,
            questionText = "빛의 속도가 가장 빠른 환경은?",
            options = listOf("물 속", "유리 속", "공기 중", "진공"),
            correctAnswerIndex = 3,
            topicId = 2
        ),
        Question(
            id = 206,
            questionText = "원자핵을 구성하는 입자가 아닌 것은?",
            options = listOf("양성자", "중성자", "전자", "쿼크"),
            correctAnswerIndex = 2, // 전자는 핵 주위를 돕니다. (쿼크는 양성자/중성자를 구성)
            topicId = 2
        ),
        Question(
            id = 207,
            questionText = "식물이 광합성을 할 때 주로 흡수하는 기체는?",
            options = listOf("산소", "질소", "이산화탄소", "수소"),
            correctAnswerIndex = 2,
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
        ),
        Question(
            id = 303,
            questionText = "영화 '어벤져스: 엔드게임'에서 타노스가 모으려 한 것은?",
            options = listOf("드래곤볼", "인피니티 스톤", "크리스탈", "마법의 램프"),
            correctAnswerIndex = 1,
            topicId = 3
        ),
        Question(
            id = 304,
            questionText = "'해리 포터' 시리즈에서 주인공 해리의 기숙사는?",
            options = listOf("슬리데린", "래번클로", "후플푸프", "그리핀도르"),
            correctAnswerIndex = 3,
            topicId = 3
        ),
        Question(
            id = 305,
            questionText = "영화 '매트릭스'에서 네오가 선택한 약의 색깔은?",
            options = listOf("파란 약", "빨간 약", "초록 약", "노란 약"),
            correctAnswerIndex = 1,
            topicId = 3
        ),
        Question(
            id = 306,
            questionText = "애니메이션 '겨울왕국'에서 엘사가 부른 메인 주제곡은?",
            options = listOf("Let It Go", "Into the Unknown", "Show Yourself", "Some Things Never Change"),
            correctAnswerIndex = 0,
            topicId = 3
        ),
        Question(
            id = 307,
            questionText = "\"I'll be back\"이라는 명대사로 유명한 영화 시리즈는?",
            options = listOf("터미네이터", "람보", "다이 하드", "미션 임파서블"),
            correctAnswerIndex = 0,
            topicId = 3
        )

    )

    /**
     * 특정 주제에 속한 문제 목록을 반환합니다.
     *
     * @param topicId 주제 ID (1=일반, 2=과학, 3=영화)
     * @return 해당 topicId를 가진 [Question] 리스트
     */
    fun getQuestionsForTopic(topicId: Int): List<Question> {
        return questions.filter { it.topicId == topicId }
    }
}