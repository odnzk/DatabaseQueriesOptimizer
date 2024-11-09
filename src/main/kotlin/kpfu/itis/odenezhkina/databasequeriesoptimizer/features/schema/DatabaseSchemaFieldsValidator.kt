package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema

class DatabaseSchemaFieldsValidator {
    fun validateDirectoryField(directory: String?): String? =
        when {
            directory == null -> "Invalid directory: cannot be null"
            directory.isBlank() -> "Invalid directory: cannot be blank"
            else -> null
        }

    fun validateVersionField(version: String?): String? =
        when {
            version == null -> "Invalid version: cannot be null"
            version.isBlank() -> "Invalid version: cannot be blank"
            version.toIntOrNull() == null -> "Invalid version: must be a number"
            version.toInt() < 1 -> "Invalid version: cannot be less than 1"
            else -> null
        }
}