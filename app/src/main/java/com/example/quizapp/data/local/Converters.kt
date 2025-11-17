package com.example.quizapp.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(list: List<String>): String {
        // ["A", "B", "C"] -> "A,B,C"
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): List<String> {
        // "A,B,C" -> ["A", "B", "C"]
        return data.split(",")
    }
}