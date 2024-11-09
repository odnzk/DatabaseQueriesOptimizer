package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.intent

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.FormatterSqlClasses

class IndentationForClassesSetting(
    val indentedClasses: Set<FormatterSqlClasses> = setOf(FormatterSqlClasses.WHERE, FormatterSqlClasses.AND, FormatterSqlClasses.OR, FormatterSqlClasses.JOIN),
    val indentation: String = "  ", // Two spaces for indentation
)

object IndentationForClassesSettingUi{
    const val INDENTED_CLASSES_LABEL = "Select indented keywords"
    const val INDENTATION_LABEL ="Enter indentation"
}