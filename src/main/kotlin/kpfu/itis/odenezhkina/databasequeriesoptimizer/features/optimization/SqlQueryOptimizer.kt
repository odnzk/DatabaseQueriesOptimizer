package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.optimization

import com.intellij.openapi.diagnostic.Logger
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.optimization.SqlQueryOptimizer.OptimizationResult
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema.CalciteSchemaMapper
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema.RoomSchemaParser
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings.PluginSettings
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings.data
import org.apache.calcite.adapter.enumerable.EnumerableConvention
import org.apache.calcite.adapter.enumerable.EnumerableRules
import org.apache.calcite.config.Lex
import org.apache.calcite.plan.ConventionTraitDef
import org.apache.calcite.plan.volcano.AbstractConverter
import org.apache.calcite.rel.RelCollationTraitDef
import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.rel2sql.RelToSqlConverter
import org.apache.calcite.rel.rules.CoreRules
import org.apache.calcite.schema.SchemaPlus
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

    private val cachedSchemas: MutableMap<String, SchemaPlus> = mutableMapOf()

    override fun optimize(sql: String): OptimizationResult {
        return try {
            val planner = createPlanner()

            val parsedTree: SqlNode = planner.parse(sql)
                ?: error("Cannot parse tree for $sql")
            val validatedSql: SqlNode = planner.validate(parsedTree)
                ?: error("Cannot validate parsed tree for $sql")
            val relAlgRepresentation = planner.rel(validatedSql).rel
                ?: error("Cannot build relation algebra representation for $sql")

            val relOptPlanner = relAlgRepresentation.cluster.planner.apply {
                val traitSet = relAlgRepresentation.traitSet.plus(EnumerableConvention.INSTANCE)
                val convertedRelAlgRepresentation = this.changeTraits(relAlgRepresentation, traitSet)
                root = convertedRelAlgRepresentation
            }
            val optimizedRelNode = relOptPlanner.findBestExp()

            OptimizationResult.Success(convertRelAlgebraRepresentationToSQL(optimizedRelNode))
        } catch (e: Exception) {
            logger.error(e)
            OptimizationResult.Error(e)
        }
    }

    private fun createPlanner(): Planner {
        val schema = loadSchema()
        val sqlParserConfig = SqlParser
            .config()
            .withLex(Lex.MYSQL)
            .withCaseSensitive(false)
        val config: FrameworkConfig = Frameworks.newConfigBuilder()
            .defaultSchema(schema)
            .parserConfig(sqlParserConfig)
            .traitDefs(ConventionTraitDef.INSTANCE, RelCollationTraitDef.INSTANCE)
            .ruleSets(
                RuleSets.ofList(
                    CoreRules.FILTER_INTO_JOIN,
                    CoreRules.JOIN_COMMUTE,
                    CoreRules.PROJECT_MERGE,
                    EnumerableRules.ENUMERABLE_PROJECT_RULE,
                    EnumerableRules.ENUMERABLE_FILTER_RULE,
                    EnumerableRules.ENUMERABLE_SORT_RULE,
                    EnumerableRules.ENUMERABLE_LIMIT_RULE,
                    EnumerableRules.ENUMERABLE_JOIN_RULE,
                    EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE,
                    AbstractConverter.ExpandConversionRule.INSTANCE,
                )
            )
            .costFactory(null)
            .build()
        return Frameworks.getPlanner(config) ?: error("Cannot create planner")
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

    private fun loadSchema(): SchemaPlus {
        val settingsState = PluginSettings.getInstance().state
        val schemeDirectory = settingsState.databaseSchemesDirectory.data()
            ?: error("No database scheme directory: set it in plugin settings")
        val schemeVersion = settingsState.databaseVersion.data()
            ?: error("No database scheme version: set it in plugin settings")
        val fullSchemePath = "${schemeDirectory}/${schemeVersion}.json"

        val schema = cachedSchemas.getOrElse(fullSchemePath){
            val roomSchema = roomSchemaParser
                .parse(fullSchemePath)
                .getOrNull()
                ?: error("Cannot parse room scheme, check settings")

            val schemaPlus = calciteSchemaMapper
                .mapToCalciteSchema(roomSchema)
                .getOrNull()
                ?: error("Invalid room scheme, cannot map it to calcite")
            cachedSchemas[fullSchemePath] = schemaPlus
            schemaPlus
        }
        return schema
    }

}