package com.example.quizapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quizapp.data.model.RankingItem
import com.example.quizapp.data.model.WrongAnswer

/**
 * Room 데이터베이스 설정 클래스.
 *
 * - 랭킹, 오답 엔티티를 포함합니다.
 * - 싱글톤 인스턴스로 앱 전체에서 공유됩니다.
 */
@Database(
    entities = [RankingItem::class, WrongAnswer::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /** 퀴즈 관련 테이블에 접근하는 DAO 입니다. */
    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * DB 싱글톤 인스턴스를 반환합니다.
         *
         * @param context 데이터베이스를 생성할 때 사용할 Application Context
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiz_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
