package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings

sealed interface SettingsField<out T : Any> {
    class Value<T : Any>(val data: T) : SettingsField<T>
    data object Empty : SettingsField<Nothing>
}

fun <T : Any> SettingsField<T>.data(): T? =when (this) {
    SettingsField.Empty -> null
    is SettingsField.Value -> data
}