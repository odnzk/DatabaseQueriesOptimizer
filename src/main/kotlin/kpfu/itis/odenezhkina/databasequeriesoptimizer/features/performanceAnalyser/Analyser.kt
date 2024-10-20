package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.performanceAnalyser

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.SqlSyntaxTree

interface ConditionAnalyser {
    fun analyse(tree: SqlSyntaxTree): Boolean
}
