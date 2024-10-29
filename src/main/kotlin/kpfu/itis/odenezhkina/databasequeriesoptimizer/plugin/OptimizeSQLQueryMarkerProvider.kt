package kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ui.UIUtil
import kpfu.itis.odenezhkina.databasequeriesoptimizer.common.SqlSyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation.api.SqlQueryValidator
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import javax.swing.Icon

private const val MARKER_ACCESSIBILITY_DESCRIPTION = "SQL optimizer icon"

class OptimizeSQLQueryMarkerProvider(
    private val validator: SqlQueryValidator,
    private val popupRenderer: PluginActionsPopupRender,
) : LineMarkerProvider {

    private sealed interface ParsedSyntaxTreeResult {
        data class Success(val tree: SqlSyntaxTree) : ParsedSyntaxTreeResult
        data object Failure : ParsedSyntaxTreeResult
    }

    private val alreadyParsedCache = mutableMapOf<String, ParsedSyntaxTreeResult>()

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val ktNamedFunction =
            PsiTreeUtil.getParentOfType(element, KtNamedFunction::class.java) ?: return null
        if (ktNamedFunction.annotationEntries.map { it.text }
                .any { it.equals("Query", ignoreCase = true) }) return null

        return when (element) {
            is KtStringTemplateEntry -> {
                val text: String? = element.text
                if (text.isNullOrBlank()) return null
                when (val cached = alreadyParsedCache[text]) {
                    is ParsedSyntaxTreeResult.Success -> return buildLineMarkerInfo(
                        element,
                        cached.tree
                    )

                    ParsedSyntaxTreeResult.Failure -> return null
                    null -> Unit
                }

                val syntaxTreeResult = validator.parseToTree(text).let { parsedTree ->
                    if (parsedTree != null) {
                        ParsedSyntaxTreeResult.Success(parsedTree)
                    } else {
                        ParsedSyntaxTreeResult.Failure
                    }
                }
                alreadyParsedCache[text] = syntaxTreeResult
                if (syntaxTreeResult is ParsedSyntaxTreeResult.Success) {
                    buildLineMarkerInfo(element, syntaxTreeResult.tree)
                } else {
                    null
                }
            }

            else -> null
        }
    }

    private fun buildLineMarkerInfo(element: PsiElement, sqlSyntaxTree: SqlSyntaxTree) =
        LineMarkerInfo(
            element,
            element.textRange,
            getThemeAwareIcon(),
            { PluginUI.NAME },
            { _, _ ->
                val editor = PsiEditorUtil.findEditor(element)
                if (editor != null) {
                    popupRenderer.showActionsPopup(editor, sqlSyntaxTree)
                }
            },
            GutterIconRenderer.Alignment.RIGHT,
            { MARKER_ACCESSIBILITY_DESCRIPTION }
        )

    private fun getThemeAwareIcon(): Icon {
        return if (!UIUtil.isUnderDarcula()) {
            IconLoader.getIcon("/icons/manageDataSources.svg", javaClass)
        } else {
            IconLoader.getIcon("/icons/manageDataSources_dark.svg", javaClass)
        }
    }

}
