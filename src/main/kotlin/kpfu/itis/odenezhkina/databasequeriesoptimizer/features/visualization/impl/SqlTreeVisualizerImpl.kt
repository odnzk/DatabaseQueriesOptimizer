package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.impl

import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.JBColor
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.treeStructure.Tree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.api.SqlTreeVisualizer
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel

private const val VISUALIZER_TITLE = "SQL Tree"

class SqlTreeVisualizerImpl : SqlTreeVisualizer {

    override fun visualize(tree: SqlSyntaxTree) {
        val treeModel = buildTree(tree.rootNode)
        val panel = JPanel(BorderLayout()).apply {
            add(ScrollPaneFactory.createScrollPane(treeModel), BorderLayout.CENTER)
            preferredSize = Dimension(500, 400)
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

            is SqlSyntaxTree.TreeNode.ParserRuleContext -> {
                for (child in treeRootNode.children) {
                    buildTreeNodes(rootNodeModel, child)
                }
            }

            is SqlSyntaxTree.TreeNode.Error -> Unit
        }

        return Tree(DefaultTreeModel(rootNodeModel)).apply {
            cellRenderer = CustomTreeCellRenderer()
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

        if (node is SqlSyntaxTree.TreeNode.ParserRuleContext) {
            for (child in node.children) {
                buildTreeNodes(currentNode, child)
            }
        }
    }

    /**
     * Custom cell renderer to apply color and icon to tree nodes
     */
    private inner class CustomTreeCellRenderer : DefaultTreeCellRenderer() {
        override fun getTreeCellRendererComponent(
            tree: JTree,
            value: Any,
            selected: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
        ): Component {
            val component = super.getTreeCellRendererComponent(
                tree,
                value,
                selected,
                expanded,
                leaf,
                row,
                hasFocus
            )
            val node = value as DefaultMutableTreeNode
            val nodeName = node.userObject as? String ?: ""

            // Customize colors based on node type
            when {
                nodeName.contains("SELECT") -> {
                    foreground = JBColor(0x3A6EA5, 0x3A6EA5) // Blue for SQL keywords
                    icon = IconLoader.getIcon("/icons/find/find@20x20.svg", javaClass)
                }

                nodeName.contains("TABLE") -> {
                    foreground = JBColor(0x8A2BE2, 0x8A2BE2) // Purple for table names
                    icon = IconLoader.getIcon(
                        "/icons/table/table.svg",
                        javaClass
                    )// Custom icon for tables
                }

                leaf -> {
                    foreground = JBColor(0x2E8B57, 0x2E8B57) // Green for leaf nodes (columns, constants)
                    icon = IconLoader.getIcon(
                        "/icons/cwmShare/cwmShare.svg",
                        javaClass
                    ) // Custom icon for columns
                }

                else -> {
                    foreground = JBColor.BLACK // Default color
                    //icon = defaultIcon // Fallback icon
                }
            }

            // Set a tooltip to display additional node info
            toolTipText = "Node Type: $nodeName"

            // Optionally, set a different font style for SQL keywords
            if (nodeName.uppercase() in setOf("SELECT", "FROM", "WHERE", "JOIN", "ON")) {
                font = font.deriveFont(Font.BOLD) // Bold font for SQL keywords
            }

            return component
        }
    }
}