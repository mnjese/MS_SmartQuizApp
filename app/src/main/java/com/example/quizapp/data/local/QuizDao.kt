package com.example.quizapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizapp.data.model.RankingItem
import com.example.quizapp.data.model.WrongAnswer
import kotlinx.coroutines.flow.Flow

/**
 * 랭킹과 오답 노트 테이블에 접근하기 위한 DAO 입니다.
 */
@Dao
interface QuizDao {

    // -------------------- 랭킹 관련 --------------------

    /**
     * 랭킹 데이터를 저장합니다.
     *
     * @param rankingItem 저장할 랭킹 엔티티
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRanking(rankingItem: RankingItem)

    /**
     * 점수 내림차순, 날짜 내림차순으로 전체 랭킹을 조회합니다.
     *
     * @return 랭킹 리스트를 방출하는 [Flow]
     */
    @Query("SELECT * FROM ranking_table ORDER BY score DESC, timestamp DESC")
    fun getAllRankings(): Flow<List<RankingItem>>

    // -------------------- 오답 노트 관련 --------------------

    /**
     * 틀린 문제를 오답 테이블에 저장합니다.
     *
     * @param wrongAnswer 저장할 오답 엔티티
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWrongAnswer(wrongAnswer: WrongAnswer)

    /**
     * 최신 순으로 모든 오답을 조회합니다.
     *
     * @return 오답 리스트를 방출하는 [Flow]
     */
    @Query("SELECT * FROM wrong_answer_table ORDER BY id DESC")
    fun getAllWrongAnswers(): Flow<List<WrongAnswer>>

    /**
     * 특정 오답 한 건을 삭제합니다.
     *
     * @param wrongAnswer 삭제할 오답 엔티티
     */
    @Delete
    suspend fun deleteWrongAnswer(wrongAnswer: WrongAnswer)
}
