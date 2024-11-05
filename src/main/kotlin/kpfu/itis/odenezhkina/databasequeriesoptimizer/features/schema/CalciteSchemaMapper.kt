package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema

import org.apache.calcite.schema.SchemaPlus
import org.apache.calcite.tools.Frameworks

class CalciteSchemaMapper {

    fun mapToCalciteSchema(roomSchema: RoomSchema): Result<SchemaPlus?> {
        return runCatching {
            val calciteSchema = Frameworks.createRootSchema(true)

            for (entity in roomSchema.roomDatabase.entities) {
                calciteSchema.add(entity.tableName, CalciteTableSchema(entity))
            }

            calciteSchema
        }
    }

}


