package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings

import com.intellij.openapi.components.Service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.Messages
import kpfu.itis.odenezhkina.databasequeriesoptimizer.plugin.PluginUI
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.DocumentFilter
import kotlin.properties.Delegates

private const val DATABASE_SCHEME_PATH_FIELD_NAME = "Database scheme path"
private const val DATABASE_VERSION_FIELD_NAME = "Database version"
private const val SETTINGS_NAME = "${PluginUI.NAME} settings"
private const val ERROR_DIALOG_TITLE = "Invalid input"
private const val SCHEME_PATH_ERROR = "Empty or invalid path, default will be used."
private const val SCHEME_VERSION_ERROR = "Empty ot invalid version, default will be used."

@Service(Service.Level.PROJECT)
class PluginSettingsUI : Configurable {

    private var databasePathField: JTextField by Delegates.notNull()
    private var databaseVersionField: JTextField by Delegates.notNull()

    // checks whether the user has made any changes to the settings.
    override fun isModified(): Boolean {
        val settings = PluginSettings.getInstance().state

        if (databasePathField.text != settings.databaseSchemePath) return true
        return databaseVersionField.text != settings.databaseVersion.toString()
    }


    // saves the settings when the user clicks "OK" or "Apply".
    override fun apply() {
        val settings = PluginSettings.getInstance()

        val schemePath = databasePathField.text ?: run {
            Messages.showErrorDialog(SCHEME_PATH_ERROR, ERROR_DIALOG_TITLE)
            settings.state.databaseSchemePath
        }
        val schemeVersion = databaseVersionField.text
            ?.toInt()
            ?.takeIf { it > 0 }
            ?: run {
                Messages.showErrorDialog(SCHEME_VERSION_ERROR, ERROR_DIALOG_TITLE)
                settings.state.databaseVersion
            }

        settings.loadState(
            PluginSettings.State(
                databaseSchemePath = schemePath,
                databaseVersion = schemeVersion,
            )
        )
    }

    // reloads the saved settings when the settings page is opened.
    override fun reset() {
        val settings = PluginSettings.getInstance().state

        databaseVersionField.text = settings.databaseVersion.toString()
        databasePathField.text = settings.databaseSchemePath
    }

    // builds the UI components (a checkbox and text field in this case) for the settings page.
    override fun createComponent(): JComponent {
        databasePathField = JTextField(DATABASE_SCHEME_PATH_FIELD_NAME)
        databaseVersionField = createDatabaseVersionField()

        return JPanel().apply {
            add(databaseVersionField)
            add(databasePathField)
        }
    }

    override fun getDisplayName(): String = SETTINGS_NAME

    private fun createDatabaseVersionField(): JTextField {
        val textField = JTextField(DATABASE_VERSION_FIELD_NAME)

        val document = textField.document as AbstractDocument
        document.documentFilter = object : DocumentFilter() {
            @Throws(BadLocationException::class)
            override fun insertString(
                fb: FilterBypass,
                offset: Int,
                string: String,
                attr: AttributeSet?
            ) {
                if (string.isNumeric()) {
                    super.insertString(fb, offset, string, attr)
                }
            }

            @Throws(BadLocationException::class)
            override fun replace(
                fb: FilterBypass,
                offset: Int,
                length: Int,
                text: String,
                attrs: AttributeSet?
            ) {
                if (text.isNumeric()) {
                    super.replace(fb, offset, length, text, attrs)
                }
            }

            @Throws(BadLocationException::class)
            override fun remove(fb: FilterBypass, offset: Int, length: Int) {
                super.remove(fb, offset, length)
            }
        }

        return textField
    }

    private fun String.isNumeric(): Boolean {
        return this.all { it.isDigit() }
    }
}
