package kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilBase
import com.intellij.util.ui.UIUtil
import javax.swing.Icon

private const val MARKER_TITLE = "Optimize SQL Query"
private const val MARKER_ASSEBILITY_DESCRIPTION = "SQL optimizer icon"
private const val ACTIONS_POPUP_TITLE = "Choose action"
private const val ACTION_OPTIMIZE_QUERY = "Optimize"
private const val ACTION_SHOW_SQL_TREE = "Show SQL tree"

class OptimizeSQLQueryMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*> {
        return LineMarkerInfo(
            element,
            element.textRange,
            getThemeAwareIcon(),
            { MARKER_TITLE },
            { _, elt ->
                val editor = PsiUtilBase.findEditor(elt)
                if (editor != null) {
                    showActionsPopup(elt, editor)
                }
            },
            GutterIconRenderer.Alignment.RIGHT,
            { MARKER_ASSEBILITY_DESCRIPTION }
        )
    }

    private fun getThemeAwareIcon(): Icon {
        return if (!UIUtil.isUnderDarcula()) {
            IconLoader.getIcon("/icons/manageDataSources.svg", javaClass)
        } else {
            IconLoader.getIcon("/icons/manageDataSources_dark.svg", javaClass)
        }
    }

    private fun showActionsPopup(element: PsiElement, editor: Editor) {
        val actions = listOf(ACTION_OPTIMIZE_QUERY, ACTION_SHOW_SQL_TREE)

        val popup: JBPopup = JBPopupFactory.getInstance()
            .createPopupChooserBuilder(actions)
            .setTitle(ACTIONS_POPUP_TITLE)
            .setMovable(false)
            .setItemChosenCallback { selectedAction ->
                performAction(selectedAction, element)
            }
            .createPopup()

        popup.showInBestPositionFor(editor)
    }

    private fun performAction(action: String, element: PsiElement) {
        when (action) {
            ACTION_OPTIMIZE_QUERY -> {
                // TODO()
            }

            ACTION_SHOW_SQL_TREE -> {
                // TODO()
            }
        }
    }

}
