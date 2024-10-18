package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.performanceAnalyser

import SQLiteParser
import SQLiteParserBaseVisitor
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.queryParser.api.SyntaxTree

class SelectAllUsageAnalyser : ConditionAnalyser {

    override fun analyse(tree: SyntaxTree): Boolean {
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
