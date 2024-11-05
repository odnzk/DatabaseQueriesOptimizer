package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema

import org.apache.calcite.rel.RelCollation
import org.apache.calcite.rel.RelCollations
import org.apache.calcite.rel.RelFieldCollation
import org.apache.calcite.rel.type.RelDataType
import org.apache.calcite.rel.type.RelDataTypeFactory
import org.apache.calcite.schema.Statistic
import org.apache.calcite.schema.Statistics
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.sql.type.SqlTypeName
import org.apache.calcite.util.ImmutableBitSet


class CalciteTableSchema(private val roomEntity: RoomSchema.RoomDatabase.RoomEntity) : AbstractTable() {
    override fun getStatistic(): Statistic {
        val collations: List<RelCollation> = roomEntity.indices.map { index ->
            RelCollations.of(index.columnNames.map { colName ->
                val columnIndex = roomEntity.roomFields.indexOfFirst { it.columnName == colName }
                RelFieldCollation(columnIndex)
            })
        }

        // Define keys using primary key columns
        val primaryKeyBitSet = ImmutableBitSet.of(
            roomEntity.roomPrimaryKey.columnNames.map { colName ->
                roomEntity.roomFields.indexOfFirst { it.columnName == colName }
            }
        )

        // Create statistics with row count, primary key, and collations
        return Statistics.of(
            0.0,                           // Estimated row count
            listOf(primaryKeyBitSet),       // Primary key as an ImmutableBitSet
            collations                      // Collations for indexes
        )
    }


    override fun getRowType(typeFactory: RelDataTypeFactory): RelDataType {
        val builder = typeFactory.builder()

        for (field in roomEntity.roomFields) {
            builder.add(field.columnName, RoomTypeToCalciteDictionary.findCalciteType(field.affinity))
        }

        return builder.build()
    }

    private enum class RoomTypeToCalciteDictionary(
        val roomType: String,
        val calciteType: SqlTypeName
    ) {
        NUMBER("INTEGER", SqlTypeName.INTEGER),
        TEXT("TEXT", SqlTypeName.VARCHAR),
        FLOATING("REAL", SqlTypeName.REAL);

        companion object{
            fun findCalciteType(roomType: String): SqlTypeName =
                entries.find { it.roomType == roomType }?.calciteType ?: SqlTypeName.ANY
        }
    }
}