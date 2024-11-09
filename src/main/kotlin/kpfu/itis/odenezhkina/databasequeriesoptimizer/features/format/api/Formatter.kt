package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api

import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree

interface Formatter {
    fun format(tree: SqlSyntaxTree): SqlSyntaxTree
}

