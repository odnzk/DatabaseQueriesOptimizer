package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation


import SQLiteLexer
import SQLiteParser
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.impl.SqlQueryParserErrorListener
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation.api.SqlQueryValidator
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class SqlQueryValidatorImpl(private val errorListenerProvider: () -> SqlQueryParserErrorListener) :
    SqlQueryValidator {

    override fun isSql(query: String): Boolean {
        return try {
            val input = CharStreams.fromString(query)
            val lexer = SQLiteLexer(input)

            val errorListener = errorListenerProvider()
            lexer.addErrorListener(errorListener)
            val tokens = CommonTokenStream(lexer)
            tokens.fill()
            if (errorListener.hasError) return false

            val parser = SQLiteParser(tokens).apply {
                addErrorListener(errorListener)
            }
            val tree = parser.sql_stmt()

            !(errorListener.hasError || tree == null)
        } catch (e: Exception) {
            false
        }
    }
}