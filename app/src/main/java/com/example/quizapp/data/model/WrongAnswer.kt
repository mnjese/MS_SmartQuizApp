package com.example.quizapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wrong_answer_table")
data class WrongAnswer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val selectedAnswerIndex: Int // 사용자가 선택했던 오답
)