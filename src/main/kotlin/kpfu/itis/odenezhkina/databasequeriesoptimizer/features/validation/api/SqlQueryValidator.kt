package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation.api

import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlQueryParserErrorListener
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation.SqlQueryValidatorImpl
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation.SqlSyntaxTreeConverter

interface SqlQueryValidator {
    fun parseToTree(query: String): SqlSyntaxTree?

    companion object {
        fun create(): SqlQueryValidator =
            SqlQueryValidatorImpl(treeConverter = SqlSyntaxTreeConverter.create()) {
                SqlQueryParserErrorListener.create()
            }
    }
}