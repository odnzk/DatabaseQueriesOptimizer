package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.removeExtraSpaces

import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.Formatter

/*
 Ensure consistent single spaces between keywords and expressions. For instance, remove any duplicate spaces and ensure that commas are followed by a space but not preceded by one.
 */
class RemoveExtraSpacesFormatter : Formatter {

    override fun format(tree: SqlSyntaxTree): SqlSyntaxTree {
        return SqlSyntaxTree(formatNode(tree.rootNode))
    }

    private fun formatNode(node: SqlSyntaxTree.TreeNode): SqlSyntaxTree.TreeNode {
        return when (node) {
            is SqlSyntaxTree.TreeNode.Leaf -> {
                // Clean up the text in the Leaf node
                val formattedName = formatText(node.name)
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

    private fun formatText(text: String): String {
        // 1. Remove extra spaces by replacing multiple spaces with a single space
        var cleanedText = text.replace(Regex("\\s+"), " ")

        // 2. Ensure commas are followed by a space but not preceded by one
        cleanedText = cleanedText.replace(Regex("\\s*,\\s*"), ", ")

        return cleanedText.trim() // Trim to remove leading/trailing spaces if present
    }
}