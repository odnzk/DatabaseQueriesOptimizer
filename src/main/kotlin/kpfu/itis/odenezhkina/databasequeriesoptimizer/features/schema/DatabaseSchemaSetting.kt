package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema

data class DatabaseSchemaSetting(
    val directory: String = "/Users/o.denezhkina/AndroidStudioProjects/SqlQueriesTestApplication/app/schemas/kfu.odenezhkina.sqlqueriestestapplication.ui.theme.AppDatabase", // TODO()
    val version: Int = 1, // TODO()
) {
    fun getPathToDatabaseSchema(): String {
        return "${directory}/${version}.json"
    }
}

object DatabaseSchemaSettingUi {
    const val DATABASE_SCHEME_PATH_FIELD_LABEL = "Enter database schemes directory"
    const val DATABASE_VERSION_FIELD_LABEL = "Enter database version using only numbers"
}