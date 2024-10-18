package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.queryParser.api

interface SqlQueryParser {
    fun parse(rawQuery: String): SyntaxTree?
}
