package kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiElement
import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.api.SqlTreeVisualizer

private const val ACTIONS_POPUP_TITLE = "Choose action"
private const val ACTION_OPTIMIZE_QUERY = "Optimize"
private const val ACTION_SHOW_SQL_TREE = "Show SQL tree"

interface PluginActionsPopupRender {

    fun showActionsPopup(element: PsiElement, editor: Editor, sqlSyntaxTree: SqlSyntaxTree)

    companion object {
        fun create(): PluginActionsPopupRender =
            PluginActionsPopupRenderImpl(sqlTreeVisualizer = SqlTreeVisualizer.create())
    }
}

class PluginActionsPopupRenderImpl(private val sqlTreeVisualizer: SqlTreeVisualizer) :
    PluginActionsPopupRender {

    override fun showActionsPopup(
        element: PsiElement,
        editor: Editor,
        sqlSyntaxTree: SqlSyntaxTree
    ) {
        val actions = listOf(ACTION_OPTIMIZE_QUERY, ACTION_SHOW_SQL_TREE)

        val popup: JBPopup = JBPopupFactory.getInstance()
            .createPopupChooserBuilder(actions)
            .setTitle(ACTIONS_POPUP_TITLE)
            .setMovable(false)
            .setItemChosenCallback { selectedAction ->
                performAction(selectedAction, element, sqlSyntaxTree)
            }
            .createPopup()

        popup.showInBestPositionFor(editor)
    }

    private fun performAction(action: String, element: PsiElement, sqlSyntaxTree: SqlSyntaxTree) {
        when (action) {
            ACTION_OPTIMIZE_QUERY -> {
                // TODO()
            }

            ACTION_SHOW_SQL_TREE -> {
                sqlTreeVisualizer.visualize(sqlSyntaxTree)
            }
        }
    }

}