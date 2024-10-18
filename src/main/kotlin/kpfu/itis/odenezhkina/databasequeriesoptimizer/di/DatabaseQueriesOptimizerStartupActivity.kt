package kpfu.itis.odenezhkina.databasequeriesoptimizer.di

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class DatabaseQueriesOptimizerStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        val editorFactory = EditorFactory.getInstance()
        editorFactory.addEditorFactoryListener(DatabaseQueriesOptimizerEditorFactoryListener()) { }
    }

}
