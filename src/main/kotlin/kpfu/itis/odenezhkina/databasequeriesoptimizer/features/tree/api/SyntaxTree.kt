package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api

data class SyntaxTree(
    val rootNode: TreeNode,
) {

    sealed interface TreeNode {
        val name: String

        data class Parent(
            override val name: String,
            val children: List<TreeNode> = emptyList(),
        ) : TreeNode

        data class Leaf(override val name: String) : TreeNode

        data class System(override val name: String) : TreeNode
    }
}
