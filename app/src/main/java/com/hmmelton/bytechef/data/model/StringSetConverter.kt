package com.hmmelton.bytechef.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class StringSetConverter {

    private val gson = Gson()
    private val setType: Type = object : TypeToken<Set<String>>() {}.type

    @TypeConverter
    fun fromString(value: String): Set<String> {
        return gson.fromJson(value, setType)
    }

    @TypeConverter
    fun fromSet(set: Set<String>): String {
        return gson.toJson(set, setType)
    }
}
