package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.breakLine.NewLinesForSpecificKeywordsSetting
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.capitalize.KeywordCapitalizationSetting
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.format.impl.intent.IndentationForClassesSetting
import kpfu.itis.odenezhkina.databasequeriesoptimizer.features.schema.DatabaseSchemaSetting

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
        val databaseSchemaSetting: DatabaseSchemaSetting = DatabaseSchemaSetting(),
        val indentationForClassesSetting: IndentationForClassesSetting = IndentationForClassesSetting(),
        val keywordCapitalizationSetting: KeywordCapitalizationSetting = KeywordCapitalizationSetting(),
        val newLinesForSpecificKeywordsSetting: NewLinesForSpecificKeywordsSetting = NewLinesForSpecificKeywordsSetting(),
    )
}
