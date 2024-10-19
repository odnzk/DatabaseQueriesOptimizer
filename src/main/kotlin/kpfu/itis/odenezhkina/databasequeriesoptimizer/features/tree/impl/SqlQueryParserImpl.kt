package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.impl

import SQLiteLexer
import SQLiteParser
import com.intellij.openapi.diagnostic.Logger
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.SqlQueryParser
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.SyntaxTree
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode

class SqlQueryParserImpl(
    private val logger: Logger,
    private val errorListenerProvider: () -> SqlQueryParserErrorListener,
) : SqlQueryParser {

    // TODO() unite somehow with validator
    override fun buildTreee(rawQuery: String): SyntaxTree? {
        return try {
            val charStream = CharStreams.fromString(rawQuery)
            val lexer = SQLiteLexer(charStream)
            val listener = errorListenerProvider()
            val tokens = CommonTokenStream(lexer)

            val parser = SQLiteParser(tokens).apply {
                addErrorListener(listener)
            }

            val tree = parser.sql_stmt()

            if (listener.hasError) {
                null
            } else {
                SyntaxTree(visitNode(tree))
            }
        } catch (e: Exception) {
            logger.error(e)
            null
        }
    }

    private fun visitNode(node: ParseTree): SyntaxTree.TreeNode {
        if (node is TerminalNode) return SyntaxTree.TreeNode.Leaf(node.text)

        val children = mutableListOf<SyntaxTree.TreeNode>()
        for (i in 0 until node.childCount) {
            val childNode = visitNode(node.getChild(i))

            if (childNode.name.isNotBlank()) {
                children.add(childNode)
            }
        }

        return if (node is SQLiteParser.Sql_stmt_listContext || node is SQLiteParser.Sql_stmtContext) {
            SyntaxTree.TreeNode.System(node.text)
        } else {
            SyntaxTree.TreeNode.Parent(node.text, children)
        }
    }
}
