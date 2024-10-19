package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api

import com.intellij.openapi.diagnostic.Logger
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.impl.SqlQueryParserErrorListener
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.impl.SqlQueryParserImpl

interface SqlQueryParser {
    fun buildTreee(rawQuery: String): SyntaxTree?

    companion object{
        fun create(): SqlQueryParser = SqlQueryParserImpl(
            Logger.getInstance(SqlQueryParserImpl::class.java),
            errorListenerProvider = { SqlQueryParserErrorListener.create() }
        )
    }
}
