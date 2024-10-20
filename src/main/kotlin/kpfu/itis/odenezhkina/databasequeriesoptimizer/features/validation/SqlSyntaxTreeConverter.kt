package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation

import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.SqlSyntaxTree
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.Trees

interface SqlSyntaxTreeConverter {
    fun convert(parsedTree: ParseTree, parser: Parser): SqlSyntaxTree

    companion object {
        fun create(): SqlSyntaxTreeConverter = SqlSyntaxTreeConverterImpl()
    }
}

class SqlSyntaxTreeConverterImpl : SqlSyntaxTreeConverter {

    override fun convert(parsedTree: ParseTree, parser: Parser): SqlSyntaxTree {
        return SqlSyntaxTree(visitNode(parsedTree, parser))
    }

    private fun visitNode(node: ParseTree, parser: Parser): SqlSyntaxTree.TreeNode {
        return if (node is TerminalNode) {
            SqlSyntaxTree.TreeNode.Leaf(node.symbol.text)
        } else {
            val nodeName = Trees.getNodeText(node, parser)
            val children = mutableListOf<SqlSyntaxTree.TreeNode>()
            for (i in 0 until node.childCount) {
                val childNode = visitNode(node.getChild(i), parser)
                children.add(childNode)
            }
            SqlSyntaxTree.TreeNode.Parent(nodeName, children)
        }
    }

}