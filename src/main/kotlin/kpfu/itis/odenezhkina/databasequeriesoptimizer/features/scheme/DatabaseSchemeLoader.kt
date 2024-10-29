package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.scheme

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.apache.calcite.rel.type.RelDataType
import org.apache.calcite.rel.type.RelDataTypeFactory
import org.apache.calcite.schema.SchemaPlus
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.sql.type.SqlTypeName
import org.apache.calcite.tools.Frameworks
import java.io.File

class DatabaseSchemeLoader {

    private data class RoomSchema(
        @SerializedName("database") val database: Database
    )

    private data class Database(
        @SerializedName("entities") val entities: List<Entity>,
        @SerializedName("version") val version: Int
    )

    private data class Entity(
        @SerializedName("tableName") val tableName: String,
        @SerializedName("fields") val fields: List<Field>
    )

    private data class Field(
        @SerializedName("fieldPath") val fieldPath: String,
        @SerializedName("affinity") val affinity: String
    )

    private class RoomTable(val entity: Entity) : AbstractTable() {
        override fun getRowType(typeFactory: RelDataTypeFactory): RelDataType {
            val builder = typeFactory.builder()

            // Add columns based on Room schema fields
            for (field in entity.fields) {
                val sqlType = when (field.affinity) {
                    "INTEGER" -> SqlTypeName.INTEGER
                    "TEXT" -> SqlTypeName.VARCHAR
                    "REAL" -> SqlTypeName.FLOAT
                    else -> SqlTypeName.ANY // Fallback for other types
                }
                builder.add(field.fieldPath, sqlType)
            }

            return builder.build()
        }
    }

    fun loadRoomSchemeAndConvertItToCalcite(path: String): SchemaPlus? {
        val file = File(path)
        val jsonString = file.readText()
        val gson = Gson()
        val roomSchema = gson.fromJson(jsonString, RoomSchema::class.java)
        val rootSchema = Frameworks.createRootSchema(true)

        for (entity in roomSchema.database.entities) {
            rootSchema.add(entity.tableName, RoomTable(entity))
        }

        return rootSchema
    }

}