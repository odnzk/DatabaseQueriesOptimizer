package kpfu.itis.odenezhkina.databasequeriesoptimizer.common

data class SqlSyntaxTree(
    val rootNode: TreeNode,
) {

    sealed interface TreeNode {
        val name: String

        data class ParserRuleContext(
            override val name: String,
            val children: List<TreeNode> = emptyList(),
        ) : TreeNode

        data class Leaf(override val name: String) : TreeNode

        data class Error(override val name: String): TreeNode
    }
}
