package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema

import com.google.gson.Gson
import java.io.File

class RoomSchemaParser(private val gson: Gson) {

    fun parse(path: String): Result<RoomSchema> {
        return runCatching {
            val jsonString = File(path).readText()
            gson.fromJson(jsonString, RoomSchema::class.java)
        }
    }

    companion object {
        fun create(): RoomSchemaParser = RoomSchemaParser(gson = Gson())
    }
}

