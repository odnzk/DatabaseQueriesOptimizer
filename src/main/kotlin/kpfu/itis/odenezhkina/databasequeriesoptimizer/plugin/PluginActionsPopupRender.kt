package kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiElement
import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.api.Formatter
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.breakLine.NewLinesForSpecificKeywordsFormatter
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.capitalize.KeywordCapitalizationFormatter
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.intent.IndentationForClassesFormatter
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.removeExtraSpaces.RemoveExtraSpacesFormatter
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.optimization.SqlQueryOptimizer
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.api.SqlTreeVisualizer

private const val ACTIONS_POPUP_TITLE = "Choose action"
private const val ACTION_OPTIMIZE_QUERY = "Optimize"
private const val ACTION_SHOW_SQL_TREE = "Show SQL tree"
private const val ACTION_FORMAT = "Format"
private const val NO_OPTIMIZARION_MESSAGE = "No optimization"

interface PluginActionsPopupRender {

    fun showActionsPopup(
        editor: Editor,
        sqlSyntaxTree: SqlSyntaxTree,
        psiElement: PsiElement,
    )

    companion object {
        fun create(): PluginActionsPopupRender =
            PluginActionsPopupRenderImpl(
                sqlTreeVisualizer = SqlTreeVisualizer.create(),
                sqlQueryOptimizer = SqlQueryOptimizer.create(),
                formatters = setOf(
                    NewLinesForSpecificKeywordsFormatter(),
                    KeywordCapitalizationFormatter(),
                    IndentationForClassesFormatter(),
                    RemoveExtraSpacesFormatter(),
                )
            )
    }
}

class PluginActionsPopupRenderImpl(
    private val sqlTreeVisualizer: SqlTreeVisualizer,
    private val sqlQueryOptimizer: SqlQueryOptimizer,
    private val formatters: Set<Formatter>,
) :
    PluginActionsPopupRender {

    override fun showActionsPopup(
        editor: Editor,
        sqlSyntaxTree: SqlSyntaxTree,
        psiElement: PsiElement,
    ) {
        val actions = listOf(ACTION_OPTIMIZE_QUERY, ACTION_SHOW_SQL_TREE, ACTION_FORMAT)

        val popup: JBPopup = JBPopupFactory.getInstance()
            .createPopupChooserBuilder(actions)
            .setTitle(ACTIONS_POPUP_TITLE)
            .setMovable(false)
            .setItemChosenCallback { selectedAction ->
                performAction(selectedAction, sqlSyntaxTree, editor, psiElement)
            }
            .createPopup()

        popup.showInBestPositionFor(editor)
    }

    private fun performAction(
        action: String,
        sqlSyntaxTree: SqlSyntaxTree,
        editor: Editor,
        psiElement: PsiElement,
    ) {
        when (action) {
            ACTION_OPTIMIZE_QUERY -> {
                val sql = StringBuilder()
                extractSqlQuery(sqlSyntaxTree.rootNode, sql)
                val hintText: String =
                    when (val optimizedQuery = sqlQueryOptimizer.optimize(sql.toString().trim())) {
                        SqlQueryOptimizer.OptimizationResult.Empty -> NO_OPTIMIZARION_MESSAGE
                        is SqlQueryOptimizer.OptimizationResult.Error -> optimizedQuery.e.message.orEmpty()
                        is SqlQueryOptimizer.OptimizationResult.Success -> optimizedQuery.optimized
                    }
                HintManager.getInstance().showInformationHint(editor, hintText)
            }

            ACTION_SHOW_SQL_TREE -> {
                sqlTreeVisualizer.visualize(sqlSyntaxTree)
            }

            ACTION_FORMAT -> {
                var formattedTree = sqlSyntaxTree
                formatters.forEach { formatter ->
                    formattedTree = formatter.format(formattedTree)
                }
                val formattedQuery = StringBuilder()
                extractSqlQuery(formattedTree.rootNode, formattedQuery)

                WriteCommandAction.runWriteCommandAction(editor.project){
                    editor.document.replaceString(
                        psiElement.textRange.startOffset,
                        psiElement.textRange.endOffset,
                        formattedQuery
                    )
                }
            }
        }
    }

    private fun extractSqlQuery(node: SqlSyntaxTree.TreeNode, builder: StringBuilder) {
        when (node) {
            is SqlSyntaxTree.TreeNode.Leaf -> {
                builder.append("${node.name} ")
            }

            is SqlSyntaxTree.TreeNode.ParserRuleContext -> {
                node.children.forEach { child ->
                    extractSqlQuery(child, builder)
                }
            }

            else -> Unit
        }
    }

}