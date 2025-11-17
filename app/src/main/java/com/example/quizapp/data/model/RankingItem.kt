package com.example.quizapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ranking_table")
data class RankingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val score: Int, // 맞힌 개수
    val totalQuestions: Int, // 총 문제 수
    val timestamp: Long // 저장된 시간 (날짜)
)