package kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.VisualPosition
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.util.TextRange
import com.intellij.ui.awt.RelativePoint
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.SqlQueryParser
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tree.api.SyntaxTree
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tooltip.SqlQueryTooltipRender
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.api.TreeVisualizer

// TODO multiline text -> selected text
class SqlQueriesListener(
    private val sqlQueryParser: SqlQueryParser,
    private val sqlTreeVisualizer: TreeVisualizer,
    private val tooltipRender: SqlQueryTooltipRender,
    private val logger: Logger,
) : EditorMouseListener {

    companion object {
        fun create(
            sqlQueryParser: SqlQueryParser,
            sqlTreeVisualizer: TreeVisualizer,
            tooltipRender: SqlQueryTooltipRender,
            logger: Logger,
        ): SqlQueriesListener = SqlQueriesListener(
            sqlQueryParser = sqlQueryParser,
            sqlTreeVisualizer = sqlTreeVisualizer,
            tooltipRender = tooltipRender,
            logger = logger,
        )
    }

    override fun mouseClicked(e: EditorMouseEvent) {
        val parsedSyntaxTreeResult = checkIfSqlQuery(e.editor)
        parsedSyntaxTreeResult?.let { parsedSyntaxTree ->
            val treeString = sqlTreeVisualizer.visualize(parsedSyntaxTree)
            tooltipRender.showBallon(calcTooltipPosition(e.editor), treeString)
        }
    }

    private fun checkIfSqlQuery(editor: Editor): SyntaxTree? {
        val caretModel: CaretModel = editor.caretModel
        val document: Document = editor.document
        val clickedOffset = caretModel.offset

        // Check clicked line
        val clickedLineNumber = document.getLineNumber(clickedOffset)
        val lineStartOffset = document.getLineStartOffset(clickedLineNumber)
        val lineEndOffset = document.getLineEndOffset(clickedLineNumber)
        val clickedLineText = document.getText(TextRange(lineStartOffset, lineEndOffset))

        // Parse the full SQL query using ANTLR
        val sqlText = document.text  // Get full document text
        return sqlQueryParser.buildTreee(sqlText)  // Assume parseSql is your ANTLR parser function
    }

    private fun calcTooltipPosition(editor: Editor): RelativePoint {
        // Get current caret position
        val caret = editor.caretModel.currentCaret
        val logicalPosition = caret.logicalPosition
        val visualPosition: VisualPosition = editor.logicalToVisualPosition(logicalPosition)

        // Calculate the coordinates of the bottom of the current line
        val point = editor.visualPositionToXY(visualPosition)
        val lineHeight = editor.lineHeight

        // Adjust the popup position to appear below the line (add line height)
        val locationBelowLine = RelativePoint(editor.contentComponent, point.apply { translate(0, lineHeight) })
        return locationBelowLine
    }

}
