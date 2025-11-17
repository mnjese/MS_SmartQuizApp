package com.example.quizapp.data

import com.example.quizapp.data.model.Question

object QuizResultHolder {
    var questions: List<Question> = emptyList()
    var userAnswers: Map<Int, Int> = emptyMap() // <QuestionID, SelectedIndex>
}