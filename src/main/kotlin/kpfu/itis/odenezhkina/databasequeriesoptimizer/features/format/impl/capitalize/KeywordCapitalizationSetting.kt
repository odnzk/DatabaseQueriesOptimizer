package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.capitalize

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.FormatterSqlClasses

class KeywordCapitalizationSetting(
    val keywords: Set<FormatterSqlClasses> = setOf(
        FormatterSqlClasses.SELECT,
        FormatterSqlClasses.FROM,
        FormatterSqlClasses.INSERT,
        FormatterSqlClasses.UPDATE,
        FormatterSqlClasses.DELETE,
        FormatterSqlClasses.JOIN,
        FormatterSqlClasses.ON,
        FormatterSqlClasses.GROUP,
        FormatterSqlClasses.BY,
        FormatterSqlClasses.ORDER,
        FormatterSqlClasses.LIMIT
    )
)

object KeywordCapitalizationSettingUi {
    const val KEYWORDS_CLASSES_LABEL = "Select keywords to capitalize"
}