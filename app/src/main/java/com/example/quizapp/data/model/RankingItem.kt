package com.example.quizapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 퀴즈 결과(점수, 날짜, 주제)를 저장하는 Room 엔티티.
 *
 * @param id 자동 생성되는 PK
 * @param score 맞힌 문제 수
 * @param totalQuestions 전체 문제 수
 * @param timestamp 저장 시각(밀리초)
 * @param topicName 문제를 푼 주제 이름
 */
@Entity(tableName = "ranking_table")
data class RankingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long,
    val topicName: String
)
