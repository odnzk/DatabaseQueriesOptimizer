package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.optimization

import com.intellij.openapi.diagnostic.Logger
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema.CalciteSchemaMapper
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema.RoomSchemaParser

interface SqlQueryOptimizer {
    sealed interface OptimizationResult {
        class Success(val optimized: String) : OptimizationResult
        data object Empty : OptimizationResult
        class Error(val e: Throwable) : OptimizationResult
    }

    fun optimize(sql: String): OptimizationResult

    companion object {
        fun create(): SqlQueryOptimizer =
            SqlQueryOptimizerImpl(
                logger = Logger.getInstance(SqlQueryOptimizerImpl::class.java),
                calciteSchemaMapper = CalciteSchemaMapper(),
                roomSchemaParser = RoomSchemaParser.create(),
            )
    }
}