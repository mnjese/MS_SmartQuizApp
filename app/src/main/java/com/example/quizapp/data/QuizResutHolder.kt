package com.example.quizapp.data

import com.example.quizapp.data.model.Question

/**
 * 퀴즈 진행 중 필요한 데이터를 임시로 저장하는 객체.
 *
 * - 화면 이동 간 많은 데이터를 넘기지 않기 위해 사용됩니다.
 * - 앱을 종료하면 초기화되며 영구 저장되지 않습니다.
 */
object QuizResultHolder {

    /** 현재 플레이한 문제 리스트 */
    var questions: List<Question> = emptyList()

    /**
     * 문제별 사용자가 선택한 답안을 저장하는 맵.
     *
     * key = Question ID
     * value = 선택한 보기 인덱스 (0~3)
     */
    var userAnswers: Map<Int, Int> = emptyMap()
}
