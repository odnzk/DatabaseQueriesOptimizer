package kpfu.itis.odenezhkina.databasequeriesoptimizer.di

import com.intellij.openapi.diagnostic.Logger
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

    override fun editorCreated(event: EditorFactoryEvent) {
        with(event.editor){
            addEditorMouseListener(provideOnDatabaseQueriesHoverListener())
            caretModel.addCaretListener(provideSelectedTextListener())
        }
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        //TODO("how to clean")
//        event.editor.removeEditorMouseListener()
    }

    private fun provideSelectedTextListener(): SelectedTextListener {
        val sqlQueryParser = AntrlSqlQueryParser(
            Logger.getInstance(AntrlSqlQueryParser::class.java),
            errorListenerProvider = { SimpleAntlrErrorListener() }
        )
        return SelectedTextListener(
            sqlQueryParser = sqlQueryParser,
            tooltipRender = SqlQueryTooltipRender(),
            sqlTreeVisualizer = TextTreeVisualizer(),
        )
    }

    private fun provideOnDatabaseQueriesHoverListener(): EditorMouseListener {
        val sqlQueryParser = AntrlSqlQueryParser(
            Logger.getInstance(AntrlSqlQueryParser::class.java),
            errorListenerProvider = { SimpleAntlrErrorListener() }
        )
        return SqlQueriesListener.create(
            sqlQueryParser = sqlQueryParser,
            logger = Logger.getInstance(SqlQueriesListener::class.java),
            tooltipRender = SqlQueryTooltipRender(),
            sqlTreeVisualizer = TextTreeVisualizer(),
        )
    }
}
