package kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.ui.awt.RelativePoint
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.queryParser.api.SqlQueryParser
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tooltip.SqlQueryTooltipRender
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.api.TreeVisualizer
import java.awt.Point

class SelectedTextListener(
    private val sqlQueryParser: SqlQueryParser,
    private val sqlTreeVisualizer: TreeVisualizer,
    private val tooltipRender: SqlQueryTooltipRender,
) : CaretListener {

    override fun caretPositionChanged(event: CaretEvent) {
        val selectedText: String? = event.editor.caretModel.currentCaret.selectedText

        if (!selectedText.isNullOrEmpty() && selectedText.isNotBlank()) {
            // TODO()
            tooltipRender.showBallon(RelativePoint(Point()), "$selectedText")

            val tree = sqlQueryParser.parse(selectedText)
            if (tree != null) {
                val treeText = sqlTreeVisualizer.visualize(tree)
                tooltipRender.showBallon(
                    calcTooltipPosition(event.editor),
                    treeText
                )
            }
        }
    }

    private fun calcTooltipPosition(editor: Editor): RelativePoint {
        val xyPosition = editor.visualPositionToXY(editor.offsetToVisualPosition(editor.caretModel.offset))

        return RelativePoint(editor.contentComponent, Point(xyPosition.x, xyPosition.y))
    }
}
