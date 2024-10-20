package kpfu.itis.odenezhkina.databasequeriesoptimizer.di

import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.validation.api.SqlQueryValidator
import kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin.OptimizeSQLQueryMarkerProvider
import kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin.PluginActionsPopupRender


class DatabaseQueriesOptimizerStartActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        LineMarkerProviders.getInstance().addExplicitExtension(
            Language.ANY,
            OptimizeSQLQueryMarkerProvider(
                validator = SqlQueryValidator.create(),
                popupRenderer = PluginActionsPopupRender.create(),
            )
        )
    }
}