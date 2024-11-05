package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema

import com.google.gson.annotations.SerializedName

data class RoomSchema(
    @SerializedName("database") val roomDatabase: RoomDatabase
) {
    data class RoomDatabase(
        @SerializedName("entities") val entities: List<RoomEntity>,
        @SerializedName("version") val version: Int
    ) {
        data class RoomEntity(
            @SerializedName("tableName") val tableName: String,
            @SerializedName("fields") val roomFields: List<RoomField>,
            @SerializedName("primaryKey") val roomPrimaryKey: RoomPrimaryKey,
            @SerializedName("indices") val indices: List<RoomIndex>,
        ) {
            data class RoomField(
                @SerializedName("columnName") val columnName: String,
                @SerializedName("affinity") val affinity: String
            )

            data class RoomPrimaryKey(
                @SerializedName("autoGenerate") val autoGenerate: Boolean,
                @SerializedName("columnNames") val columnNames: List<String>,
            )

            data class RoomIndex(
                @SerializedName("name") val name: String,
                @SerializedName("unique") val unique: Boolean,
                @SerializedName("columnNames") val columnNames: List<String>,
            )
        }
    }
}