package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.ProjectManager

@State(name = "SqlPluginSettings", storages = [Storage("SqlPluginSettings.xml")])
class PluginSettings : PersistentStateComponent<PluginSettings.State> {

    private var _state: State = State()

    override fun getState(): State = _state

    override fun loadState(newState: State) {
        _state = newState
    }

    companion object {
        fun getInstance(): PluginSettings {
            return service()
        }
    }

    data class State(
        val databaseSchemePath: String = ProjectManager.getInstance().openProjects.first().basePath.toString(),
        val databaseVersion: Int = 1,
    )
}
