package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation

import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.tree.ParseTree

interface SqlSyntaxTreeConverter {
    fun convert(parsedTree: ParseTree, parser: Parser): SqlSyntaxTree

    companion object {
        fun create(): SqlSyntaxTreeConverter = SqlSyntaxTreeConverterImpl()
    }
}