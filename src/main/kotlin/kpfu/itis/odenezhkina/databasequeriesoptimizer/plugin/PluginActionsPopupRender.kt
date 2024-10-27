package kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.optimization.SqlQueryOptimizer
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.api.SqlTreeVisualizer

private const val ACTIONS_POPUP_TITLE = "Choose action"
private const val ACTION_OPTIMIZE_QUERY = "Optimize"
private const val ACTION_SHOW_SQL_TREE = "Show SQL tree"

interface PluginActionsPopupRender {

    fun showActionsPopup(
        editor: Editor,
        sqlSyntaxTree: SqlSyntaxTree,
    )

    companion object {
        fun create(): PluginActionsPopupRender =
            PluginActionsPopupRenderImpl(
                sqlTreeVisualizer = SqlTreeVisualizer.create(),
                sqlQueryOptimizer = SqlQueryOptimizer.create(),
            )
    }
}

class PluginActionsPopupRenderImpl(
    private val sqlTreeVisualizer: SqlTreeVisualizer,
    private val sqlQueryOptimizer: SqlQueryOptimizer,
) :
    PluginActionsPopupRender {

    override fun showActionsPopup(
        editor: Editor,
        sqlSyntaxTree: SqlSyntaxTree,
    ) {
        val actions = listOf(ACTION_OPTIMIZE_QUERY, ACTION_SHOW_SQL_TREE)

        val popup: JBPopup = JBPopupFactory.getInstance()
            .createPopupChooserBuilder(actions)
            .setTitle(ACTIONS_POPUP_TITLE)
            .setMovable(false)
            .setItemChosenCallback { selectedAction ->
                performAction(selectedAction, sqlSyntaxTree, editor)
            }
            .createPopup()

        popup.showInBestPositionFor(editor)
    }

    private fun performAction(action: String, sqlSyntaxTree: SqlSyntaxTree, editor: Editor) {
        when (action) {
            ACTION_OPTIMIZE_QUERY -> {
                val sql = StringBuilder()
                extractSqlQuery(sqlSyntaxTree.rootNode, sql)
                val hintText: String =
                    when (val optimizedQuery = sqlQueryOptimizer.optimize(sql.toString())) {
                        SqlQueryOptimizer.OptimizationResult.Empty -> "No optimization"
                        is SqlQueryOptimizer.OptimizationResult.Error -> optimizedQuery.e.message.orEmpty()
                        is SqlQueryOptimizer.OptimizationResult.Success -> optimizedQuery.optimized
                    }
                HintManager.getInstance().showInformationHint(editor, hintText)
            }

            ACTION_SHOW_SQL_TREE -> {
                sqlTreeVisualizer.visualize(sqlSyntaxTree)
            }
        }
    }

    private fun extractSqlQuery(node: SqlSyntaxTree.TreeNode, builder: StringBuilder) {
        when (node) {
            is SqlSyntaxTree.TreeNode.Leaf -> {
                builder.append(node.name)
            }

            is SqlSyntaxTree.TreeNode.Parent -> {
                node.children.forEach { child ->
                    extractSqlQuery(child, builder)
                }
            }
        }
    }

}