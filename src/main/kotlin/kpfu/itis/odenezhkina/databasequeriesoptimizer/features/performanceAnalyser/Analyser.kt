package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.performanceAnalyser

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.SyntaxTree

interface ConditionAnalyser {
    fun analyse(tree: SyntaxTree): Boolean
}
