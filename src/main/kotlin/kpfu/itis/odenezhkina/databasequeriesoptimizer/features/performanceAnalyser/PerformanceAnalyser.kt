package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.performanceAnalyser

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.queryParser.api.Query

interface PerformanceAnalyser {
    fun analyse(query: Query)
}
