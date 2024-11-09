package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.capitalize

import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.Formatter
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.SQLCommonKeywords
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.containsClass
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings.PluginSettings

/* Convert SQL keywords (e.g., SELECT, FROM, WHERE) to uppercase.
 This is visually appealing and makes the structure of the query more evident.
 */
class KeywordCapitalizationFormatter : Formatter {

    override fun format(tree: SqlSyntaxTree): SqlSyntaxTree {
        return SqlSyntaxTree(formatNode(PluginSettings.getInstance().state.keywordCapitalizationSetting.keywords, tree.rootNode))
    }

    private fun formatNode(keywords: Set<SQLCommonKeywords>, node: SqlSyntaxTree.TreeNode): SqlSyntaxTree.TreeNode {
        return when (node) {
            is SqlSyntaxTree.TreeNode.Leaf -> {
                // If the leaf node name is a keyword, convert it to uppercase
                val formattedName = if (keywords.containsClass(node.name)) {
                    node.name.uppercase()
                } else {
                    node.name
                }
                node.copy(name = formattedName)
            }
            is SqlSyntaxTree.TreeNode.ParserRuleContext -> {
                // Recursively format each child node
                node.copy(children = node.children.map { formatNode(keywords, it) })
            }
            is SqlSyntaxTree.TreeNode.Error -> {
                // Return the Error node as-is
                node
            }
        }
    }

}