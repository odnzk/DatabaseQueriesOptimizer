package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.intent

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.SQLCommonKeywords

data class IndentationForClassesSetting(
    val indentedClasses: Set<SQLCommonKeywords> = setOf(SQLCommonKeywords.WHERE, SQLCommonKeywords.AND, SQLCommonKeywords.OR, SQLCommonKeywords.JOIN),
    val indentation: String = "  ", // Two spaces for indentation
)

object IndentationForClassesSettingUi{
    const val INDENTED_CLASSES_LABEL = "Select indented keywords"
    const val INDENTATION_LABEL ="Enter indentation"
}