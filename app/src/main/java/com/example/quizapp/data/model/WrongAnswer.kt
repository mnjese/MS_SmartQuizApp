package com.example.quizapp.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 사용자가 틀린 문제를 저장하기 위한 Room 엔티티.
 * 같은 문제는 한 번만 저장되며, 다시 틀리면 기존 기록을 덮어씁니다.
 *
 * @param id 자동 생성되는 PK
 * @param questionId 문제 고유 ID (Question.id와 매핑, 중복 방지용)
 * @param questionText 문제 내용
 * @param options 보기 리스트
 * @param correctAnswerIndex 정답 인덱스
 * @param selectedAnswerIndex 사용자가 선택한 보기 인덱스
 */
@Entity(
    tableName = "wrong_answer_table",
    indices = [
        Index(
            value = ["questionId"],
            unique = true // 같은 questionId는 하나만 존재하도록 강제
        )
    ]
)
data class WrongAnswer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val questionId: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val selectedAnswerIndex: Int
)
