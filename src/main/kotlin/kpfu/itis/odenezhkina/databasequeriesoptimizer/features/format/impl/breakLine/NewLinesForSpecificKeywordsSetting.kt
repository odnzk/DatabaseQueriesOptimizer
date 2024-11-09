package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.breakLine

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.SQLCommonKeywords

data class NewLinesForSpecificKeywordsSetting(
    val keywords: Set<SQLCommonKeywords> = setOf(SQLCommonKeywords.WHERE),
)

object NewLinesForSpecificKeywordsSettingUi{
    const val NEW_LINE_KEYWORDS_CLASSES_LABEL = "Select keywords to start a new line"
}