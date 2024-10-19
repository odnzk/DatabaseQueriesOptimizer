package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation.api

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.impl.SqlQueryParserErrorListener
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation.SqlQueryValidatorImpl

interface SqlQueryValidator {
    fun isSql(query: String): Boolean

    companion object {
        fun create(): SqlQueryValidator = SqlQueryValidatorImpl { SqlQueryParserErrorListener.create() }
    }
}