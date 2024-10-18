package kpfu.itis.odenezhkina.databasequeriesoptimizer.di

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.event.EditorMouseListener
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.queryParser.impl.AntrlSqlQueryParser
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.queryParser.impl.SimpleAntlrErrorListener
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.tooltip.SqlQueryTooltipRender
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.visualization.impl.TextTreeVisualizer
import kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin.SelectedTextListener
import kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin.SqlQueriesListener

class DatabaseQueriesOptimizerEditorFactoryListener : EditorFactoryListener {

    private val store: HashMap<Class<*>, Any> = HashMap()

    override fun editorCreated(event: EditorFactoryEvent) {
        registerSQLQueriesListener(event.editor)
        registerSelectedTextListener(event.editor.caretModel)
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        store.values.forEach { value ->
            when(value){
                is EditorMouseListener -> {
                    event.editor.removeEditorMouseListener(value)
                }
                is CaretListener ->{
                    event.editor.caretModel.removeCaretListener(value)
                }
            }
        }
    }

    private fun registerSelectedTextListener(caretModel: CaretModel) {
        val sqlQueryParser = AntrlSqlQueryParser(
            Logger.getInstance(AntrlSqlQueryParser::class.java),
            errorListenerProvider = { SimpleAntlrErrorListener() }
        )
        val listener = SelectedTextListener(
            sqlQueryParser = sqlQueryParser,
            tooltipRender = SqlQueryTooltipRender(),
            sqlTreeVisualizer = TextTreeVisualizer(),
        )

        store[SelectedTextListener::class.java] = listener
        caretModel.addCaretListener(listener)
    }

    private fun registerSQLQueriesListener(editor: Editor){
        val sqlQueryParser = AntrlSqlQueryParser(
            Logger.getInstance(AntrlSqlQueryParser::class.java),
            errorListenerProvider = { SimpleAntlrErrorListener() }
        )
        val listener = SqlQueriesListener.create(
            sqlQueryParser = sqlQueryParser,
            logger = Logger.getInstance(SqlQueriesListener::class.java),
            tooltipRender = SqlQueryTooltipRender(),
            sqlTreeVisualizer = TextTreeVisualizer(),
        )

        store[SqlQueriesListener::class.java] = listener
        editor.addEditorMouseListener(listener)
    }
}
