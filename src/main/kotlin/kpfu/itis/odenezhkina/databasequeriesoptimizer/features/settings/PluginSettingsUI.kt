package kpfu.itis.odenezhkina.databasequeriesoptimizer.features.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.JBUI
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.DocumentFilter
import kotlin.properties.Delegates

// /Users/o.denezhkina/AndroidStudioProjects/SqlQueriesTestApplication/app/schemas/kfu.odenezhkina.sqlqueriestestapplication.ui.theme.AppDatabase
private const val DATABASE_SCHEME_PATH_FIELD_NAME = "Database schemes directory"
private const val DATABASE_SCHEME_PATH_FIELD_LABEL = "Enter database schemes directory"
private const val DATABASE_VERSION_FIELD_NAME = "Database version"
private const val DATABASE_VERSION_FIELD_LABEL = "Enter database version using only numbers"
private const val ERROR_DIALOG_TITLE = "Invalid input"
private const val SCHEME_PATH_ERROR = "Empty or invalid directory, default will be used."
private const val SCHEME_VERSION_ERROR = "Empty ot invalid version, default will be used."

class PluginSettingsUI : Configurable {

    private var databasePathField: JTextField by Delegates.notNull()
    private var databaseVersionField: JTextField by Delegates.notNull()

    // checks whether the user has made any changes to the settings.
    override fun isModified(): Boolean {
        val settings = PluginSettings.getInstance().state

        return when{
            settings.databaseSchemesDirectory is SettingsField.Empty -> true
            settings.databaseVersion is SettingsField.Empty -> true
            settings.databaseVersion.data()?.toString() != databaseVersionField.text.toString() -> true
            settings.databaseSchemesDirectory.data() != databasePathField.text -> true
            else -> false
        }
    }


    // saves the settings when the user clicks "OK" or "Apply".
    override fun apply() {
        val settings = PluginSettings.getInstance()

        val schemePath: String = databasePathField.text ?: run {
            Messages.showErrorDialog(SCHEME_PATH_ERROR, ERROR_DIALOG_TITLE)
            return
        }
        val schemeVersion: Int = databaseVersionField.text
            ?.toInt()
            ?.takeIf { it > 0 }
            ?: run {
                Messages.showErrorDialog(SCHEME_VERSION_ERROR, ERROR_DIALOG_TITLE)
                return
            }

        settings.loadState(
            PluginSettings.State(
                databaseSchemesDirectory = SettingsField.Value(schemePath),
                databaseVersion = SettingsField.Value(schemeVersion),
            )
        )
    }

    // reloads the saved settings when the settings page is opened.
    override fun reset() {
        val settings = PluginSettings.getInstance().state

        databaseVersionField.text = settings.databaseVersion.data()?.toString()
        databasePathField.text = settings.databaseSchemesDirectory.data()
    }

    // builds the UI components (a checkbox and text field in this case) for the settings page.
    override fun createComponent(): JComponent {
        databasePathField = JTextField(DATABASE_SCHEME_PATH_FIELD_NAME)
        databaseVersionField = createDatabaseVersionField()

        return JPanel(GridBagLayout()).apply {
            val constraints = GridBagConstraints().apply {
                insets = JBUI.insets(5, 10)
                fill = GridBagConstraints.HORIZONTAL
            }

            // 1 row
            constraints.gridx = 0
            constraints.gridy = 0
            constraints.weightx = 0.0
            add(JLabel(DATABASE_SCHEME_PATH_FIELD_LABEL), constraints)
            constraints.gridx = 1
            constraints.weightx = 1.0
            add(databasePathField, constraints)

            // 2 row
            constraints.gridx = 0
            constraints.gridy = 1
            constraints.weightx = 0.0
            add(JLabel(DATABASE_VERSION_FIELD_LABEL), constraints)
            constraints.gridx = 1
            constraints.weightx = 1.0
            add(databaseVersionField, constraints)
        }
    }

    override fun getDisplayName(): String = ""

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
