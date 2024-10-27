package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.optimization

import com.intellij.openapi.diagnostic.Logger
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.optimization.SqlQueryOptimizer.OptimizationResult
import org.apache.calcite.plan.ConventionTraitDef
import org.apache.calcite.plan.RelOptPlanner
import org.apache.calcite.plan.volcano.VolcanoPlanner
import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.rel2sql.RelToSqlConverter
import org.apache.calcite.rel.rules.CoreRules
import org.apache.calcite.sql.SqlNode
import org.apache.calcite.sql.SqlWriter
import org.apache.calcite.sql.SqlWriterConfig
import org.apache.calcite.sql.dialect.CalciteSqlDialect
import org.apache.calcite.sql.pretty.SqlPrettyWriter
import org.apache.calcite.tools.FrameworkConfig
import org.apache.calcite.tools.Frameworks
import org.apache.calcite.tools.Planner

interface SqlQueryOptimizer {
    sealed interface OptimizationResult {
        class Success(val optimized: String) : OptimizationResult
        data object Empty : OptimizationResult
        class Error(val e: Throwable) : OptimizationResult
    }

    fun optimize(sql: String): OptimizationResult

    companion object {
        fun create(): SqlQueryOptimizer =
            SqlQueryOptimizerImpl(Logger.getInstance(SqlQueryOptimizerImpl::class.java))
    }
}

class SqlQueryOptimizerImpl(private val logger: Logger) : SqlQueryOptimizer {

    override fun optimize(sql: String): OptimizationResult {
        val config: FrameworkConfig = Frameworks.newConfigBuilder().build()
        val planner: Planner = Frameworks.getPlanner(config)

        return try {
            val parsedTree: SqlNode = planner.parse(sql)
                ?: return OptimizationResult.Error(IllegalStateException("Cannot parse tree fro $sql"))
            val validatedSql: SqlNode = planner.validate(parsedTree)
                ?: return OptimizationResult.Error(IllegalStateException("Cannot validate parsed tree for $sql"))
            val relAlgRepresentation = planner.rel(validatedSql).rel
                ?: return OptimizationResult.Error(IllegalStateException("Cannot build relation algebra representation for $sql"))
            val optimizedRelAg = optimizeRelAlgRepresentation(relAlgRepresentation)
                ?: return OptimizationResult.Empty

            OptimizationResult.Success(convertRelAlgebraRepresentationToSQL(optimizedRelAg))
        } catch (e: Exception) {
            logger.error(e)
            OptimizationResult.Error(e)
        }
    }

    private fun optimizeRelAlgRepresentation(relNode: RelNode): RelNode? {
        val planner = createRelOptPlanner().apply { root = relNode }
        return planner.findBestExp()
    }

    private fun convertRelAlgebraRepresentationToSQL(relNode: RelNode): String {
        val sqlConverter = RelToSqlConverter(CalciteSqlDialect.DEFAULT)
        val sqlNode = sqlConverter.visitRoot(relNode).asStatement()

        val sqlWriter: SqlWriter = SqlPrettyWriter(SqlWriterConfig.of())
        sqlNode.unparse(sqlWriter, 0, 0)

        return sqlWriter.toString()
    }

    private fun createRelOptPlanner(): RelOptPlanner {
        return VolcanoPlanner().apply {
            addRelTraitDef(ConventionTraitDef.INSTANCE)
            addRule(CoreRules.FILTER_INTO_JOIN)
            addRule(CoreRules.JOIN_COMMUTE)
            addRule(CoreRules.PROJECT_MERGE)
        }
    }

}