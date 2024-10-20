package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.performanceAnalyser

import SQLiteParser
import SQLiteParserBaseVisitor
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.SqlSyntaxTree

class SelectAllUsageAnalyser : ConditionAnalyser {

    override fun analyse(tree: SqlSyntaxTree): Boolean {
        val visitor = object : SQLiteParserBaseVisitor<Void?>() {
            override fun visitSelect_core(ctx: SQLiteParser.Select_coreContext): Void? {
                if (ctx.result_column().any { it.text == "*" }) {
                    println("Warning: Query contains SELECT *")
                }
                return null
            }
        }

        //visitor.visit(tree)

        // TODO()
        return true
    }
}
