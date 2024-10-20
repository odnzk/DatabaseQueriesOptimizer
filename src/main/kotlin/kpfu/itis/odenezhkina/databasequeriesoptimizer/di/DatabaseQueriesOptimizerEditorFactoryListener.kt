package kpfu.itis.odenezhkina.databasequeriesoptimizer.di

import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.event.EditorMouseListener

class DatabaseQueriesOptimizerEditorFactoryListener : EditorFactoryListener {

    private val store: HashMap<Class<*>, Any> = HashMap()

    override fun editorCreated(event: EditorFactoryEvent) {
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        store.values.forEach { value ->
            when (value) {
                is EditorMouseListener -> {
                    event.editor.removeEditorMouseListener(value)
                }

                is CaretListener -> {
                    event.editor.caretModel.removeCaretListener(value)
                }
            }
        }
    }
}
