package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.impl

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.SyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.api.TreeVisualizer

class TextTreeVisualizer : TreeVisualizer {

    override fun visualize(tree: SyntaxTree): String {
        return printNode(tree.rootNode, 0)
    }

    private fun printNode(node: SyntaxTree.TreeNode, level: Int): String {
        node
        val result = StringBuilder()

        repeat(level) { result.append("  ") }
        when (node) {
            is SyntaxTree.TreeNode.Leaf -> {
                result.append(node.name).append("\n")
            }
            is SyntaxTree.TreeNode.Parent -> {
                result.append(node.name).append("\n")
                for (child in node.children) {
                    result.append(printNode(child, level + 1))
                }
            }
            is SyntaxTree.TreeNode.System -> Unit
        }

        return result.toString()
    }
}
