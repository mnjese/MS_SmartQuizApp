package com.example.quizapp.data.local

import androidx.room.TypeConverter

/**
 * Room에서 사용하기 위한 타입 변환기입니다.
 *
 * - List<String> ↔ String 변환을 제공합니다.
 */
class Converters {

    /**
     * 문자열 리스트를 하나의 문자열로 변환합니다.
     *
     * @param list 변환할 문자열 리스트
     * @return 콤마(,)로 연결된 하나의 문자열
     */
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }

    /**
     * 하나의 문자열을 문자열 리스트로 변환합니다.
     *
     * @param data 콤마(,)로 구분된 문자열
     * @return 분리된 문자열 리스트
     */
    @TypeConverter
    fun toList(data: String): List<String> {
        return data.split(",")
    }
}
