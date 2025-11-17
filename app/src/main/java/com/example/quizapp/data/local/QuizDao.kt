package com.example.quizapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizapp.data.model.RankingItem
import com.example.quizapp.data.model.WrongAnswer
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    // --- 랭킹(Ranking) 관련 ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRanking(rankingItem: RankingItem)

    @Query("SELECT * FROM ranking_table ORDER BY score DESC, timestamp DESC")
    fun getAllRankings(): Flow<List<RankingItem>>

    // --- 오답 노트(WrongAnswer) 관련 ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWrongAnswer(wrongAnswer: WrongAnswer)

    @Query("SELECT * FROM wrong_answer_table ORDER BY id DESC")
    fun getAllWrongAnswers(): Flow<List<WrongAnswer>>
}