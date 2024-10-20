package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.impl

import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.treeStructure.Tree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.api.SqlTreeVisualizer
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

private const val VISUALIZER_TITLE = "SQL Tree"

class SqlTreeVisualizerImpl : SqlTreeVisualizer {

    override fun visualize(tree: SqlSyntaxTree) {
        val treeModel = buildTree(tree.rootNode)
        val panel = JPanel(BorderLayout()).apply {
            add(ScrollPaneFactory.createScrollPane(treeModel), BorderLayout.CENTER)
            preferredSize = Dimension(400, 300)
        }
        JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, treeModel)
            .setTitle(VISUALIZER_TITLE)
            .setResizable(true)
            .setMovable(true)
            .setRequestFocus(true)
            .createPopup()
            .showInFocusCenter()
    }

    private fun buildTree(treeRootNode: SqlSyntaxTree.TreeNode): Tree {
        val rootNodeModel = DefaultMutableTreeNode(treeRootNode.name)

        when (treeRootNode) {
            is SqlSyntaxTree.TreeNode.Leaf -> {
                buildTreeNodes(rootNodeModel, treeRootNode)
            }

            is SqlSyntaxTree.TreeNode.Parent -> {
                for (child in treeRootNode.children) {
                    buildTreeNodes(rootNodeModel, child)
                }
            }
        }

        return Tree(DefaultTreeModel(rootNodeModel)).apply {
            for (row in 0 until rowCount) {
                expandAllNodes(this, 0, rowCount)
            }
        }
    }

    private fun expandAllNodes(tree: Tree, startIndex: Int, rowCount: Int) {
        for (i in startIndex until rowCount) {
            tree.expandRow(i)
        }

        if (tree.rowCount != rowCount) {
            expandAllNodes(tree, rowCount, tree.rowCount)
        }
    }

    private fun buildTreeNodes(parentNode: DefaultMutableTreeNode, node: SqlSyntaxTree.TreeNode) {
        val currentNode = DefaultMutableTreeNode(node.name)
        parentNode.add(currentNode)

        if (node is SqlSyntaxTree.TreeNode.Parent) {
            for (child in node.children) {
                buildTreeNodes(currentNode, child)
            }
        }
    }
}