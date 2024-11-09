package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api

enum class SQLCommonKeywords {
    SELECT, AND, OR, FROM, WHERE, INSERT, UPDATE, DELETE, JOIN, ON, GROUP, BY, ORDER, LIMIT;
}

fun Set<SQLCommonKeywords>.containsClass(sqlClass: String): Boolean =
    map { it.name }.contains(sqlClass.uppercase())