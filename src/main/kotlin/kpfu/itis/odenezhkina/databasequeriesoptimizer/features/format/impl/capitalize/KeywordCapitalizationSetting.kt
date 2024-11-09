package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.capitalize

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.SQLCommonKeywords

data class KeywordCapitalizationSetting(
    val keywords: Set<SQLCommonKeywords> = setOf(
        SQLCommonKeywords.SELECT,
        SQLCommonKeywords.FROM,
        SQLCommonKeywords.INSERT,
        SQLCommonKeywords.UPDATE,
        SQLCommonKeywords.DELETE,
        SQLCommonKeywords.JOIN,
        SQLCommonKeywords.ON,
        SQLCommonKeywords.GROUP,
        SQLCommonKeywords.BY,
        SQLCommonKeywords.ORDER,
        SQLCommonKeywords.LIMIT
    )
)

object KeywordCapitalizationSettingUi {
    const val CAPITALIZED_KEYWORDS_CLASSES_LABEL = "Select keywords to capitalize"
}