package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation

import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.Trees

class SqlSyntaxTreeConverterImpl : SqlSyntaxTreeConverter {

    override fun convert(parsedTree: ParseTree, parser: Parser): SqlSyntaxTree {
        return SqlSyntaxTree(visitNode(parsedTree, parser))
    }

    private fun visitNode(node: ParseTree, parser: Parser): SqlSyntaxTree.TreeNode {
        return when(node){
            is TerminalNode -> SqlSyntaxTree.TreeNode.Leaf(node.symbol.text)
            is ParserRuleContext ->{
                val nodeName = Trees.getNodeText(node, parser)
                val children = mutableListOf<SqlSyntaxTree.TreeNode>()
                for (i in 0 until node.childCount) {
                    val childNode = visitNode(node.getChild(i), parser)
                    children.add(childNode)
                }
                SqlSyntaxTree.TreeNode.ParserRuleContext(nodeName, children)
            }
            else -> SqlSyntaxTree.TreeNode.Error(node.text)
        }
    }

}