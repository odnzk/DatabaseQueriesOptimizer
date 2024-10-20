package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.impl

import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree

// TODO() write tests or?
class SqlTreeToTextConverterImpl {

    fun convert(tree: SqlSyntaxTree): String {
        val result = StringBuilder()
        appendNode(tree.rootNode, result, "", false)
        return result.toString()
    }

    private fun appendNode(
        node: SqlSyntaxTree.TreeNode,
        stringBuilder: StringBuilder,
        prefix: String,
        lastChild: Boolean,
    ) {
        stringBuilder.append("$prefix ${if (lastChild) "└──" else "├──"} ${node.name}")

        when (node) {
            is SqlSyntaxTree.TreeNode.Leaf -> return
            is SqlSyntaxTree.TreeNode.Parent -> {
                val newPrefix = "$prefix${if (lastChild) "" else "│"}  "
                node.children.dropLast(1).forEach { child ->
                    appendNode(child, stringBuilder, newPrefix, false)
                }
                appendNode(node.children.last(), stringBuilder, newPrefix, true)
            }
        }
    }
}
