package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.performanceAnalyser

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.Query

interface PerformanceAnalyser {
    fun analyse(query: Query)
}
