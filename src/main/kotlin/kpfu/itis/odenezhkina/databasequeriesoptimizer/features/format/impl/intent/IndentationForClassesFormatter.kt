package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.intent

import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.Formatter
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.containsClass
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings.PluginSettings

/*
 Indent lines that start with specific clauses like WHERE, AND, OR, or JOIN.
 You can keep it simple by adding a fixed number of spaces or tabs.
 */

class IndentationForClassesFormatter : Formatter {

    override fun format(tree: SqlSyntaxTree): SqlSyntaxTree {
        return SqlSyntaxTree(formatNode(PluginSettings.getInstance().state.indentationForClassesSetting, tree.rootNode, indentLevel = 0))
    }

    private fun formatNode(state: IndentationForClassesSetting, node: SqlSyntaxTree.TreeNode, indentLevel: Int): SqlSyntaxTree.TreeNode {
        return when (node) {
            is SqlSyntaxTree.TreeNode.Leaf -> {
                // If the node name is a clause that requires indentation, indent it

                val formattedName = if (state.indentedClasses.containsClass(node.name)) {
                    state.indentation.repeat(indentLevel) + node.name
                } else {
                    node.name
                }
                node.copy(name = formattedName)
            }
            is SqlSyntaxTree.TreeNode.ParserRuleContext -> {
                // Increase indentation for children of certain clauses
                val childIndentLevel = if (state.indentedClasses.containsClass(node.name)) indentLevel + 1 else indentLevel
                node.copy(children = node.children.map { formatNode(state, it, childIndentLevel) })
            }
            is SqlSyntaxTree.TreeNode.Error -> {
                // Return Error nodes as-is
                node
            }
        }
    }


}