package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation


import SQLiteLexer
import SQLiteParser
import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlQueryParserErrorListener
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation.api.SqlQueryValidator
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class SqlQueryValidatorImpl(
    private val treeConverter: SqlSyntaxTreeConverter,
    private val errorListenerProvider: () -> SqlQueryParserErrorListener,
) : SqlQueryValidator {

    override fun parseToTree(query: String): SqlSyntaxTree? {
        return try {
            val input = CharStreams.fromString(query)
            val lexer = SQLiteLexer(input)

            val errorListener = errorListenerProvider()
            lexer.addErrorListener(errorListener)
            val tokens = CommonTokenStream(lexer)
            tokens.fill()
            if (errorListener.hasError) return null

            val parser = SQLiteParser(tokens).apply {
                addErrorListener(errorListener)
            }
            val tree = parser.sql_stmt()

            if (errorListener.hasError || tree == null) {
                null
            } else {
                treeConverter.convert(tree, parser)
            }
        } catch (e: Exception) {
            null
        }
    }
}