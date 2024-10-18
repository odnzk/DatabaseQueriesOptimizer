package kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin.highlight

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color

class CodeHighlighterAction{
    fun highlightLine(editor: Editor, line: Int) {
        val markupModel: MarkupModel = editor.markupModel
        val startOffset = editor.document.getLineStartOffset(line)
        val endOffset = editor.document.getLineEndOffset(line)

        val textAttributes = TextAttributes().apply {
            foregroundColor = Color(255, 0, 0) // Example: red color for highlight
        }

        markupModel.addLineHighlighter(line, HighlighterLayer.ADDITIONAL_SYNTAX, textAttributes)
    }

}
