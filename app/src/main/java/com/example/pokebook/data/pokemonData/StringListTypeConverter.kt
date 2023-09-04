package com.example.pokebook.data.pokemonData

import android.util.JsonReader
import android.util.JsonWriter
import androidx.room.TypeConverter
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter


class StringListTypeConverter {
    @TypeConverter
    fun fromStringList(strings: List<String>?): String? {
        if (strings == null) {
            return null
        }

        val result = StringWriter()
        val json = JsonWriter(result)

        try {
            json.beginArray()

            strings.forEach {
                json.value(it)
            }

            json.endArray()
            json.close()
        } catch (e: IOException) {
            return null
        }

        return result.toString()
    }

    @TypeConverter
    fun toStringList(strings: String?): List<String>? {
        if (strings == null) {
            return null
        }

        val reader = StringReader(strings)
        val json = JsonReader(reader)
        val result = mutableListOf<String>()

        try {
            json.beginArray()

            while (json.hasNext()) {
                result.add(json.nextString())
            }

            json.endArray()
        } catch (e: IOException) {
            return null
        }

        return result
    }
}
