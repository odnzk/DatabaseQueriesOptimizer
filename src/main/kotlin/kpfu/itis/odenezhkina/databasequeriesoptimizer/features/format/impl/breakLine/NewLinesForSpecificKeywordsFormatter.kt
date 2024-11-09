package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.breakLine

import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.Formatter
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.SQLCommonKeywords
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.containsClass
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings.PluginSettings

/*
Place keywords such as SELECT, FROM, WHERE, GROUP BY, and ORDER BY on new lines. This makes the structure of the SQL query clearer and separates the main parts of the query.
 */
class NewLinesForSpecificKeywordsFormatter : Formatter {

    private val keywordsRequiringNewLine: Set<SQLCommonKeywords> =
        PluginSettings.getInstance().state.newLinesForSpecificKeywordsSetting.keywords
    private val newline = "\n"

    override fun format(tree: SqlSyntaxTree): SqlSyntaxTree {
        return SqlSyntaxTree(formatNode(tree.rootNode))
    }

    private fun formatNode(node: SqlSyntaxTree.TreeNode): SqlSyntaxTree.TreeNode {
        return when (node) {
            is SqlSyntaxTree.TreeNode.Leaf -> {
                // If the node's name is a keyword that should be on a new line, add a newline before it
                val formattedName = if (keywordsRequiringNewLine.containsClass(node.name)) {
                    newline + node.name
                } else {
                    node.name
                }
                node.copy(name = formattedName)
            }

            is SqlSyntaxTree.TreeNode.ParserRuleContext -> {
                // Recursively format each child node
                node.copy(children = node.children.map { formatNode(it) })
            }

            is SqlSyntaxTree.TreeNode.Error -> {
                // Return Error nodes as-is
                node
            }
        }
    }
}