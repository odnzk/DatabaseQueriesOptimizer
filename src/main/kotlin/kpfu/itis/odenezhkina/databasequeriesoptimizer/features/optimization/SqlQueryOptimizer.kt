package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.optimization

import com.intellij.openapi.diagnostic.Logger
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.optimization.SqlQueryOptimizer.OptimizationResult
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema.CalciteSchemaMapper
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema.RoomSchemaParser
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings.PluginSettings
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings.data
import org.apache.calcite.plan.ConventionTraitDef
import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.rel2sql.RelToSqlConverter
import org.apache.calcite.rel.rules.CoreRules
import org.apache.calcite.sql.SqlDialect
import org.apache.calcite.sql.SqlNode
import org.apache.calcite.sql.SqlWriterConfig
import org.apache.calcite.sql.dialect.CalciteSqlDialect
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.pretty.SqlPrettyWriter
import org.apache.calcite.tools.FrameworkConfig
import org.apache.calcite.tools.Frameworks
import org.apache.calcite.tools.Planner
import org.apache.calcite.tools.RuleSets


class SqlQueryOptimizerImpl(
    private val logger: Logger,
    private val roomSchemaParser: RoomSchemaParser,
    private val calciteSchemaMapper: CalciteSchemaMapper
) : SqlQueryOptimizer {

    override fun optimize(sql: String): OptimizationResult {
        return try {
            val settingsState = PluginSettings.getInstance().state
            val schemeDirectory =
                settingsState.databaseSchemesDirectory.data() ?: return OptimizationResult.Error(
                    IllegalStateException("No database scheme directory: set it in plugin settings")
                )
            val schemeVersion =
                settingsState.databaseVersion.data() ?: return OptimizationResult.Error(
                    IllegalStateException("No database scheme version: set it in plugin settings")
                )
            val fullSchemePath = "${schemeDirectory}/${schemeVersion}.json"
            val roomSchema = roomSchemaParser.parse(fullSchemePath).getOrNull()
                ?: return OptimizationResult.Error(IllegalStateException("Cannot parse room scheme, check settings"))

            val scheme = calciteSchemaMapper
                .mapToCalciteSchema(roomSchema)
                .getOrNull()
                ?: return OptimizationResult.Error(IllegalStateException("Invalid room scheme, cannot map it to calcite"))

            val sqlParserConfig = SqlParser.config().withCaseSensitive(false)
            val config: FrameworkConfig = Frameworks
                .newConfigBuilder()
                .defaultSchema(scheme)
                .parserConfig(sqlParserConfig)
                .traitDefs(listOf(ConventionTraitDef.INSTANCE))
                .ruleSets(
                    RuleSets.ofList(
                        CoreRules.FILTER_INTO_JOIN,
                        CoreRules.JOIN_COMMUTE,
                        CoreRules.PROJECT_MERGE
                    )
                )
                .build()
            val planner: Planner = Frameworks.getPlanner(config)

            val parsedTree: SqlNode = planner.parse(sql)
                ?: return OptimizationResult.Error(IllegalStateException("Cannot parse tree for $sql"))
            val validatedSql: SqlNode = planner.validate(parsedTree)
                ?: return OptimizationResult.Error(IllegalStateException("Cannot validate parsed tree for $sql"))
            val relAlgRepresentation = planner.rel(validatedSql).rel
                ?: return OptimizationResult.Error(IllegalStateException("Cannot build relation algebra representation for $sql"))

            OptimizationResult.Success(convertRelAlgebraRepresentationToSQL(relAlgRepresentation))
        } catch (e: Exception) {
            logger.error(e)
            OptimizationResult.Error(e)
        }
    }


    private fun convertRelAlgebraRepresentationToSQL(
        relNode: RelNode,
        dialect: SqlDialect = CalciteSqlDialect.DEFAULT
    ): String {
        val sqlConverter = RelToSqlConverter(dialect)
        val sqlNode = sqlConverter
            .visitRoot(relNode)
            .asStatement()

        val sqlWriterConfig = SqlWriterConfig
            .of()
            .withDialect(dialect)

        return SqlPrettyWriter(sqlWriterConfig).apply {
            sqlNode.unparse(this, 0, 0)
        }.toString()
    }

}