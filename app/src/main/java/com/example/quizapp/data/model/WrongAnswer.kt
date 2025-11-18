package com.example.quizapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 사용자가 틀린 문제를 저장하기 위한 Room 엔티티.
 *
 * @param id 자동 생성되는 PK
 * @param questionText 문제 내용
 * @param options 보기 리스트
 * @param correctAnswerIndex 정답 인덱스
 * @param selectedAnswerIndex 사용자가 선택한 보기 인덱스
 */
@Entity(tableName = "wrong_answer_table")
data class WrongAnswer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val selectedAnswerIndex: Int
)
